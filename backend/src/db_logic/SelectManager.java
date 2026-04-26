package db_logic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectManager {
    private final DbPathResolver pathResolver;
    private final TableFileHandler tableFileHandler;
    private final ConditionEvaluator conditionEvaluator;

    public SelectManager(DbPathResolver pathResolver, TableFileHandler tableFileHandler) {
        this.pathResolver = pathResolver;
        this.tableFileHandler = tableFileHandler;
        this.conditionEvaluator = new ConditionEvaluator();
    }

    public List<String> selectRows(SelectQuery query, TableReference tableReference) throws IOException {
        return selectRows(query, tableReference, null);
    }

    public List<String> selectRows(SelectQuery query, TableReference tableReference, ConditionSpec condition) throws IOException {
        return selectRows(query, tableReference, condition, null);
    }

    public List<String> selectRows(SelectQuery query, TableReference tableReference, ConditionSpec condition, List<String> groupByColumns) throws IOException {
        // Si hay JOINs
        if (tableReference.hasJoins()) {
            return handleJoinQuery(query, tableReference, condition, groupByColumns);
        }

        File tableFile = requireTableFile(tableReference.getTableName());
        List<String> headerColumns = tableFileHandler.readHeaderColumns(tableFile);
        if (headerColumns.isEmpty()) {
            throw new IOException("La tabla no tiene columnas definidas para usar SELECT: " + tableReference.getTableName());
        }

        List<String> result = new ArrayList<String>();
        
        // Si hay agregaciones (con o sin GROUP BY)
        if (query.hasAggregations()) {
            return handleAggregationQuery(query, headerColumns, tableFile, condition, groupByColumns);
        }

        List<Integer> selectedIndexes = resolveColumnIndexes(query, headerColumns, tableReference);
        result.add(buildHeaderRow(headerColumns, selectedIndexes));

        List<String> lines = tableFileHandler.readAllLines(tableFile);
        int startIndex = 2;  // Línea 0: PRIMARY KEY metadata, Línea 1: headers, Línea 2+: datos
        
        for (int i = startIndex; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] rowValues = line.split(",", -1);
            if (!matchesCondition(rowValues, headerColumns, tableReference, condition)) {
                continue;
            }
            result.add(buildDataRow(rowValues, selectedIndexes));
        }

        return result;
    }

    private File requireTableFile(String tableName) throws IOException {
        File tableFile = pathResolver.getTableFile(tableName, "SELECT");
        if (!tableFile.exists() || !tableFile.isFile()) {
            throw new IOException("La tabla no existe: " + tableName);
        }
        return tableFile;
    }

    private List<Integer> resolveColumnIndexes(SelectQuery query, List<String> headerColumns, TableReference tableReference) throws IOException {
        List<Integer> selectedIndexes = new ArrayList<Integer>();

        if (query.isSelectAll()) {
            for (int i = 0; i < headerColumns.size(); i++) {
                selectedIndexes.add(i);
            }
            return selectedIndexes;
        }

        if (query.getFields().isEmpty()) {
            throw new IOException("Las funciones de agregacion aun no estan soportadas en la ejecucion.");
        }

        for (FieldReference field : query.getFields()) {
            validateQualifier(field, tableReference);

            int columnIndex = headerColumns.indexOf(field.getColumnName());
            if (columnIndex < 0) {
                throw new IOException("La columna no existe en la tabla " + tableReference.getTableName() + ": " + field.getColumnName());
            }

            selectedIndexes.add(columnIndex);
        }

        return selectedIndexes;
    }

    private boolean matchesCondition(String[] rowValues, List<String> headerColumns, TableReference tableReference, ConditionSpec condition) throws IOException {
        if (condition == null) {
            return true;
        }

        if (condition.usesFieldComparison()) {
            throw new IOException("SELECT solo soporta condiciones WHERE columna = valor.");
        }

        FieldReference leftField = condition.getLeftField();
        validateQualifier(leftField, tableReference);

        int columnIndex = headerColumns.indexOf(leftField.getColumnName());
        if (columnIndex < 0) {
            throw new IOException("La columna no existe en la tabla " + tableReference.getTableName() + ": " + leftField.getColumnName());
        }

        if (columnIndex >= rowValues.length) {
            return false;
        }

        return conditionEvaluator.matches(rowValues[columnIndex], condition);
    }

    private void validateQualifier(FieldReference field, TableReference tableReference) throws IOException {
        if (field.getQualifier() == null) {
            return;
        }

        String alias = tableReference.getAlias();
        String tableName = tableReference.getTableName();
        if (field.getQualifier().equals(alias) || field.getQualifier().equals(tableName)) {
            return;
        }

        throw new IOException("El campo " + field.getQualifier() + "." + field.getColumnName() + " no pertenece a la tabla seleccionada.");
    }

    private String buildHeaderRow(List<String> headerColumns, List<Integer> selectedIndexes) {
        List<String> selectedColumns = new ArrayList<String>();
        for (Integer index : selectedIndexes) {
            selectedColumns.add(headerColumns.get(index));
        }
        return String.join(",", selectedColumns);
    }

    private String buildDataRow(String[] rowValues, List<Integer> selectedIndexes) {
        List<String> selectedValues = new ArrayList<String>();
        for (Integer index : selectedIndexes) {
            if (index < rowValues.length) {
                selectedValues.add(rowValues[index]);
            } else {
                selectedValues.add("");
            }
        }
        return String.join(",", selectedValues);
    }

    private List<String> handleAggregationQuery(SelectQuery query, List<String> headerColumns, File tableFile, ConditionSpec condition, List<String> groupByColumns) throws IOException {
        List<String> result = new ArrayList<String>();
        
        // Construir header
        List<String> headerParts = new ArrayList<>();
        if (groupByColumns != null && !groupByColumns.isEmpty()) {
            headerParts.addAll(groupByColumns);
        }
        for (AggregationFunction agg : query.getAggregations()) {
            headerParts.add(agg.getDisplayName());
        }
        result.add(String.join(",", headerParts));
        
        // Leer datos
        List<String[]> dataRows = new ArrayList<>();
        List<String> lines = tableFileHandler.readAllLines(tableFile);
        int startIndex = 2;  // Línea 0: PRIMARY KEY metadata, Línea 1: headers, Línea 2+: datos
        
        for (int i = startIndex; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            
            String[] rowValues = line.split(",", -1);
            if (condition != null && !matchesConditionSimple(rowValues, headerColumns, condition)) {
                continue;
            }
            dataRows.add(rowValues);
        }
        
        // Procesar agregaciones
        try {
            List<AggregationProcessor.AggregationResult> aggregationResults = AggregationProcessor.processAggregations(dataRows, headerColumns, query.getAggregations(), groupByColumns);
            
            // Construir resultados
            for (AggregationProcessor.AggregationResult aggResult : aggregationResults) {
                List<String> rowParts = new ArrayList<>(aggResult.groupValues);
                for (AggregationFunction agg : query.getAggregations()) {
                    Double value = aggResult.aggregateValues.get(agg.getDisplayName());
                    if (value != null) {
                        if (agg.getType() == AggregationFunction.Type.AVG || agg.getType() == AggregationFunction.Type.SUM) {
                            rowParts.add(String.format("%.2f", value));
                        } else {
                            rowParts.add(String.valueOf(value.intValue()));
                        }
                    } else {
                        rowParts.add("0");
                    }
                }
                result.add(String.join(",", rowParts));
            }
        } catch (Exception e) {
            throw new IOException("Error al procesar agregaciones: " + e.getMessage());
        }
        
        return result;
    }

    private List<String> handleJoinQuery(SelectQuery query, TableReference mainTable, ConditionSpec condition, List<String> groupByColumns) throws IOException {
        List<String> result = new ArrayList<>();
        
        // Leer tabla principal
        File mainFile = requireTableFile(mainTable.getTableName());
        List<String> mainHeaders = tableFileHandler.readHeaderColumns(mainFile);
        String mainPK = tableFileHandler.getPrimaryKeyColumn(mainFile);
        int mainStartIndex = (mainPK != null && !mainPK.isEmpty() ? 2 : 1);
        List<String> mainLines = tableFileHandler.readAllLines(mainFile);
        String[] mainHeaderArray = mainHeaders.toArray(new String[0]);
        
        // Almacenar las filas combinadas
        List<JoinProcessor.JoinedRow> allJoinedRows = new ArrayList<>();
        
        try {
            // Procesar cada JOIN
            for (int j = 0; j < mainTable.getJoinedTables().size(); j++) {
                TableReference joinedTable = mainTable.getJoinedTables().get(j);
                JoinCondition joinCond = mainTable.getJoinConditions().get(j);
                
                File joinFile = requireTableFile(joinedTable.getTableName());
                List<String> joinHeaders = tableFileHandler.readHeaderColumns(joinFile);
                String joinPK = tableFileHandler.getPrimaryKeyColumn(joinFile);
                int joinStartIndex = (joinPK != null && !joinPK.isEmpty() ? 2 : 1);
                List<String> joinLines = tableFileHandler.readAllLines(joinFile);
                String[] joinHeaderArray = joinHeaders.toArray(new String[0]);
                String[] joinDataArray = joinLines.subList(joinStartIndex, joinLines.size()).toArray(new String[0]);
                
                if (j == 0) {
                    // Primer JOIN: usar tabla principal contra primera tabla a joinear
                    String[] mainDataArray = mainLines.subList(mainStartIndex, mainLines.size()).toArray(new String[0]);
                    
                    // Resolver qué columna corresponde a qué tabla según sus nombres/alias
                    String leftColName = resolveJoinColumn(mainTable, joinCond.getLeftTable(), joinCond.getLeftColumn());
                    String rightColName = resolveJoinColumn(joinedTable, joinCond.getRightTable(), joinCond.getRightColumn());
                    
                    // Si están al revés (tabla derecha en left y tabla izquierda en right), intercambiar
                    if (joinCond.getLeftTable() != null && joinCond.getLeftTable().equalsIgnoreCase(joinedTable.getAlias())) {
                        // Intercambiar: la condición está al revés
                        String temp = leftColName;
                        leftColName = rightColName;
                        rightColName = temp;
                        
                        String[] tempHeaders = mainHeaderArray;
                        mainHeaderArray = joinHeaderArray;
                        joinHeaderArray = tempHeaders;
                        
                        String[] tempData = mainDataArray;
                        mainDataArray = joinDataArray;
                        joinDataArray = tempData;
                    }
                    
                    // Crear nueva condición con columnas resueltas
                    JoinCondition resolvedCond = new JoinCondition(mainTable.getAlias(), leftColName, joinedTable.getAlias(), rightColName);
                    allJoinedRows = JoinProcessor.performInnerJoin(mainHeaderArray, mainDataArray, joinHeaderArray, joinDataArray, resolvedCond);
                } else {
                    // Múltiples JOINs: usar resultados anteriores (no implementado completamente)
                    // Para ahora, solo retornamos los resultados del primer JOIN
                    // Una implementación completa requeriría convertir JoinedRow de vuelta a headers/data
                }
            }
        } catch (Exception e) {
            throw new IOException("Error al realizar JOIN: " + e.getMessage());
        }
        
        if (allJoinedRows.isEmpty()) {
            return result;
        }
        
        // Construir headers combinados
        JoinProcessor.JoinedRow firstRow = allJoinedRows.get(0);
        result.add(String.join(",", firstRow.columnNames));
        
        // Aplicar WHERE si existe
        for (JoinProcessor.JoinedRow joinedRow : allJoinedRows) {
            if (condition != null) {
                if (!matchesJoinCondition(joinedRow.values, joinedRow.columnNames, condition)) {
                    continue;
                }
            }
            result.add(String.join(",", joinedRow.values));
        }
        
        return result;
    }

    private boolean matchesJoinCondition(String[] rowValues, List<String> headerColumns, ConditionSpec condition) throws IOException {
        if (condition == null) {
            return true;
        }

        FieldReference leftField = condition.getLeftField();
        int columnIndex = headerColumns.indexOf(leftField.getColumnName());
        if (columnIndex < 0) {
            return false;
        }

        if (columnIndex >= rowValues.length) {
            return false;
        }

        return conditionEvaluator.matches(rowValues[columnIndex], condition);
    }

    private String resolveJoinColumn(TableReference table, String tableAlias, String columnName) {
        // Simplemente retorna el nombre de columna sin considerar el alias
        // Porque el alias no afecta cómo se llama la columna en la tabla
        return columnName;
    }

    private boolean matchesConditionSimple(String[] rowValues, List<String> headerColumns, ConditionSpec condition) throws IOException {
        if (condition == null) {
            return true;
        }

        if (condition.usesFieldComparison()) {
            throw new IOException("SELECT solo soporta condiciones WHERE columna = valor.");
        }

        FieldReference leftField = condition.getLeftField();
        int columnIndex = headerColumns.indexOf(leftField.getColumnName());
        if (columnIndex < 0) {
            return false;
        }

        if (columnIndex >= rowValues.length) {
            return false;
        }

        return conditionEvaluator.matches(rowValues[columnIndex], condition);
    }
}


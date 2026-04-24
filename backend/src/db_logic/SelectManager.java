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
        if (tableReference.isJoinReference()) {
            throw new IOException("SELECT con JOIN aun no esta soportado en la ejecucion.");
        }

        File tableFile = requireTableFile(tableReference.getTableName());
        List<String> headerColumns = tableFileHandler.readHeaderColumns(tableFile);
        if (headerColumns.isEmpty()) {
            throw new IOException("La tabla no tiene columnas definidas para usar SELECT: " + tableReference.getTableName());
        }

        List<Integer> selectedIndexes = resolveColumnIndexes(query, headerColumns, tableReference);
        List<String> result = new ArrayList<String>();
        result.add(buildHeaderRow(headerColumns, selectedIndexes));

        List<String> lines = tableFileHandler.readAllLines(tableFile);
        for (int i = 1; i < lines.size(); i++) {
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
}

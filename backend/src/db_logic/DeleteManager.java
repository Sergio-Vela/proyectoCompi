package db_logic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeleteManager {
    private final DbPathResolver pathResolver;
    private final TableFileHandler tableFileHandler;
    private final ConditionEvaluator conditionEvaluator;

    public DeleteManager(DbPathResolver pathResolver, TableFileHandler tableFileHandler) {
        this.pathResolver = pathResolver;
        this.tableFileHandler = tableFileHandler;
        this.conditionEvaluator = new ConditionEvaluator();
    }

    public void deleteRows(String tableName, ConditionSpec condition) throws IOException {
        File tableFile = requireTableFile(tableName);
        List<String> allLines = tableFileHandler.readAllLines(tableFile);

        if (allLines.isEmpty()) {
            throw new IOException("La tabla esta vacia: " + tableName);
        }

        List<String> headerColumns = tableFileHandler.readHeaderColumns(tableFile);
        String primaryKeyColumn = tableFileHandler.getPrimaryKeyColumn(tableFile);
        
        if (headerColumns.isEmpty()) {
            throw new IOException("La tabla no tiene columnas definidas.");
        }

        List<String> linesToKeep = new ArrayList<>();
        linesToKeep.add(allLines.get(0));
        
        if (primaryKeyColumn != null && !primaryKeyColumn.isEmpty()) {
            linesToKeep.add(primaryKeyColumn);
        }

        int deletedCount = 0;
        int startIndex = (primaryKeyColumn != null && !primaryKeyColumn.isEmpty() ? 2 : 1);
        
        if (condition == null) {
            // DELETE sin WHERE - borra todo
            deletedCount = allLines.size() - startIndex;
        } else {
            // DELETE con WHERE - borra solo las que cumplan
            for (int i = startIndex; i < allLines.size(); i++) {
                String line = allLines.get(i);
                String[] rowValues = line.split(",", -1);
                
                if (matchesCondition(rowValues, headerColumns, condition)) {
                    deletedCount++;
                } else {
                    linesToKeep.add(line);
                }
            }
        }

        if (deletedCount == 0) {
            throw new IOException("Ningun registro coincide con la condicion WHERE.");
        }

        tableFileHandler.writeAllLines(tableFile, linesToKeep);
    }

    private boolean matchesCondition(String[] rowValues, List<String> headerColumns, ConditionSpec condition) throws IOException {
        if (condition == null) {
            return false;
        }

        if (condition.usesFieldComparison()) {
            throw new IOException("DELETE solo soporta condiciones WHERE columna = valor.");
        }

        FieldReference leftField = condition.getLeftField();
        int columnIndex = headerColumns.indexOf(leftField.getColumnName());
        if (columnIndex < 0) {
            throw new IOException("La columna no existe en la tabla: " + leftField.getColumnName());
        }

        if (columnIndex >= rowValues.length) {
            return false;
        }

        return conditionEvaluator.matches(rowValues[columnIndex], condition);
    }

    private File requireTableFile(String tableName) throws IOException {
        File tableFile = pathResolver.getTableFile(tableName, "DELETE");
        if (!tableFile.exists() || !tableFile.isFile()) {
            throw new IOException("La tabla no existe: " + tableName);
        }
        return tableFile;
    }
}

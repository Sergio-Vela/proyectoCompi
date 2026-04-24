package db_logic;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UpdateManager {
    private final DbPathResolver pathResolver;
    private final TableFileHandler tableFileHandler;
    private final ConditionEvaluator conditionEvaluator;

    public UpdateManager(DbPathResolver pathResolver, TableFileHandler tableFileHandler) {
        this.pathResolver = pathResolver;
        this.tableFileHandler = tableFileHandler;
        this.conditionEvaluator = new ConditionEvaluator();
    }

    public void updateRows(String tableName, List<ColumnAssignment> assignments, ConditionSpec condition) throws IOException {
        File tableFile = requireTableFile(tableName);
        List<String> lines = tableFileHandler.readAllLines(tableFile);
        List<String> headerColumns = tableFileHandler.readHeaderColumns(tableFile);

        if (headerColumns.isEmpty()) {
            throw new IOException("La tabla no tiene columnas definidas para usar UPDATE: " + tableName);
        }

        if (condition == null) {
            throw new IOException("UPDATE requiere una condicion WHERE para ejecutarse.");
        }

        if (condition.usesFieldComparison()) {
            throw new IOException("UPDATE solo soporta condiciones WHERE columna = valor.");
        }

        int whereIndex = requireColumnIndex(headerColumns, requireSimpleField(condition.getLeftField(), "WHERE"));

        for (ColumnAssignment assignment : assignments) {
            requireColumnIndex(headerColumns, assignment.getColumnName());
        }

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] rowValues = line.split(",", -1);
            if (rowValues.length != headerColumns.size()) {
                continue;
            }

            if (!conditionEvaluator.matches(rowValues[whereIndex], condition)) {
                continue;
            }

            for (ColumnAssignment assignment : assignments) {
                int assignmentIndex = requireColumnIndex(headerColumns, assignment.getColumnName());
                rowValues[assignmentIndex] = assignment.getValue();
            }

            lines.set(i, String.join(",", rowValues));
        }

        tableFileHandler.writeAllLines(tableFile, lines);
    }

    private File requireTableFile(String tableName) throws IOException {
        File tableFile = pathResolver.getTableFile(tableName, "UPDATE");
        if (!tableFile.exists() || !tableFile.isFile()) {
            throw new IOException("La tabla no existe: " + tableName);
        }
        return tableFile;
    }

    private int requireColumnIndex(List<String> headerColumns, String columnName) throws IOException {
        int columnIndex = headerColumns.indexOf(columnName);
        if (columnIndex < 0) {
            throw new IOException("La columna no existe en la tabla: " + columnName);
        }
        return columnIndex;
    }

    private String requireSimpleField(FieldReference field, String context) throws IOException {
        if (field == null || field.getQualifier() != null) {
            throw new IOException(context + " solo soporta columnas simples sin alias.");
        }
        return field.getColumnName();
    }
}

package db_logic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InsertManager {
    private final DbPathResolver pathResolver;
    private final TableFileHandler tableFileHandler;

    public InsertManager(DbPathResolver pathResolver, TableFileHandler tableFileHandler) {
        this.pathResolver = pathResolver;
        this.tableFileHandler = tableFileHandler;
    }

    public void insertValues(String tableName, List<String> values) throws IOException {
        File tableFile = requireTableFile(tableName);
        List<String> headerColumns = tableFileHandler.readHeaderColumns(tableFile);

        if (!headerColumns.isEmpty() && headerColumns.size() != values.size()) {
            throw new IOException("La cantidad de valores no coincide con las columnas de la tabla: " + tableName);
        }

        String primaryKeyColumn = tableFileHandler.getPrimaryKeyColumn(tableFile);
        validatePrimaryKeyUniqueness(tableFile, headerColumns, primaryKeyColumn, values);

        tableFileHandler.appendRow(tableFile, values);
    }

    public void insertValues(String tableName, List<String> insertColumns, List<String> values) throws IOException {
        File tableFile = requireTableFile(tableName);
        List<String> headerColumns = tableFileHandler.readHeaderColumns(tableFile);

        if (insertColumns.size() != values.size()) {
            throw new IOException("La cantidad de columnas no coincide con la cantidad de valores en INSERT.");
        }

        if (headerColumns.isEmpty()) {
            throw new IOException("La tabla no tiene columnas definidas para usar INSERT con lista de columnas.");
        }

        List<String> orderedValues = new ArrayList<String>();
        for (int i = 0; i < headerColumns.size(); i++) {
            orderedValues.add("");
        }

        for (int i = 0; i < insertColumns.size(); i++) {
            String columnName = insertColumns.get(i);
            int columnIndex = headerColumns.indexOf(columnName);

            if (columnIndex < 0) {
                throw new IOException("La columna no existe en la tabla " + tableName + ": " + columnName);
            }

            orderedValues.set(columnIndex, values.get(i));
        }

        String primaryKeyColumn = tableFileHandler.getPrimaryKeyColumn(tableFile);
        validatePrimaryKeyUniqueness(tableFile, headerColumns, primaryKeyColumn, orderedValues);

        tableFileHandler.appendRow(tableFile, orderedValues);
    }

    private void validatePrimaryKeyUniqueness(File tableFile, List<String> headerColumns, String primaryKeyColumn, List<String> newValues) throws IOException {
        if (primaryKeyColumn == null || primaryKeyColumn.trim().isEmpty()) {
            return;
        }

        primaryKeyColumn = primaryKeyColumn.trim();
        
        // Buscar el índice de la columna PRIMARY KEY (con trim en comparación)
        int primaryKeyIndex = -1;
        for (int i = 0; i < headerColumns.size(); i++) {
            if (headerColumns.get(i).trim().equalsIgnoreCase(primaryKeyColumn)) {
                primaryKeyIndex = i;
                break;
            }
        }
        
        // Si la columna no existe, simplemente no validamos duplicados
        if (primaryKeyIndex < 0) {
            return;
        }

        if (primaryKeyIndex >= newValues.size()) {
            return;
        }

        String newPrimaryKeyValue = newValues.get(primaryKeyIndex).trim();
        if (newPrimaryKeyValue.isEmpty()) {
            return;
        }

        List<String> allLines = tableFileHandler.readAllLines(tableFile);
        int startRow = 2;  // Línea 0: PRIMARY KEY metadata, Línea 1: headers, Línea 2+: datos

        for (int i = startRow; i < allLines.size(); i++) {
            String line = allLines.get(i);
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",", -1);
            if (primaryKeyIndex < parts.length) {
                String existingValue = parts[primaryKeyIndex].trim();
                if (existingValue.equals(newPrimaryKeyValue)) {
                    throw new IOException("Ya existe un registro con la misma PRIMARY KEY: " + newPrimaryKeyValue);
                }
            }
        }
    }

    private File requireTableFile(String tableName) throws IOException {
        File tableFile = pathResolver.getTableFile(tableName, "INSERT");
        if (!tableFile.exists() || !tableFile.isFile()) {
            throw new IOException("La tabla no existe: " + tableName);
        }
        return tableFile;
    }
}


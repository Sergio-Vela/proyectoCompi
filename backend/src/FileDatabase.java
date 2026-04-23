import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class FileDatabase {
    public static final String ROOT_FOLDER = ".." + File.separator + "data";
    public static final String DEFAULT_DB = "default";

    private final File root;
    private final File databaseDir;

    public FileDatabase() {
        this(new File(ROOT_FOLDER), DEFAULT_DB);
    }

    public FileDatabase(File root, String databaseName) {
        this.root = root;
        if (!this.root.exists()) {
            this.root.mkdirs();
        }
        this.databaseDir = new File(this.root, databaseName);
        if (!this.databaseDir.exists()) {
            this.databaseDir.mkdirs();
        }
    }

    public void createDatabase(String databaseName) throws IOException {
        File dbDir = new File(root, databaseName);
        if (dbDir.exists() && !dbDir.isDirectory()) {
            throw new IOException("Ya existe un archivo con el nombre de la base de datos");
        }
        if (!dbDir.exists() && !dbDir.mkdirs()) {
            throw new IOException("No se pudo crear la base de datos " + databaseName);
        }
    }

    public void createTable(String tableName, List<String> columns) throws IOException {
        File table = getTableFile(tableName);
        if (table.exists()) {
            throw new IOException("La tabla '" + tableName + "' ya existe");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(table.toPath(), StandardCharsets.UTF_8)) {
            writer.write(String.join(",", columns));
            writer.newLine();
        }
    }

    public void insert(String tableName, List<String> columnNames, List<String> values) throws IOException {
        File table = getTableFile(tableName);
        List<String> header = readHeader(table);

        if (columnNames == null || columnNames.isEmpty()) {
            if (values.size() != header.size()) {
                throw new IOException("La cantidad de valores no coincide con la cantidad de columnas");
            }
            appendRow(table, header, mapRow(header, values));
            return;
        }

        if (columnNames.size() != values.size()) {
            throw new IOException("La cantidad de columnas no coincide con la cantidad de valores");
        }

        Map<String, String> row = new LinkedHashMap<>();
        for (int i = 0; i < columnNames.size(); i++) {
            String column = normalizeColumnName(columnNames.get(i));
            if (!header.contains(column)) {
                throw new IOException("La columna '" + column + "' no existe en la tabla");
            }
            row.put(column, values.get(i));
        }

        appendRow(table, header, fillMissingColumns(header, row));
    }

    public void insertRows(String tableName, List<String> targetColumns, List<Map<String, String>> rows) throws IOException {
        if (rows == null || rows.isEmpty()) {
            return;
        }

        if (targetColumns == null || targetColumns.isEmpty()) {
            for (Map<String, String> row : rows) {
                List<String> values = new ArrayList<>();
                for (String column : readHeader(getTableFile(tableName))) {
                    values.add(row.getOrDefault(column, ""));
                }
                insert(tableName, null, values);
            }
            return;
        }

        for (Map<String, String> row : rows) {
            List<String> values = new ArrayList<>();
            for (String target : targetColumns) {
                values.add(row.getOrDefault(normalizeColumnName(target), ""));
            }
            insert(tableName, targetColumns, values);
        }
    }

    public List<Map<String, String>> select(List<String> selectedFields, String tableName, Condition condition) throws IOException {
        File table = getTableFile(tableName);
        List<String> header = readHeader(table);
        List<Map<String, String>> result = new ArrayList<>();

        List<String> requestedFields = new ArrayList<>();
        boolean selectAll = selectedFields.size() == 1 && "*".equals(selectedFields.get(0));
        boolean countStar = selectedFields.size() == 1 && "COUNT(*)".equalsIgnoreCase(selectedFields.get(0));

        if (selectAll || countStar) {
            requestedFields = header;
        } else {
            for (String field : selectedFields) {
                requestedFields.add(normalizeColumnName(field));
            }
            for (String field : requestedFields) {
                if (!header.contains(field)) {
                    throw new IOException("La columna '" + field + "' no existe en la tabla");
                }
            }
        }

        List<String> lines = Files.readAllLines(table.toPath(), StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) {
            String rowText = lines.get(i);
            if (rowText.trim().isEmpty()) {
                continue;
            }
            Map<String, String> row = parseRow(header, rowText);
            if (condition == null || condition.matches(row)) {
                if (countStar) {
                    Map<String, String> countRow = new LinkedHashMap<>();
                    countRow.put("COUNT(*)", "1");
                    result.add(countRow);
                } else if (selectAll) {
                    result.add(row);
                } else {
                    Map<String, String> selectedRow = new LinkedHashMap<>();
                    for (String field : requestedFields) {
                        selectedRow.put(field, row.getOrDefault(field, ""));
                    }
                    result.add(selectedRow);
                }
            }
        }

        return result;
    }

    public int selectCount(String tableName, Condition condition) throws IOException {
        File table = getTableFile(tableName);
        List<String> header = readHeader(table);
        int count = 0;

        List<String> lines = Files.readAllLines(table.toPath(), StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) {
            String rowText = lines.get(i);
            if (rowText.trim().isEmpty()) {
                continue;
            }
            Map<String, String> row = parseRow(header, rowText);
            if (condition == null || condition.matches(row)) {
                count++;
            }
        }

        return count;
    }

    public int update(String tableName, Map<String, String> assignments, Condition condition) throws IOException {
        File table = getTableFile(tableName);
        List<String> header = readHeader(table);
        List<String> lines = Files.readAllLines(table.toPath(), StandardCharsets.UTF_8);
        List<String> outputLines = new ArrayList<>();
        outputLines.add(String.join(",", header));
        int updatedCount = 0;

        for (int i = 1; i < lines.size(); i++) {
            String rowText = lines.get(i);
            if (rowText.trim().isEmpty()) {
                outputLines.add(rowText);
                continue;
            }
            Map<String, String> row = parseRow(header, rowText);
            if (condition == null || condition.matches(row)) {
                for (Map.Entry<String, String> entry : assignments.entrySet()) {
                    String column = normalizeColumnName(entry.getKey());
                    if (!header.contains(column)) {
                        throw new IOException("La columna '" + column + "' no existe en la tabla");
                    }
                    row.put(column, entry.getValue());
                }
                updatedCount++;
            }
            outputLines.add(String.join(",", rowValues(header, row)));
        }

        Files.write(table.toPath(), outputLines, StandardCharsets.UTF_8);
        return updatedCount;
    }

    private File getTableFile(String tableName) {
        return new File(databaseDir, tableName + ".txt");
    }

    private List<String> readHeader(File table) throws IOException {
        List<String> lines = Files.readAllLines(table.toPath(), StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            throw new IOException("La tabla no contiene cabecera");
        }
        return parseHeader(lines.get(0));
    }

    private List<String> parseHeader(String headerLine) {
        String[] parts = headerLine.split(",", -1);
        List<String> header = new ArrayList<>();
        for (String part : parts) {
            header.add(part.trim());
        }
        return header;
    }

    private Map<String, String> parseRow(List<String> header, String rowText) {
        String[] parts = rowText.split(",", -1);
        Map<String, String> row = new LinkedHashMap<>();
        for (int i = 0; i < header.size(); i++) {
            String value = i < parts.length ? parts[i].trim() : "";
            row.put(header.get(i), value);
        }
        return row;
    }

    private void appendRow(File table, List<String> header, Map<String, String> row) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(table.toPath(), StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.APPEND, java.nio.file.StandardOpenOption.CREATE)) {
            writer.write(String.join(",", rowValues(header, row)));
            writer.newLine();
        }
    }

    private Map<String, String> mapRow(List<String> header, List<String> values) {
        Map<String, String> row = new LinkedHashMap<>();
        for (int i = 0; i < header.size(); i++) {
            String value = i < values.size() ? values.get(i) : "";
            row.put(header.get(i), value);
        }
        return row;
    }

    private Map<String, String> fillMissingColumns(List<String> header, Map<String, String> row) {
        Map<String, String> filled = new LinkedHashMap<>();
        for (String column : header) {
            filled.put(column, row.getOrDefault(column, ""));
        }
        return filled;
    }

    private List<String> rowValues(List<String> header, Map<String, String> row) {
        List<String> values = new ArrayList<>();
        for (String column : header) {
            values.add(row.getOrDefault(column, ""));
        }
        return values;
    }

    public static String normalizeColumnName(String columnName) {
        int idx = columnName.lastIndexOf('.');
        return idx >= 0 ? columnName.substring(idx + 1) : columnName;
    }

    public static class Condition {
        public final String left;
        public final String right;
        public final boolean rightIsField;

        public Condition(String left, String right, boolean rightIsField) {
            this.left = normalizeColumnName(left);
            this.right = normalizeColumnName(right);
            this.rightIsField = rightIsField;
        }

        public boolean matches(Map<String, String> row) {
            String leftValue = row.get(left);
            String rightValue = rightIsField ? row.get(right) : right;
            if (leftValue == null || rightValue == null) {
                return false;
            }
            return leftValue.equals(rightValue);
        }
    }
}

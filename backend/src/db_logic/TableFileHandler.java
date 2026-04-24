package db_logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TableFileHandler {
    public void createTableFile(File tableFile, List<String> columns) throws IOException {
        createTableFile(tableFile, columns, null);
    }

    public void createTableFile(File tableFile, List<String> columns, String primaryKeyColumn) throws IOException {
        if (!tableFile.createNewFile()) {
            throw new IOException("No se pudo crear la tabla: " + removeExtension(tableFile.getName()));
        }

        if (columns != null && !columns.isEmpty()) {
            // Limpiar los marcadores __PK__ de los nombres de columnas
            List<String> cleanColumns = new ArrayList<String>();
            for (String col : columns) {
                cleanColumns.add(col.replace("__PK__", ""));
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tableFile))) {
                writer.write(String.join(",", cleanColumns));
                writer.newLine();
                if (primaryKeyColumn != null && !primaryKeyColumn.isEmpty()) {
                    writer.write(primaryKeyColumn);
                    writer.newLine();
                }
            }
        }
    }

    public List<String> readHeaderColumns(File tableFile) throws IOException {
        List<String> lines = readAllLines(tableFile);
        if (lines.isEmpty()) {
            return new ArrayList<String>();
        }

        String firstLine = lines.get(0).trim();
        if (firstLine.isEmpty()) {
            return new ArrayList<String>();
        }

        String[] parts = firstLine.split(",");
        List<String> columns = new ArrayList<String>();
        for (String part : parts) {
            columns.add(part.trim());
        }
        return columns;
    }

    public String getPrimaryKeyColumn(File tableFile) throws IOException {
        List<String> lines = readAllLines(tableFile);
        if (lines.size() < 2) {
            return null;
        }

        String secondLine = lines.get(1).trim();
        if (secondLine.isEmpty()) {
            return null;
        }

        return secondLine;
    }

    public void appendRow(File tableFile, List<String> values) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tableFile, true))) {
            writer.write(String.join(",", values));
            writer.newLine();
        }
    }

    public List<String> readAllLines(File tableFile) throws IOException {
        return Files.readAllLines(tableFile.toPath());
    }

    public void writeAllLines(File tableFile, List<String> lines) throws IOException {
        Files.write(Path.of(tableFile.toURI()), lines);
    }

    private String removeExtension(String fileName) {
        int extensionIndex = fileName.lastIndexOf('.');
        if (extensionIndex < 0) {
            return fileName;
        }
        return fileName.substring(0, extensionIndex);
    }
}

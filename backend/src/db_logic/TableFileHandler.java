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
                // Línea 0: Nombre de la PRIMARY KEY (vacío si no tiene)
                if (primaryKeyColumn != null && !primaryKeyColumn.isEmpty()) {
                    writer.write(primaryKeyColumn);
                } else {
                    writer.write("");
                }
                writer.newLine();
                // Línea 1: Headers
                writer.write(String.join(",", cleanColumns));
                writer.newLine();
            }
        }
    }

    public List<String> readHeaderColumns(File tableFile) throws IOException {
        List<String> lines = readAllLines(tableFile);
        if (lines.size() < 2) {
            return new ArrayList<String>();
        }

        String headerLine = lines.get(1).trim();
        if (headerLine.isEmpty()) {
            return new ArrayList<String>();
        }

        String[] parts = headerLine.split(",");
        List<String> columns = new ArrayList<String>();
        for (String part : parts) {
            columns.add(part.trim());
        }
        return columns;
    }

    public String getPrimaryKeyColumn(File tableFile) throws IOException {
        List<String> lines = readAllLines(tableFile);
        if (lines.isEmpty()) {
            return null;
        }

        String firstLine = lines.get(0).trim();
        if (firstLine.isEmpty()) {
            return null;
        }

        return firstLine;
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

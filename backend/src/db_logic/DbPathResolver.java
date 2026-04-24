package db_logic;

import java.io.File;
import java.io.IOException;

public class DbPathResolver {
    private final File rootDirectory;

    public DbPathResolver() {
        this.rootDirectory = new File("..", "db");
    }

    public void ensureRootDirectory() throws IOException {
        if (rootDirectory.exists()) {
            if (!rootDirectory.isDirectory()) {
                throw new IOException("La ruta de bases de datos no es un directorio: " + rootDirectory.getPath());
            }
            return;
        }

        if (!rootDirectory.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta raiz de bases de datos.");
        }
    }

    public void validateIdentifier(String identifier, String entityName) throws IOException {
        if (identifier == null || !identifier.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            throw new IOException("Nombre de " + entityName + " invalido: " + identifier);
        }
    }

    public File getDatabaseDirectory(String databaseName) {
        return new File(rootDirectory, databaseName);
    }

    public File getActiveDatabaseDirectory(String operationName) throws IOException {
        ensureRootDirectory();

        String activeDatabase = DatabaseContext.getActiveDatabase();
        if (activeDatabase == null || activeDatabase.isEmpty()) {
            throw new IOException("No hay una base de datos activa. Ejecuta USE nombre_base antes de " + operationName + ".");
        }

        File databaseDirectory = getDatabaseDirectory(activeDatabase);
        if (!databaseDirectory.exists() || !databaseDirectory.isDirectory()) {
            throw new IOException("La base de datos activa no existe en disco: " + activeDatabase);
        }

        return databaseDirectory;
    }

    public File getTableFile(String tableName, String operationName) throws IOException {
        validateIdentifier(tableName, "tabla");
        File databaseDirectory = getActiveDatabaseDirectory(operationName);
        return new File(databaseDirectory, tableName + ".txt");
    }
}

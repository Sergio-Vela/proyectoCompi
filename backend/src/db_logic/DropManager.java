package db_logic;

import java.io.File;
import java.io.IOException;

public class DropManager {
    private final DbPathResolver pathResolver;

    public DropManager(DbPathResolver pathResolver) {
        this.pathResolver = pathResolver;
    }

    public void dropDatabase(String databaseName) throws IOException {
        pathResolver.validateIdentifier(databaseName, "base de datos");
        pathResolver.ensureRootDirectory();

        File databaseDirectory = pathResolver.getDatabaseDirectory(databaseName);

        if (!databaseDirectory.exists()) {
            throw new IOException("La base de datos no existe: " + databaseName);
        }

        if (!databaseDirectory.isDirectory()) {
            throw new IOException("La ruta existe pero no es un directorio: " + databaseName);
        }

        if (!deleteRecursively(databaseDirectory)) {
            throw new IOException("No se pudo eliminar la base de datos: " + databaseName);
        }
        
        if (databaseName.equals(DatabaseContext.getActiveDatabase())) {
            DatabaseContext.setActiveDatabase("");
        }
    }

    public void dropTable(String tableName) throws IOException {
        File tableFile = pathResolver.getTableFile(tableName, "DROP TABLE");

        if (!tableFile.exists()) {
            throw new IOException("La tabla no existe: " + tableName);
        }

        if (!tableFile.isFile()) {
            throw new IOException("La ruta existe pero no es un archivo: " + tableName);
        }

        if (!tableFile.delete()) {
            throw new IOException("No se pudo eliminar la tabla: " + tableName);
        }
    }

    private boolean deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    if (!deleteRecursively(child)) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }
}

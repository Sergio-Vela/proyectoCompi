package db_logic;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileDatabaseManager {
    private final DbPathResolver pathResolver;
    private final TableFileHandler tableFileHandler;
    private final InsertManager insertManager;
    private final UpdateManager updateManager;
    private final SelectManager selectManager;
    private final DeleteManager deleteManager;
    private final DropManager dropManager;

    public FileDatabaseManager() {
        this.pathResolver = new DbPathResolver();
        this.tableFileHandler = new TableFileHandler();
        this.insertManager = new InsertManager(pathResolver, tableFileHandler);
        this.updateManager = new UpdateManager(pathResolver, tableFileHandler);
        this.selectManager = new SelectManager(pathResolver, tableFileHandler);
        this.deleteManager = new DeleteManager(pathResolver, tableFileHandler);
        this.dropManager = new DropManager(pathResolver);
    }

    public void createDatabase(String databaseName) throws IOException {
        pathResolver.validateIdentifier(databaseName, "base de datos");
        pathResolver.ensureRootDirectory();

        File databaseDirectory = pathResolver.getDatabaseDirectory(databaseName);

        if (databaseDirectory.exists()) {
            if (!databaseDirectory.isDirectory()) {
                throw new IOException("Ya existe un archivo con el nombre de la base: " + databaseName);
            }
            return;
        }

        if (!databaseDirectory.mkdirs()) {
            throw new IOException("No se pudo crear la base de datos: " + databaseName);
        }
    }

    public void useDatabase(String databaseName) throws IOException {
        pathResolver.validateIdentifier(databaseName, "base de datos");
        pathResolver.ensureRootDirectory();

        File databaseDirectory = pathResolver.getDatabaseDirectory(databaseName);
        if (!databaseDirectory.exists() || !databaseDirectory.isDirectory()) {
            throw new IOException("La base de datos no existe: " + databaseName);
        }

        DatabaseContext.setActiveDatabase(databaseName);
    }

    public void createTable(String tableName, List<String> columns) throws IOException {
        createTable(tableName, columns, null);
    }

    public void createTable(String tableName, List<String> columns, String primaryKeyColumn) throws IOException {
        File tableFile = pathResolver.getTableFile(tableName, "CREATE TABLE");
        if (tableFile.exists()) {
            throw new IOException("La tabla ya existe: " + tableName);
        }

        if (primaryKeyColumn != null && !primaryKeyColumn.isEmpty()) {
            if (columns == null || !columns.contains(primaryKeyColumn)) {
                throw new IOException("La columna PRIMARY KEY '" + primaryKeyColumn + "' no existe en las columnas de la tabla.");
            }
        }

        tableFileHandler.createTableFile(tableFile, columns, primaryKeyColumn);
    }

    public void insertIntoTable(String tableName, List<String> values) throws IOException {
        insertManager.insertValues(tableName, values);
    }

    public void insertIntoTable(String tableName, List<String> columns, List<String> values) throws IOException {
        insertManager.insertValues(tableName, columns, values);
    }

    public void updateTable(String tableName, List<ColumnAssignment> assignments, ConditionSpec condition) throws IOException {
        updateManager.updateRows(tableName, assignments, condition);
    }

    public List<String> selectFromTable(SelectQuery query, TableReference tableReference) throws IOException {
        return selectManager.selectRows(query, tableReference);
    }

    public List<String> selectFromTable(SelectQuery query, TableReference tableReference, ConditionSpec condition) throws IOException {
        return selectManager.selectRows(query, tableReference, condition);
    }

    public List<String> selectFromTable(SelectQuery query, TableReference tableReference, ConditionSpec condition, List<String> groupByColumns) throws IOException {
        return selectManager.selectRows(query, tableReference, condition, groupByColumns);
    }

    public void deleteFromTable(String tableName, ConditionSpec condition) throws IOException {
        deleteManager.deleteRows(tableName, condition);
    }

    public void dropDatabase(String databaseName) throws IOException {
        dropManager.dropDatabase(databaseName);
    }

    public void dropTable(String tableName) throws IOException {
        dropManager.dropTable(tableName);
    }
}


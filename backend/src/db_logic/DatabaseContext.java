package db_logic;

public final class DatabaseContext {
    private static String activeDatabase;

    private DatabaseContext() {
    }

    public static String getActiveDatabase() {
        return activeDatabase;
    }

    public static void setActiveDatabase(String databaseName) {
        activeDatabase = databaseName;
    }
}

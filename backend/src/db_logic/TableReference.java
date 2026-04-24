package db_logic;

public class TableReference {
    private final String tableName;
    private final String alias;
    private final boolean joinReference;

    public TableReference(String tableName, String alias, boolean joinReference) {
        this.tableName = tableName;
        this.alias = alias;
        this.joinReference = joinReference;
    }

    public String getTableName() {
        return tableName;
    }

    public String getAlias() {
        return alias;
    }

    public boolean isJoinReference() {
        return joinReference;
    }
}

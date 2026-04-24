package db_logic;

public class FieldReference {
    private final String qualifier;
    private final String columnName;

    public FieldReference(String qualifier, String columnName) {
        this.qualifier = qualifier;
        this.columnName = columnName;
    }

    public String getQualifier() {
        return qualifier;
    }

    public String getColumnName() {
        return columnName;
    }
}

package db_logic;

public class ColumnAssignment {
    private final String columnName;
    private final String value;

    public ColumnAssignment(String columnName, String value) {
        this.columnName = columnName;
        this.value = value;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getValue() {
        return value;
    }
}

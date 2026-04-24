package db_logic;

public class AggregationFunction {
    public enum Type {
        SUM, AVG, COUNT
    }
    
    private Type type;
    private String columnName;
    
    public AggregationFunction(Type type, String columnName) {
        this.type = type;
        this.columnName = columnName;
    }
    
    public Type getType() {
        return type;
    }
    
    public String getColumnName() {
        return columnName;
    }
    
    public String getDisplayName() {
        switch (type) {
            case SUM:
                return "suma";
            case AVG:
                return "promedio";
            case COUNT:
                return "conteo";
            default:
                return "desconocido";
        }
    }
}

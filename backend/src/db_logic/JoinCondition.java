package db_logic;

public class JoinCondition {
    private String leftTable;
    private String leftColumn;
    private String rightTable;
    private String rightColumn;
    
    public JoinCondition(String leftTable, String leftColumn, String rightTable, String rightColumn) {
        this.leftTable = leftTable;
        this.leftColumn = leftColumn;
        this.rightTable = rightTable;
        this.rightColumn = rightColumn;
    }
    
    public String getLeftTable() {
        return leftTable;
    }
    
    public String getLeftColumn() {
        return leftColumn;
    }
    
    public String getRightTable() {
        return rightTable;
    }
    
    public String getRightColumn() {
        return rightColumn;
    }
}

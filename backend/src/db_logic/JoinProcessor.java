package db_logic;

import java.util.ArrayList;
import java.util.List;


public class JoinProcessor {
    
    public static class JoinedRow {
        public String[] values;
        public List<String> columnNames;
        
        public JoinedRow(String[] values, List<String> columnNames) {
            this.values = values;
            this.columnNames = columnNames;
        }
    }
    
    public static List<JoinedRow> performInnerJoin(
            String[] leftHeaders, String[] leftData,
            String[] rightHeaders, String[] rightData,
            JoinCondition joinCondition) throws Exception {
        
        List<JoinedRow> result = new ArrayList<>();
        
        // Encontrar índices de las columnas de JOIN
        int leftJoinIndex = -1;
        int rightJoinIndex = -1;
        
        for (int i = 0; i < leftHeaders.length; i++) {
            if (leftHeaders[i].trim().equals(joinCondition.getLeftColumn())) {
                leftJoinIndex = i;
                break;
            }
        }
        
        for (int i = 0; i < rightHeaders.length; i++) {
            if (rightHeaders[i].trim().equals(joinCondition.getRightColumn())) {
                rightJoinIndex = i;
                break;
            }
        }
        
        if (leftJoinIndex < 0 || rightJoinIndex < 0) {
            throw new Exception("Columnas de JOIN no encontradas");
        }
        
        // Realizar INNER JOIN
        for (int i = 0; i < leftData.length; i++) {
            String[] leftRow = leftData[i].split(",", -1);
            if (leftRow.length <= leftJoinIndex) continue;
            
            String leftValue = leftRow[leftJoinIndex].trim();
            
            for (int j = 0; j < rightData.length; j++) {
                String[] rightRow = rightData[j].split(",", -1);
                if (rightRow.length <= rightJoinIndex) continue;
                
                String rightValue = rightRow[rightJoinIndex].trim();
                
                if (leftValue.equals(rightValue)) {
                    // Combinar filas
                    String[] combined = new String[leftRow.length + rightRow.length];
                    System.arraycopy(leftRow, 0, combined, 0, leftRow.length);
                    System.arraycopy(rightRow, 0, combined, leftRow.length, rightRow.length);
                    
                    List<String> combinedHeaders = new ArrayList<>();
                    for (String h : leftHeaders) {
                        combinedHeaders.add(h.trim());
                    }
                    for (String h : rightHeaders) {
                        combinedHeaders.add(h.trim());
                    }
                    
                    result.add(new JoinedRow(combined, combinedHeaders));
                }
            }
        }
        
        return result;
    }
}

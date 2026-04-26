package db_logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregationProcessor {
    
    public static class AggregationResult {
        public List<String> groupValues;
        public Map<String, Double> aggregateValues;
        
        public AggregationResult(List<String> groupValues) {
            this.groupValues = groupValues;
            this.aggregateValues = new HashMap<>();
        }
    }
    
    public static List<AggregationResult> processAggregations(
            List<String[]> dataRows,
            List<String> headerColumns,
            List<AggregationFunction> aggregations,
            List<String> groupByColumns) throws Exception {
        
        Map<String, AggregationResult> groups = new HashMap<>();
        
        // Si no hay GROUP BY, usar una sola clave
        boolean hasGroupBy = groupByColumns != null && !groupByColumns.isEmpty();
        
        for (String[] row : dataRows) {
            String groupKey = hasGroupBy 
                ? buildGroupKey(row, headerColumns, groupByColumns)
                : "global";  // Clave única para agregar todo
            
            if (!groups.containsKey(groupKey)) {
                List<String> groupValues = hasGroupBy
                    ? extractGroupValues(row, headerColumns, groupByColumns)
                    : new ArrayList<>();  // Vacío si no hay GROUP BY
                groups.put(groupKey, new AggregationResult(groupValues));
            }
            
            AggregationResult result = groups.get(groupKey);
            
            for (AggregationFunction agg : aggregations) {
                String columnName = agg.getColumnName();
                int columnIndex = headerColumns.indexOf(columnName);
                
                if (columnIndex >= 0 && columnIndex < row.length) {
                    String value = row[columnIndex].trim();
                    String key = agg.getDisplayName();
                    
                    if (!result.aggregateValues.containsKey(key)) {
                        result.aggregateValues.put(key, 0.0);
                    }
                    
                    switch (agg.getType()) {
                        case SUM:
                            try {
                                double num = Double.parseDouble(value.isEmpty() ? "0" : value);
                                result.aggregateValues.put(key, result.aggregateValues.get(key) + num);
                            } catch (NumberFormatException e) {
                                // Ignorar valores no numéricos
                            }
                            break;
                        case AVG:
                            try {
                                double num = Double.parseDouble(value.isEmpty() ? "0" : value);
                                result.aggregateValues.put(key, result.aggregateValues.get(key) + num);
                            } catch (NumberFormatException e) {
                                // Ignorar valores no numéricos
                            }
                            break;
                        case COUNT:
                            result.aggregateValues.put(key, result.aggregateValues.get(key) + 1);
                            break;
                    }
                } else if (agg.getColumnName().equals("*") && agg.getType() == AggregationFunction.Type.COUNT) {
                    String key = agg.getDisplayName();
                    if (!result.aggregateValues.containsKey(key)) {
                        result.aggregateValues.put(key, 0.0);
                    }
                    result.aggregateValues.put(key, result.aggregateValues.get(key) + 1);
                }
            }
        }
        
        // Calcular promedios
        for (AggregationResult result : groups.values()) {
            for (AggregationFunction agg : aggregations) {
                if (agg.getType() == AggregationFunction.Type.AVG) {
                    String key = agg.getDisplayName();
                    int count = 0;
                    for (String[] row : dataRows) {
                        String groupKey = hasGroupBy
                            ? buildGroupKey(row, headerColumns, groupByColumns)
                            : "global";
                        if (buildGroupKey(result.groupValues, groupByColumns).equals(groupKey)) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        result.aggregateValues.put(key, result.aggregateValues.get(key) / count);
                    }
                }
            }
        }
        
        return new ArrayList<>(groups.values());
    }
    
    private static String buildGroupKey(String[] row, List<String> headerColumns, List<String> groupByColumns) throws Exception {
        StringBuilder key = new StringBuilder();
        for (String col : groupByColumns) {
            int idx = headerColumns.indexOf(col);
            if (idx >= 0 && idx < row.length) {
                key.append(row[idx]).append("|");
            }
        }
        return key.toString();
    }
    
    private static String buildGroupKey(List<String> groupValues, List<String> groupByColumns) {
        if (groupByColumns == null || groupByColumns.isEmpty()) {
            return "global";
        }
        StringBuilder key = new StringBuilder();
        for (String value : groupValues) {
            key.append(value).append("|");
        }
        return key.toString();
    }
    
    private static List<String> extractGroupValues(String[] row, List<String> headerColumns, List<String> groupByColumns) {
        List<String> values = new ArrayList<>();
        for (String col : groupByColumns) {
            int idx = headerColumns.indexOf(col);
            if (idx >= 0 && idx < row.length) {
                values.add(row[idx].trim());
            }
        }
        return values;
    }
}

package db_logic;

import java.util.List;

public class SelectQuery {
    private final boolean selectAll;
    private final List<FieldReference> fields;
    private final List<AggregationFunction> aggregations;
    private final List<String> groupByColumns;

    public SelectQuery(boolean selectAll, List<FieldReference> fields) {
        this(selectAll, fields, null, null);
    }

    public SelectQuery(boolean selectAll, List<FieldReference> fields, List<AggregationFunction> aggregations, List<String> groupByColumns) {
        this.selectAll = selectAll;
        this.fields = fields;
        this.aggregations = aggregations;
        this.groupByColumns = groupByColumns;
    }

    public boolean isSelectAll() {
        return selectAll;
    }

    public List<FieldReference> getFields() {
        return fields;
    }

    public List<AggregationFunction> getAggregations() {
        return aggregations;
    }

    public List<String> getGroupByColumns() {
        return groupByColumns;
    }

    public boolean hasAggregations() {
        return aggregations != null && !aggregations.isEmpty();
    }

    public boolean hasGroupBy() {
        return groupByColumns != null && !groupByColumns.isEmpty();
    }
}


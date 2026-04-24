package db_logic;

import java.util.List;

public class SelectQuery {
    private final boolean selectAll;
    private final List<FieldReference> fields;

    public SelectQuery(boolean selectAll, List<FieldReference> fields) {
        this.selectAll = selectAll;
        this.fields = fields;
    }

    public boolean isSelectAll() {
        return selectAll;
    }

    public List<FieldReference> getFields() {
        return fields;
    }
}

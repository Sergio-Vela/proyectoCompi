package db_logic;

import java.util.ArrayList;
import java.util.List;

public class TableReference {
    private final String tableName;
    private final String alias;
    private final boolean joinReference;
    private final List<TableReference> joinedTables;
    private final List<JoinCondition> joinConditions;

    public TableReference(String tableName, String alias, boolean joinReference) {
        this.tableName = tableName;
        this.alias = alias;
        this.joinReference = joinReference;
        this.joinedTables = new ArrayList<>();
        this.joinConditions = new ArrayList<>();
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

    public List<TableReference> getJoinedTables() {
        return joinedTables;
    }

    public List<JoinCondition> getJoinConditions() {
        return joinConditions;
    }

    public void addJoin(TableReference table, JoinCondition condition) {
        joinedTables.add(table);
        joinConditions.add(condition);
    }

    public boolean hasJoins() {
        return !joinedTables.isEmpty();
    }
}


package db_logic;

public class ConditionSpec {
    private final ComparisonOperator operator;
    private final FieldReference leftField;
    private final FieldReference rightField;
    private final String rightValue;

    public ConditionSpec(ComparisonOperator operator, FieldReference leftField, FieldReference rightField, String rightValue) {
        this.operator = operator;
        this.leftField = leftField;
        this.rightField = rightField;
        this.rightValue = rightValue;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    public FieldReference getLeftField() {
        return leftField;
    }

    public FieldReference getRightField() {
        return rightField;
    }

    public String getRightValue() {
        return rightValue;
    }

    public boolean usesFieldComparison() {
        return rightField != null;
    }
}

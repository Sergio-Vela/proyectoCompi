package db_logic;

import java.io.IOException;

public class ConditionEvaluator {
    public boolean matches(String leftValue, ConditionSpec condition) throws IOException {
        String rightValue = condition.getRightValue();

        if (rightValue == null) {
            throw new IOException("La condicion requiere un valor a la derecha para evaluarse.");
        }

        switch (condition.getOperator()) {
            case EQUALS:
                return leftValue.equals(rightValue);
            case GREATER_THAN:
                return compareValues(leftValue, rightValue) > 0;
            case LESS_THAN:
                return compareValues(leftValue, rightValue) < 0;
            case GREATER_THAN_OR_EQUALS:
                return compareValues(leftValue, rightValue) >= 0;
            case LESS_THAN_OR_EQUALS:
                return compareValues(leftValue, rightValue) <= 0;
            case NOT_EQUALS:
                return compareValues(leftValue, rightValue) != 0;
            default:
                throw new IOException("Operador de comparacion no soportado.");
        }
    }

    private int compareValues(String leftValue, String rightValue) {
        Double leftNumber = parseNumber(leftValue);
        Double rightNumber = parseNumber(rightValue);

        if (leftNumber != null && rightNumber != null) {
            return Double.compare(leftNumber, rightNumber);
        }

        return leftValue.compareTo(rightValue);
    }

    private Double parseNumber(String value) {
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

package org.DB.DAO.ManagerConnections;

public enum ConditionsDBOperators {
    EQUALS("="),
    NOT_EQUALS("!="),
    GREATER_THAN(">"),
    GREATER_OR_EQUALS(">="),
    LESS_THAN("<"),
    LESS_OR_EQUALS("<=");
    /*LIKE("LIKE"),
    IN("IN"),
    NOT_IN("NOT IN"),
    BETWEEN("BETWEEN"),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL");*/

    private final String symbol;

    ConditionsDBOperators(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}

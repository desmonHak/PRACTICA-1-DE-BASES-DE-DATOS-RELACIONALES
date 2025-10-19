package org.DB.Exceptions.ExceptionsDB;

import java.sql.SQLException;

public class SQLUnknownException extends RuntimeException {
    public SQLUnknownException(SQLException exception) {
        super("Error code SQL: %s ".formatted(exception.getSQLState()) + exception.getMessage());
    }
}

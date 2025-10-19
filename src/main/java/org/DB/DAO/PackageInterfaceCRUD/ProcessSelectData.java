package org.DB.DAO.PackageInterfaceCRUD;

import java.sql.SQLException;

@FunctionalInterface
public interface ProcessSelectData<T> {
    Object invokeProcessSelectData(T data) throws SQLException;
}

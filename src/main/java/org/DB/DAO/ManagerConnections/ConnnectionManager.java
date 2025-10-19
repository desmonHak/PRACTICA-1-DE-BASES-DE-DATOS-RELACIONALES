package org.DB.DAO.ManagerConnections;

import org.DB.DAO.PackageInterfaceCRUD.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public abstract class ConnnectionManager<T, TypeDataSelect> {

    public abstract T getNewConection() throws SQLException, IOException;
    public abstract void closeConection() throws SQLException;
    public abstract T connect();
    public abstract void update(
            UpdateDB<T> update_process,
            String table,
            HashMap<String, Object> data,
            List<ConditionsDB> condiciones
    ) throws SQLException, IOException;

    public abstract void insert(
            InsertDB<T> update_process,
            String Table,
            HashMap<String, Object> data
    ) throws SQLException, IOException;

    public abstract Object select(
            SelectDB<T, TypeDataSelect> update_process,
            List<String> select_fields,
            String table,
            List<ConditionsDB> condiciones,
            ProcessSelectData<TypeDataSelect> process
    ) throws SQLException, IOException;

    public abstract void delete(
            DeleteDB<T> update_process,
            String table,
            List<ConditionsDB> condiciones
    ) throws SQLException, IOException;

}

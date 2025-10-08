package org.practica1debasesdedatosrelacionales.DAO.ManagerConnections;

import org.practica1debasesdedatosrelacionales.DAO.PackageInterfaceCRUD.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public interface ConnnectionManager<T, TypeDataSelect> {

    T getNewConection() throws SQLException, IOException;
    void closeConection() throws SQLException;
    T connect();
    void update(
            UpdateDB<T> update_process,
            String table,
            HashMap<String, Object> data,
            List<ConditionsDB> condiciones
    ) throws SQLException;

    void insert(
            InsertDB<T> update_process,
            String Table,
            HashMap<String, Object> data
    ) throws SQLException;

    Object select(
            SelectDB<T, TypeDataSelect> update_process,
            List<String>select_fields,
            String table,
            List<ConditionsDB> condiciones,
            ProcessSelectData<TypeDataSelect> process
    ) throws SQLException;

    void delete(
            DeleteDB<T> update_process,
            String table,
            List<ConditionsDB> condiciones
    ) throws SQLException;

}

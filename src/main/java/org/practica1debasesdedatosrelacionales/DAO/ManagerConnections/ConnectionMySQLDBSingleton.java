package org.practica1debasesdedatosrelacionales.DAO.ManagerConnections;

import org.practica1debasesdedatosrelacionales.DAO.PackageInterfaceCRUD.*;
import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.SingletonException;
import org.practica1debasesdedatosrelacionales.util.R;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static org.practica1debasesdedatosrelacionales.DAO.ManagerConnections.ConditionsDB.check_type_data;

public class ConnectionMySQLDBSingleton implements ConnnectionManager<Connection, ResultSet> {
    private static Connection instance = null;
    public static boolean modo_debug = false;

    private static void print_debug(Object data) {
        if (modo_debug) {
            System.out.println(data);
        }
    }

    /**
     * Convierte una lista de condiciones en su representacion de cadenas
     * @param conditions condiciones a formatear
     * @return devolvemos un string que representa las condiciones unidas con AND's
     */
    private static String build_data_conditions(List<ConditionsDB> conditions) {
        // Añadir las condiciones
        StringBuilder whereClause = new StringBuilder();
        int index = 0;
        for (ConditionsDB condition : conditions) {
            whereClause.append(condition.toString());
            if (index < conditions.size() - 1) {
                whereClause.append(" AND ");
            }
            index++;
        }

        return whereClause.toString();
    }


    public UpdateDB<Connection> update_element = (connection, table,
                                                  data, conditions) ->{
        // construir la parte de la sentencia con los datos a modificar:
        StringBuilder fields = new StringBuilder();
        // almacena los campos a modificar:
        Set<String> fields_key = data.keySet();
        int index_field = 0; // counter de campo
        for (String field : fields_key) {
            // campo = valor
            fields.append(field);
            fields.append(" = ");


            fields.append(
                    check_type_data( // comrpobar si es valido para SQL
                            data.get(field))); // obtener el dato con el que actualizar

            if (index_field < fields_key.size() - 1) {
                fields.append(", ");
            }
            index_field++;
        }

        String sql = "UPDATE %s SET %s WHERE %s".formatted(table, fields.toString(),
                build_data_conditions(conditions));
        print_debug(sql);
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.executeUpdate();
    };

    public InsertDB<Connection> insert_element = (connection, table, objects_insert) -> {
        // Convierte objects_insert (HashMap<String, Object>) en campos y valores
        HashMap<String, Object> data = (HashMap<String, Object>) objects_insert;
        StringBuilder campos = new StringBuilder();
        StringBuilder valores = new StringBuilder();
        Set<String> claves = data.keySet();
        int index = 0;

        for (String key : claves) {
            campos.append(key);
            // agregamos el valor comprobando antes si es valido
            valores.append(check_type_data(data.get(key)));
            if (index < claves.size() - 1) {
                campos.append(", ");
                valores.append(", ");
            }
            index++;
        }

        String sql = "INSERT INTO %s (%s) VALUES (%s);".formatted(
                table, campos.toString(), valores.toString()
        );
        print_debug(sql);
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.executeUpdate();
    };

    public SelectDB<Connection, ResultSet> select_element = (
            connection, select_fields,
            table, condiciones, process_select_data) -> {
        // Construir la cadena de campos a seleccionar
        String campos = String.join(", ", (List<String>) select_fields);

        // si no hay condiciones se indicara where True, que siempre sera verdadero y no aplicara filtro
        String whereClause = "True";
        // si no hay condiciones
        if (condiciones != null) {
            // Construir la parte WHERE basada en las condiciones
            whereClause = build_data_conditions(condiciones);
        }

        String sql = "SELECT %s FROM %s WHERE %s;".formatted(campos, table, whereClause);
        print_debug(sql);
        PreparedStatement ps = connection.prepareStatement(sql);

        // Ejecutar la consulta
        ResultSet rs = ps.executeQuery();

        // Retornar los datos como especifico el usuario
        return process_select_data.invokeProcessSelectData(rs);
    };

    public DeleteDB<Connection> delete_element = (connection, table, condiciones) -> {
        // Construir la cláusula WHERE con las condiciones
        String whereClause = build_data_conditions(condiciones);

        // Generar la sentencia SQL DELETE
        String sql = "DELETE FROM %s WHERE %s".formatted(table, whereClause);
        print_debug(sql);

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.executeUpdate();
    };



    @Override
    public Connection getNewConection() throws SQLException, IOException {
        Properties configuration = new Properties();

        configuration.load(R.getProperties("databaseMySQL.properties"));
        String host = configuration.getProperty("host");
        String port = configuration.getProperty("port");
        String name = configuration.getProperty("name");
        String username = configuration.getProperty("username");
        String password = configuration.getProperty("password");

        return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + name + "?serverTimezone=UTC",
                username, password);
    }

    @Override
    public void closeConection() throws SQLException {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }

    @Override
    public Connection connect() {
        if (instance == null) {
            throw new SingletonException(this.getClass(),
                    "No se inicializo la clase o se cerro la conexion, debe obtener una instancia con getInstance()");
        }
        return instance;
    }


    @Override
    public void update(
            UpdateDB<Connection> update_process,
            String table,
            HashMap<String, Object> data,
            List<ConditionsDB> condiciones
    ) throws SQLException {
        if (instance == null) {
            throw new SingletonException(this.getClass(),
                    "No se inicializo la clase o se cerro la conexion, debe obtener una instancia con getInstance()");
        }
        update_process.invokeUpdate(instance, table, data, condiciones);
    }

    @Override
    public void insert(InsertDB<Connection> insert_process, String table, HashMap<String, Object> data) throws SQLException {
        if (instance == null) {
            throw new SingletonException(this.getClass(),
                    "No se inicializo la clase o se cerro la conexion, debe obtener una instancia con getInstance()");
        }
        insert_process.invokeInsert(instance, table, data);
    }

    @Override
    public Object select(
            SelectDB<Connection, ResultSet> select_process,
            List<String>select_fields,
            String table,
            List<ConditionsDB> condiciones,
            ProcessSelectData<ResultSet> process
    ) throws SQLException {
        if (instance == null) {
            throw new SingletonException(this.getClass(),
                    "No se inicializo la clase o se cerro la conexion, debe obtener una instancia con getInstance()");
        }
        return select_process.invokeSelect(instance, select_fields, table, condiciones, process);
    }

    @Override
    public void delete(DeleteDB<Connection> delete_process, String table, List<ConditionsDB> condiciones ) throws SQLException {
        if (instance == null) {
            throw new SingletonException(this.getClass(),
                    "No se inicializo la clase o se cerro la conexion, debe obtener una instancia con getInstance()");
        }
        delete_process.invokeRemove(instance, table, condiciones);
    }


    public static Connection getInstance() throws SQLException, IOException {
        if (instance == null) {
            instance = new ConnectionMySQLDBSingleton().getNewConection();
        }
        return instance;
    }


}

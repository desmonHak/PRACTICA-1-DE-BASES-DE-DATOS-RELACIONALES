package org.practica1debasesdedatosrelacionales.DAO.ManagerConnections;

import org.junit.jupiter.api.Test;
import org.practica1debasesdedatosrelacionales.DAO.PackageInterfaceCRUD.ProcessSelectData;
import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.SingletonException;
import org.practica1debasesdedatosrelacionales.domain.Cita;
import org.practica1debasesdedatosrelacionales.domain.DNI;
import org.practica1debasesdedatosrelacionales.domain.Especialidad;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionMySQLDBSingletonTest {
    ConnectionMySQLDBSingleton mySQLManager = new ConnectionMySQLDBSingleton();

    @Test
    void getNewConection() throws SQLException, IOException {
        // una primera vez deberemos obtener la nueva coneccion
        System.out.println(ConnectionMySQLDBSingleton.getInstance());
        System.out.println(mySQLManager.connect());
    }

    @Test
    void closeConection() throws SQLException, IOException {
        System.out.println("Antigua referencia: ");
        System.out.println(ConnectionMySQLDBSingleton.getInstance());
        mySQLManager.closeConection();
        System.out.println("Nueva referencia al cerrar conexion y pedir una instancia: ");
        System.out.println(ConnectionMySQLDBSingleton.getInstance());
    }

    @Test
    void connect() throws SQLException, IOException {
        // al usar connect, deberemos inicializar una instancia antes o este error aparecera
        try {
            System.out.println(mySQLManager.connect());
        } catch (SingletonException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // obtenemos una instancia por primera vez si nunca se pidio una:
        System.out.println(ConnectionMySQLDBSingleton.getInstance());
        System.out.println(mySQLManager.connect());
    }

    @Test
    void update() throws SQLException, IOException {
        // activar el modo de depuracion:
        ConnectionMySQLDBSingleton.modo_debug = true;

        System.out.println(ConnectionMySQLDBSingleton.getInstance());

        HashMap<String, Object> values_update = new HashMap<>();
        values_update.put("fecha_cita", Date.valueOf("2025-10-20"));

        // actualizar donde el numero de cita sea 1 y el DNI sea 48901940F
        ConditionsDB condicion1 = new ConditionsDB("dni",
                ConditionsDBOperators.EQUALS, "48901940F");
        ConditionsDB condicion2 = new ConditionsDB("numero_cita",
                ConditionsDBOperators.EQUALS, 1);

        mySQLManager.update(mySQLManager.update_element,
                "Citas", values_update, List.of(condicion1, condicion2));

    }

    @Test
    void insert() throws SQLException, IOException {
        System.out.println(ConnectionMySQLDBSingleton.getInstance());

        // Activar modo debug para ver la consulta SQL generada
        ConnectionMySQLDBSingleton.modo_debug = true;

        // Preparar datos para insertar
        HashMap<String, Object> newData = new HashMap<>();
        newData.put("dni", "48901940F");
        newData.put("numero_cita", 4);
        newData.put("fecha_cita", Date.valueOf(LocalDate.of(2025, 10, 20)));
        newData.put("especialidad", "Cirugia");

        // Ejecutar inserción en la tabla "Citas"
        mySQLManager.insert(mySQLManager.insert_element, "Citas", newData);
    }


    @Test
    void select() throws SQLException, IOException {

        System.out.println(ConnectionMySQLDBSingleton.getInstance());

        // Activar modo debug para ver la consulta SQL generada
        ConnectionMySQLDBSingleton.modo_debug = true;

        List<String> campos = List.of("dni", "numero_cita", "fecha_cita", "especialidad");
        ConditionsDB condicion1 = new ConditionsDB("dni", ConditionsDBOperators.EQUALS, "48901940F");
        //ConditionsDB condicion2 = new ConditionsDB("numero_cita", ConditionsDBOperators.EQUALS, 1);

         Object resultados = mySQLManager.select(
                mySQLManager.select_element,
                campos,
                "Citas",
                List.of(condicion1/*, condicion2*/),
                new ProcessSelectData<>() {
                    // como procesar los datos de la select
                    @Override
                    public Object invokeProcessSelectData(ResultSet data) throws SQLException {
                        List<Cita> citas = new ArrayList<>();
                        while (data.next()) {
                            citas.add(new Cita(
                                    new DNI(data.getString("dni")),
                                    data.getDate("fecha_cita"),
                                    new Especialidad(data.getString("especialidad")),
                                    data.getInt("numero_cita")
                            ));
                        }
                        return citas;
                    }
                }
        );

        for (Cita cita : (ArrayList<Cita>) resultados) {
            System.out.println(cita);
        }

    }

    @Test
    void delete() throws SQLException, IOException {
        System.out.println(ConnectionMySQLDBSingleton.getInstance());

        // Activar modo debug para ver la consulta SQL generada
        ConnectionMySQLDBSingleton.modo_debug = true;

        // Condiciones para eliminar, por ejemplo, donde dni sea un cierto valor y numero_cita igual a un valor
        ConditionsDB condicion1 = new ConditionsDB("dni", ConditionsDBOperators.EQUALS, "48901940F");
        ConditionsDB condicion2 = new ConditionsDB("numero_cita", ConditionsDBOperators.EQUALS, 4);

        // Ejecutar el borrado en la tabla "Citas"
        mySQLManager.delete(mySQLManager.delete_element, "Citas", List.of(condicion1, condicion2));

        // Opcional: para verificar, puedes hacer un select para asegurar que no quedan esos registros
        List<String> campos = List.of("dni", "numero_cita");
        Object resultados = mySQLManager.select(
                mySQLManager.select_element,
                campos,
                "Citas",
                List.of(condicion1, condicion2),
                new ProcessSelectData<>() {
                    @Override
                    public Object invokeProcessSelectData(ResultSet data) throws SQLException {
                        List<HashMap<String, Object>> registros = new ArrayList<>();
                        while (data.next()) {
                            HashMap<String, Object> registro = new HashMap<>();
                            registro.put("dni", data.getString("dni"));
                            registro.put("numero_cita", data.getInt("numero_cita"));
                            registros.add(registro);
                        }
                        return registros;
                    }
                }
        );

        assertTrue(((List<?>) resultados).isEmpty(), "Los registros no fueron eliminados correctamente");
    }


    @Test
    void getInstance() {
    }
}
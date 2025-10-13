package org.practica1debasesdedatosrelacionales.DAO.ManagerConnections;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.practica1debasesdedatosrelacionales.DAO.PackageInterfaceCRUD.ProcessSelectData;
import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.SingletonException;
import org.practica1debasesdedatosrelacionales.domain.Cita;
import org.practica1debasesdedatosrelacionales.domain.DNI;
import org.practica1debasesdedatosrelacionales.domain.Especialidad;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionMongoDBSingletonTest {
    ConnectionMongoDBSingleton mongoDBManager = new ConnectionMongoDBSingleton();

    @Test
    void getInstance() {
    }

    @Test
    void getNewConection() throws IOException {
        // una primera vez deberemos obtener la nueva coneccion
        System.out.println(ConnectionMongoDBSingleton.getInstance());
        System.out.println(mongoDBManager.connect());
    }

    @Test
    void closeConection() throws IOException {
        System.out.println("Antigua referencia: ");
        System.out.println(ConnectionMongoDBSingleton.getInstance());
        mongoDBManager.closeConection();
        System.out.println("Nueva referencia al cerrar conexion y pedir una instancia: ");
        System.out.println(ConnectionMongoDBSingleton.getInstance());
    }

    @Test
    void connect() throws IOException {
        // al usar connect, deberemos inicializar una instancia antes o este error aparecera
        try {
            System.out.println(mongoDBManager.connect());
        } catch (SingletonException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // obtenemos una instancia por primera vez si nunca se pidio una:
        System.out.println(ConnectionMongoDBSingleton.getInstance());
        System.out.println(mongoDBManager.connect());
    }

    @Test
    void update() throws SQLException, IOException {
        // activar el modo de depuracion:
        ConnectionMongoDBSingleton.modo_debug =  true;

        System.out.println(ConnectionMongoDBSingleton.getInstance());

        HashMap<String, Object> values_update = new HashMap<>();
        Date fechaJava = Date.from(Instant.parse("2025-10-22T00:00:00Z"));

        values_update.put("fecha_cita",fechaJava);

        // actualizar donde el numero de cita sea 1 y el DNI sea 48901940F
        ConditionsDB condicion1 = new ConditionsDB("dni",
                ConditionsDBOperators.EQUALS, "48901940F");
        ConditionsDB condicion2 = new ConditionsDB("numero_cita",
                ConditionsDBOperators.EQUALS, 1);

        // indicar que la base de datos con la que trabajamos ahora es Citas:
        mongoDBManager.setDataBaseName("centro_medico");
        mongoDBManager.update(mongoDBManager.update_element,
                "Citas", values_update, List.of(condicion1, condicion2));

    }

    @Test
    void insert() throws SQLException, IOException {
        System.out.println(ConnectionMongoDBSingleton.getInstance());

        // Activar modo debug
        ConnectionMongoDBSingleton.modo_debug = true;

        HashMap<String, Object> newData = new HashMap<>();
        newData.put("dni", "48901940F");
        newData.put("numero_cita", 5);
        newData.put("fecha_cita", Date.from(LocalDate.of(2025, 10, 23).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        newData.put("especialidad", "Pediatria");

        mongoDBManager.setDataBaseName("centro_medico");
        mongoDBManager.insert(mongoDBManager.insert_element, "Citas", newData);
    }
    @Test
    void select() throws SQLException, IOException {
        System.out.println(ConnectionMongoDBSingleton.getInstance());

        ConnectionMongoDBSingleton.modo_debug = true;

        List<String> campos = new ArrayList<>();
        campos.add("dni");
        campos.add("numero_cita");
        campos.add("fecha_cita");
        campos.add("especialidad");

        ConditionsDB condicion1 = new ConditionsDB("dni", ConditionsDBOperators.EQUALS, "48901940F");

        mongoDBManager.setDataBaseName("centro_medico");

        Object resultados = mongoDBManager.select(
                mongoDBManager.select_element,
                campos,
                "Citas",
                List.of(condicion1),
                new ProcessSelectData<Iterable<Document>>() {
                    @Override
                    public Object invokeProcessSelectData(Iterable<Document> data) {
                        List<Cita> citas = new ArrayList<>();
                        for (Document doc : data) {
                            java.util.Date fecha = doc.getDate("fecha_cita");

                            citas.add(new Cita(
                                    new DNI(doc.getString("dni")),
                                    new java.sql.Date(fecha.getTime()), // convertir la fecha a lo que queremos
                                    new Especialidad(doc.getString("especialidad")),
                                    doc.getInteger("numero_cita")
                            ));
                        }
                        return citas;
                    }
                }
        );

        for (Cita cita : (List<Cita>) resultados) {
            System.out.println(cita);
        }
    }



    @Test
    void delete() throws SQLException, IOException {
        System.out.println(ConnectionMongoDBSingleton.getInstance());

        ConnectionMongoDBSingleton.modo_debug = true;

        ConditionsDB condicion1 = new ConditionsDB("dni", ConditionsDBOperators.EQUALS, "48901940F");
        ConditionsDB condicion2 = new ConditionsDB("numero_cita", ConditionsDBOperators.EQUALS, 5);

        mongoDBManager.setDataBaseName("centro_medico");

        // Ejecutar delete
        mongoDBManager.delete(mongoDBManager.delete_element, "Citas", List.of(condicion1, condicion2));

        // Verificar que no quedaran documentos con esas condiciones
        Object resultados = mongoDBManager.select(
                mongoDBManager.select_element,
                List.of("dni", "numero_cita"),
                "Citas",
                List.of(condicion1, condicion2),
                new ProcessSelectData<Iterable<Document>>() {
                    @Override
                    public Object invokeProcessSelectData(Iterable<Document> data) {
                        List<Document> docs = new ArrayList<>();
                        data.forEach(docs::add); // añadimos cada documento a nuestra lista
                        return docs;
                    }
                }
        );

        assertTrue(((List<?>) resultados).isEmpty(), "No se eliminaron correctamente los documentos");
    }

}
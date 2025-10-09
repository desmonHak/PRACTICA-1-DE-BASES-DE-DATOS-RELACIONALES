package org.practica1debasesdedatosrelacionales.DAO;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.mongodb.MongoClient;
import org.bson.Document;
import org.practica1debasesdedatosrelacionales.DAO.ManagerConnections.*;
import org.practica1debasesdedatosrelacionales.DAO.PackageInterfaceCRUD.ProcessSelectData;
import org.practica1debasesdedatosrelacionales.DAO.PackageInterfaceCRUD.SelectDB;
import org.practica1debasesdedatosrelacionales.domain.Cita;
import org.practica1debasesdedatosrelacionales.domain.DNI;
import org.practica1debasesdedatosrelacionales.domain.Especialidad;
import org.practica1debasesdedatosrelacionales.domain.Paciente;

public class CitaDAO {
    private Object conn;
    public ConnnectionManager instance_manager;
    public Class<?> manager;

    public static boolean modo_debug = false;
    static void print_debug(Object data) {
        if (modo_debug) {
            System.out.println(data);
        }
    }

    public void connect(Class<?> managerDBClass) throws SQLException, IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        //load_especialidades(); // cargamos todas las especialidades
        // inicializar con mongoDB o MySQL
        this.manager = managerDBClass;

        Method metodo = this.manager.getMethod("getInstance");
        print_debug("llamando a: " + metodo);

        // creamos una instancia de la clase recibida
        this.instance_manager = (ConnnectionManager)this.manager.getDeclaredConstructor().newInstance();

        /**
         * invocamos el metodo getInstance que tiene la clase instanciada,
         * en caso de ser ConnectionMongoDBSingleton devuelve una instancia de MongoClient,
         * si es ConnectionMySQLDBSingleton devuelve una instancia de Connection
         */
        conn = metodo.invoke(this.instance_manager);
    }


    public void insert(Cita cita) throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Preparar datos para insertar
        HashMap<String, Object> newData = new HashMap<>();
        newData.put("dni", cita.getDni());
        newData.put("numero_cita", cita.getNumero_cita());
        newData.put("fecha_cita", cita.getFecha_cita());
        newData.put("especialidad", cita.getEspecialidad());

        Method metodoInsert = this.manager.getMethod("insert");
        print_debug("llamando a: " + metodoInsert);

        // Ejecutar inserción en la tabla "Citas"
        //mySQLManager.insert_element

        metodoInsert.invoke(this.instance_manager,null , "Citas", newData);
    }

    public List<Cita> select(DNI dni) throws SQLException, IOException, IllegalAccessException {
        if (conn == null ) {
            throw new SQLException("Conexión a base de datos no establecida.");
        }
        List<String> campos = new ArrayList<>();
        campos.add("dni");
        campos.add("numero_cita");
        campos.add("fecha_cita");
        campos.add("especialidad");

        ArrayList<ConditionsDB> condiciones = new ArrayList<>();
        condiciones.add(new ConditionsDB("dni", ConditionsDBOperators.EQUALS, dni.toString()));

        // buscamos el atributo select_element en las clases ConnectionMongoDBSingleton o ConnectionMySQLSingleton
        Field lambda_select = null;
        for (Field field: this.manager.getDeclaredFields()) {
            print_debug(field.getName());
            if (field.getName().equals("select_element")) {
                lambda_select = field;
                break;
            }
        }

        if (manager == ConnectionMongoDBSingleton.class) {
            print_debug("Cambiando a la DB centro_medico");
            ((ConnectionMongoDBSingleton)instance_manager).setDataBaseName("centro_medico");
        }

        assert lambda_select != null;
        Object resultados = ((ConnnectionManager)instance_manager).select(
                /**
                 * obtenemos el lambda "select_element" definidos en los manegadores de clase
                 * y lo casteo
                 */
                (SelectDB)lambda_select.get(instance_manager),
                campos,
                "Citas",
                condiciones,
                new ProcessSelectData<>() {
                    @Override
                    public Object invokeProcessSelectData(Object data) throws SQLException {
                        List<Cita> citas = new ArrayList<>();
                        if (manager == ConnectionMongoDBSingleton.class) {
                            for (Document doc : (Iterable<Document>)data) {
                                Date fecha = doc.getDate("fecha_cita");

                                citas.add(new Cita(
                                        new DNI(doc.getString("dni")),
                                        new java.sql.Date(fecha.getTime()), // convertir la fecha a lo que queremos
                                        new Especialidad(doc.getString("especialidad")),
                                        doc.getInteger("numero_cita")
                                ));
                            }
                        } else {
                            ResultSet data_ = (ResultSet)data;
                            while (data_.next()) {
                                citas.add(new Cita(
                                        new DNI(data_.getString("dni")),
                                        data_.getDate("fecha_cita"),
                                        new Especialidad(data_.getString("especialidad")),
                                        data_.getInt("numero_cita")
                                ));
                            }
                        }
                        return citas;
                    }
                }
        );


        return (List<Cita>) resultados;

    }


    public void delete(Paciente paciente, Cita cita) throws SQLException {
        if (conn == null) {
            throw new SQLException("Conexión a base de datos no establecida.");
        }/*
        PreparedStatement ps = conn.prepareStatement(
                "delete from Citas where DNI = ? AND numero_cita = ?");

        ps.setString(1, paciente.getDni().toString());
        ps.setInt(2, cita.getNumero_cita());
        ps.executeUpdate();
    }

    public void update(Cita cita) throws SQLException {
        if (conn == null || conn.isClosed()) {
            throw new SQLException("Conexión a base de datos no establecida.");
        }

        String sql = """
            UPDATE Citas
            SET fecha_cita = ?, especialidad = ?
            WHERE DNI = ? AND numero_cita = ?;
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, cita.getFecha_cita());
            ps.setString(2, cita.getEspecialidad().toString());
            ps.setString(3, cita.getDni().toString());
            ps.setInt(4, cita.getNumero_cita());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se encontró la cita para actualizar: " +
                        cita.getDni() + " #" + cita.getNumero_cita());
            }
        }*/
    }

    public int getMaxNumeroCita() throws SQLException {
        /*
        if (conn == null) {
            throw new SQLException("Conexión a base de datos no establecida.");
        }

        String sql = "SELECT MAX(numero_cita) AS max_cita FROM Citas";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("max_cita");
            } else {
                return 0; // Si no hay citas, devolver 0
            }
        }*/
        return 0;
    }

    public void update(Cita citaNow) {
    }
}

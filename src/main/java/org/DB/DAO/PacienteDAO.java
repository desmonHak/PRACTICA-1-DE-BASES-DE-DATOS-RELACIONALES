package org.DB.DAO;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import org.bson.Document;
import org.DB.DAO.ManagerConnections.*;
import org.DB.DAO.PackageInterfaceCRUD.InsertDB;
import org.DB.DAO.PackageInterfaceCRUD.ProcessSelectData;
import org.DB.DAO.PackageInterfaceCRUD.SelectDB;
import org.DB.Exceptions.ExceptionsDB.SQLDataNotFound;
import org.DB.Exceptions.ExceptionsDB.SQLUnknownException;
import org.DB.Exceptions.ExceptionsDB.SingletonException;
import org.DB.domain.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PacienteDAO implements OperationsCRUD_DAO {

    private Object conn;
    public Object instance_manager;
    public Class<?> manager;

    public Object getConn() {
        return conn;
    }
    public static boolean modo_debug = false;
    static void print_debug(Object data) {
        if (modo_debug) {
            System.out.println(data);
        }
    }

    public PacienteDAO(Class<?> manager) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (
                manager == ConnectionMongoDBSingleton.class ||
                        manager == ConnectionMySQLDBSingleton.class) {
            // inicializar con mongoDB o MySQL
            this.manager = manager;

            Method metodo = manager.getMethod("getInstance");
            print_debug("llamando a: " + metodo);

            // creamos una instancia de la clase recibida
            this.instance_manager = manager.getDeclaredConstructor().newInstance();

            /**
             * invocamos el metodo getInstance que tiene la clase instanciada,
             * en caso de ser ConnectionMongoDBSingleton devuelve una instancia de MongoClient,
             * si es ConnectionMySQLDBSingleton devuelve una instancia de Connection
             */
            try {
                conn = metodo.invoke(this.instance_manager);
            } catch (InvocationTargetException e) {
                Throwable causaReal = e.getTargetException(); // o e.getCause(), puedo obtener el error que se causo en la invocacion real

                if (causaReal instanceof SingletonException) { /**
                    * si este error ocurrio, la base de datos no esta ejecutandose lo mas seguro, quiero
                    * caputar el error en el controller de login, asi al presionar el boton de logeo si ocurre este error
                    * poder lanzar una ventana de error
                    */
                    throw (SingletonException)causaReal;
                }
            } catch (Exception e) { // por defecto imprimire la informacion de error
                e.printStackTrace();
            }
        } else {
            System.out.println("La clase no se reconoce: " + manager);
        }
    }


    @Override
    public void insert(@NotNull Object object) throws SQLException, IllegalAccessException, IOException {

        // Preparar datos para insertar
        HashMap<String, Object> newData = new HashMap<>();
        newData.put("email", ((Paciente)(object)).getEmail());
        newData.put("password", ((Paciente)(object)).getHashClass().getHash());
        newData.put("dni", ((Paciente)(object)).getDni().toString());
        newData.put("nombre", ((Paciente)(object)).getNombre());
        newData.put("direccion", ((Paciente)(object)).getDireccion());
        newData.put("telefono", ((Paciente)(object)).getTelefono());

        if (manager == ConnectionMongoDBSingleton.class) {
            print_debug("Cambiando a la DB centro_medico");
            ((ConnectionMongoDBSingleton)instance_manager).setDataBaseName("centro_medico");
        }

        // buscamos el atributo insert_element en las clases ConnectionMongoDBSingleton o ConnectionMySQLSingleton
        Field lambda_insert = null;
        for (Field field: this.manager.getDeclaredFields()) {
            print_debug(field.getName());
            if (field.getName().equals("insert_element")) {
                lambda_insert = field;
                break;
            }
        }

        assert lambda_insert != null;
        ((ConnnectionManager)instance_manager).insert(
                (InsertDB) lambda_insert.get(instance_manager),
                "Paciente",
                newData
        );
    }

    @Override
    public Paciente select(Object object) throws SQLException, SQLDataNotFound, SQLUnknownException, IllegalAccessException, CommunicationsException, IOException {

        List<String> campos = new ArrayList<>();
        campos.add("dni");
        campos.add("nombre");
        campos.add("password");
        campos.add("telefono");
        campos.add("direccion");
        campos.add("email");

        ArrayList<ConditionsDB> condiciones = new ArrayList<>();
        condiciones.add((ConditionsDB) object);

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

                (SelectDB)lambda_select.get(instance_manager),
                campos,
                "Paciente",
                condiciones,
                new ProcessSelectData<>() {
                    @Override
                    public Object invokeProcessSelectData(Object data) throws SQLException {
                        List<Paciente> pacientes = new ArrayList<>();
                        if (manager == ConnectionMongoDBSingleton.class) {
                            for (Document doc : (Iterable<Document>)data) {
                                pacientes.add(new Paciente(
                                        new DNI(doc.getString("dni")),
                                        doc.getString("nombre"),
                                        doc.getString("email"),
                                        new SHA256(doc.getString("password"), true),
                                        doc.getString("telefono"),
                                        doc.getString("direccion")
                                ));
                            }
                        } else {
                            ResultSet data_ = (ResultSet)data;
                            while (data_.next()) {
                                pacientes.add(new Paciente(
                                        new DNI(data_.getString("dni")),
                                        data_.getString("nombre"),
                                        data_.getString("email"),
                                        new SHA256(data_.getString("password"), true),
                                        data_.getString("telefono"),
                                        data_.getString("direccion")
                                ));
                            }
                        }
                        return pacientes;
                    }
                }
        );

        if (((ArrayList)resultados).size() > 1) {
            throw new SQLDataNotFound("Hay dos pacientes con el mismo DNI");
        } else {
            return (Paciente) ((ArrayList)resultados).getFirst();
        }
    }

    @Override
    public void delete(Object object) throws SQLException, IllegalAccessException, IOException {

    }

    @Override
    public void update(Object object) throws IllegalAccessException, SQLException, IOException {

    }


}

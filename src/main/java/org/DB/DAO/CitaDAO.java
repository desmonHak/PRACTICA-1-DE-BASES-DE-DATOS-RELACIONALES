package org.DB.DAO;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.bson.Document;
import org.DB.DAO.ManagerConnections.*;
import org.DB.DAO.PackageInterfaceCRUD.*;
import org.DB.Exceptions.ExceptionsDB.SingletonException;
import org.DB.domain.Cita;
import org.DB.domain.DNI;
import org.DB.domain.Especialidad;

public class CitaDAO implements OperationsCRUD_DAO {
    Object conn;
    public ConnnectionManager instance_manager;
    public Class<?> manager;

    public static boolean modo_debug = false;


    public CitaDAO(Class<?> managerDBClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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
    }


    static void print_debug(Object data) {
        if (modo_debug) {
            System.out.println(data);
        }
    }


    @Override
    public void insert(Object object) throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        // Preparar datos para insertar
        HashMap<String, Object> newData = new HashMap<>();
        newData.put("dni", ((Cita)object).getDni().toString());
        newData.put("numero_cita", ((Cita)object).getNumero_cita());
        newData.put("fecha_cita", ((Cita)object).getFecha_cita());
        newData.put("especialidad", ((Cita)object).getEspecialidad().toString());

        //Method metodoInsert = this.manager.getMethod("insert");
        //print_debug("llamando a: " + metodoInsert);

        // Ejecutar insercion en la tabla "Citas"
        //mySQLManager.insert_element

        //metodoInsert.invoke(this.instance_manager,null , "Citas", newData);

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
                "Citas",
                newData
        );
    }

    @Override
    public List<Cita> select(Object object) throws SQLException, IOException, IllegalAccessException {

        List<String> campos = new ArrayList<>();
        campos.add("dni");
        campos.add("numero_cita");
        campos.add("fecha_cita");
        campos.add("especialidad");

        ArrayList<ConditionsDB> condiciones = new ArrayList<>();
        condiciones.add(new ConditionsDB("dni", ConditionsDBOperators.EQUALS, ((DNI)object).toString()));

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


    @Override
    public void delete(Object object) throws SQLException, IllegalAccessException, IOException {
        ArrayList<ConditionsDB> condiciones = new ArrayList<>();

        // where DNI = ?, numero_cita = ?
        condiciones.add(new ConditionsDB(
                "dni", ConditionsDBOperators.EQUALS, ((Cita)object).getDni().toString()
        ));
        condiciones.add(new ConditionsDB(
                "numero_cita", ConditionsDBOperators.EQUALS, ((Cita)object).getNumero_cita()
        ));

        if (manager == ConnectionMongoDBSingleton.class) {
            print_debug("Cambiando a la DB centro_medico");
            ((ConnectionMongoDBSingleton)instance_manager).setDataBaseName("centro_medico");
        }

        // buscamos el atributo delete_element en las clases ConnectionMongoDBSingleton o ConnectionMySQLSingleton
        Field lambda_delete = null;
        for (Field field: this.manager.getDeclaredFields()) {
            print_debug(field.getName());
            if (field.getName().equals("delete_element")) {
                lambda_delete = field;
                break;
            }
        }

        ((ConnnectionManager)instance_manager)
                .delete((DeleteDB)lambda_delete.get(instance_manager),
                        "Citas", condiciones);

    }

    @Override
    public void update(Object object) throws IllegalAccessException, SQLException, IOException {
        HashMap<String, Object> values_update = new HashMap<>();
        values_update.put("fecha_cita", ((Cita)object).getFecha_cita());
        values_update.put("especialidad", ((Cita)object).getEspecialidad().toString());

        ArrayList<ConditionsDB> condiciones = new ArrayList<>();
        condiciones.add(new ConditionsDB("dni",
                ConditionsDBOperators.EQUALS, ((Cita)object).getDni().toString()));
        condiciones.add(new ConditionsDB("numero_cita",
                ConditionsDBOperators.EQUALS, ((Cita)object).getNumero_cita()));

        // buscamos el atributo select_update en las clases ConnectionMongoDBSingleton o ConnectionMySQLSingleton
        Field lambda_update = null;
        for (Field field: this.manager.getDeclaredFields()) {
            print_debug(field.getName());
            if (field.getName().equals("update_element")) {
                lambda_update = field;
                break;
            }
        }

        if (manager == ConnectionMongoDBSingleton.class) {
            print_debug("Cambiando a la DB centro_medico");
            ((ConnectionMongoDBSingleton)instance_manager).setDataBaseName("centro_medico");
        }

        assert lambda_update != null;
        ((ConnnectionManager)instance_manager).update(
            (UpdateDB)lambda_update.get(instance_manager),
            "Citas",
            values_update,
            condiciones
        );

    }

    public int getMaxNumeroCita() throws SQLException, IllegalAccessException, IOException {

        List<String> campos = new ArrayList<>();
        campos.add("numero_cita");

        instance_manager.connect();

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
        Object resultados = instance_manager.select(
                /**
                 * obtenemos el lambda "select_element" definidos en los manegadores de clase
                 * y lo casteo
                 */
                (SelectDB)lambda_select.get(instance_manager),
                campos,
                "Citas",
                null,
                new ProcessSelectData<>() {
                    @Override
                    public Object invokeProcessSelectData(Object data) throws SQLException {
                        List<Integer> citas = new ArrayList<>();
                        if (manager == ConnectionMongoDBSingleton.class) {
                            for (Document doc : (Iterable<Document>)data) {
                                citas.add(doc.getInteger("numero_cita"));
                            }
                        } else {
                            ResultSet data_ = (ResultSet)data;
                            while (data_.next()) {
                                citas.add(data_.getInt("numero_cita"));
                            }
                        }
                        return citas;
                    }
                }
        );




        ((List<Integer>)resultados).sort( (a, b) -> {
            // -1 uno para que se ordenen de mayor a menor
            return -1 * a.compareTo(b);
        } );

        return ((List<Integer>)resultados).getFirst();
    }
}

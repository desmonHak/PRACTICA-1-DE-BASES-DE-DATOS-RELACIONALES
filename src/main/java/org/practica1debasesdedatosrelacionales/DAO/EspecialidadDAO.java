package org.practica1debasesdedatosrelacionales.DAO;

import com.mongodb.MongoClient;
import org.bson.Document;
import org.practica1debasesdedatosrelacionales.DAO.ManagerConnections.ConnectionMongoDBSingleton;
import org.practica1debasesdedatosrelacionales.DAO.ManagerConnections.ConnectionMySQLDBSingleton;
import org.practica1debasesdedatosrelacionales.DAO.PackageInterfaceCRUD.ProcessSelectData;
import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.SQLDataNotFound;
import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.SQLUnknownException;
import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.SingletonException;
import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.TypeDataUnknown;
import org.practica1debasesdedatosrelacionales.domain.*;
import org.practica1debasesdedatosrelacionales.util.R;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EspecialidadDAO {
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

    public EspecialidadDAO(Class<?> manager) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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
            throw new TypeDataUnknown("La clase SGDB: " + manager + " no se reconoce");
        }
    }

    // inicializa las especialidades obteniendolas de la DB
    public List<String> load_especialidades() throws SQLException, IOException {


        List<String> campos = List.of("nombre");

        if (this.manager == ConnectionMongoDBSingleton.class) {

            ((ConnectionMongoDBSingleton)this.instance_manager).setDataBaseName("centro_medico");

            Object resultados = ((ConnectionMongoDBSingleton)this.instance_manager).select(
                    ((ConnectionMongoDBSingleton)this.instance_manager).select_element,
                    campos,
                    "Especialidad",
                    null, // sin condiciones
                    new ProcessSelectData<Iterable<Document>>() {
                        // como procesar los datos de la select
                        @Override
                        public Object invokeProcessSelectData(Iterable<Document> data) throws SQLException {
                            List<String> especialidades = new ArrayList<>();
                            for (Document doc : data) {
                                especialidades.add(doc.getString("nombre"));
                            }
                            return especialidades;
                        }
                    }
            );
            return (ArrayList<String>)resultados;

        } else if (this.manager == ConnectionMySQLDBSingleton.class) {
            ConnectionMySQLDBSingleton instanceMySQL = (ConnectionMySQLDBSingleton) this.instance_manager;

            Object resultados = instanceMySQL.select(
                    instanceMySQL.select_element,
                    campos,
                    "Especialidad",
                    null, // sin condiciones
                    new ProcessSelectData<>() {
                        // como procesar los datos de la select
                        @Override
                        public Object invokeProcessSelectData(ResultSet data) throws SQLException {
                            List<String> especialidades = new ArrayList<>();
                            while (data.next()) {
                                especialidades.add(data.getString("nombre"));
                            }
                            return especialidades;
                        }
                    }
            );
            return (ArrayList<String>)resultados;
        } else {
            throw new TypeDataUnknown("La clase %s no es valida".formatted(this.manager));
        }

    }

}

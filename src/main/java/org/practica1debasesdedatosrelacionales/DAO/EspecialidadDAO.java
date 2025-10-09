package org.practica1debasesdedatosrelacionales.DAO;

import org.bson.Document;
import org.practica1debasesdedatosrelacionales.DAO.ManagerConnections.ConnectionMongoDBSingleton;
import org.practica1debasesdedatosrelacionales.DAO.ManagerConnections.ConnectionMySQLDBSingleton;
import org.practica1debasesdedatosrelacionales.DAO.PackageInterfaceCRUD.ProcessSelectData;
import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.SQLDataNotFound;
import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.SQLUnknownException;
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
    public Object instance_mangaer;
    public Class<?> mangaer;

    public Object getConn() {
        return conn;
    }
    public static boolean modo_debug = false;
    private static void print_debug(Object data) {
        if (modo_debug) {
            System.out.println(data);
        }
    }

    public void connect(Class<?> manager) throws SQLException, IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        if (
                manager == ConnectionMongoDBSingleton.class ||
                        manager == ConnectionMySQLDBSingleton.class) {
            // inicializar con mongoDB o MySQL
            this.mangaer = manager;

            Method metodo = manager.getMethod("getInstance");
            print_debug("llamando a: " + metodo);

            // creamos una instancia de la clase recibida
            this.instance_mangaer = manager.getDeclaredConstructor().newInstance();

            /**
             * invocamos el metodo getInstance que tiene la clase instanciada,
             * en caso de ser ConnectionMongoDBSingleton devuelve una instancia de MongoClient,
             * si es ConnectionMySQLDBSingleton devuelve una instancia de Connection
             */
            conn = metodo.invoke(this.instance_mangaer);
        } else {
            System.out.println("La clase no se reconoce: " + manager);
        }
    }
    public void desconnect() throws SQLException {
        ((Connection)conn).close();
    }

    // inicializa las especialidades obteniendolas de la DB
    public List<String> load_especialidades() throws SQLException, IOException {

        ConnectionMongoDBSingleton instanceMongo = null;
        ConnectionMySQLDBSingleton instanceMySQL = null;

        List<String> campos = List.of("nombre");

        if (this.mangaer == ConnectionMongoDBSingleton.class) {
            instanceMongo = (ConnectionMongoDBSingleton) this.instance_mangaer;

            instanceMongo.setDataBaseName("centro_medico");

            Object resultados = instanceMongo.select(
                    instanceMongo.select_element,
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

        } else if (this.mangaer == ConnectionMySQLDBSingleton.class) {
            instanceMySQL = (ConnectionMySQLDBSingleton) this.instance_mangaer;

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
            throw new TypeDataUnknown("La clase %s no es valida".formatted(this.mangaer));
        }

    }

}

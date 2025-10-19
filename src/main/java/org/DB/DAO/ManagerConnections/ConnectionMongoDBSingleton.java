package org.practica1debasesdedatosrelacionales.DAO.ManagerConnections;


import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.practica1debasesdedatosrelacionales.DAO.PackageInterfaceCRUD.*;
import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.SingletonException;
import org.practica1debasesdedatosrelacionales.util.R;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Usaremos el patron Singleton para tener una unica instancia de
 * conexion
 */
public class ConnectionMongoDBSingleton extends ConnnectionManager<MongoClient, Iterable<Document>> {

    final public static String name_manager = "MongoDB";

    /**
     * variable estatica que almacenara la unica existencia que habra
     */
    private static MongoClient instance = null;
    public static boolean modo_debug = false;

    private String dataBaseName;

    private static void print_debug(Object data) {
        if (modo_debug) {
            System.out.println(data);
        }
    }

    public static void setInstance(MongoClient instance) {
        ConnectionMongoDBSingleton.instance = instance;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public ConnectionMongoDBSingleton() {}
    public ConnectionMongoDBSingleton(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public static MongoClient getInstance() throws IOException {
        if (instance == null) {
            instance = new ConnectionMongoDBSingleton().getNewConection();
        }
        return instance;
    }

    /**
     * crea un documento BSON con todos los pares campo=valor
     * @param data datos que convertir a BSON
     * @return Documento con los datos
     */
    private static Document convertToDocument(HashMap<String, Object> data) {
        return new Document(data);
    }

    // crear un filtro BSON
    private static Document buildFilterFromConditions(List<ConditionsDB> conditions) {
        Document filtro = new Document();
        for (ConditionsDB condition : conditions) {
            String field = condition.getField();
            Object value = condition.getValue();
            switch (condition.getCondition()) {
                case EQUALS:
                    filtro.append(field, value);
                    break;
                case NOT_EQUALS:
                    filtro.append(field, new Document("$ne", value));
                    break;
                case GREATER_THAN:
                    filtro.append(field, new Document("$gt", value));
                    break;
                case GREATER_OR_EQUALS:
                    filtro.append(field, new Document("$gte", value));
                    break;
                case LESS_THAN:
                    filtro.append(field, new Document("$lt", value));
                    break;
                case LESS_OR_EQUALS:
                    filtro.append(field, new Document("$lte", value));
                    break;
                default:
                    throw new IllegalArgumentException("Operador no soportado: " + condition.getCondition());
            }
        }
        return filtro;
    }

    public UpdateDB<MongoClient> update_element = (connection, table,
                                                         data, conditions) -> {
        try {
            MongoDatabase database = connection.getDatabase(this.dataBaseName);
            MongoCollection<Document> collection = database.getCollection(table);

            // Convertir data y condiciones
            Document data_in_document = convertToDocument(data);
            Document filtro = buildFilterFromConditions(conditions);

            // Crear el documento de actualizacion
            Document updateOperation = new Document("$set", data_in_document);

            print_debug(filtro);
            print_debug(updateOperation);
            if (modo_debug) {
                for (Document doc : collection.find(filtro)) {
                    print_debug("Pre-update: " + doc.toJson());
                }
            }

            // Ejecutar la actualizacion
            UpdateResult result = collection.updateMany(filtro, updateOperation);
            print_debug("Documentos actualizados: " + result.getModifiedCount());


            print_debug("Actualizacion completada en coleccion " + table);
            if (modo_debug) {
                for (Document doc : collection.find(filtro)) {
                    print_debug(doc.toJson());
                }
            }


        } catch (MongoException e) {
            System.err.println("Error al actualizar datos en MongoDB: " + e.getMessage());
        }
    };

    public InsertDB<MongoClient> insert_element = (connection, table, objects_insert) -> {
        try {
            MongoDatabase database = connection.getDatabase(this.dataBaseName);
            MongoCollection<Document> collection = database.getCollection(table);

            // Convertir el HashMap a documento BSON
            HashMap<String, Object> data = (HashMap<String, Object>) objects_insert;

            // es necesario convertir los objetos de tipo sql.Date de my sql en objetos
            // de tipo util.Date, aunque una extiende de otra, no hacer esto
            // hara que se genere un error
            Map<String, Object> convertedData = new HashMap<>();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                Object value = entry.getValue();

                if (value instanceof java.sql.Date sqlDate) {
                    // Convertir a java.util.Date
                    convertedData.put(entry.getKey(), new java.util.Date(sqlDate.getTime()));
                } else {
                    convertedData.put(entry.getKey(), value);
                }
            }


            Document document = new Document(convertedData);

            // Insertar el documento en la coleccion
            collection.insertOne(document);

            if (modo_debug) {
                System.out.println("Documento insertado en coleccion " + table + ": " + document.toJson());
            }
        } catch (MongoException e) {
            System.err.println("Error al insertar datos en MongoDB: " + e.getMessage());
        }
    };


    public SelectDB<MongoClient, Iterable<Document>> select_element = (
            connection, select_fields,
            table, condiciones,
            process_select_data) -> {
        MongoDatabase database = connection.getDatabase(this.dataBaseName);
        MongoCollection<Document> collection = database.getCollection(table);

        // un filtro vacio por defecto es como poner  "db["Especialidad"].find({})"
        Document filtro = new Document();
        // Construir filtro BSON a partir de condiciones
        if (condiciones != null) {
            filtro = buildFilterFromConditions(condiciones);
            print_debug("Filtro de consulta: " + filtro.toJson());
        }

        // Obtener cursor con documentos que cumplen el filtro
        Iterable<Document> documentos = collection.find(filtro);

        // Usar la funcion de procesamiento definida por el usuario sobre los documentos
        // La interfaz ProcessSelectData debe poder trabajar con Iterable<Document>
        return process_select_data.invokeProcessSelectData(documentos);
    };

    public DeleteDB<MongoClient> delete_element = (connection, collection, condiciones) -> {
        try {
            MongoDatabase database = connection.getDatabase(this.dataBaseName);
            MongoCollection<Document> mongoCollection = database.getCollection(collection);

            // Construir filtro BSON
            Document filtro = buildFilterFromConditions(condiciones);
            print_debug("Filtro para delete: " + filtro.toJson());

            // Eliminar documentos que cumplen el filtro
            long deletedCount = mongoCollection.deleteMany(filtro).getDeletedCount();

            print_debug("Documentos eliminados: " + deletedCount);
        } catch (MongoException e) {
            System.err.println("Error al eliminar documentos en MongoDB: " + e.getMessage());
        }
    };

    @Override
    public MongoClient getNewConection() throws IOException {
        final int MAX_CONNECTION_RETRIES = 3;
        final int RETRY_DELAY_MS = 2000;
        MongoClient client = null;
        int attempts = 0;

        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE); // indicar que solo quiero ver errores graves de MongoDB


        try {
            Properties properties = new Properties();
            properties.load(R.getProperties("databaseMongoDB.properties"));
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            String host = properties.getProperty("host");
            int port = Integer.parseInt(properties.getProperty("port"));


            while (attempts < MAX_CONNECTION_RETRIES) {
                try {
                    client = new MongoClient(
                            new MongoClientURI(String.format(
                                    // con serverSelectionTimeoutMS=50, indico un timeout de 50ms, en lugar de los 30s por
                                    // defecto, de esta manera la GUI apenas se queda bloqueada
                                    "mongodb://%s:%s@%s:%d/?authSource=admin&serverSelectionTimeoutMS=50",
                                    username, password, host, port
                            ))
                    );

                    // la conexion se pudo realizar, necesito hacer este ping por que al crear la conexion, si no se puede
                    // establecer, no se generara un error en mi hilo, sino en el hilo del gestor de la libreria, por lo
                    // que no puedo capturarlo directamente, mi estrategia sera entonces intentar hacer la conexion y forzar
                    // un ping para averiguar si la conexion se realizo o no, en caso de error, es que no se pudo hacer lo
                    // mas posible

                    MongoDatabase db = client.getDatabase("admin");
                    db.runCommand(new org.bson.Document("ping", 1));
                    System.out.println("Conexion exitosa a MongoDB");
                    return client;

                } catch (MongoSocketOpenException | MongoTimeoutException e) {
                    attempts++;
                    System.err.println("Intento " + attempts + " fallido: " + e.getMessage());
                    if (attempts < MAX_CONNECTION_RETRIES) {
                        try {
                            Thread.sleep(RETRY_DELAY_MS);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error inesperado al conectar a MongoDB: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        throw new MongoTimeoutException ("No se pudo conectar a MongoDB despues de " + MAX_CONNECTION_RETRIES + " intentos.");
    }

    /**
     * Cierra la unica instancia existente, si se quiere volver a crear una nueva,
     * debera llamarse a getInstance()
     */
    @Override
    public void closeConection() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }

    /**
     * Devuelve la instancia de la conexion actual sin inicializarla
     * @return
     */
    @Override
    public MongoClient connect() {
        if (instance == null) {
            throw new SingletonException(this.getClass(),
                    "No se inicializo la clase o se cerro la conexion, debe obtener una instancia con getInstance()");
        }
        return instance;
    }

    @Override
    public void update(UpdateDB<MongoClient> update_process, String collection, HashMap<String, Object> data, List<ConditionsDB> condiciones) throws SQLException, IOException {
        if (instance == null) {
            throw new SingletonException(this.getClass(),
                    "No se inicializo la clase o se cerro la conexion, debe obtener una instancia con getInstance()");
        }
        try {
            update_process.invokeUpdate(instance, collection, data, condiciones);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(InsertDB<MongoClient> insert_process, String collection, HashMap<String, Object> data) throws SQLException, IOException {
        if (instance == null) {
            throw new SingletonException(this.getClass(),
                    "No se inicializo la clase o se cerro la conexion, debe obtener una instancia con getInstance()");
        }
        try {
            insert_process.invokeInsert(instance, collection, data);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object select(
            SelectDB<MongoClient, Iterable<Document>> select_process,
            List<String>select_fields,
            String collection,
            List<ConditionsDB> condiciones,
            ProcessSelectData<Iterable<Document>> process
    ) throws SQLException, IOException {
        if (instance == null) {
            throw new SingletonException(this.getClass(),
                    "No se inicializo la clase o se cerro la conexion, debe obtener una instancia con getInstance()");
        }
        try {
            return select_process.invokeSelect(instance, select_fields, collection, condiciones, process);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(DeleteDB<MongoClient> delete_process, String collection, List<ConditionsDB> condiciones) throws SQLException, IOException {
        if (instance == null) {
            throw new SingletonException(this.getClass(),
                    "No se inicializo la clase o se cerro la conexion, debe obtener una instancia con getInstance()");
        }
        try {
            delete_process.invokeRemove(instance, collection, condiciones);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

package org.practica1debasesdedatosrelacionales.DAO;

import org.practica1debasesdedatosrelacionales.DAO.PackageInterfaceCRUD.ProcessSelectData;
import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.SQLDataNotFound;
import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.SQLUnknownException;
import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.TypeDataUnknown;
import org.practica1debasesdedatosrelacionales.domain.*;
import org.practica1debasesdedatosrelacionales.util.R;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

import static org.practica1debasesdedatosrelacionales.DAO.EspecialidadDAO.load_especialidades;

public class PacienteDAO {

    private Connection conn;
    public void connect() throws SQLException, IOException {
        load_especialidades(); // cargamos todas las especialidades

        Properties configuration = new Properties();

        configuration.load(R.getProperties("databaseMongoDB.properties"));
        String host = configuration.getProperty("host");
        String port = configuration.getProperty("port");
        String name = configuration.getProperty("name");
        String username = configuration.getProperty("username");
        String password = configuration.getProperty("password");

        conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + name + "?serverTimezone=UTC",
                username, password);
    }
    public void desconnect() throws SQLException {
        conn.close();
    }


    public void insert(Paciente paciente) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Paciente (email, password, DNI, nombre, direccion, telefono) VALUES (?, ?, ?, ?, ?, ?);");
        ps.setString(1, paciente.getEmail());
        ps.setString(2, paciente.getHashClass().getHash());
        ps.setString(3, paciente.getDni().toString());
        ps.setString(4, paciente.getNombre());
        ps.setString(5, paciente.getDireccion());
        ps.setString(6, paciente.getTelefono());
        ps.executeUpdate();
    }

    public Paciente select(DNI dni) throws SQLException, SQLDataNotFound, SQLUnknownException {
        PreparedStatement ps = conn.prepareStatement(
                "select nombre, email, password, telefono, direccion from Paciente where DNI = ?");

        ps.setString(1, dni.toString());
        ResultSet rs = ps.executeQuery();
        rs.next();


        try {
            return new Paciente(
                    dni,
                    rs.getString(1),
                    rs.getString(2),
                    new SHA256(rs.getString(3), true),
                    rs.getString(4),
                    rs.getString(5)
            );
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            System.out.println("SQLState: " + sqlState);
            if (sqlState.equals("S1000")) {
                System.out.println("No se encontro estos datos, salida: " + e.getMessage());
                throw new SQLDataNotFound("No se encontro estos datos: " + e.getMessage());
            } else {
                System.out.println("Error SQL desconocido/no contemplado: " + e.getMessage());
                throw new SQLUnknownException(e);
            }
        }

    }

    /**
     * Permite obtener los datos de la DB usando una clausula where,
     * donde se puede especificar el campo de busqueda via field_by_search,
     * y el valor a buscar con value_search,
     * Se debe pasar una funcion lambda "process" donde se procese el Result Set
     * obtenido, y se cree una instancia de Paciente que se retorne
     *
     * @param field_by_search Campo a usar de busqueda, DNI, email, etc
     * @param value_search Valor a buscar
     * @param process funcion lambda que usar para procesar los datos
     * @return Un paciente buscado segun los campos anteriores
     * @throws SQLException Posiblemente no se encontro al paciente.
     * @throws TypeDataUnknown No se pudo adivinar el tipo de dato del que se trata
     */
    public Paciente select(
            String field_by_search, Object value_search,
            ProcessSelectData process
    ) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "select dni, nombre, email, password, telefono, direccion from Paciente where %s = ?".formatted(
                        field_by_search
                ));


        if (value_search.getClass().equals(Date.class)) {
            ps.setDate(1, (Date)value_search);
        } else if (value_search.getClass().equals(String.class)) {
            ps.setString(1, (String)value_search);
        } else if (value_search.getClass().equals(Integer.class)) {
            ps.setInt(1, (Integer) value_search);
        } else {
            throw new TypeDataUnknown("No se pudo obtener el tipo de dato del miembro value_search: " + value_search);
        }
        System.out.println(ps);

        ResultSet rs = ps.executeQuery();
        rs.next();

        return (Paciente) process.invokeProcessSelectData(rs);
    }


    public void delete(DNI dni) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "delete from Paciente where DNI = ?");

        ps.setString(1, dni.toString());
        ps.executeUpdate();
    }

}

package org.practica1debasesdedatosrelacionales.DAO;

import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.CargoIsNull;
import org.practica1debasesdedatosrelacionales.domain.Cita;
import org.practica1debasesdedatosrelacionales.domain.DNI;
import org.practica1debasesdedatosrelacionales.domain.Especialidad;
import org.practica1debasesdedatosrelacionales.domain.Paciente;
import org.practica1debasesdedatosrelacionales.util.R;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.practica1debasesdedatosrelacionales.DAO.EspecialidadDAO.load_especialidades;

public class CitaDAO {
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



    public void insert(Cita cita) throws SQLException {
        if (conn == null || conn.isClosed()) {
            throw new SQLException("Conexión a base de datos no establecida.");
        }
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Citas (DNI, numero_cita, fecha_cita, especialidad) VALUES (?, ?, ?, ?);");
        ps.setString(1, cita.getDni().toString());
        ps.setInt(2, cita.getNumero_cita());
        ps.setDate(3, cita.getFecha_cita());
        ps.setString(4, cita.getEspecialidad().toString());
        ps.executeUpdate();
    }

    public List<Cita> select(DNI dni) throws SQLException, IOException {
        if (conn == null || conn.isClosed()) {
            throw new SQLException("Conexión a base de datos no establecida.");
        }
        ArrayList<Cita> citas = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "select * from Citas where DNI = ?");

        ps.setString(1, dni.toString());
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {

            String especialidad_str = rs.getString(4);
            Especialidad especialidad = null;
            for (Especialidad espe : Especialidad.values()) {
                if (especialidad_str.equalsIgnoreCase(espe.toString())) {
                    especialidad = espe;
                }
            }

            citas.add(new Cita(
                    dni,
                    rs.getDate(3),
                    especialidad,
                    rs.getInt(2)
            ));

            if (especialidad == null) {
                throw new CargoIsNull("El cargo no se pudo identificar: " + especialidad_str);
            }
        }

        return citas;

    }


    public void delete(Paciente paciente, Cita cita) throws SQLException {
        if (conn == null || conn.isClosed()) {
            throw new SQLException("Conexión a base de datos no establecida.");
        }
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
        }
    }

    public int getMaxNumeroCita() throws SQLException {
        if (conn == null || conn.isClosed()) {
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
        }
    }

}

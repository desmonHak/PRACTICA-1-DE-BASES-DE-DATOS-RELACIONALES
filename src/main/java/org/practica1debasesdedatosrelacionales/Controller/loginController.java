package org.practica1debasesdedatosrelacionales.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.StageStyle;
import org.apache.commons.codec.digest.DigestUtils;
import org.practica1debasesdedatosrelacionales.DAO.PacienteDAO;
import org.practica1debasesdedatosrelacionales.DAO.ProcessResulSet;
import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.SQLDataNotFound;
import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.SQLUnknownException;
import org.practica1debasesdedatosrelacionales.InitWindows;
import org.practica1debasesdedatosrelacionales.domain.DNI;
import org.practica1debasesdedatosrelacionales.domain.Paciente;
import org.practica1debasesdedatosrelacionales.domain.SHA256;
import org.practica1debasesdedatosrelacionales.util.AlertsGlobal;
import org.practica1debasesdedatosrelacionales.util.R;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class loginController {
    public TextField fieldEmail;
    public PasswordField fieldPassword;

    public static Paciente paciente_login;
    private double xOffset = 0;
    private double yOffset = 0;

    public void onActionLogin(ActionEvent actionEvent) throws SQLException, IOException {

        ProcessResulSet process = (ResultSet rs)->{
            if (rs == null) {
                return null;
            } else {
                try {
                    SHA256 hash_pass = new SHA256(rs.getString(4), true);
                    System.out.println("Contraseña DB almacenada : " + hash_pass);
                    return new Paciente(
                            new DNI(rs.getString(1)),
                            rs.getString(2),
                            rs.getString(3),
                            hash_pass,
                            rs.getString(5),
                            rs.getString(6)
                    );
                } catch (SQLException e) {
                    String sqlState = e.getSQLState();
                    System.out.println("SQLState: " + sqlState);
                    if (sqlState.equals("S1000")) {
                        System.out.println("No se encontro estos datos, salida: " + e.getMessage());
                        throw new SQLDataNotFound("No se encontro estos datos" + e.getMessage());
                    } else {
                        System.out.println("Error SQL desconocido/no contemplado: " + e.getMessage());
                        throw new SQLUnknownException(e);
                    }
                }
            }
        };

        PacienteDAO conn = new PacienteDAO();
        conn.connect();

        String error_msg = "El usuario no existe.";
        try {
            // si se produce un error tipo SQLDataNotFound, entonces el correo no esta registrado
            Paciente paciente = conn.select("email", fieldEmail.getText(), process);

            conn.desconnect();

            if (paciente.getHashClass().check_password(fieldPassword.getText())) {
                // guardamos globalmente el paciente que ingreso
                paciente_login = paciente;

                FXMLLoader fxmlLoader = new FXMLLoader(R.getUI("Cuestionario.fxml"));
                AnchorPane panel = null;
                try {
                    panel = fxmlLoader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                /**
                 * agrego el comportamiento de poder desplazar la ventana,
                 * al quitar el marco nativo de la ventana, se debe programar
                 * el desplazamiento de la ventana
                 */
                panel.setOnMousePressed(event -> {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                });
                panel.setOnMouseDragged(event -> {
                    InitWindows.p_stage.setX(event.getScreenX() - xOffset);
                    InitWindows.p_stage.setY(event.getScreenY() - yOffset);
                });

                Scene scene_formulary = new Scene(panel);
                InitWindows.p_stage.setScene(scene_formulary);


            } else {
                error_msg = "Las credenciales no son correctas";
                System.out.println(paciente + "\n" + paciente.getHashClass());
                System.out.println(DigestUtils.sha256Hex(fieldPassword.getText()));
                throw new SQLDataNotFound(error_msg); // generar un error, las credenciales no son correctas
            }
        } catch (SQLDataNotFound e) {
            // mostrar el error
            AlertsGlobal.invokeAlert("Error", error_msg);
        }

    }


    public void onActionExit(ActionEvent actionEvent) {
        System.exit(0);
    }
}


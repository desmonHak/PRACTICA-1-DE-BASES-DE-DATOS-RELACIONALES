package org.practica1debasesdedatosrelacionales.Controller;

import com.mongodb.MongoSocketOpenException;
import com.mongodb.MongoTimeoutException;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.codec.digest.DigestUtils;
import org.practica1debasesdedatosrelacionales.DAO.ManagerConnections.*;
import org.practica1debasesdedatosrelacionales.DAO.PacienteDAO;
import org.practica1debasesdedatosrelacionales.DAO.PackageInterfaceCRUD.ProcessSelectData;
import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.SQLDataNotFound;
import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.SQLUnknownException;
import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.SingletonException;
import org.practica1debasesdedatosrelacionales.InitWindows;
import org.practica1debasesdedatosrelacionales.domain.DNI;
import org.practica1debasesdedatosrelacionales.domain.Paciente;
import org.practica1debasesdedatosrelacionales.domain.SHA256;
import org.practica1debasesdedatosrelacionales.util.AlertsGlobal;
import org.practica1debasesdedatosrelacionales.util.R;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class loginController {
    public TextField fieldEmail;
    public PasswordField fieldPassword;

    public static Paciente paciente_login;
    private double xOffset = 0;
    private double yOffset = 0;

    public void onActionLogin(ActionEvent actionEvent) throws SQLException, IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {

        try {
            PacienteDAO conn = new PacienteDAO(InitWindows.managerDBClass);

            String error_msg = "El usuario no existe.";
            try {
                Paciente paciente = null;
                try {
                    // si se produce un error tipo SQLDataNotFound, entonces el correo no esta registrado
                    paciente = conn.select(new ConditionsDB("email", ConditionsDBOperators.EQUALS, fieldEmail.getText()));
                } catch (NullPointerException e) {
                    throw new SingletonException(this.getClass(), "La base de datos no esta abierta");
                }


                /**
                 * obtenemos la clase SHA256 del paciente y accedemos al mectodo check para averiguar
                 * si la contraseña ingresada es la misma que la ya existente en la DB
                 */
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
            } catch (SQLDataNotFound | NoSuchElementException e) {
                // mostrar el error
                AlertsGlobal.invokeAlert("Error", error_msg);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } catch (SingletonException e) {
            AlertsGlobal.invokeAlert("Error", "La base de datos no esta abierta o se cerro sin previo aviso, abrala o vuelva a intentarlo");
        }

    }


    public void onActionExit(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void onActionConfig(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(R.getUI("conf_panel.fxml"));
        AnchorPane panel = null;
        try {
            panel = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
    }

    public void onActionSetSGDBToMySQL(ActionEvent actionEvent) throws SQLException, IOException {

        System.out.println("Intentando usar el gestor MySQL, SGDB actual: " + InitWindows.managerDBClass.getName());
        try {
            // cambiar el gestor de SGDB a MySQL
            ConnectionMySQLDBSingleton.getInstance();
            InitWindows.managerDBClass = ConnectionMySQLDBSingleton.class;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            if (e instanceof CommunicationsException) {
                AlertsGlobal.invokeAlert("Error",
                        String.format("La base de datos %s no esta abierta o se cerro sin previo aviso, abrala o vuelva a intentarlo, se mantendra el SGDB anterior si se selecciono anteriormente",
                                ConnectionMySQLDBSingleton.name_manager)
                );
            } else {
                e.printStackTrace();
            }
        }

    }

    public void onActionSetSGDBToMongoDB(ActionEvent actionEvent) {
        System.out.println("Intentando usar el gestor MongoDB, SGDB actual: " + InitWindows.managerDBClass.getName());
        try {
            // cambiar el gestor de SGDB a MongoDB
            ConnectionMongoDBSingleton.getInstance();
            InitWindows.managerDBClass = ConnectionMongoDBSingleton.class;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            if (e instanceof MongoSocketOpenException || e instanceof ConnectException || e instanceof MongoTimeoutException) {
                AlertsGlobal.invokeAlert("Error",
                        String.format("La base de datos %s no esta abierta o se cerro sin previo aviso, abrala o vuelva a intentarlo, se mantendra el SGDB anterior si se selecciono anteriormente",
                                ConnectionMongoDBSingleton.name_manager)
                );
            } else {
                e.printStackTrace();
            }
        }
    }
}


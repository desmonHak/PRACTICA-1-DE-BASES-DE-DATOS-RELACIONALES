package org.practica1debasesdedatosrelacionales;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.practica1debasesdedatosrelacionales.Controller.loginController;
import org.practica1debasesdedatosrelacionales.DAO.ManagerConnections.ConnectionMongoDBSingleton;
import org.practica1debasesdedatosrelacionales.util.R;

import java.io.IOException;
import java.net.URL;

public class InitWindows extends Application {
    public static Stage p_stage;
    public static Scene loginScene;

    // por defecto se usara mongo DB
    public static Class<?> managerDBClass = ConnectionMongoDBSingleton.class;

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws IOException {
        p_stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = R.getUI("login.fxml");
        assert url != null;
        System.out.println(url.getPath());
        fxmlLoader.setLocation(url);

        // quitamos el marco a la ventana
        stage.initStyle(StageStyle.UNDECORATED);
        AnchorPane panel = fxmlLoader.load();

        loginScene = new Scene(panel);

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
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        stage.setScene(loginScene);
        stage.show();
    }
}

package org.practica1debasesdedatosrelacionales.util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class AlertsGlobal {

    /**
     * Crea una ventana de emergencia sin devolver la instancia
     * @param title titulo de la ventana
     * @param description descripcion de la ventana
     */

    public static void invokeAlert(Alert. AlertType alertType, String title, String description) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(description);
        alert.show();
    }

    public static void invokeAlert(String title, String description) {
        invokeAlert(Alert.AlertType.ERROR, title, description);
    }

}

package org.DB.util;

import javafx.scene.control.Alert;

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

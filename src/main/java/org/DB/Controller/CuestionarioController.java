package org.DB.Controller;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import io.github.desmonhak.JColorsTerm;
import io.github.desmonhak.Log.Log;
import io.github.desmonhak.Log.TypeLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import org.DB.DAO.CitaDAO;
import org.DB.DAO.EspecialidadDAO;
import org.DB.DAO.ManagerConnections.ConditionsDB;
import org.DB.DAO.ManagerConnections.ConditionsDBOperators;
import org.DB.DAO.ManagerConnections.ConnectionMongoDBSingleton;
import org.DB.DAO.ManagerConnections.Hibernate.N_1.CitaHibernateDAO;
import org.DB.DAO.ManagerConnections.Hibernate.N_1.EspecialidadHibernateDAO;
import org.DB.DAO.ManagerConnections.Hibernate.N_1.HibernateDAOMethods;
import org.DB.DAO.ManagerConnections.Hibernate.N_1.PacienteHibernateDAO;
import org.DB.DAO.PacienteDAO;
import org.DB.Exceptions.DniException;
import org.DB.Exceptions.ExceptionsDB.SQLDataNotFound;
import org.DB.InitWindows;
import org.DB.domain.*;
import org.DB.util.AlertsGlobal;
import org.DB.util.R;
import org.DB.util.UInt32_t;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLNonTransientConnectionException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;

import static io.github.desmonhak.JColorsTerm.dump_buffer_cli;

public class CuestionarioController implements Initializable {

    public Canvas IDdrawCanvas;
    public Slider sliderZoom;
    public ComboBox<Especialidad> textFieldEspecialidad;
    public TableView<Cita> tableCitas;
    public TableColumn<Cita, Integer> colNCitas;
    public TableColumn<Cita, Date> colFecha;
    public TableColumn<Cita, Especialidad> colEspecialidad;
    public TextField textFieldTelf;
    public TextField textFieldDireccion;
    public TextField textFieldName;
    public TextField textFieldDNI;
    public TextField textFieldNumeroCita;
    public DatePicker fieldDateCita;

    Log log = new Log("log.txt", TypeLog.INFO);

    // zoom actual, solo actualizamos si el zoom cambio
    private int actual_zoom = 0;

    // almacena la cita seleccionada en la tabla
    private Cita cita_now;

    private CitaDAO citasDB = null;
    private double xOffset = 0;
    private double yOffset = 0;

    public CuestionarioController() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    }

    // solo repintar si el zoom fue cambiado
    void paint_id_draw(int blockSize) throws IOException {
        if (actual_zoom == blockSize) {
            return;
        } else {
            actual_zoom = blockSize;
            force_paint_id_draw(actual_zoom);
        }
    }

    /**
     * Solo se llamara para forzar el pintado, el resto de casos se llama a paint_id_draw
     * @param blockSize tamaño de los pixeles al hacer zoom
     */
    void force_paint_id_draw(int blockSize) throws IOException {
        String hash_user_data = loginController.paciente_login.get_hash_user_data().getHash();
        log.print_log("Hash data user: " + hash_user_data + "\n");

        GraphicsContext gc = IDdrawCanvas.getGraphicsContext2D();

        int len_hash = hash_user_data.length();
        int index_hash = 0;
        int width = (int) IDdrawCanvas.getWidth();
        int height = (int) IDdrawCanvas.getHeight();

        int width_reduced = width / blockSize;
        int height_reduced = height / blockSize;
        java.awt.Color[] paintedColors = new java.awt.Color[(int) (width_reduced * height_reduced)]; // array para almacenar colores
        java.awt.Color defaultColor = new java.awt.Color(0, 0, 0); // negro opaco
        Arrays.fill(paintedColors, defaultColor);

        for (int x = 0; x < width; x += blockSize) {
            for (int y = 0; y < height; y += blockSize) {
                // index para acceder a cada byte del hash:
                if (index_hash >= len_hash) {
                    index_hash = 0;
                }

                // generar la opacidad de forma pseudoaleatorias
                Random r = new Random(hash_user_data.charAt((index_hash) % len_hash));
                float opacity = ((float) r.nextInt(0, 100) / 100);
                if (opacity > 1.0) {
                    opacity %= 1.0F;
                }
                //System.out.println(opacity);

                // obtenemos un byte, un hash SHA256 tiene 256 bytes,
                // por lo que 256/8 = 32 bytes
                int unsignedByte = Byte.toUnsignedInt((byte)hash_user_data.charAt(index_hash));
                Color my_pixel = SHA256.generate_color_RGB(
                        UInt32_t.valueOf(unsignedByte),
                        UInt32_t.valueOf(hash_user_data.charAt(Math.abs((index_hash - 33 * 4) % len_hash))),
                        UInt32_t.valueOf(hash_user_data.charAt((index_hash + 33 * 4) % len_hash)),
                        UInt32_t.valueOf(hash_user_data.charAt(Math.abs((index_hash - 66 * 8) % len_hash))),
                        UInt32_t.valueOf(hash_user_data.charAt((index_hash + 66 * 8) % len_hash)),
                        UInt32_t.valueOf(hash_user_data.charAt(Math.abs((index_hash - 99 * 16) % len_hash))),
                        UInt32_t.valueOf(hash_user_data.charAt((index_hash + 99 * 16) % len_hash)),
                        opacity
                );


                // pintar los bloques de pixeles segun el color obtenido
                for (int dx = 0; dx < blockSize; dx++) {
                    for (int dy = 0; dy < blockSize; dy++) {

                        if (x + dx < width && y + dy < height) {

                            gc.getPixelWriter().setColor(x + dx, y + dy, my_pixel);

                        }
                    }
                }


                javafx.scene.paint.Color fx = my_pixel;
                int idx_x = Math.min(x / blockSize, width_reduced - 1);
                int idx_y = Math.min(y / blockSize, height_reduced - 1);
                paintedColors[idx_y * width_reduced + idx_x] = new java.awt.Color(
                        (float) fx.getRed(),
                        (float) fx.getGreen(),
                        (float) fx.getBlue(),
                        (float) fx.getOpacity()); // alpha);  // guardar color

                //if ((x + y) % 2 == 0) {
                //    gc.getPixelWriter().setColor(x, y, Color.RED);
                //} else {
                //    gc.getPixelWriter().setColor(x, y, Color.BLUE);
                //}

                // avanzamos un byte del hash
                index_hash++;
            }

        }
        JColorsTerm.dump_buffer_cli(paintedColors, width_reduced, height_reduced, false, "   ");
    }

    /**
     * Actualizamos la tabla con los nuevos datos de Base de datos
     */
    void updateTable() {

        try {
            try {
                ObservableList<Cita> citas = null;
                if (InitWindows.managerDBClass != HibernateDAOMethods.class) {
                    citas =
                            FXCollections.observableList(
                                    citasDB.select(
                                            loginController.paciente_login.getDni()));
                } else {
                    CitaHibernateDAO citaHibernateDAO = new CitaHibernateDAO();
                    citas = FXCollections.observableList(
                            citaHibernateDAO.getAll(
                                    loginController.paciente_login.getDni()
                            )
                    );
                }
                tableCitas.setItems(citas);
            } catch (SQLException e) {
                // error al obtener los datos
                throw new RuntimeException(e);
            }
        } catch (IOException |
                 IllegalAccessException e) {
            // error al conectar la base de datos
            throw new RuntimeException(e);
        }
        tableCitas.setRowFactory( tv -> {
            TableRow<Cita> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                // al hacer click sobre una fila de la tabla
                if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
                    cita_now = row.getItem(); // obtener la cita si la fila no esta vacia
                    try {
                        log.print_log(cita_now.toString() + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    // y cambiar los campos de texto con los datos de la cita seleccionada
                    textFieldNumeroCita.setText(cita_now.getNumero_cita().toString());
                    textFieldEspecialidad.setValue(cita_now.getEspecialidad());
                    fieldDateCita.setValue(cita_now.getFecha_cita().toLocalDate());
                }
            });
            return row ;
        });
    }

    /**
     * Actualizar los campos de la interfaz grafica, con los datos del
     * paciente registrado(loginController.paciente_login)
     *
     */
    void update_user_Gui_data() {
        textFieldTelf.setText(loginController.paciente_login.getTelefono());
        textFieldDireccion.setText(loginController.paciente_login.getDireccion());
        textFieldName.setText(loginController.paciente_login.getNombre());
        textFieldDNI.setText(loginController.paciente_login.getDni().toString());
        fieldDateCita.setValue(LocalDate.now());
        try {
            if (InitWindows.managerDBClass != HibernateDAOMethods.class) {
                int n_cita = citasDB.getMaxNumeroCita(); // primero hay que conectarse;
                // cambiar el campo del numero de cita, con el valor obtenido de la DB
                textFieldNumeroCita.setText(String.valueOf(n_cita));
            } else {
                int n_cita = CitaHibernateDAO.getMaxNumeroCita(); // primero hay que conectarse;
                // cambiar el campo del numero de cita, con el valor obtenido de la DB
                textFieldNumeroCita.setText(String.valueOf(n_cita));
            }
        } catch (SQLException | IllegalAccessException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Campo para inicializar los datos y comporamientos de la GUI
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (InitWindows.managerDBClass != HibernateDAOMethods.class) {
            try {
                citasDB = new CitaDAO(InitWindows.managerDBClass);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            log.clear_file();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /**
         * Pintar por primera vez el ID unico usando bloques de 8 pixeles
         */
        try {
            paint_id_draw(8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // obtener el valor del elemento Slider, convertirlo a un valor int para el calculo y forzar el repintado del ID
        sliderZoom.valueProperty().addListener((obs, oldVal, newVal) -> {
            try {
                log.print_log(String.valueOf((int)newVal.doubleValue()) + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                paint_id_draw((int)newVal.doubleValue());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // indicar como obtener los dato de la columna
        colEspecialidad.setCellValueFactory(
                new PropertyValueFactory<Cita, Especialidad>("especialidad"));
        colFecha.setCellValueFactory(
                new PropertyValueFactory<Cita, Date>("fecha_cita"));
        colNCitas.setCellValueFactory(
                new PropertyValueFactory<Cita, Integer>("numero_cita"));

        // actualizar la tabla con los datos del usuario
        updateTable();

        /**
         * obtener todas las especialidades de la base de datos
         * y añadirlas en el desplegable
         */
        EspecialidadDAO espe = null;
        if (InitWindows.managerDBClass != HibernateDAOMethods.class) {
            try {
                log.print_log(String.valueOf(InitWindows.managerDBClass) + "\n");
                espe = new EspecialidadDAO(InitWindows.managerDBClass);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                try {
                    log.print_log("No se pudo instanciar la clase EspecialidadDAO con " + InitWindows.managerDBClass + "\n");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                for (String especialidad : espe.load_especialidades()) {
                    textFieldEspecialidad.getItems().add(new Especialidad(especialidad));
                }
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            EspecialidadHibernateDAO especialidadHibernateDAO = new EspecialidadHibernateDAO();
            for (Especialidad especialidad : especialidadHibernateDAO.getAll()) {
                textFieldEspecialidad.getItems().add(especialidad);
            }
        }

        // actualizar los datos de la interfaz con los datos del usuario
        update_user_Gui_data();

        // obtengo la escena a traves del canvas
        IDdrawCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {

                // añadir el filtrado de teclas, aqui indicamos que hacer cuando pulsamos alguna tecla en especifico
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        try {
                            log.print_log("Usando el SGDB: " + InitWindows.managerDBClass + "\n");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        // si se presiona enter, buscamos al paciente a traves del DNI

                        PacienteDAO pacienteDB = null;
                        try {
                            if (InitWindows.managerDBClass != HibernateDAOMethods.class) {
                                pacienteDB = new PacienteDAO(InitWindows.managerDBClass);
                            }
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                        Paciente paciente = null;
                        try {
                            // obtener el DNI del usuario validando si es un DNI
                            DNI dni = new DNI(textFieldDNI.getText());
                            if (InitWindows.managerDBClass != HibernateDAOMethods.class) {
                                try {
                                    // obtenemos el paciente a traves del DNI si todo fue bieb
                                    paciente = pacienteDB.select(new ConditionsDB("dni", ConditionsDBOperators.EQUALS, dni.toString()));
                                } catch (SQLException | IllegalAccessException e) {
                                    if (e instanceof CommunicationsException || e instanceof SQLNonTransientConnectionException) {
                                        AlertsGlobal.invokeAlert("Error", "La conexion con el SGDB se cerro sin aviso.");
                                        return;
                                    } else {
                                        throw new RuntimeException(e);
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } else { // hiberante
                                PacienteHibernateDAO pacienteHibernateDAO = new PacienteHibernateDAO();
                                paciente = pacienteHibernateDAO.getById(dni);
                            }
                            if (paciente != null) {
                                // cambiamos el paciente si todo fue bien y actualizamos la GUI con los nuevos datos
                                loginController.paciente_login = paciente;
                                update_user_Gui_data(); // actualizar los datos de la interfaz grafica con los datos nuevos
                                updateTable(); // actualizar tabla
                                force_paint_id_draw(actual_zoom); // repintar el ID del usuario, forzandolo
                            } else {
                                // si paciente es null el paciente no se encontro
                                throw new SQLDataNotFound("Este paciente no existe");
                            }
                        } catch (DniException e) {
                            AlertsGlobal.invokeAlert("Error", e.getMessage());
                        } catch (SQLDataNotFound e) {
                            AlertsGlobal.invokeAlert("Error",
                                    "El paciente con DNI %s no existe".formatted(textFieldDNI.getText()));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        // consumimos el evento
                        event.consume();
                    } else if (event.getCode() == KeyCode.BACK_SPACE) {
                        // si se presiona la tecla borrar, se borra todos los datos de los campos
                        onActionBorrarDatosPaciente(null);
                        event.consume();
                    }
                });
            }
        });



    }

    public void onActionVolver(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(R.getUI("login.fxml"));
        Parent panel = loader.load();

        // obtengo los botones para cambiar el estado dependiendo de la seleccion
        RadioButton mongoButton = (RadioButton) panel.lookup("#mongoButton");
        RadioButton mysqlButton = (RadioButton) panel.lookup("#mysqlButton");

        if (InitWindows.managerDBClass == ConnectionMongoDBSingleton.class) {
            mongoButton.setSelected(true);
            mysqlButton.setSelected(false);
        } else {
            mysqlButton.setSelected(true);
            mongoButton.setSelected(false);
        }

        Scene loginScene = new Scene(panel);

        // me permite especificar como se movera la ventana
        panel.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        panel.setOnMouseDragged(event -> {
            InitWindows.p_stage.setX(event.getScreenX() - xOffset);
            InitWindows.p_stage.setY(event.getScreenY() - yOffset);
        });

        // volver a la ventana de login
        InitWindows.p_stage.setScene(loginScene);
        InitWindows.p_stage.show();
    }

    public void onActionAddCita(ActionEvent actionEvent) throws SQLException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int n_cita = 0;
        Especialidad especialidad = textFieldEspecialidad.getValue();

        if (especialidad == null) {
            AlertsGlobal.invokeAlert("Error", "Debe seleccionar una especialidad");
            return;
        }

        if (InitWindows.managerDBClass != HibernateDAOMethods.class) {
            try {
                // obtener el numero de cita + 1
                n_cita = citasDB.getMaxNumeroCita() + 1; // primero hay que conectarse;

                // crear una cita con los datos obtenidos
                Cita cita = new Cita(
                        new DNI(textFieldDNI.getText()),
                        Date.valueOf(fieldDateCita.getValue()),
                        especialidad,
                        n_cita
                );

                // insertar en la base de datos
                try {
                    citasDB.insert(cita);
                } catch (SQLIntegrityConstraintViolationException _) {
                }
            } catch (DniException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                AlertsGlobal.invokeAlert("Error", e.getMessage());
            } catch (SQLNonTransientConnectionException e) {
                AlertsGlobal.invokeAlert("Error", "La conexion con la base de datos se cerro inesperadamente");
            }
        } else {
            n_cita = CitaHibernateDAO.getMaxNumeroCita() + 1; // primero hay que conectarse;
            // crear una cita con los datos obtenidos
            Cita cita = new Cita(
                    new DNI(textFieldDNI.getText()),
                    Date.valueOf(fieldDateCita.getValue()),
                    especialidad,
                    n_cita
            );
            CitaHibernateDAO citaHibernateDAO = new CitaHibernateDAO();
            citaHibernateDAO.save(cita);

        }

        textFieldNumeroCita.setText(String.valueOf(n_cita));
        // actualizar la tabla de la DB para mostrar el nuevo campo ingresada
        updateTable();
    }

    public void onActionDeleteCita(ActionEvent actionEvent) throws SQLException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // si no escogio una cita, mostrar error
        if (cita_now != null) {
            // eliminar la cita seleccionado
            if (InitWindows.managerDBClass != HibernateDAOMethods.class) {
                try {
                    citasDB.delete(cita_now);
                } catch (SQLNonTransientConnectionException e) {
                    AlertsGlobal.invokeAlert("Error", "La conexion con la base de datos se cerro inesperadamente");
                }
            } else {
                CitaHibernateDAO citaHibernateDAO = new CitaHibernateDAO();
                citaHibernateDAO.deleteById(cita_now.getNumero_cita());
            }

            updateTable();
        }  else {
            AlertsGlobal.invokeAlert("Error", "Debe selecionar una cita en la tabla");
        }
    }

    public void onActionModificarCita(ActionEvent actionEvent) throws SQLException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        /**
         * si la cita no fue seleccionada mostrar error
         */
        if (cita_now != null) {

            cita_now.setEspecialidad(textFieldEspecialidad.getValue());
            cita_now.setFecha_cita(Date.valueOf(fieldDateCita.getValue()));

            if (InitWindows.managerDBClass != HibernateDAOMethods.class) {
                // actualizar la cita
                try {
                    citasDB.update(cita_now);
                } catch (SQLNonTransientConnectionException e) {
                    AlertsGlobal.invokeAlert("Error", "La conexion con la base de datos se cerro inesperadamente");
                }
            } else {
                CitaHibernateDAO citaHibernateDAO = new CitaHibernateDAO();
                citaHibernateDAO.update(cita_now);
            }

            // actualizar tabla
            updateTable();
        } else {
            AlertsGlobal.invokeAlert("Error", "Debe seleccionar una cita");
        }
    }

    // borrar todos los campos de texto
    public void onActionBorrarDatosPaciente(ActionEvent actionEvent) {
        textFieldTelf.setText("");
        textFieldDireccion.setText("");
        textFieldName.setText("");
        textFieldDNI.setText("");
    }
}

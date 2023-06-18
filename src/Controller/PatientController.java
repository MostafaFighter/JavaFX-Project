package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class PatientController extends Application implements Initializable{
    
    
    String DB_URL = "jdbc:mysql://localhost:3306/clinic_appointments";
    String DB_PASS = "";
    String DB_USER = "root";


    @FXML
    private TableColumn<Appointment, String> DateColumn;

    @FXML
    private TableColumn<Appointment, String> DayColumn;

    @FXML
    private TableColumn<Appointment, Integer> IDColumn;

    @FXML
    private TableColumn<Appointment, String> StatusColumn;

    @FXML
    private TableColumn<Appointment, String> TimeColumn;

    @FXML
    private Button btnCreateAppointment;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnShowBookedAppointments;

    @FXML
    private Button btnShowFreeAppointments;

    @FXML
    private Button btnUpdate;

    @FXML
    private TableView<Appointment> table;
    
    @FXML
    void Book(ActionEvent event) {
        Appointment appointment = table.getSelectionModel().getSelectedItem();
        
        if (appointment != null) {
            if (appointment.getStatus().equalsIgnoreCase("f")) {
                
                try {
                    Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                    
                    String updateQuery = "UPDATE appointments SET status = 'b' WHERE id = ?";
                    PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
                    updateStatement.setInt(1, appointment.getId());
                    
                    int rowsAffected = updateStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        showAlert(AlertType.INFORMATION, "Appointment Booked", "Appointment booked successfully.");

                        populateTable();
                    } else {
                        showAlert(AlertType.ERROR, "Booking Failed", "Failed to book the appointment.");
                    }
                    
                    updateStatement.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                showAlert(AlertType.WARNING, "Invalid Appointment", "The selected appointment is already booked or not available for booking.");
            }
        } else {
            showAlert(AlertType.WARNING, "No Appointment Selected", "Please select an appointment to book.");
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    
    @FXML
    void Logout(ActionEvent event) {
        Stage currentStage = (Stage) btnLogout.getScene().getWindow();
            try{
                Parent root = FXMLLoader.load(getClass().getResource("../View/Login.fxml"));
                Scene newScene = new Scene(root);
                currentStage.setScene(newScene);
                currentStage.show();
            }catch (IOException e) {
                e.printStackTrace();
            }
    }
    
    private void populateTable() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            String query = "SELECT * FROM appointments JOIN booked_appointments ON appointments.id = booked_appointments.appointment_id JOIN users ON booked_appointments.user_id = users.id";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String date = resultSet.getString("appointment_date");
                String day = resultSet.getString("appointment_day");
                String time = resultSet.getString("appointment_time");
                String status = resultSet.getString("status");

                Appointment appointment = new Appointment(id, date, day, time, status);
                appointments.add(appointment);
            }

            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        IDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        DayColumn.setCellValueFactory(new PropertyValueFactory<>("day"));
        TimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        StatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.setItems(appointments);
    }
        
    @FXML
    void ShowBookedAppointments(ActionEvent event) {
        populateTable();
        ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();

        for (Appointment appointment : table.getItems()) {
            if (appointment.getStatus().equalsIgnoreCase("b") || appointment.getStatus().equalsIgnoreCase("booked")) {
                filteredAppointments.add(appointment);
            }
        }

        table.setItems(filteredAppointments);
    }

    @FXML
    void ShowFreeAppointments(ActionEvent event) {
        populateTable();
        ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();

        for (Appointment appointment : table.getItems()) {
            if (appointment.getStatus().equalsIgnoreCase("f") || appointment.getStatus().equalsIgnoreCase("free")) {
                filteredAppointments.add(appointment);
            }
        }

        table.setItems(filteredAppointments);
    }
        
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTable();
    }
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../View/PatientDashboard.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

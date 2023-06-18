package Controller;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class DoctorAppointmentsController extends Application implements Initializable{
    
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
    private TableColumn<Appointment, String> PatientIDColumn;

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
    private Button btnShowBookedAppointments;

    @FXML
    private Button btnShowFreeAppointments;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnReset;

    @FXML
    private TableView<Appointment> table;

    @FXML
    private TextField txtDate;

    @FXML
    private TextField txtDay;

    @FXML
    private TextField txtPatientID;

    @FXML
    private TextField txtSearch;

    @FXML
    private TextField txtStatus;

    @FXML
    private TextField txtTime;
    @FXML
    private Button btnBack;
    
    @FXML
    void Back(ActionEvent event) {
        Stage currentStage = (Stage) btnBack.getScene().getWindow();
            try{
                Parent root = FXMLLoader.load(getClass().getResource("../View/DoctorDashboard.fxml"));
                Scene newScene = new Scene(root);
                currentStage.setScene(newScene);
                currentStage.show();
            }catch (IOException e) {
                e.printStackTrace();
            }
    }

    @FXML
    void CreateAppointment(ActionEvent event) {
        String date, day, time, status;
        int patientID;

        patientID = Integer.parseInt(txtPatientID.getText());
        date = txtDate.getText();
        day = txtDay.getText();
        time = txtTime.getText();
        status = txtStatus.getText();

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            String query = "INSERT INTO appointments (appointment_date, appointment_day, appointment_time, status) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, date);
            statement.setString(2, day);
            statement.setString(3, time);
            statement.setString(4, status);

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            int appointmentId = -1;
            if (generatedKeys.next()) {
                appointmentId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Failed to get the ID of the created appointment.");
            }

            String userQuery = "SELECT * FROM users WHERE id = ? LIMIT 1";
            PreparedStatement stmtGetUserId = conn.prepareStatement(userQuery);
            stmtGetUserId.setInt(1, patientID);

            ResultSet rs = stmtGetUserId.executeQuery();
            int userId = -1;
            if (rs.next()) {
                userId = rs.getInt("id");
            } else {
                throw new SQLException("User with ID " + patientID + " not found.");
            }

            if (patientID == userId) {
                String querySet = "INSERT INTO booked_appointments (appointment_id, user_id, status, doctor_comment) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(querySet);
                stmt.setInt(1, appointmentId);
                stmt.setInt(2, userId);
                stmt.setString(3, status);
                stmt.setString(4, "");

                stmt.executeUpdate();
            }

            statement.close();
            stmtGetUserId.close();
            conn.close();
            txtPatientID.setText("");
            txtDate.setText("");
            txtDay.setText("");
            txtTime.setText("");
            txtStatus.setText("");

            populateTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void Delete(ActionEvent event) {
        Appointment selectedAppointment = table.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            int appointment_id = selectedAppointment.getId();
            try{
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                String query = "DELETE FROM appointments WHERE id = ?";
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setInt(1, appointment_id);

                statement.executeUpdate();
                statement.close();
                conn.close();
                System.out.println("Deleted Successfully!");
                populateTable();
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    void Search(ActionEvent event) {
        populateTable();
        String searchQuery = txtSearch.getText().trim().toLowerCase();
        ObservableList<Appointment> searchResults = FXCollections.observableArrayList();

        if (searchQuery.isEmpty()) {
            populateTable();
        } else {
            for (Appointment appointment : table.getItems()) {
                if (appointment.getPatientName().toLowerCase().contains(searchQuery)) {
                    searchResults.add(appointment);
                }
            }
            table.setItems(searchResults);
        }
    }

    @FXML
    void Reset(ActionEvent event) {
        populateTable();
    }

    @FXML
    void Update(ActionEvent event) {
        Appointment selectedAppointment = table.getSelectionModel().getSelectedItem();
        String Date, Day, Time, Status;
        int patientID;

        if (selectedAppointment != null) {
            try {
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                int appointmentID = selectedAppointment.getId();

                        
                patientID = Integer.parseInt(txtPatientID.getText());
                Date = txtDate.getText();
                Day = txtDay.getText();
                Time = txtTime.getText();
                Status = txtStatus.getText();
                String query = "UPDATE appointments SET appointment_date = ?, appointment_day = ?, appointment_time = ?, status = ? WHERE id = ?";
                PreparedStatement statement = conn.prepareStatement(query);
                
                statement.setString(1, Date);
                statement.setString(2, Day);
                statement.setString(3, Time);
                statement.setString(4, Status);
                statement.setInt(5, appointmentID);
                
                statement.executeUpdate();
                System.out.println("Appointment data has been updated successfully!");

                txtPatientID.setText("");
                txtDate.setText("");
                txtDay.setText("");
                txtTime.setText("");
                txtStatus.setText("");
                
                statement.close();
                conn.close();
                
                populateTable();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

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




    private void populateTable() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            String query = "SELECT appointments.id, appointments.appointment_date, appointments.appointment_day, appointments.appointment_time, appointments.status, users.firstname FROM appointments JOIN booked_appointments ON appointments.id = booked_appointments.appointment_id JOIN users ON booked_appointments.user_id = users.id";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String date = resultSet.getString("appointment_date");
                String day = resultSet.getString("appointment_day");
                String time = resultSet.getString("appointment_time");
                String status = resultSet.getString("status");
                String patientName = resultSet.getString("firstname");

                Appointment appointment = new Appointment(id, date, day, time, status);
                appointment.setPatientName(patientName);
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
        PatientIDColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
        cellData.getValue().getPatientName()));


        table.setItems(appointments);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTable();
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Appointment selectedAppointment = table.getSelectionModel().getSelectedItem();
                if (selectedAppointment != null) {
                    int appointmentId = selectedAppointment.getId();
                    try {
                        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                        String queryGetData = "SELECT * FROM appointments WHERE id = ?";
                        PreparedStatement state = conn.prepareStatement(queryGetData);
                        
                        state.setInt(1, appointmentId);
                        ResultSet resultSet = state.executeQuery();
                        if (resultSet.next()) {
                            String date = resultSet.getString("appointment_date");
                            txtDate.setText(date);
                            String day = resultSet.getString("appointment_day");
                            txtDay.setText(day);
                            String time = resultSet.getString("appointment_time");
                            txtTime.setText(time);
                            String status = resultSet.getString("status");
                            txtStatus.setText(status);
                        }
                        resultSet.close();
                        state.close();
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../View/ManageAppointment.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

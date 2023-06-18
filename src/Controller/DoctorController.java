package Controller;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ResourceBundle;

import Model.DatabaseConnection;
import javafx.application.Application;
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

public class DoctorController  extends Application implements Initializable{

    String DB_URL = "jdbc:mysql://localhost:3306/clinic_appointments";
    String DB_PASS = "";
    String DB_USER = "root";

    @FXML
    private TableColumn<Patient, Integer> IDColumn;

    @FXML
    private TableColumn<Patient, Integer> AgeColumn;

    @FXML
    private TableColumn<Patient, String> EmailColumn;

    @FXML
    private TableColumn<Patient, String> FirstNameColumn;

    @FXML
    private TableColumn<Patient, String> GenderColumn;

    @FXML
    private TableColumn<Patient, String> LastNameColumn;

    @FXML
    private TableColumn<Patient, Integer> PhoneColumn;

    @FXML
    private Button btnManageAppointment;

    @FXML
    private Button btnManagePatient;

    @FXML
    private Button btnSearch;
    
    @FXML
    private Button btnReset;

    @FXML
    private TableView<Patient> table;

    @FXML
    private TextField txtSearch;
        
    @FXML
    private Button btnLogout;
    
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

    @FXML
    void ManageAppointment(ActionEvent event) {
        Stage currentStage = (Stage) btnManageAppointment.getScene().getWindow();
            try{
                Parent root = FXMLLoader.load(getClass().getResource("../View/ManageAppointment.fxml"));
                Scene newScene = new Scene(root);
                currentStage.setScene(newScene);
                currentStage.show();
            }catch (IOException e) {
                e.printStackTrace();
            }
    }

    @FXML
    void ManagePatient(ActionEvent event) {
        Stage currentStage = (Stage) btnManagePatient.getScene().getWindow();
            try{
                Parent root = FXMLLoader.load(getClass().getResource("../View/ManagePatient.fxml"));
                Scene newScene = new Scene(root);
                currentStage.setScene(newScene);
                currentStage.show();
            }catch (IOException e) {
                e.printStackTrace();
            }
    }

    @FXML
    void Search(ActionEvent event) {
        populateTable();
        String searchQuery = txtSearch.getText().trim().toLowerCase();
        ObservableList<Patient> searchResults = FXCollections.observableArrayList();

        if (searchQuery.isEmpty()) {
            populateTable();
        } else {
            for (Patient patient : table.getItems()) {
                if (patient.getFirstName().toLowerCase().contains(searchQuery)) {
                    searchResults.add(patient);
                }
            }
            table.setItems(searchResults);
        }
    }
    @FXML
    void Reset(ActionEvent event) {
        populateTable();
    }
    private void populateTable() {
        ObservableList<Patient> patients = FXCollections.observableArrayList();

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            String query = "SELECT * FROM users WHERE role = 'p'";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            ResultSetMetaData meta = resultSet.getMetaData();
            int count = meta.getColumnCount();
            System.out.println(count);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                int age = resultSet.getInt("age");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String gender = resultSet.getString("gender");

                Patient patient = new Patient(id,firstName, lastName, age, email, phone, gender);
                patients.add(patient);
            }

            for (int i = 1; i <= count; i++) {
                String name = meta.getColumnName(i);
                System.out.println("Column Name: " + name);
            }
            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        IDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        FirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        LastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        AgeColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        EmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        PhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        GenderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        // table.getColumns().addAll(FirstNameColumn, LastNameColumn, AgeColumn, EmailColumn, PhoneColumn, GenderColumn);

        table.setItems(patients);
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
        Parent root = FXMLLoader.load(getClass().getResource("../View/DoctorDashboard.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

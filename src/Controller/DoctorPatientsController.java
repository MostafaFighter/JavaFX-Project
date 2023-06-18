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

public class DoctorPatientsController extends Application implements Initializable{
    
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
    private Button btnCreatePatient;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnUpdate;

    @FXML
    private TableView<Patient> table;

    @FXML
    private TextField txtAge;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtGender;

    @FXML
    private TextField txtLastName;

    @FXML
    private TextField txtPassword;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtSearch;

    @FXML
    private Button btnReset;

    @FXML
    private TextField txtUsername;
        
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
    void Create(ActionEvent event) {
        String username, firstName, lastName, email, gender,password;
        int age, phone;

        username = txtUsername.getText();
        firstName = txtFirstName.getText();
        lastName = txtLastName.getText();
        email = txtEmail.getText();
        gender = txtGender.getText();
        password = txtPassword.getText();
        age = Integer.parseInt(txtAge.getText());
        phone = Integer.parseInt(txtPhone.getText());

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            
            String query = "INSERT INTO users (username ,password, firstname, lastname, email, gender, age, phone, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'p')";
            PreparedStatement statement = conn.prepareStatement(query);
            
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, firstName);
            statement.setString(4, lastName);
            statement.setString(5, email);
            statement.setString(6, gender);
            statement.setInt(7, age);
            statement.setInt(8, phone);
            
            statement.executeUpdate();
            System.out.println("Patient created successfully");
            
            statement.close();
            conn.close();

            txtPassword.setText("");
            txtUsername.setText("");
            txtFirstName.setText("");
            txtLastName.setText("");
            txtEmail.setText("");
            txtGender.setText("");
            txtAge.setText("");
            txtPhone.setText("");
            txtUsername.requestFocus();
            
            populateTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Delete(ActionEvent event) {
        Patient selectedPatient = table.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            int user_id = selectedPatient.getId();
            try{
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                String query = "DELETE FROM users WHERE id = ?";
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setInt(1, user_id);

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
    
    @FXML
    void Update(ActionEvent event) {
        Patient selectedPatient = table.getSelectionModel().getSelectedItem();
        int userId, age, phone;
        String username,password, firstName, lastName, email, gender;

        if (selectedPatient != null) {
            try {
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                int patientId = selectedPatient.getId();

                username = txtUsername.getText();
                password = txtPassword.getText();
                firstName = txtFirstName.getText();
                lastName = txtLastName.getText();
                email = txtEmail.getText();
                gender = txtGender.getText();
                age = Integer.parseInt(txtAge.getText());
                phone = Integer.parseInt(txtPhone.getText());
                
                String query = "UPDATE users SET username = ?,password = ?, firstname = ?, lastname = ?, age = ?, email = ?, phone = ?,gender = ?, role = 'p' WHERE id = ?";
                PreparedStatement statement = conn.prepareStatement(query);
                
                statement.setString(1, username);
                statement.setString(2, password);
                statement.setString(3, firstName);
                statement.setString(4, lastName);
                statement.setInt(5, age);
                statement.setString(6, email);
                statement.setInt(7, phone);
                statement.setString(8, gender);
                statement.setInt(9, patientId);
                
                statement.executeUpdate();
                System.out.println("Patient data has been updated successfully!");

                txtPassword.setText("");
                txtUsername.setText("");
                txtFirstName.setText("");
                txtLastName.setText("");
                txtEmail.setText("");
                txtGender.setText("");
                txtAge.setText("");
                txtPhone.setText("");
                
                statement.close();
                conn.close();
                
                populateTable();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private void populateTable() {

        ObservableList<Patient> patients = FXCollections.observableArrayList();
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/clinic_appointments", "root", "");

            String query = "SELECT * FROM users WHERE role = 'p'";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

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

        table.setItems(patients);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTable();

        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Patient selectedPatient = table.getSelectionModel().getSelectedItem();
                if (selectedPatient != null) {
                    int PatientId = selectedPatient.getId();
                    try {
                        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                        String queryGetData = "SELECT * FROM users WHERE id = ?";
                        PreparedStatement state = conn.prepareStatement(queryGetData);
                        
                        state.setInt(1, PatientId);
                        ResultSet resultSet = state.executeQuery();
                        if (resultSet.next()) {
                            String username = resultSet.getString("username");
                            txtUsername.setText(username);
                            String password = resultSet.getString("password");
                            txtPassword.setText(password);
                            String FirstName = resultSet.getString("firstname");
                            txtFirstName.setText(FirstName);
                            String LastName = resultSet.getString("lastname");
                            txtLastName.setText(LastName);
                            int age = resultSet.getInt("age");
                            txtAge.setText(String.valueOf(age));
                            String Email = resultSet.getString("email");
                            txtEmail.setText(Email);
                            int phone = resultSet.getInt("phone");
                            txtPhone.setText(String.valueOf(phone));
                            String gender = resultSet.getString("gender");
                            txtGender.setText(gender);
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
        Parent root = FXMLLoader.load(getClass().getResource("../View/ManagePatient.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

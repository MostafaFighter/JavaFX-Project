package Controller.LoginSystem;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Register extends Application{
        
    String DB_URL = "jdbc:mysql://localhost:3306/clinic_appointments";
    String DB_PASS = "";
    String DB_USER = "root";

    
    @FXML
    private Button btnCreateAccount;

    @FXML
    private Button btnLoginRedirect;

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
    private PasswordField txtPassword;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtUsername;

    @FXML
    void Redirect(ActionEvent event) {
        Stage currentStage = (Stage) btnLoginRedirect.getScene().getWindow();
            try{
                Parent root = FXMLLoader.load(getClass().getResource("../../View/Login.fxml"));
                Scene newScene = new Scene(root);
                currentStage.setScene(newScene);
                currentStage.show();
            }catch (IOException e) {
                e.printStackTrace();
            }
    }

    @FXML
    void CreateAccount(ActionEvent event) {
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
            System.out.println("Account created successfully");
            showRegistrationSuccessAlert();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void showRegistrationSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration Successful");
        alert.setHeaderText(null);
        alert.setContentText("Account created successfully!");
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../../View/Register.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

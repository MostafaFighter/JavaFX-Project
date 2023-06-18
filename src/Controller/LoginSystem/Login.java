package Controller.LoginSystem;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Login extends Application{

    String DB_URL = "jdbc:mysql://localhost:3306/clinic_appointments";
    String DB_PASS = "";
    String DB_USER = "root";

    @FXML
    private Button btnLogin;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtUsername;
        
    @FXML
    void Create(ActionEvent event) {
        Stage currentStage = (Stage) btnLogin.getScene().getWindow();
            try{
                Parent root = FXMLLoader.load(getClass().getResource("../../View/Register.fxml"));
                Scene newScene = new Scene(root);
                currentStage.setScene(newScene);
                currentStage.show();
            }catch (IOException e) {
                e.printStackTrace();
            }
    }

    @FXML
    void Login(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        char validation = authenticateUser(username, password);
        if (validation == 'a') {
            System.out.println("Welcome Admin!");
            Stage currentStage = (Stage) btnLogin.getScene().getWindow();
            try{
                Parent root = FXMLLoader.load(getClass().getResource("../../View/DoctorDashboard.fxml"));
                Scene newScene = new Scene(root);
                currentStage.setScene(newScene);
                currentStage.show();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }else if(validation == 'p'){
            System.out.println("Welcome Patient!");
            Stage currentStage = (Stage) btnLogin.getScene().getWindow();
            try{
                Parent root = FXMLLoader.load(getClass().getResource("../../View/PatientDashboard.fxml"));
                Scene newScene = new Scene(root);
                currentStage.setScene(newScene);
                currentStage.show();
            }catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid username or password. Please try again.");
            showLoginErrorAlert();
        }
    }
    private char authenticateUser(String username, String password) {
        char LoginType = 'c';
        try{
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            String query = "select * from users";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                if(username.equalsIgnoreCase(rs.getString("username")) &&
                password.equals(rs.getString("password"))){
                    LoginType = rs.getString("role").charAt(0);
                    break;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return LoginType;
    }
    private void showLoginErrorAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText("Invalid username or password");
        alert.setContentText("Please enter a valid username and password.");

        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../../View/Login.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

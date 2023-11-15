package Controller;

import db.DBConnector;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPageController {
    public AnchorPane root;
    public PasswordField txtPassword;
    public TextField txtUserName;
    public static String logingUserName;
    public static String logingUserID;

    public void lblCreateAccOnClick(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("../view/NewAccForm.fxml"));
        Scene scene = new Scene(parent);
        Stage primaryStage = (Stage) root.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Register Form");
        primaryStage.centerOnScreen();

    }
    public void btnLoginOnAction(ActionEvent actionEvent) {
        if (txtUserName.getText().trim().isEmpty()){
            txtUserName.requestFocus();
        } else if (txtPassword.getText().trim().isEmpty()) {
            txtPassword.requestFocus();
        }else {
            whenLogin();
        }
    }
    public void txtPasswordOnAction(ActionEvent actionEvent) {

            whenLogin();

    }
    public void whenLogin(){
        String userName = txtUserName.getText();
        String password = txtPassword.getText();
        Connection connection = DBConnector.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from user where userName = ? and password = ? ");
            preparedStatement.setObject(1,userName);
            preparedStatement.setObject(2,password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                logingUserName = resultSet.getString(2);
                logingUserID = resultSet.getString(1);
                Parent parent = FXMLLoader.load(getClass().getResource("../view/ToDoLIstForm.fxml"));
                Scene scene = new Scene(parent);
                Stage primaryStage = (Stage) root.getScene().getWindow();
                primaryStage.setScene(scene);
                primaryStage.setTitle("To Do List");
                primaryStage.centerOnScreen();

            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR,"Your password not correct...");
                alert.showAndWait();
                txtPassword.clear();
                txtUserName.clear();
                txtUserName.requestFocus();
            }
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

    public void lblForgetPasswordOnMouseClicked(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("../view/ForgetPasswordForm.fxml"));
        Scene scene = new Scene(parent);
        Stage primaryStage = (Stage) root.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Forget Password Form");
        primaryStage.centerOnScreen();

    }
}

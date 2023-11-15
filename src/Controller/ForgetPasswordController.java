package Controller;

import db.DBConnector;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class ForgetPasswordController {

    public TextField txtEmail;
    public PasswordField txtConfirmPassword;
    public PasswordField txtNewPassword;
    public AnchorPane root;
    public Label lblPasswordNotSame;

    public void initialize(){

        setLabelVisible(false);
    }
    public void btnSubmitOnAction(ActionEvent actionEvent) throws IOException {

        if (txtEmail.getText().trim().isEmpty()){
            txtEmail.requestFocus();
        } else if (txtNewPassword.getText().trim().isEmpty()) {
            txtNewPassword.requestFocus();
        } else if (txtConfirmPassword.getText().trim().isEmpty()) {
            txtConfirmPassword.requestFocus();
        }else {
            String txtEmailText = txtEmail.getText();
            String newPassword = txtNewPassword.getText();
            String confirmPassword = txtConfirmPassword.getText();
            boolean equals = newPassword.equals(confirmPassword);
            if(equals){
                setBorderColor("transparent");
                setLabelVisible(false);
            }else {
                setBorderColor("red");
                txtConfirmPassword.requestFocus();
                setLabelVisible(true);
            }
            Connection connection = DBConnector.getInstance().getConnection();
            try {
                PreparedStatement preparedStatement1 = connection.prepareStatement("insert into forgetpw value (?,?)");
                preparedStatement1.setObject(1,txtEmailText);
                preparedStatement1.setObject(2,newPassword);
                preparedStatement1.executeUpdate();
                PreparedStatement preparedStatement = connection.prepareStatement("update user set password = ? where Email = ?");
                preparedStatement.setObject(1,newPassword);
                preparedStatement.setObject(2,txtEmailText);
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Password Changed..");
            alert.showAndWait();
            Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginPageForm.fxml"));
            Scene scene = new Scene(parent);
            Stage primarystage = (Stage) root.getScene().getWindow();
            primarystage.setScene(scene);
            primarystage.centerOnScreen();
            primarystage.setTitle("Login Form");

        }
    }
    public void setBorderColor(String color){
        txtNewPassword.setStyle("-fx-border-color: "+color);
        txtConfirmPassword.setStyle("-fx-border-color: "+color);
    }
    public void setLabelVisible(boolean isVisible){

        lblPasswordNotSame.setVisible(isVisible);
    }

}

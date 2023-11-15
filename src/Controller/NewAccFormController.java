package Controller;

import db.DBConnector;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;

public class NewAccFormController {
    public AnchorPane root;
    public PasswordField txtNewPassward;
    public PasswordField txtConfirm;
    public Label lblPasswordNotSame;
    public TextField txtUserName;
    public TextField txtEmail;
    public Button btnRegister;
    public Label lblUID;

    public void initialize(){
        setLabelVisible(false);
        setDisable(true);
    }
    public void btnRegisterOnAction(ActionEvent actionEvent)  {

        if (txtUserName.getText().trim().isEmpty()){
            txtUserName.requestFocus();
        } else if (txtEmail.getText().trim().isEmpty()) {
            txtEmail.requestFocus();
        } else if (txtNewPassward.getText().trim().isEmpty()) {
            txtNewPassward.requestFocus();
        }else if (txtConfirm.getText().trim().isEmpty()){
            txtConfirm.requestFocus();
        }
        else {
            String newPassword = txtNewPassward.getText();
            String confirmPassword = txtConfirm.getText();
            boolean equals = newPassword.equals(confirmPassword);
            if(equals){
                setBorderColor("transparent");
                setLabelVisible(false);
                register();
            }else {
                setBorderColor("red");
                txtNewPassward.requestFocus();
                setLabelVisible(true);
            }

        }
    }
    public void setBorderColor(String color){
        txtNewPassward.setStyle("-fx-border-color: "+color);
        txtConfirm.setStyle("-fx-border-color: "+color);

    }
    public void setLabelVisible(boolean isVisible){

        lblPasswordNotSame.setVisible(isVisible);
    }
    public void btnAddUserOnAction(ActionEvent actionEvent) {
        setDisable(false);
        autoGenerateID();
        Connection connection = DBConnector.getInstance().getConnection();
        System.out.println(connection);

    }
    public void setDisable(boolean isDisable){
        btnRegister.setDisable(isDisable);
        txtUserName.setDisable(isDisable);
        txtEmail.setDisable(isDisable);
        txtNewPassward.setDisable(isDisable);
        txtConfirm.setDisable(isDisable);

    }
    public void autoGenerateID(){
        Connection connection = DBConnector.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from user order by id desc limit 1");
            boolean isExist = resultSet.next();
            if (isExist){
                String userId = resultSet.getString(1);
                userId = userId.substring(1);
                int intID = Integer.parseInt(userId);
                intID++;
                if (intID<10){
                    lblUID.setText("U00"+intID);
                }else if(intID<100) {
                    lblUID.setText("U0"+intID);
                }else {
                    lblUID.setText("U"+intID);
                }
            }else {
                lblUID.setText("U001");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void register(){
        Connection connection = DBConnector.getInstance().getConnection();
        String id = lblUID.getText();
        String userName = txtUserName.getText();
        String email = txtEmail.getText();
        String password = txtConfirm.getText();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into user value (?,?,?,?)");
            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,userName);
            preparedStatement.setObject(3,email);
            preparedStatement.setObject(4,password);
            int i = preparedStatement.executeUpdate();
            if (i!=0){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Account Created..");
                alert.showAndWait();
                Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginPageForm.fxml"));
                Scene scene = new Scene(parent);
                Stage primarystage = (Stage) root.getScene().getWindow();
                primarystage.setScene(scene);
                primarystage.centerOnScreen();
                primarystage.setTitle("Login Form");
            }
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }
}

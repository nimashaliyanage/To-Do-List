package Controller;

import db.DBConnector;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tmodel.ToDoTM;
import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class ToDoListController {
    public Label lblUserID;
    public Label lblWelcome;
    public AnchorPane root;
    public Pane subroot;
    public TextField txtTaskName;
    public ListView<ToDoTM> lstToDo;
    public TextField txtDeleteUpdate;
    public Button btnDelete;
    public Button btnUpdate;
    public  String selectedID = null;

    public void initialize(){
        lblUserID.setText(LoginPageController.logingUserID);
        lblWelcome.setText("Hello "+LoginPageController.logingUserName+" welcome to To Do List.");
        subroot.setVisible(false);
        loadList();
        isDisable(true);
        lstToDo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTM>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTM> observable, ToDoTM oldValue, ToDoTM newValue) {
                isDisable(false);
                subroot.setVisible(false);
                ToDoTM selectedItem = lstToDo.getSelectionModel().getSelectedItem();
                if (selectedItem==null){
                    return;
                }
                txtDeleteUpdate.setText(selectedItem.getDescription());
                selectedID = selectedItem.getId();

            }
        });

    }
    public void isDisable(boolean isdisable){
        txtDeleteUpdate.setDisable(isdisable);
        btnDelete.setDisable(isdisable);
        btnUpdate.setDisable(isdisable);
    }

    public void btnLogOut(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to Logout...?", ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get().equals(ButtonType.YES)){
            Parent parent = FXMLLoader.load(getClass().getResource("../view/LoginPageForm.fxml"));
            Scene scene = new Scene(parent);
            Stage primaryStage = (Stage) root.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login Form");
            primaryStage.centerOnScreen();
        }
    }
    public void btnAddNewToDo(ActionEvent actionEvent) {


        lstToDo.getSelectionModel().clearSelection();
        isDisable(true);
        subroot.setVisible(true);
        txtTaskName.requestFocus();
    }
    public void btnAddToList(ActionEvent actionEvent) {
        addToList();
    }
    public void txtDescriptionOnAction(ActionEvent actionEvent) {

        addToList();
    }
    public void addToList(){
        String id = autoGenerateID();
        String description = txtTaskName.getText();
        String userId = lblUserID.getText();
        Connection connection = DBConnector.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into todo values(?,?,?)");
            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,description);
            preparedStatement.setObject(3,userId);
            preparedStatement.executeUpdate();
            txtTaskName.clear();
            subroot.setVisible(false);
            loadList();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public String autoGenerateID(){
        Connection connection = DBConnector.getInstance().getConnection();
        String todoID = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from todo order by id desc limit 1");
            boolean isExist = resultSet.next();
            if (isExist){
                todoID = resultSet.getString(1);
                todoID = todoID.substring(1);
                int intID = Integer.parseInt(todoID);
                intID++;
                if (intID<10){
                    todoID = "T00" + intID;
                }else if(intID<100) {
                    todoID = "T0"+ intID;
                }else {
                    todoID = "T"+ intID;
                }
            }
            else {
                todoID ="T001";
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return todoID;
    }
    public  void loadList(){
        ObservableList<ToDoTM> todos = lstToDo.getItems();
        todos.clear();
        Connection connection = DBConnector.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from todo where userId = ?");
            preparedStatement.setObject(1,LoginPageController.logingUserID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String id = resultSet.getString(1);
                String description = resultSet.getString(2);
                String userId = resultSet.getString(3);
                ToDoTM object = new ToDoTM(id,description,userId);
                todos.add(object);
            }
            lstToDo.refresh();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you want delete this todo?",ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get().equals(ButtonType.YES)){
            Connection connection = DBConnector.getInstance().getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from todo where id = ?");
                preparedStatement.setObject(1,selectedID);
                preparedStatement.executeUpdate();
                loadList();
                txtDeleteUpdate.clear();
                isDisable(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void btnUpdateOnAction(ActionEvent actionEvent) {
        String selectedTxt = txtDeleteUpdate.getText();
        Connection connection = DBConnector.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update todo set description = ? where id = ?");
            preparedStatement.setObject(1,selectedTxt);
            preparedStatement.setObject(2,selectedID);
            preparedStatement.executeUpdate();
            txtDeleteUpdate.clear();
            loadList();
            isDisable(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

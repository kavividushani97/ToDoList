package Controller;

import db.DBConnection;
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
import tm.ToDoTM;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class ToDoFormController {
    public Label lblId;
    public Label lblWelcomeNote;
    public Pane subRoot;
    public TextField txtNewToDo;
    public AnchorPane root;
    public ListView<ToDoTM> lstToDoList;
    public TextField txtSelectedText;
    public Button btnDelete;
    public Button btnUpdate;

    public String id;

    public void initialize(){
        lblId.setText(LoginFormController.enteredUserId);
        lblWelcomeNote.setText("ToDo List: Hi! "+ LoginFormController.enteredUserName +" Welcome");

        subRoot.setVisible(false);

        loadList();

        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);
        txtSelectedText.setDisable(true);

        lstToDoList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTM>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTM> observable, ToDoTM oldValue, ToDoTM newValue) {
                btnDelete.setDisable(false);
                btnUpdate.setDisable(false);
                txtSelectedText.setDisable(false);

                txtSelectedText.requestFocus();

                subRoot.setVisible(false);

                //ToDoTM selectedItem = lstToDoList.getSelectionModel().getSelectedItem();

                if(newValue==null){
                    return;
                }

                String description = newValue.getDescription();

                txtSelectedText.setText(description);

                id = newValue.getId();
            }
        });
    }

    public void btnAddNewToDoOnAction(ActionEvent actionEvent) {
        subRoot.setVisible(true);
        txtNewToDo.requestFocus();

        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);
        txtSelectedText.setDisable(true);
    }

    public void btnLogOutOnAction(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to logout", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){
            Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
            Scene scene = new Scene(parent);

            Stage primaryStage = (Stage) this.root.getScene().getWindow();

            primaryStage.setScene(scene);
            primaryStage.setTitle("Login Form");
            primaryStage.centerOnScreen();
        }

    }

    public void btnAddToListOnAction(ActionEvent actionEvent) {

        if(txtNewToDo.getText().trim().isEmpty()){
            txtNewToDo.requestFocus();
        }else{
            String id = autoGenerate();
            String description = txtNewToDo.getText();
            String user_id = lblId.getText();

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into todos values(?,?,?);");
                preparedStatement.setObject(1,id);
                preparedStatement.setObject(2,description);
                preparedStatement.setObject(3,user_id);

                int i = preparedStatement.executeUpdate();

                System.out.println(i);

                subRoot.setVisible(false);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            loadList();
        }

    }

    public String autoGenerate(){
        Connection connection = DBConnection.getInstance().getConnection();
        String newId = "";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from todos order by id desc limit 1");
            boolean isExist = resultSet.next();

            if(isExist){
                String oldId = resultSet.getString(1);
                oldId = oldId.substring(1, oldId.length());
                int intId = Integer.parseInt(oldId);

                intId++;

                if(intId<10){
                    newId = "T00" + intId;
                }else if(intId<100){
                    newId = "T0" + intId;
                }else{
                    newId = "T" + intId;
                }

            }else{
                newId = "T001";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return newId;
    }

    public void loadList(){
        ObservableList<ToDoTM> todos = lstToDoList.getItems();

        todos.clear();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from todos where user_id = ?");

            preparedStatement.setObject(1,LoginFormController.enteredUserId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){

                String id = resultSet.getString(1);
                String description = resultSet.getString(2);
                String user_id = resultSet.getString(3);

                ToDoTM toDoTM = new ToDoTM(id,description,user_id);

                todos.add(toDoTM);

            }
            lstToDoList.refresh();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        String description = txtSelectedText.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update todos set description = ? where id = ?");

            preparedStatement.setObject(1,description);
            preparedStatement.setObject(2,id);

            preparedStatement.executeUpdate();

            loadList();

            txtSelectedText.setDisable(true);
            btnDelete.setDisable(true);
            btnUpdate.setDisable(true);

            txtSelectedText.clear();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to delete this ToDo..?",ButtonType.YES,ButtonType.NO);

        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){
            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from todos where id = ?");

                preparedStatement.setObject(1,id);
                preparedStatement.executeUpdate();

                loadList();

                txtSelectedText.setDisable(true);
                btnDelete.setDisable(true);
                btnUpdate.setDisable(true);

                txtSelectedText.clear();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}

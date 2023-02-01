package Controller;

import db.DBConnection;
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

public class LoginFormController {
    public AnchorPane root;
    public TextField txtUserName;
    public PasswordField txtPassword;

    public static String enteredUserName;
    public static String enteredUserId;



    public void lblCreateNewAccountOnMouseClicked(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("../view/CreateNewAccountForm.fxml"));
        Scene scene = new Scene(parent);

        Stage primaryStage = (Stage) this.root.getScene().getWindow();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Create New Account");
        primaryStage.centerOnScreen();

    }

    public void btnLoginOnAction(ActionEvent actionEvent) {
        String username = txtUserName.getText();
        String password = txtPassword.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from user where password = ? and user_name = ?;");
            preparedStatement.setObject(1,password);
            preparedStatement.setObject(2,username);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean isExist = resultSet.next();
            if(isExist){

                enteredUserId = resultSet.getString(1);
                enteredUserName = resultSet.getString(2);

                Parent parent = FXMLLoader.load(this.getClass().getResource("../view/ToDoForm.fxml"));
                Scene scene = new Scene(parent);

                Stage primaryStage = (Stage) this.root.getScene().getWindow();

                primaryStage.setScene(scene);
                primaryStage.setTitle("To Do Form");
                primaryStage.centerOnScreen();

            }else{
                new Alert(Alert.AlertType.ERROR,"Invalid Login").showAndWait();
                txtUserName.clear();
                txtPassword.clear();
                txtUserName.requestFocus();
            }
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }
}

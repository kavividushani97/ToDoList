package Controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class CreateNewAccountFormController {
    public TextField txtUserName;
    public TextField txtEmail;
    public Button btnRegister;
    public PasswordField txtNewPassword;
    public PasswordField txtConfirmPassword;
    public Label lblUserId;
    public Label lblNewPassword;
    public Label lblConfirmPassword;
    public AnchorPane root;

    public void initialize(){
        txtUserName.setDisable(true);
        txtEmail.setDisable(true);
        txtNewPassword.setDisable(true);
        txtConfirmPassword.setDisable(true);
        btnRegister.setDisable(true);

        lblNewPassword.setVisible(false);
        lblConfirmPassword.setVisible(false);
    }

    public void lblStartNowOnMouseClicked(MouseEvent mouseEvent) {
        txtUserName.setDisable(false);
        txtEmail.setDisable(false);
        txtNewPassword.setDisable(false);
        txtConfirmPassword.setDisable(false);
        btnRegister.setDisable(false);

        txtUserName.requestFocus();

        autoGenerateID();
    }

    public void autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from user order by id desc limit 1");

            boolean isExist = resultSet.next();

            if(isExist){
                String oldId = resultSet.getString(1);
                int length = oldId.length();
                String id = oldId.substring(1,length);
                int intId = Integer.parseInt(id);
                intId = intId + 1;

                if(intId < 10){
                    lblUserId.setText("U00"+intId);
                }else if(intId < 100){
                    lblUserId.setText("U0"+intId);
                }else{
                    lblUserId.setText("U"+intId);
                }

            }else {
                lblUserId.setText("U001");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void btnRegisterOnAction(ActionEvent actionEvent) {
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        boolean isEqual = newPassword.equals(confirmPassword);

        if(isEqual){
            txtNewPassword.setStyle("-fx-border-color: transparent;-fx-background-radius: 8px");
            txtConfirmPassword.setStyle("-fx-border-color: transparent;-fx-background-radius: 8px");

            lblNewPassword.setVisible(false);
            lblConfirmPassword.setVisible(false);

            register();
        }else{
            txtNewPassword.setStyle("-fx-border-color: red;-fx-border-radius: 8px;-fx-background-radius: 8px");
            txtConfirmPassword.setStyle("-fx-border-color: red;-fx-border-radius: 8px;-fx-background-radius: 8px");

            lblNewPassword.setVisible(true);
            lblConfirmPassword.setVisible(true);

            txtNewPassword.requestFocus();
        }
    }

    public void register(){
        String id = lblUserId.getText();
        String userName = txtUserName.getText();
        String email = txtEmail.getText();
        String password = txtConfirmPassword.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into user values(?,?,?,?)");
            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,userName);
            preparedStatement.setObject(3,password);
            preparedStatement.setObject(4,email);

            int i = preparedStatement.executeUpdate();

            if(i!=0){
                new Alert(Alert.AlertType.CONFIRMATION,"Successfully Registered...").showAndWait();

                try {
                    Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
                    Scene scene = new Scene(parent);

                    Stage primaryStage = (Stage) this.root.getScene().getWindow();
                    primaryStage.setScene(scene);
                    primaryStage.setTitle("Login Form");
                    primaryStage.centerOnScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                new Alert(Alert.AlertType.CONFIRMATION,"Something went wrong...").showAndWait();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}

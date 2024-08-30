package edu.iCET.controller;

import com.jfoenix.controls.JFXButton;
import edu.iCET.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ViewHistoryFormCalculator implements Initializable {

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnSearch;
    @FXML
    private TableColumn<?, ?> colDate;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colStatus;

    @FXML
    private TableColumn<?, ?> colTitle;

    @FXML
    private TableView<Task> tblTask;

    @FXML
    private DatePicker dtpck;

    private static Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTable();
    }

    private void loadTable(){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("completionDate"));

        ObservableList<Task> taskObservableList = FXCollections.observableArrayList();

        tblTask.setItems(taskObservableList);

        try {
            String SQL = "SELECT * FROM Tasks WHERE task_status='Done'";
            Connection connection = db.DBConnection.getInstance().getConnection();
            PreparedStatement psTm = connection.prepareStatement(SQL);
            ResultSet resultSet = psTm.executeQuery();
            while (resultSet.next()){
                Task task =new Task(
                        resultSet.getInt("task_id"),
                        resultSet.getString("task_title"),
                        resultSet.getString("task_status"),
                        resultSet.getDate("completion_date").toLocalDate()
                        );
                taskObservableList.add(task);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnBackOnAction(ActionEvent event) throws Exception{
        changeScene("/view/dash_form.fxml");
    }

    @FXML
    void btnSearchOnAction(ActionEvent actionEvent){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("completionDate"));

        ObservableList<Task> taskObservableList = FXCollections.observableArrayList();

        tblTask.setItems(taskObservableList);

        try {
            String SQL = "SELECT * FROM Tasks WHERE task_status='Done' AND completion_date='" + dtpck.getValue().toString() + "'";
            Connection connection = db.DBConnection.getInstance().getConnection();
            PreparedStatement psTm = connection.prepareStatement(SQL);
            ResultSet resultSet = psTm.executeQuery();
            while (resultSet.next()){
                Task task =new Task(
                        resultSet.getInt("task_id"),
                        resultSet.getString("task_title"),
                        resultSet.getString("task_status"),
                        resultSet.getDate("completion_date").toLocalDate()
                );
                taskObservableList.add(task);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void changeScene(String fxmlFile) throws Exception {
        Parent pane = FXMLLoader.load(ViewHistoryFormCalculator.class.getResource(fxmlFile));
        stage.getScene().setRoot(pane);
    }

    public static void getStage(Stage primaryStage){
        stage=primaryStage;
    }

}

package edu.iCET.controller;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DashFormController implements Initializable {

    @FXML
    private JFXListView<String> lstDone;

    @FXML
    private JFXListView<String> lstToDo;

    @FXML
    private JFXTextField txtTask;

    private static Stage stage;

    @FXML
    void btnAddOnAction(ActionEvent event) {
        if (!(txtTask.getText().isEmpty())){
            try {
                String sql ="INSERT INTO Tasks VALUES(?,?,?,?)";
                Connection connection = db.DBConnection.getInstance().getConnection();
                PreparedStatement psTm = connection.prepareStatement(sql);
                psTm.setObject(1,0);
                psTm.setObject(2,txtTask.getText());
                psTm.setObject(3,"Active");
                psTm.setObject(4, LocalDate.now());
                if(psTm.executeUpdate()>0){
                    new Alert(Alert.AlertType.INFORMATION,"Task Added!").show();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            txtTask.setText("");
            setTodoList();
            setDoneList();
        }

    }

    @FXML
    void btnViewHistoryOnAction(ActionEvent event) throws Exception{
        changeScene("/view/view_history_form.fxml");
    }

    private void setTodoList(){
        ObservableList<String> stringObservableList = FXCollections.observableArrayList();

        lstToDo.setItems(stringObservableList);

        try {
            String sql = "SELECT task_title FROM Tasks WHERE task_status='Active'";
            Connection connection = db.DBConnection.getInstance().getConnection();
            PreparedStatement psTm = connection.prepareStatement(sql);
            ResultSet resultSet = psTm.executeQuery();
            while (resultSet.next()){
                String taskTitle = resultSet.getString("task_title");
                stringObservableList.add(taskTitle);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        lstToDo.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    private final HBox hbox = new HBox();
                    private final Text text = new Text();
                    private final CheckBox checkBox = new CheckBox();

                    {
                        hbox.getChildren().addAll(text, checkBox);
                        hbox.setSpacing(10);

                        checkBox.setOnAction(event -> {
                            if (checkBox.isSelected()) {
                                try {
                                    String sql ="UPDATE Tasks SET task_Status='Done', completion_date=? WHERE task_title=?";
                                    Connection connection = db.DBConnection.getInstance().getConnection();
                                    PreparedStatement psTm = connection.prepareStatement(sql);
                                    psTm.setObject(1,LocalDate.now());
                                    psTm.setString(2,text.getText());
                                    if(psTm.executeUpdate()>0){
                                        new Alert(Alert.AlertType.INFORMATION,"Task Done!").show();
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                                setTodoList();
                                setDoneList();
                            }
                        });
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            text.setText(item);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
    }

    private void setDoneList() {
        ObservableList<String> stringObservableList = FXCollections.observableArrayList();

        lstDone.setItems(stringObservableList);

        try {
            String sql = "SELECT task_title FROM Tasks WHERE task_status='Done'";
            Connection connection = db.DBConnection.getInstance().getConnection();
            PreparedStatement psTm = connection.prepareStatement(sql);
            ResultSet resultSet = psTm.executeQuery();
            while (resultSet.next()) {
                String taskTitle = resultSet.getString("task_title");
                stringObservableList.add(taskTitle);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void changeScene(String fxmlFile) throws Exception {
        Parent pane = FXMLLoader.load(DashFormController.class.getResource(fxmlFile));
        stage.getScene().setRoot(pane);
    }

    public static void getStage(Stage primaryStage){
        stage=primaryStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTodoList();
        setDoneList();
    }

}

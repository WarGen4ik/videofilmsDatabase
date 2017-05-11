package sample.Controllers;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;
import sample.Controllers.editWindow.EditController;
import sample.Interfaces.impls.DataBaseAddressBook;
import sample.Objects.VideoFilm;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Илья on 22.03.2017.
 */
public class MainController implements Initializable {
    @FXML
    private CustomTextField txtField;
    @FXML
    private TableView tableView;
    @FXML
    private Label lbl;
    @FXML
    private TableColumn<VideoFilm, String> tableColumnFIO;
    @FXML
    private TableColumn<VideoFilm, String> tableColumnGenre;
    @FXML
    private TableColumn<VideoFilm, String> tableColumnYear;

    private FXMLLoader loader = new FXMLLoader();
    private EditController editController;
    private Stage editDialogStage;
    private Parent fxmlEdit;
    private Stage parentStage;
    private ResourceBundle resourceBundle;
    private DataBaseAddressBook dataBaseAddressBook = new DataBaseAddressBook();


    public void buttonPressed(ActionEvent actionEvent) {

        Object event = actionEvent.getSource();

        if (!(event instanceof Button))
            return;

        Button btn = (Button)event;
        VideoFilm videoFilm = (VideoFilm)tableView.getSelectionModel().getSelectedItem();

        switch (btn.getId()){
            case "addBtn":{
                editController.setVideoFilm(new VideoFilm(0,"", "", ""));
                showDialog();
            }break;
            case "changeBtn":{
                if (videoFilm == null)
                    break;
                editController.setVideoFilm(videoFilm);
                showDialog();
            }break;
            case "deleteBtn":{
                dataBaseAddressBook.delete(videoFilm);
            }break;
        }
    }
    private void initListernes(){
        dataBaseAddressBook.getList().addListener(new ListChangeListener<VideoFilm>() {
            @Override
            public void onChanged(Change<? extends VideoFilm> c) {
                updateCount();
            }
        });

        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2){
                    editController.setVideoFilm((VideoFilm)tableView.getSelectionModel().getSelectedItem());
                    showDialog();
                }
            }
        });
    }

    public void setParentStage(Stage parentStage){
        this.parentStage = parentStage;
    }

    private void updateCount(){
        lbl.setText(resourceBundle.getString("count") + dataBaseAddressBook.getList().size());
    }

    private void showDialog() {
        if (editDialogStage == null) {
            editDialogStage = new Stage();
            editDialogStage.setTitle(resourceBundle.getString("edit"));
            editDialogStage.setMinWidth(335);
            editDialogStage.setMinHeight(120);
            editDialogStage.setResizable(false);
            editDialogStage.setScene(new Scene(fxmlEdit));
            editDialogStage.initModality(Modality.WINDOW_MODAL);
            editDialogStage.initOwner(parentStage);
            editController.setAddressBook(dataBaseAddressBook);
        }
        editDialogStage.show();
    }
    public void initialize(URL url, ResourceBundle resources) {
        resourceBundle = resources;
        tableColumnFIO.setCellValueFactory(new PropertyValueFactory<VideoFilm, String>("name"));
        tableColumnGenre.setCellValueFactory(new PropertyValueFactory<VideoFilm, String>("genre"));
        tableColumnYear.setCellValueFactory(new PropertyValueFactory<VideoFilm, String>("year"));

        setupClearButtonField(txtField);
        initListernes();
        initLoader();
        updateCount();
    }

    private void initLoader() {
        tableView.setItems(dataBaseAddressBook.getList());

        try{
            loader.setLocation(getClass().getResource("editWindow/editWindow.fxml"));
            loader.setResources(ResourceBundle.getBundle("sample.Bundles.Locale", new Locale("ru")));
            fxmlEdit = loader.load();
            editController = loader.getController();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void findAction(ActionEvent actionEvent) {
        dataBaseAddressBook.getListFromNameOrGenreOrYear(txtField.getText());
    }

    private void setupClearButtonField (CustomTextField customTextField){
        try{
            Method m = TextFields.class.getDeclaredMethod("setupClearButtonField",TextField.class, ObjectProperty.class);
            m.setAccessible(true);
            m.invoke(null, customTextField, customTextField.rightProperty());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

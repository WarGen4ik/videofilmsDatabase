package sample.Controllers;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;
import sample.Controllers.editWindow.EditController;
import sample.Interfaces.impls.DataBaseAddressBook;
import sample.Objects.VideoFilm;
import sample.Objects.ViewUtil;

import java.io.File;
import java.io.FileOutputStream;
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
                editController.setVideoFilm(new VideoFilm(DataBaseAddressBook.MAX_ID,"", "", ""));
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
            case "toExcelBtn":{
                excelBtnPressed();
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
            editDialogStage.getScene().getStylesheets().add("mainStyle.css");
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

    private void excelBtnPressed(){
        if (dataBaseAddressBook.getList().size() == 0){
            ViewUtil.alert(new Alert(Alert.AlertType.ERROR),
                    "Ошибка",
                    null,
                    "Вы не добавили ни одной записи в бд!",
                    parentStage.getScene(),
                    new ButtonType("ОК", ButtonBar.ButtonData.CANCEL_CLOSE)).showAndWait();
            return;
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory =
                directoryChooser.showDialog(parentStage);

        if(selectedDirectory == null){
            ViewUtil.alert(new Alert(Alert.AlertType.INFORMATION),
                    "Can not find directory",
                    null,
                    "Please choose directory path",
                    parentStage.getScene(),
                    new ButtonType("ОК", ButtonBar.ButtonData.CANCEL_CLOSE)).showAndWait();
            return;
        }else{
            try {
                Workbook wb = new HSSFWorkbook();
                Sheet sheet = wb.createSheet("Видеофильмы");

                ObservableList<VideoFilm> list =  dataBaseAddressBook.getList();

                Row rowNames = sheet.createRow(0);
                Cell cellNames = rowNames.createCell(0);
                cellNames.setCellValue("ID");
                cellNames = rowNames.createCell(1);
                cellNames.setCellValue("Название");
                cellNames = rowNames.createCell(2);
                cellNames.setCellValue("Жанр");
                cellNames = rowNames.createCell(3);
                cellNames.setCellValue("Год выпуска");

                for (int i = 0; i < list.size(); i++){
                    Row row = sheet.createRow(i+1);
                    for (int j = 0; j < 4; j++) {
                        org.apache.poi.ss.usermodel.Cell cell = row.createCell(j);
                        switch (j) {
                            case 0: cell.setCellValue((int)list.get(i).getNextValue(j)); break;
                            case 1:case 2:case 3: cell.setCellValue((String)list.get(i).getNextValue(j)); break;
                        }
                    }


                }
                FileOutputStream fos = new FileOutputStream(selectedDirectory.getAbsolutePath() + "\\videofilms.xls");
                wb.write(fos);
                fos.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}

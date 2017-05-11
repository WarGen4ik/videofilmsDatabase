package sample.Controllers.editWindow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.Interfaces.impls.DataBaseAddressBook;
import sample.Objects.VideoFilm;


/**
 * Created by Илья on 13.03.2017.
 */
public class EditController {
    @FXML
    Button okBtn;
    @FXML
    Button cancelBtn;
    @FXML
    TextField nameField;
    @FXML
    TextField genreField;
    @FXML
    TextField yearField;
    @FXML
    Label lblName;
    @FXML
    Label lblNumber;

    private DataBaseAddressBook addressBook;
    private VideoFilm videoFilm;

    public void setAddressBook(DataBaseAddressBook addressBook){
        this.addressBook = addressBook;
    }

    public void cancelAction(ActionEvent actionEvent) {
        Node node = (Node)actionEvent.getSource();
        Stage stage = (Stage)node.getScene().getWindow();
        stage.hide();
    }

    public void setVideoFilm(VideoFilm videoFilm){
        this.videoFilm = videoFilm;

        nameField.setText(videoFilm.getName());
        genreField.setText(videoFilm.getGenre());
        yearField.setText(videoFilm.getYear());
    }

    public void actionSave(ActionEvent actionEvent) {
        if (nameField.getText().equals("")) {
            return;
        }
        videoFilm.setName(nameField.getText());
        videoFilm.setGenre(genreField.getText());
        videoFilm.setYear(yearField.getText());
        if (!addressBook.getList().contains(videoFilm)){
            if (!videoFilm.getName().equals(""))
                addressBook.add(videoFilm);
            else return;
        }
        else {
            if (!videoFilm.getName().equals(""))
                addressBook.change(videoFilm);
            else return;
        }
        cancelAction(actionEvent);
    }
}

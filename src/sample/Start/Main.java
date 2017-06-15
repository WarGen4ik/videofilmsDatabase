package sample.Start;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sample.Controllers.MainController;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("main.fxml"));
            Locale locale = new Locale("ru");

            loader.setResources(ResourceBundle.getBundle("sample.Bundles.Locale", locale));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.setParentStage(primaryStage);

            primaryStage.setMinHeight(440);
            primaryStage.setMinWidth(350);

            primaryStage.setTitle(loader.getResources().getString("addressBook"));
            primaryStage.getIcons().add(new Image("sample/images/img.png"));

            primaryStage.setScene(new Scene(root));
            primaryStage.getScene().getStylesheets().add("mainStyle.css");
            primaryStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}

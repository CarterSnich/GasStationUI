
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class App extends Application {
    
    private static Image windowIcon = new Image("file:src/img/ico/Fuel_Engines.png");

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("ui.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        
        Scene scene = new Scene(root);

        ((ViewController)fxmlLoader.getController()).setStage((Stage)stage);

        stage.setTitle("The Skeld Gas Station");  
        stage.getIcons().add(windowIcon);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

    }
}

package client.mainWindow;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindow {

    public MainWindow(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/mainWindow.fxml"));
        Parent root = fxmlLoader.load();
        stage.setTitle("Box Client");
        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.show();
    }
}

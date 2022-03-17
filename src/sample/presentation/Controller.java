package sample.presentation;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {

    @FXML
    private void client() throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("../presentation/clients.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Clients");
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }
    @FXML
    private void order() throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("../presentation/orders.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Orders");
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }
    @FXML
    private void product() throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("../presentation/products.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Products");
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }
}

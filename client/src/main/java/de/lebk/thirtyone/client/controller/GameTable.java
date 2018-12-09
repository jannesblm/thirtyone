package de.lebk.thirtyone.client.controller;

import de.lebk.thirtyone.client.ObservableClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;


public class GameTable extends Application
{
    private ObservableClient client;

    @FXML
    private TextArea logArea;

    @FXML
    private Button connectButton;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(this.getClass().getClassLoader().getResource("GameTable.fxml"));

        primaryStage.setTitle("Thirtyone");
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    @FXML
    private void initialize()
    {
        client = new ObservableClient();

        client.getConnectedProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() -> connectButton.setText(newValue ? "Verlassen" : "Verbinden")));
    }

    public void onConnectButtonClick(MouseEvent mouseEvent)
    {
        if (!client.isConnected()) {
            TextInputDialog connectDialog = new TextInputDialog("localhost:25565");

            connectDialog.setTitle("Verbinden");
            connectDialog.setHeaderText("Mit einem Server verbinden");
            connectDialog.setContentText("Adresse:");

            Optional<String> result = connectDialog.showAndWait();

            result.ifPresent(input -> {
                try {
                    URI uri = new URI("tcp://" + input);
                    String host = uri.getHost();
                    int port = uri.getPort();

                    if (uri.getHost() == null || uri.getPort() == -1) {
                        throw new URISyntaxException(uri.toString(),
                                "URI must have host and port parts");
                    }

                    client.setHost(host);
                    client.setPort(port);

                    clientLog("Versuche mit " + host + ":" + port + " zu verbinden...");

                    client.connectAsync();

                    // TODO: React on connect success or fail
                } catch (URISyntaxException e) {
                    new Alert(Alert.AlertType.ERROR, "Der Hostname ist ungültig.").show();
                }
            });
        } else {
            client.disconnect();
        }
    }

    public void clientLog(String text)
    {
        logArea.appendText(text + System.lineSeparator());
    }
}

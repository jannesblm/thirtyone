package de.lebk.thirtyone.client.controller;

import de.lebk.thirtyone.client.ObservableClient;
import de.lebk.thirtyone.game.Round;
import de.lebk.thirtyone.game.item.Card;
import de.lebk.thirtyone.game.item.Deck;
import de.lebk.thirtyone.game.network.Message;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class GameTable extends Application
{
    private static final Logger LOG = LogManager.getLogger();

    private ObservableClient client;

    @FXML
    private TextArea logArea;

    @FXML
    private Button connectButton;

    @FXML
    private Button startButton;

    @FXML
    private Button pushButton;

    @FXML
    private Button passButton;

    @FXML
    private HBox currentPlayerBox;

    @FXML
    private HBox leftOpponentBox;

    @FXML
    private HBox middleOpponentBox;

    @FXML
    private HBox rightOpponentBox;

    @FXML
    private HBox middleBox;


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
                Platform.runLater(() -> {
                    if (newValue) {
                        connectButton.setText("Verlassen");
                    } else {
                        connectButton.setText("Verbinden");
                        startButton.setDisable(true);
                        pushButton.setDisable(true);
                        passButton.setDisable(true);
                    }
                }));

        client.getPlayerProperty().addListener((observableValue, oldPlayer, player) -> {
            LOG.debug("The player has changed: " + player);

            Round round = player.getRound();

            if (round.getPlayers().size() >= 1) {
                startButton.setDisable(false);
            } else {
                startButton.setDisable(true);
            }

            LOG.debug("Round status: " + round.isStarted());

            if (round.isStarted()) {
                startButton.setDisable(true);
                pushButton.setDisable(false);
                passButton.setDisable(false);
            } else {
                pushButton.setDisable(true);
                passButton.setDisable(true);
            }

            if (player.getDeck().size() > 0) {
                LOG.debug("Refresh player box");
                Platform.runLater(() -> {
                    refreshCurrentPlayerBox();
                });
            }
        });


        refreshCurrentPlayerBox();

        initCards();
    }

    private void initCards(){
        initCardToggleButtons();
        initCardButtons();
    }

    private void initCardToggleButtons(){
        List<Node> cardGroups = new ArrayList<>();
        cardGroups.add(currentPlayerBox);
        cardGroups.add(middleBox);

        for(Node cardGroup : cardGroups){
            Set<Node> cardButtons = cardGroup.lookupAll(".cardButton");

            for (Node cardButton : cardButtons){
                final String cardName = "back.png";
                setCardForToggleButton(cardName, (ToggleButton) cardButton);
            }
        }
    }

    private void initCardButtons(){
        List<Node> cardGroups = new ArrayList<>();
        cardGroups.add(leftOpponentBox);
        cardGroups.add(middleOpponentBox);
        cardGroups.add(rightOpponentBox);

        for(Node cardGroup : cardGroups){
            Set<Node> cardButtons = cardGroup.lookupAll(".cardButton");

            for (Node cardButton : cardButtons){
                final String cardName = "back.png";
                setCardForButton(cardName,(Button) cardButton);
            }
        }
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

    public void refreshCurrentPlayerBox()
    {
        Set<Node> cardButtons = currentPlayerBox.lookupAll(".cardButton");
        Deck deck = client.getPlayerProperty().get().getDeck();
        int i = 0;
        for (Node cardButton : cardButtons) {
            // TODO: Unsafe
            deck.get(i).ifPresent(card -> setCardForToggleButton(card.getImageName(), (ToggleButton) cardButton));
            i++;
        }
    }

    private void setCardForToggleButton(String cardName, ToggleButton toggleButton){
        Image card = new Image(this.getClass().getClassLoader().getResource("cards/" + cardName).toString());

        final ImageView toggleImage = new ImageView(card);
        toggleImage.setFitHeight(180);
        toggleImage.setFitWidth(130);

        toggleButton.setGraphic(toggleImage);
    }

    private void setCardForButton(String cardName, Button button){
        Image card = new Image(this.getClass().getClassLoader().getResource("cards/" + cardName).toString());

        final ImageView toggleImage = new ImageView(card);
        toggleImage.setFitHeight(90);
        toggleImage.setFitWidth(60);

        button.setGraphic(toggleImage);
    }

    public void onStartButtonClick(MouseEvent mouseEvent)
    {
        LOG.debug(client.getPlayerProperty().get().getChannel());
        client.getPlayerProperty().get()
                .getChannel()
                .ifPresent(ch -> ch.writeAndFlush(Message.prepare("START")));
    }

    public void onPushButtonClick(MouseEvent mouseEvent)
    {
        client.getPlayerProperty().get()
                .getChannel()
                .ifPresent(ch -> ch.writeAndFlush(Message.prepare("PUSH")));
    }

    public void onPassButtonClick(MouseEvent mouseEvent)
    {
        client.getPlayerProperty().get()
                .getChannel()
                .ifPresent(ch -> ch.writeAndFlush(Message.prepare("PASS")));
    }
}

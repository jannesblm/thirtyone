package de.lebk.thirtyone.client.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import de.lebk.thirtyone.client.ThreadedClient;
import de.lebk.thirtyone.game.Player;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class GameTable extends Application
{
    private static final Logger LOG = LogManager.getLogger();

    private ThreadedClient client;

    @FXML
    private TextArea logArea;

    @FXML
    private Button connectButton;

    @FXML
    private Button startButton;

    @FXML
    private Button swapButton;

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

    @FXML
    private Label pointLabel;

    @FXML
    private Label lifeLabel;

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
        client = new ThreadedClient();

        client.getConnectedProperty().addListener((oldConnected, connected) -> Platform.runLater(() -> {
            if (connected) {
                connectButton.setText("Verlassen");
            } else {
                connectButton.setText("Verbinden");
                startButton.setDisable(true);
                swapButton.setDisable(true);
                pushButton.setDisable(true);
                passButton.setDisable(true);

                emptyAllBoxes();
            }
        }));

        client.getMessageProperty().addListener((oldMessage, message) -> clientLog(message));

        client.getPlayerProperty().addListener(((oldPlayer, player) -> {
            LOG.debug("The player has changed: " + player);

            Round round = player.getRound();

            if (round.getPlayers().size() >= 1) {
                startButton.setDisable(false);
            } else {
                startButton.setDisable(true);
            }

            if (round.isStarted()) {
                startButton.setDisable(true);

                if (player.isOnTurn()) {
                    swapButton.setDisable(false);
                    passButton.setDisable(player.isPassed());
                    pushButton.setDisable(false);
                } else {
                    swapButton.setDisable(true);
                    pushButton.setDisable(true);
                    passButton.setDisable(true);
                }
            } else {
                swapButton.setDisable(true);
                pushButton.setDisable(true);
                passButton.setDisable(true);
            }

            final float points = player.getDeck().getPoints();
            final int lifes = player.getLifes();

            Platform.runLater(() -> {
                refreshAllBoxes();

                pointLabel.setText("Punkte: " + points);
                lifeLabel.setText("Leben: " + lifes);
            });

        }));
        emptyAllBoxes();
    }

    public void onConnectButtonClick(MouseEvent mouseEvent)
    {
        if (!client.isConnected()) {
            TextInputDialog connectDialog = new TextInputDialog("localhost:9942");

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

    public void refreshBox(final HBox box, final Deck deck)
    {
        box.lookupAll(".cardButton").forEach(button -> {
            Optional<Card> card = deck.deal(1).get(0);

            String cardName;

            if (card.isPresent()) {
                cardName = card.get().getImageName();
            } else {
                cardName = "back.png";
            }

            final ImageView image = new ImageView(this.getClass().getClassLoader().getResource("cards/" + cardName).toString());

            image.setFitHeight(((ToggleButton) button).getPrefHeight() - 10);
            image.setFitWidth(((ToggleButton) button).getPrefWidth() - 10);

            ((ToggleButton) button).setGraphic(image);
        });
    }

    public void emptyAllBoxes()
    {
        refreshBox(currentPlayerBox, new Deck());
        refreshBox(middleBox, new Deck());
    }

    public void refreshAllBoxes()
    {
        Player player = client.getPlayerProperty().getValue();

        refreshBox(currentPlayerBox, player.getDeck());
        refreshBox(middleBox, player.getRound().getMiddle());

        List<Player> opponents = player.getRound()
                .getPlayers()
                .stream()
                .filter(p -> !p.equals(player))
                .collect(Collectors.toList());

        List<HBox> opponentBoxes = List.of(leftOpponentBox, middleOpponentBox, rightOpponentBox);

        for (int i = 0; i < opponents.size(); i++) {
            if (i >= opponentBoxes.size()) {
                break;
            }

            refreshBox(opponentBoxes.get(i), opponents.get(i).getDeck());
        }
    }


    public void onStartButtonClick(MouseEvent mouseEvent)
    {
        LOG.debug(client.getPlayerProperty().getValue().getChannel());
        send(new Message("START"));
    }

    public void onPushButtonClick(MouseEvent mouseEvent)
    {
        send(new Message("PUSH"));
    }

    public void onPassButtonClick(MouseEvent mouseEvent)
    {
        send(new Message("PASS"));
    }

    private List<Node> getWithCondition(final String type, Node node, Function<Object, Boolean> condition)
    {
        return node.lookupAll(type)
                .stream()
                .filter(condition::apply)
                .collect(Collectors.toList());
    }

    public void onSwapButtonClick(MouseEvent mouseEvent)
    {
        List<Node> playerCards = getWithCondition("ToggleButton", currentPlayerBox,
                node -> node instanceof ToggleButton);
        List<Node> middleCards = getWithCondition("ToggleButton", middleBox,
                node -> node instanceof ToggleButton);

        OptionalInt playerIndex = IntStream.range(0, playerCards.size())
                .filter(i -> ((ToggleButton) playerCards.get(i)).isSelected())
                .findFirst();
        OptionalInt middleIndex = IntStream.range(0, playerCards.size())
                .filter(i -> ((ToggleButton) middleCards.get(i)).isSelected())
                .findFirst();

        if (!playerIndex.isPresent() || !middleIndex.isPresent()) {
            // The user did not select a card from player box and middle box!
            return;
        }

        JsonArray swap = new JsonArray();
        swap.add(new JsonPrimitive(playerIndex.getAsInt()));
        swap.add(new JsonPrimitive(middleIndex.getAsInt()));

        send(new Message("SWAP", swap));
    }

    public void send(Message message)
    {
        client.getPlayerProperty().getValue().send(message);
    }
}

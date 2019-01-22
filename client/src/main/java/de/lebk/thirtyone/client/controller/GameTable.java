package de.lebk.thirtyone.client.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import de.lebk.thirtyone.client.ConnectStatus;
import de.lebk.thirtyone.client.ThreadedClient;
import de.lebk.thirtyone.game.Player;
import de.lebk.thirtyone.game.Round;
import de.lebk.thirtyone.game.item.Card;
import de.lebk.thirtyone.game.item.Deck;
import de.lebk.thirtyone.game.item.DeckEmptyException;
import de.lebk.thirtyone.game.network.Message;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class GameTable extends Application
{
    private static final Logger LOG = LogManager.getLogger();

    private ThreadedClient client;

    @FXML
    private TextFlow logArea;

    @FXML
    private ScrollPane logAreaScrollPane;

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

    @FXML
    private Label nameLabel;

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
        primaryStage.setScene(new Scene(root, 900, 700));
        primaryStage.show();

    }

    @FXML
    private void initialize()
    {
        logAreaScrollPane.vvalueProperty().bind(logArea.heightProperty());

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

        client.getMessageProperty().addListener((oldMessage, message) ->
                clientLog(message.getMessage(), message.getColor()));

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
            final String name = player.getName();

            Platform.runLater(() -> {
                refreshAllBoxes();

                nameLabel.setText("Name: " + name);
                pointLabel.setText("Punkte: " + points);
                lifeLabel.setText("Leben: " + lifes);
            });

        }));

        emptyAllBoxes();
    }

    public void onConnectButtonClick(MouseEvent mouseEvent)
    {
        if (!client.isConnected()) {
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Verbinden");
            dialog.setHeaderText("Mit einem Server verbinden");

            ButtonType connectButtonType = new ButtonType("Verbinden", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField playerField = new TextField();
            playerField.setText("Spieler");

            TextField hostField = new TextField();
            hostField.setText("localhost:9942");

            grid.add(new Label("Name:"), 0, 0);
            grid.add(playerField, 1, 0);
            grid.add(new Label("Adresse:"), 0, 1);
            grid.add(hostField, 1, 1);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == connectButtonType) {
                    return new Pair<>(playerField.getText(), hostField.getText());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();

            result.ifPresent(connectValues -> {
                try {
                    URI uri = new URI("tcp://" + connectValues.getValue());
                    String host = uri.getHost();
                    int port = uri.getPort();

                    if (uri.getHost() == null || uri.getPort() == -1) {
                        throw new URISyntaxException(uri.toString(),
                                "URI must have host and port parts");
                    }

                    client.setHost(host);
                    client.setPort(port);
                    client.setPlayerName(connectValues.getKey());

                    clientLog("Versuche als '" + connectValues.getKey() + "' mit " + host + ":"
                            + port + " zu verbinden...");

                    CompletableFuture<ConnectStatus> connected = client.connectAsync();

                    connected.whenComplete((status, exception) -> {
                        if (!status.isSuccess()) {
                            clientLog("Verbindung fehlgeschlagen: " + status.getError().getMessage(), Color.RED);
                        } else {
                            clientLog("Verbunden, Anmeldung erfolgt.", Color.GREEN);
                        }
                    });

                } catch (URISyntaxException e) {
                    new Alert(Alert.AlertType.ERROR, "Der Hostname ist ungültig.").show();
                }
            });
        } else {
            client.disconnect();
        }
    }

    public void clientLog(final String text)
    {
        clientLog(text, Color.BLACK);
    }

    public void clientLog(final String text, final Color color)
    {
        Platform.runLater(() -> {
            String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
            Text logEntry = new Text(timeStamp + " " + text + System.lineSeparator());

            logEntry.setFont(Font.font("Consolas"));
            logEntry.setFill(color);
            logArea.getChildren().add(logEntry);
        });
    }

    public void refreshBox(final HBox box, final Deck deck)
    {
        box.lookupAll(".cardButton").forEach(button -> {
            Optional<Card> card;

            try {
                card = deck.deal(1).get(0);
            } catch (DeckEmptyException exception) {
                card = Optional.empty();
            }

            String cardName;

            if (card.isPresent()) {
                cardName = card.get().getImageName();
            } else {
                // Gracefully use back image when we weren't able to pop a card
                cardName = "back.png";
            }

            final ImageView image = new ImageView(
                    this.getClass()
                            .getClassLoader()
                            .getResource("cards/" + cardName)
                            .toString()
            );

            image.setFitHeight(((ButtonBase) button).getPrefHeight() - 10);
            image.setFitWidth(((ButtonBase) button).getPrefWidth() - 10);

            ((ButtonBase) button).setGraphic(image);
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

        final List<Player> opponents = player.getRound()
                .getPlayers()
                .stream()
                .filter(p -> !p.equals(player))
                .collect(Collectors.toList());

        final List<HBox> opponentBoxes = List.of(leftOpponentBox, middleOpponentBox, rightOpponentBox);

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
        client.getPlayerProperty()
                .getValue()
                .send(message);
    }
}

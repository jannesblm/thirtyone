package de.lebk.thirtyone.game.network;

import com.google.gson.JsonPrimitive;
import de.lebk.thirtyone.game.GameException;
import de.lebk.thirtyone.game.Player;
import de.lebk.thirtyone.game.Round;
import de.lebk.thirtyone.game.RoundEnd;
import de.lebk.thirtyone.game.item.Card;
import de.lebk.thirtyone.game.item.Deck;
import de.lebk.thirtyone.game.item.DeckEmptyException;
import de.lebk.thirtyone.game.network.exception.ConnectError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class NetworkRound extends Round
{
    private static final Logger LOG = LogManager.getLogger();

    protected static final int MAX_PLAYERS = 4;
    protected Deck staple;

    public void join(NetworkPlayer player) throws ConnectError
    {
        if (players.size() >= MAX_PLAYERS) {
            throw new ConnectError("Maximale Rundengröße erreicht");
        }

        if (isStarted()) {
            throw new ConnectError("Die Runde hat bereits begonnen!");
        }

        boolean nameTaken = players.stream().anyMatch(p -> p.getName().equalsIgnoreCase(player.getName()));

        if (nameTaken) {
            throw new ConnectError("Dieser Name ist bereits vergeben!");
        }

        if (playerCount() == 0) {
            setCurrentPlayer(player);
        }

        players.add(player);
    }

    public void leave(NetworkPlayer player)
    {
        players.remove(player);
    }

    public void updateAll()
    {
        updateAll(null);
    }

    public void updateAll(final String cause)
    {
        for (Player player : players) {
            ((NetworkPlayer) player).update(cause);
        }
    }

    public void broadcast(final String message)
    {
        for (Player player : players) {
            player.send(new Message("TELL", new JsonPrimitive(message)));
        }
    }

    public void dealMiddle() throws DeckEmptyException
    {
        setMiddle(staple.deal(3));
    }

    public void start() throws DeckEmptyException
    {
        if (isStarted()) {
            return;
        }

        staple = Deck.newSkat();

        dealMiddle();
        setStarted(true);

        for (Player player : players) {
            player.setDeck(staple.deal(3));
        }

        updateAll("Die Runde wurde gestartet.");

        currentPlayer.send(new Message("TELL",
                new JsonPrimitive("Sie sind dran!")));
    }

    public void reset()
    {
        setMiddle(new Deck(3));
        setStarted(false);

        for (Player player : players) {
            player.setPassed(false);
            player.setDeck(new Deck(3));
        }
    }

    public void swap(int playerIndex, int middleIndex) throws GameException, RoundEnd
    {
        Optional<Player> currentPlayer = getCurrentPlayer();

        if (!currentPlayer.isPresent()) {
            throw new GameException("Current player is not present.");
        }

        Deck deck = currentPlayer.get().getDeck();

        Optional<Card> playerSwap = deck.get(playerIndex);
        Optional<Card> middleSwap = middle.get(middleIndex);

        if (!playerSwap.isPresent() || !middleSwap.isPresent()) {
            throw new GameException("Either player swap or middle swap requested is not present.");
        }

        deck.swap(playerSwap.get(), middleSwap.get());
        middle.swap(middleSwap.get(), playerSwap.get());

        if (deck.getPoints() == 31.0 || deck.getPoints() == 33.0) {
            throw new RoundEnd(currentPlayer.get().getName() + " hat die höchste Punktzahl erreicht!");
        }
    }

    public Player next(final String message) throws GameException, RoundEnd
    {
        List<Player> playerList = List.copyOf(players);

        if (playerList.size() == 0) {
            throw new GameException("No player remaining.");
        }

        if (currentPlayer == null) {
            currentPlayer = playerList.get(0);
        }

        int currentIndex = playerList.indexOf(currentPlayer);
        int nextIndex = currentIndex + 1 < playerList.size() ? currentIndex + 1 : 0;

        if (nextIndex == 0) {
            // Each player had a turn
            boolean allPushed = playerList.stream()
                    .allMatch(player -> ((NetworkPlayer) player).isPushed());

            if (allPushed) {
                // A new middle shall be dealt

                try {
                    dealMiddle();

                    broadcast("Drei neue Karten in die Mitte!");
                } catch (DeckEmptyException exception) {
                    broadcast("Der Stapel ist leer. Es können keine neuen Karten ausgeteilt werden!");
                }
            }

            // Reset each players pushed state
            players.forEach(player -> ((NetworkPlayer) player)
                    .setPushed(false));
        }

        currentPlayer = playerList.get(nextIndex);

        if (currentPlayer.isPassed()) {
            throw new RoundEnd(currentPlayer.getName() + " hat gepasst!");
        }

        updateAll(message);
        currentPlayer.send(new Message("TELL", new JsonPrimitive("Sie sind dran!")));

        return currentPlayer;
    }
}

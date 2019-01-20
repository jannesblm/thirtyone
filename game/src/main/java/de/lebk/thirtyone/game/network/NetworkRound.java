package de.lebk.thirtyone.game.network;

import de.lebk.thirtyone.game.GameException;
import de.lebk.thirtyone.game.Player;
import de.lebk.thirtyone.game.Round;
import de.lebk.thirtyone.game.item.Card;
import de.lebk.thirtyone.game.item.Deck;
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

    public NetworkRound()
    {
        staple = Deck.newSkat();
    }

    public void join(NetworkPlayer player) throws ConnectError
    {
        if (players.size() >= MAX_PLAYERS) {
            throw new ConnectError("Round size limit reached!");
        }

        if (players.size() == 0) {
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
        for (Player player : players) {
            ((NetworkPlayer) player).update();
        }
    }

    public void start()
    {
        setMiddle(staple.deal(3));
        setStarted(true);

        for (Player player : players) {
            player.setDeck(staple.deal(3));
        }

        updateAll();
    }

    public void swap(Player p, Card card, Card middleCard) throws GameException
    {
        Optional<Player> player = getPlayer(p);

        if (!player.isPresent()) {
            throw new GameException("Swapping player was not found in this round!");
        }

        Deck deck = player.get().getDeck();

        deck.swap(card, middleCard);
        middle.swap(middleCard, card);
    }

    public Player nextPlayer() throws GameException
    {
        List<Player> playerList = List.copyOf(players);

        for (Player player : playerList) {
            if (currentPlayer == null) {
                return player;
            } else {
                if (currentPlayer.getUuid() != player.getUuid()) {
                    return player;
                } else {
                    int index = playerList.indexOf(player);

                    if (index == -1) {
                        throw new GameException("Player list does not contain player during iteration.");
                    }

                    return playerList.get(index + 1 >= playerList.size() ? 0 : index + 1);
                }
            }
        }

        throw new GameException("Did not select a new player. Is the player list empty?");
    }
}

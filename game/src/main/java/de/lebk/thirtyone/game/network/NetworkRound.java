package de.lebk.thirtyone.game.network;

import de.lebk.thirtyone.game.Player;
import de.lebk.thirtyone.game.Round;
import de.lebk.thirtyone.game.item.Deck;
import de.lebk.thirtyone.game.item.DeckIntegrityException;
import de.lebk.thirtyone.game.network.exception.ConnectError;

public class NetworkRound extends Round
{
    protected static final int MAX_PLAYERS = 4;
    protected Deck staple;

    public NetworkRound()
    {
        staple = Deck.newDeck();
    }

    public void join(NetworkPlayer player) throws ConnectError
    {
        if (players.size() >= MAX_PLAYERS) {
            throw new ConnectError("Round size limit reached!");
        }

        players.add(player);
    }

    public void leave(NetworkPlayer player)
    {
        players.remove(player);
    }

    public void broadcast(Message message)
    {
        for (Player player : players) {
            ((NetworkPlayer) player).send(message);
        }
    }

    public void start() throws DeckIntegrityException
    {
        for (Player p : players) {
            p.setDeck(staple.deal(3));
            p.getRound().setStarted(true);
            ((NetworkPlayer) p).update();
        }
    }
}

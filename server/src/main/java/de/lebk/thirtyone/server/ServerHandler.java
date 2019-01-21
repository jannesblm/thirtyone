package de.lebk.thirtyone.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import de.lebk.thirtyone.game.GameException;
import de.lebk.thirtyone.game.Player;
import de.lebk.thirtyone.game.RoundEnd;
import de.lebk.thirtyone.game.network.Message;
import de.lebk.thirtyone.game.network.NetworkPlayer;
import de.lebk.thirtyone.game.network.NetworkRound;
import de.lebk.thirtyone.game.network.exception.ConnectError;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ServerHandler extends SimpleChannelInboundHandler<Message>
{
    private static final Logger LOG = LogManager.getLogger();

    private final NetworkPlayer player;

    ServerHandler(NetworkRound round)
    {
        player = new NetworkPlayer(round);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message)
    {
        if (! player.isJoined()) {
            if (!message.getCommand().equalsIgnoreCase("HELLO")) {
                LOG.debug("Player " + player.getUuid() + " is not joined and did not send HELLO command.");

                try {
                    ctx.close().sync();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }

            try {
                // TODO: Version validation

                player.join(ctx.channel());

                LOG.info("Player " + player.getRound().playerCount() + " joined with UUID " + player.getUuid());
                return;
            } catch (ConnectError e) {
                player.disconnect(e.getMessage());
                LOG.debug("Player " + player.getUuid() + " could not join: " + e.getMessage());
            } catch (Exception e) {
                player.disconnect("Internal server error");
                LOG.debug("Internal error on player join: " + e.getMessage());
                e.printStackTrace();
            }
        }

        NetworkRound round = player.getRound();

        if (round.isStarted() && !player.isOnTurn()) {
            player.disconnect("Not your turn!");
        }

        try {
            switch (message.getCommand()) {
                case "START":
                    round.start();
                    break;
                case "PUSH":
                    // TODO: Re-deal three cards when every player pushed.
                    round.next(player.getName() + " schiebt.");
                    break;
                case "PASS":
                    player.setPassed(true);
                    round.next(player.getName() + " passt.");
                    break;
                case "SWAP":
                    JsonArray swapIndexes = (JsonArray) message.getJSON();
                    round.swap(swapIndexes.get(0).getAsInt(), swapIndexes.get(1).getAsInt());
                    round.next(player.getName() + " tauscht eine Karte.");
                    break;
            }
        } catch (RoundEnd roundEnd) {
            round.setStarted(false);

            round.broadcast(new Message("TELL",
                    new JsonPrimitive("Runde vorbei: " + roundEnd.getMessage())));

            // Determine winner and losers
            List<Player> winners = round.getWinner();
            List<Player> losers = round.getLoser();

            round.broadcast(new Message("TELL", new JsonPrimitive(
                    "Gewinner: " + String.join(", ",
                            winners.stream().map(Player::getName).toArray(String[]::new))
            )));

            round.broadcast(new Message("TELL", new JsonPrimitive(
                    "Verlierer: " + String.join(", ",
                            losers.stream().map(Player::getName).toArray(String[]::new)) + " (-1 Leben)"
            )));

            losers.forEach(player -> player.setLifes(player.getLifes() - 1));

            // TODO: Kick players with zero lifes left?

            round.updateAll(null);
            round.reset();
        } catch (GameException exception) {
            LOG.error(exception);
        }

    }

    public void channelInactive(ChannelHandlerContext ctx)
    {
        LOG.info("Player " + player.getUuid() + " (" + ctx.channel().remoteAddress() + ") left.");
        player.leave();
    }

    public void channelActive(ChannelHandlerContext ctx)
    {
        LOG.debug("Got new connection from " + ctx.channel().remoteAddress() + ". Player with UUID "
                + player.getUuid() + " instantiated.");
    }

}

package de.lebk.thirtyone.server;

import de.lebk.thirtyone.game.GameException;
import de.lebk.thirtyone.game.network.Message;
import de.lebk.thirtyone.game.network.NetworkPlayer;
import de.lebk.thirtyone.game.network.NetworkRound;
import de.lebk.thirtyone.game.network.exception.ConnectError;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

        try {
            switch (message.getCommand()) {
                case "START":
                    if (!round.isStarted() && round.playerCount() >= 1) {
                        round.start();
                    }
                    break;
                case "PUSH":
                    round.setCurrentPlayer(round.nextPlayer());
                    round.updateAll();
                    break;
                case "PASS":
                    player.setPassed(true);
                    round.setCurrentPlayer(round.nextPlayer());
                    round.updateAll();
                    break;
                case "SWAP":
                    break;
            }
        } catch (GameException exception) {
            LOG.debug(exception);
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

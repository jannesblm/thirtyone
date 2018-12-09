package de.lebk.thirtyone.server;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import de.lebk.thirtyone.game.network.Message;
import de.lebk.thirtyone.game.network.NetworkPlayer;
import de.lebk.thirtyone.game.network.NetworkRound;
import de.lebk.thirtyone.game.network.exception.ConnectError;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class PlayerHandler extends SimpleChannelInboundHandler<Message>
{
    private static final Logger LOG = LogManager.getLogger();

    private final NetworkRound round;
    private final NetworkPlayer player;

    PlayerHandler(NetworkRound round)
    {
        player = new NetworkPlayer();
        this.round = round;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message)
    {
        if (player.isConnected()) {

        } else {
            if (!message.getCommand().equals("HELLO")) {
                LOG.debug("Player " + player.getUuid() + " is not connected and did not send HELLO command. Disconnecting.");
                ctx.close();
            }

            try {
                JsonElement versionMember = message.get("version").orElseThrow();
                String version = versionMember.getAsString();

                Server.validateVersion(version);
                round.join(player);

                // Join was successful. Welcome the player.
                ctx.writeAndFlush(Message.prepare("HELLO"));
                LOG.info("Player " + round.playerCount() + " joined with UUID " + player.getUuid());
            } catch (ConnectError e) {
                Map<String, String> reason = Map.of("reason", e.getMessage());

                ctx.writeAndFlush(Message.prepare("BYE",
                        new GsonBuilder().create().toJsonTree(reason))).addListener(ChannelFutureListener.CLOSE);
                LOG.debug("Player " + player.getUuid() + " could not join: " + e.getMessage());
            } catch (Exception e) {
                Map<String, String> reason = Map.of("reason", "Internal server error");

                ctx.writeAndFlush(Message.prepare("BYE",
                        new GsonBuilder().create().toJsonTree(reason))).addListener(ChannelFutureListener.CLOSE);
                LOG.debug("Internal error on player join: " + e.getMessage());
            }
        }
    }

    public void channelInactive(ChannelHandlerContext ctx)
    {
        LOG.info("Player " + player.getUuid() + " (" + ctx.channel().remoteAddress() + ") left.");
        round.leave(player);
    }

    public void channelActive(ChannelHandlerContext ctx)
    {
        LOG.debug("Got new connection from " + ctx.channel().remoteAddress() + ". Player with UUID "
                + player.getUuid() + " instantiated.");
    }

}

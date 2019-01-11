package de.lebk.thirtyone.client;

import de.lebk.thirtyone.game.Player;
import de.lebk.thirtyone.game.network.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientHandler extends SimpleChannelInboundHandler<Message>
{
    private static final Logger LOG = LogManager.getLogger();

    private ObservedProperty<Player> player;

    public ClientHandler(ObservedProperty<Player> player)
    {
        this.player = player;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message)
    {
        LOG.debug("Got command: " + message.getCommand());

        if (message.getCommand().equalsIgnoreCase("PLAYER")) {
            Player newPlayer = Player.fromJson(message.getJSON());
            newPlayer.setChannel(ctx.channel());

            this.player.change(newPlayer);
        }
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        ctx.fireChannelInactive();
    }
}

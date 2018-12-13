package de.lebk.thirtyone.client;

import de.lebk.thirtyone.game.Player;
import de.lebk.thirtyone.game.Round;
import de.lebk.thirtyone.game.item.Deck;
import de.lebk.thirtyone.game.network.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.UuidUtil;

import java.util.UUID;

public class ClientHandler extends SimpleChannelInboundHandler<Message>
{
    private static final Logger LOG = LogManager.getLogger();

    private Player player;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message)
    {
        if (player == null) {
            boolean valid = message.getCommand().equalsIgnoreCase("HELLO")
                    && message.get("uuid").isPresent();

            if (! valid) {
                return;
            }

            player = new Player(UUID.fromString(message.get("uuid").get().getAsString()));
        }
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        ctx.fireChannelInactive();
    }
}

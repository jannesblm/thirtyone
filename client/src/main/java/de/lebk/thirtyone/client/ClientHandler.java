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

    private Player player;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message)
    {
        LOG.debug(message);
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        ctx.fireChannelInactive();
    }
}

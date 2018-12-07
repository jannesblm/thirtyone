package de.lebk.thirtyone.server;

import de.lebk.thirtyone.game.network.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class MessageHandler extends ChannelInboundHandlerAdapter
{
    private static final Logger LOG = LogManager.getLogger(MessageHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    {
        Optional<Message> message = Message.parse((String) msg);
    }
}

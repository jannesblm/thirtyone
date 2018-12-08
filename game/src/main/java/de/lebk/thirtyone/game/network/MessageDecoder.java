package de.lebk.thirtyone.game.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.SocketAddress;
import java.util.List;

public class MessageDecoder extends MessageToMessageDecoder<String>
{
    private static final Logger LOG = LogManager.getLogger();

    @Override
    protected void decode(ChannelHandlerContext ctx, String s, List<Object> out)
    {
        SocketAddress remoteAddress = ctx.channel().remoteAddress();

        Message.parse(s)
                .ifPresentOrElse(out::add, () -> {
                    LOG.debug("Got invalid message from " + remoteAddress + ". Closing connection.");
                    ctx.close();
                });
    }
}

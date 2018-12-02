package de.lebk.thirtyone.game.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerHandler extends SimpleChannelInboundHandler<String>
{
    private static final Logger logger = LogManager.getLogger(ServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception
    {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        ByteBuf in = (ByteBuf) msg;
        try {
            while (in.isReadable()) {
                char text = (char) in.readByte();
                System.out.println(text);
                logger.info(text);
                System.out.flush();
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}

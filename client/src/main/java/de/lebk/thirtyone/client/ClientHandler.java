package de.lebk.thirtyone.client;

import de.lebk.thirtyone.game.network.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<Message>
{
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message)
    {

    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        ctx.fireChannelInactive();
    }
}

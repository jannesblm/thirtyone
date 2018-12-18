package de.lebk.thirtyone.client;

import de.lebk.thirtyone.game.network.exception.ConnectError;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Client
{
    private static final Logger LOG = LogManager.getLogger();

    private String host;
    private int port;

    Client()
    {
        host = "";
        port = 0;
    }

    void connect(ChannelInitializer initializer) throws Exception
    {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(initializer);

            ChannelFuture f = bootstrap.connect(host, port);

            LOG.info("Trying " + host + ":" + port);

            f.addListener((ChannelFutureListener) cf -> {
                if (!cf.isSuccess()) {
                    throw new ConnectError("Connection failed");
                }

                onConnect(f.channel());
            });

            f.channel().closeFuture().addListener((ChannelFutureListener) cf -> onDisconnect(f.cause())).sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public void setHost(String newHost)
    {
        host = newHost;
    }

    public void setPort(int newPort)
    {
        port = newPort;
    }

    public abstract void disconnect();

    public abstract void onConnect(Channel ch);

    public abstract void onDisconnect(Throwable cause);
}

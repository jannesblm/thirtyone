package de.lebk.thirtyone.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BiConsumer;

public abstract class Client
{
    private static final Logger LOG = LogManager.getLogger();

    private String host;
    private String playerName;
    private int port;

    Client()
    {
        host = "";
        playerName = "Spieler";
        port = 0;
    }

    void connect(ChannelInitializer initializer, BiConsumer<Boolean, Throwable> result) throws Exception
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
                result.accept(cf.isSuccess(), cf.cause());

                onConnect(f.channel());
            });

            f.channel().closeFuture().addListener((ChannelFutureListener) cf -> onDisconnect(f.cause())).sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public void setHost(final String host)
    {
        this.host = host;
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public void setPlayerName(final String playerName)
    {
        this.playerName = playerName;
    }

    public void setPort(final int port)
    {
        this.port = port;
    }

    public abstract void disconnect();

    public abstract void onConnect(Channel ch);

    public abstract void onDisconnect(Throwable cause);
}

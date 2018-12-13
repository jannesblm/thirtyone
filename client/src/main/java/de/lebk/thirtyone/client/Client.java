package de.lebk.thirtyone.client;

import de.lebk.thirtyone.game.Player;
import de.lebk.thirtyone.game.network.MessageDecoder;
import de.lebk.thirtyone.game.network.exception.ConnectError;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import javafx.beans.property.SimpleObjectProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;

public abstract class Client
{
    private static final Logger LOG = LogManager.getLogger();

    protected SimpleObjectProperty<Player> player;
    protected String host;
    protected int port;

    public Client()
    {
        player = new SimpleObjectProperty<>(new Player());
        host = "";
        port = 0;
    }

    public void connect() throws Exception
    {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>()
            {
                @Override
                public void initChannel(SocketChannel ch) throws Exception
                {
                    ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                    ch.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8));
                    ch.pipeline().addLast(new MessageDecoder());
                    ch.pipeline().addLast(new ClientHandler(player));
                }
            });

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

    public SimpleObjectProperty<Player> getPlayerProperty()
    {
        return player;
    }
}

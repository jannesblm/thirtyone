package de.lebk.thirtyone.client;

import de.lebk.thirtyone.game.Player;
import de.lebk.thirtyone.game.network.Message;
import de.lebk.thirtyone.game.network.MessageDecoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;

public class ThreadedClient extends Client
{
    private static final Logger LOG = LogManager.getLogger();

    private BooleanProperty connected;
    private SimpleObjectProperty<Player> player;

    private Channel channel;
    private Thread thread;

    public ThreadedClient()
    {
        super();
        connected = new SimpleBooleanProperty(false);
        player = new SimpleObjectProperty<>(new Player());
    }

    public void connectAsync()
    {
        thread = new Thread(() -> {
            try {
                connect(new ChannelInitializer<SocketChannel>()
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
            } catch (Exception e) {
                LOG.debug(e);
            }
        });

        thread.start();
    }

    @Override
    public void disconnect()
    {
        if (connected.getValue()) {
            channel.writeAndFlush(Message.prepare("BYE"))
                    .addListeners(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void onConnect(Channel ch)
    {
        channel = ch;
        connected.setValue(true);

        channel.writeAndFlush(Message.prepare("HELLO"));
    }

    @Override
    public void onDisconnect(Throwable cause)
    {
        connected.setValue(false);
    }

    public BooleanProperty getConnectedProperty()
    {
        return connected;
    }

    public SimpleObjectProperty<Player> getPlayerProperty()
    {
        return player;
    }

    public boolean isConnected()
    {
        return connected.getValue();
    }
}

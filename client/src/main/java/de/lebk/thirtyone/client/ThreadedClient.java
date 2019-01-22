package de.lebk.thirtyone.client;

import com.google.gson.JsonPrimitive;
import de.lebk.thirtyone.game.Player;
import de.lebk.thirtyone.game.network.Message;
import de.lebk.thirtyone.game.network.MessageDecoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class ThreadedClient extends Client
{
    private static final Logger LOG = LogManager.getLogger();

    private ObservedProperty<Boolean> connected;
    private ObservedProperty<Player> player;
    private ObservedProperty<LogMessage> message;

    private Channel channel;
    private Thread thread;

    public ThreadedClient()
    {
        super();

        connected = new ObservedProperty<>(false);
        player = new ObservedProperty<>(new Player());
        message = new ObservedProperty<>(new LogMessage(""));
    }

    public CompletableFuture<ConnectStatus> connectAsync()
    {
        CompletableFuture<ConnectStatus> status = new CompletableFuture<>();

        thread = new Thread(() -> {
            try {
                connect(
                        new ChannelInitializer<SocketChannel>()
                        {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception
                            {
                                ch.pipeline().addLast(new LineBasedFrameDecoder(3072));
                                ch.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8));
                                ch.pipeline().addLast(new MessageDecoder());
                                ch.pipeline().addLast(new ClientHandler(player, message));
                            }
                        },
                        (result, exception) -> status.complete(
                                new ConnectStatus(result, exception)
                        )
                );
            } catch (Exception exception) {
                LOG.debug(exception);
            }
        });

        thread.start();

        return status;
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
        connected.change(true);

        channel.writeAndFlush(Message.prepare("HELLO", new JsonPrimitive(this.getPlayerName())));
    }

    @Override
    public void onDisconnect(Throwable cause)
    {
        connected.change(false);
        message.change(new LogMessage("Verbindung verloren.", Color.RED));
    }

    public ObservedProperty<Boolean> getConnectedProperty()
    {
        return connected;
    }

    public ObservedProperty<Player> getPlayerProperty()
    {
        return player;
    }

    public ObservedProperty<LogMessage> getMessageProperty()
    {
        return message;
    }

    public boolean isConnected()
    {
        return connected.getValue();
    }
}

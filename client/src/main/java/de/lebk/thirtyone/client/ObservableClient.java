package de.lebk.thirtyone.client;

import de.lebk.thirtyone.game.network.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ObservableClient extends ThreadedClient
{
    private BooleanProperty connected;
    private String lastError;

    private Channel channel;

    public ObservableClient()
    {
        super();
        connected = new SimpleBooleanProperty(false);
        lastError = "";
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
    }

    @Override
    public void onDisconnect(Throwable cause)
    {
        if (cause != null) {
            lastError = cause.getMessage();
        }

        connected.setValue(false);
    }

    public BooleanProperty getConnectedProperty()
    {
        return connected;
    }

    public String getLastError()
    {
        return lastError;
    }

    public boolean isConnected()
    {
        return connected.getValue();
    }
}

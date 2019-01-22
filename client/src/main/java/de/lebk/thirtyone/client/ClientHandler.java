package de.lebk.thirtyone.client;

import com.google.gson.JsonObject;
import de.lebk.thirtyone.game.Player;
import de.lebk.thirtyone.game.network.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientHandler extends SimpleChannelInboundHandler<Message>
{
    private static final Logger LOG = LogManager.getLogger();

    private ObservedProperty<Player> player;
    private ObservedProperty<LogMessage> message;

    public ClientHandler(ObservedProperty<Player> player, ObservedProperty<LogMessage> message)
    {
        this.player = player;
        this.message = message;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message)
    {
        LOG.debug("Got command: " + message.getCommand());

        switch (message.getCommand()) {
            case "PLAYER":
                Player newPlayer = Player.fromJson(message.getJSON());
                newPlayer.setChannel(ctx.channel());

                this.player.change(newPlayer);
                break;
            case "TELL":
                this.message.change(new LogMessage("[SERVER] " + message.getJSON().getAsString()));
                break;
            case "BYE":
                JsonObject reasonObject = message.getJSON().getAsJsonObject();
                this.message.change(new LogMessage("[SERVER] " + reasonObject.get("reason").getAsString(), Color.RED));
                break;
        }
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        ctx.fireChannelInactive();
    }
}

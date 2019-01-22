package de.lebk.thirtyone.client;

import javafx.scene.paint.Color;

public class LogMessage
{
    private final String message;
    private final Color color;

    public LogMessage(final String message)
    {
        this.message = message;
        this.color = Color.BLACK;
    }

    public LogMessage(final String message, final Color color)
    {
        this.message = message;
        this.color = color;
    }

    public String getMessage()
    {
        return message;
    }

    public Color getColor()
    {
        return color;
    }
}

package de.lebk.thirtyone.game.network;

public class MessageParseException extends Exception
{
    MessageParseException(String message)
    {
        super(message);
    }

    MessageParseException(String message, Throwable cause)
    {
        super(message, cause);
    }
}

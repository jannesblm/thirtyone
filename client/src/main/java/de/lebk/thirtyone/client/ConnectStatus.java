package de.lebk.thirtyone.client;

public final class ConnectStatus
{
    private final boolean success;
    private final Throwable error;

    public ConnectStatus()
    {
        success = true;
        error = null;
    }

    public ConnectStatus(Throwable error)
    {
        success = false;
        this.error = error;
    }

    public ConnectStatus(boolean success, Throwable error)
    {
        this.success = success;
        this.error = error;
    }

    public Throwable getError()
    {
        return error;
    }

    public boolean isSuccess()
    {
        return success;
    }
}

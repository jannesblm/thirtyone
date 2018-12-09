package de.lebk.thirtyone.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ThreadedClient extends Client
{
    private static final Logger LOG = LogManager.getLogger();

    protected Thread serverThread;

    ThreadedClient()
    {
        super();
    }

    public void connectAsync()
    {
        serverThread = new Thread(() -> {
            try {
                connect();
            } catch (Exception e) {
                LOG.debug(e);
            }
        });

        serverThread.start();
    }
}

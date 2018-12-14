package de.lebk.thirtyone.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main
{
    private static final Logger LOG = LogManager.getLogger();

    public static void main(String[] args)
    {
        LOG.info("Starting Thirtyone Server");

        Server server = new Server(25566);

        try {
            server.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

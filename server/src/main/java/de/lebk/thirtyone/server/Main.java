package de.lebk.thirtyone.server;

public class Main
{
    public static void main(String[] args)
    {
        Server server = new Server(25565);

        try {
            server.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package de.lebk.thirtyone.client;

public class Main
{
    public static void main(String[] args)
    {
        Client client = new Client("localhost", 25565);
        try {
            client.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

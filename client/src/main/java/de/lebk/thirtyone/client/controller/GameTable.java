package de.lebk.thirtyone.client.controller;

import de.lebk.thirtyone.client.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class GameTable extends Application
{
    private Client client;

    public static void main(String[] args)
    {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(this.getClass().getClassLoader().getResource("GameTable.fxml"));

        primaryStage.setTitle("Thirtyone");
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public void currentPlayerBoxClicked(MouseEvent mouseEvent)
    {
        System.out.println(((ToggleButton) mouseEvent.getSource()).getUserData());
    }

    public void rightOpponentBoxClicked(MouseEvent mouseEvent)
    {
    }

    public void middleOpponentBoxClicked(MouseEvent mouseEvent)
    {
    }

    public void leftOpponentBoxClicked(MouseEvent mouseEvent)
    {
    }

    public void middleBoxClicked(MouseEvent mouseEvent)
    {
    }
}

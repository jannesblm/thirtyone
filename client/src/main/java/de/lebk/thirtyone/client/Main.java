package de.lebk.thirtyone.client;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    public void start(Stage stage)
    {
        stage.setTitle("Thirtyone");

        Group root = new Group();
        Scene scene = new Scene(root);
        stage.setScene(scene);


    }
}

package com.npc.rk4;

import com.npc.rk4.Models.Model;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage){
        Model.getInstance().getViewFactory().showAppWindow();
    }

    public static void main(String[] args) {
        launch();
    }
}
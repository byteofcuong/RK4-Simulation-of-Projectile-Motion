package com.npc.rk4.Views;

import com.npc.rk4.Controllers.MenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewFactory {
    private static ViewFactory viewFactory;
    private MenuController menuController;

    private ViewFactory() {}

    public static ViewFactory getInstance() {
        if (viewFactory == null) {
            viewFactory = new ViewFactory();
        }
        return viewFactory;
    }

    public Pane getMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Menu.fxml"));
            Pane menuPane = loader.load();
            menuController = loader.getController();
            return menuPane;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public MenuController getMenuController() {
        return menuController;
    }

    public void showAppWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/App.fxml"));
        createStage(loader);
    }

    private void createStage(FXMLLoader loader) {
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Runge-Kutta Trajectory Simulation");
        stage.show();
    }
}
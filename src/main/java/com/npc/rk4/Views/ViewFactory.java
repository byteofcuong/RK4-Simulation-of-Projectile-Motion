package com.npc.rk4.Views;

import com.npc.rk4.Controllers.MenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

    public Stage createStage(FXMLLoader fxmlLoader) {
        Stage stage = null;
        try {
            Parent parent = fxmlLoader.load();
            stage = new Stage();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.setTitle("RK4");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stage;
    }
}

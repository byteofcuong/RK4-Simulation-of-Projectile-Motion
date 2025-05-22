package com.npc.rk4.Controllers;

import com.npc.rk4.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    public BorderPane app_parent;

    private static AppController instance;

    public static AppController getInstance() {
        return instance;
    }

    public AppController() {
        instance = this;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        app_parent.setCenter(Model.getInstance().getViewFactory().getOneGraphsView());
        app_parent.setTop(Model.getInstance().getViewFactory().getMenu());

    }

    private void handleChangeMenu() {
        Model.getInstance().getViewFactory().getSelectedMenuItem()
                .addListener(((observableValue, oldVal, newVal) -> {
                    switch (newVal) {
                        case ALPHA -> {
                            //a
                        }
                        case MASS -> {
                           //m
                        }
                        case N_STEP -> {
                            //n
                        }
                        case X0 -> {
                            //x
                        }
                        case Y0 -> {
                            //y
                        }
                        case VX0 -> {
                            //vx
                        }
                        case VY0 -> {
                            //vy
                        }
                        case CALCULATE -> {
                            //c
                        }
                        case ONE_GRAPHS -> {
                            app_parent.setCenter(Model.getInstance().getViewFactory().getOneGraphsView());
                        }
                        case FOUR_GRAPHS -> {

                        }
                    }
                }));
    }
}

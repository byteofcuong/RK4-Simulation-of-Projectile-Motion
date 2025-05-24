package com.npc.rk4.Models;

import com.npc.rk4.Controllers.MenuController;
import com.npc.rk4.Views.ViewFactory;

public class Model {
    private static Model model;
    private final ViewFactory viewFactory;
    private final MenuController menuController;

    private Model() {
        this.menuController = new MenuController();
        this.viewFactory = ViewFactory.getInstance();
    }

    public static synchronized Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    public MenuController getMenuController() {
        return menuController;
    }
}

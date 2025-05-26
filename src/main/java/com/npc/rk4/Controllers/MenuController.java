package com.npc.rk4.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleButton;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    public Spinner<Double> alpha_spn;
    public Spinner<Double> mass_spn;
    public Spinner<Double> nstep_spn;
    public Spinner<Double> x0_spn;
    public Spinner<Double> y0_spn;
    public Spinner<Double> vx0_spn;
    public Spinner<Double> vy0_spn;
    public Button calculate_btn;
    public ToggleButton onegraphs_btn;
    public ToggleButton fourgraphs_btn;

    private AppController appController;

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        double dmax = Double.MAX_VALUE;

        SpinnerValueFactory<Double> alphaFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, dmax, 0.1, 0.1);
        alpha_spn.setValueFactory(alphaFactory);

        SpinnerValueFactory<Double> massFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, dmax, 1.0, 1.0);
        mass_spn.setValueFactory(massFactory);

        SpinnerValueFactory<Double> nstepFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(10, 1000, 50, 10);
        nstep_spn.setValueFactory(nstepFactory);

        SpinnerValueFactory<Double> x0Factory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(-100000000, dmax, 0, 1);
        x0_spn.setValueFactory(x0Factory);

        SpinnerValueFactory<Double> y0Factory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, dmax, 0, 1);
        y0_spn.setValueFactory(y0Factory);

        SpinnerValueFactory<Double> vx0Factory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, dmax, 50, 1);
        vx0_spn.setValueFactory(vx0Factory);
        vx0_spn.setEditable(true);

        SpinnerValueFactory<Double> vy0Factory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, dmax, 50, 1);
        vy0_spn.setValueFactory(vy0Factory);
        vy0_spn.setEditable(true);

        calculate_btn.setOnAction(this::onCalculate);
        onegraphs_btn.setOnAction(e -> showSingleGraph());
        fourgraphs_btn.setOnAction(e -> showFourGraphs());
    }

    public double[] getSpinnerValues() {
        return new double[] {
                alpha_spn.getValue(),
                mass_spn.getValue(),
                nstep_spn.getValue(),
                x0_spn.getValue(),
                y0_spn.getValue(),
                vx0_spn.getValue(),
                vy0_spn.getValue()
        };
    }

    private void onCalculate(ActionEvent event) {
        if (appController != null) {
            appController.calculateTrajectory();
        }
    }

    private void showSingleGraph() {
        if (appController != null) {
            appController.showSingleGraph();
            onegraphs_btn.setSelected(true);
            fourgraphs_btn.setSelected(false);
        }
    }

    private void showFourGraphs() {
        if (appController != null) {
            appController.showFourGraphs();
            onegraphs_btn.setSelected(false);
            fourgraphs_btn.setSelected(true);
        }
    }
}
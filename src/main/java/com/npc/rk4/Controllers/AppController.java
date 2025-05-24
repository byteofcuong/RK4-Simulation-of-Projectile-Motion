package com.npc.rk4.Controllers;

import com.npc.rk4.Models.TrajectoryModel;
import com.npc.rk4.Views.ViewFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    @FXML
    private BorderPane app_parent;

    private MenuController menuController;
    private Pane chartPane;

    private LineChart<Number, Number> trajectoryChart;
    private LineChart<Number, Number> xTimeChart;
    private LineChart<Number, Number> yTimeChart;
    private LineChart<Number, Number> vxTimeChart;
    private LineChart<Number, Number> vyTimeChart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Tạo phần menu và lấy controller
        Pane menuPane = ViewFactory.getInstance().getMenu();
        menuController = ViewFactory.getInstance().getMenuController();

        // Thiết lập liên kết giữa MenuController và AppController
        if (menuController != null) {
            menuController.setAppController(this);
        }

        // Tạo chart pane
        chartPane = new Pane();

        // Khởi tạo các biểu đồ
        initializeCharts();

        // Thiết lập mặc định hiển thị một biểu đồ
        showSingleGraph();

        // Thiết lập layout
        app_parent.setTop(menuPane);
        app_parent.setCenter(chartPane);
    }

    private void initializeCharts() {
        // Biểu đồ quỹ đạo
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("X Position (m)");
        yAxis.setLabel("Y Position (m)");
        trajectoryChart = new LineChart<>(xAxis, yAxis);
        trajectoryChart.setTitle("Quỹ đạo y(x)");
        trajectoryChart.setPrefSize(800, 600);

        // Biểu đồ x(t)
        NumberAxis xtX = new NumberAxis();
        NumberAxis xtY = new NumberAxis();
        xtX.setLabel("Time (s)");
        xtY.setLabel("X Position (m)");
        xTimeChart = new LineChart<>(xtX, xtY);
        xTimeChart.setTitle("x(t)");
        xTimeChart.setPrefSize(400, 300);

        // Biểu đồ y(t)
        NumberAxis ytX = new NumberAxis();
        NumberAxis ytY = new NumberAxis();
        ytX.setLabel("Time (s)");
        ytY.setLabel("Y Position (m)");
        yTimeChart = new LineChart<>(ytX, ytY);
        yTimeChart.setTitle("y(t)");
        yTimeChart.setPrefSize(400, 300);

        // Biểu đồ vx(t)
        NumberAxis vxtX = new NumberAxis();
        NumberAxis vxtY = new NumberAxis();
        vxtX.setLabel("Time (s)");
        vxtY.setLabel("X Velocity (m/s)");
        vxTimeChart = new LineChart<>(vxtX, vxtY);
        vxTimeChart.setTitle("vx(t)");
        vxTimeChart.setPrefSize(400, 300);

        // Biểu đồ vy(t)
        NumberAxis vytX = new NumberAxis();
        NumberAxis vytY = new NumberAxis();
        vytX.setLabel("Time (s)");
        vytY.setLabel("Y Velocity (m/s)");
        vyTimeChart = new LineChart<>(vytX, vytY);
        vyTimeChart.setTitle("vy(t)");
        vyTimeChart.setPrefSize(400, 300);

        // Điều chỉnh vị trí cho 4 biểu đồ
        xTimeChart.setLayoutX(0);
        xTimeChart.setLayoutY(0);

        yTimeChart.setLayoutX(400);
        yTimeChart.setLayoutY(0);

        vxTimeChart.setLayoutX(0);
        vxTimeChart.setLayoutY(300);

        vyTimeChart.setLayoutX(400);
        vyTimeChart.setLayoutY(300);
    }

    public void showSingleGraph() {
        chartPane.getChildren().clear();
        chartPane.getChildren().add(trajectoryChart);
    }

    public void showFourGraphs() {
        chartPane.getChildren().clear();
        chartPane.getChildren().addAll(xTimeChart, yTimeChart, vxTimeChart, vyTimeChart);
    }

    public void calculateTrajectory() {
        if (menuController == null) return;

        double[] values = menuController.getSpinnerValues();
        double alpha = values[0];
        double mass = values[1];
        int nSteps = (int) values[2];
        double x0 = values[3];
        double y0 = values[4];
        double vx0 = values[5];
        double vy0 = values[6];

        // Tính toán quỹ đạo
        TrajectoryModel.TrajectoryData data = TrajectoryModel.calculateTrajectory(
                alpha, mass, nSteps, x0, y0, vx0, vy0);

        // Cập nhật biểu đồ
        updateCharts(data);
    }

    private void updateCharts(TrajectoryModel.TrajectoryData data) {
        // Xóa dữ liệu cũ
        trajectoryChart.getData().clear();
        xTimeChart.getData().clear();
        yTimeChart.getData().clear();
        vxTimeChart.getData().clear();
        vyTimeChart.getData().clear();

        // Tạo series cho biểu đồ quỹ đạo
        XYChart.Series<Number, Number> trajectorySeries = new XYChart.Series<>();
        trajectorySeries.setName("Quỹ đạo");

        // Tạo series cho các biểu đồ theo thời gian
        XYChart.Series<Number, Number> xSeries = new XYChart.Series<>();
        xSeries.setName("x(t)");

        XYChart.Series<Number, Number> ySeries = new XYChart.Series<>();
        ySeries.setName("y(t)");

        XYChart.Series<Number, Number> vxSeries = new XYChart.Series<>();
        vxSeries.setName("vx(t)");

        XYChart.Series<Number, Number> vySeries = new XYChart.Series<>();
        vySeries.setName("vy(t)");

        // Thêm dữ liệu vào series
        for (int i = 0; i < data.time.length; i++) {
            if (data.y[i] >= 0) { // Chỉ hiển thị khi vật ở trên mặt đất (y >= 0)
                // Thêm điểm vào series quỹ đạo
                XYChart.Data<Number, Number> trajectoryPoint = new XYChart.Data<>(data.x[i], data.y[i]);
                trajectorySeries.getData().add(trajectoryPoint);

                // Thêm điểm vào series x(t)
                XYChart.Data<Number, Number> xPoint = new XYChart.Data<>(data.time[i], data.x[i]);
                xSeries.getData().add(xPoint);

                // Thêm điểm vào series y(t)
                XYChart.Data<Number, Number> yPoint = new XYChart.Data<>(data.time[i], data.y[i]);
                ySeries.getData().add(yPoint);

                // Thêm điểm vào series vx(t)
                XYChart.Data<Number, Number> vxPoint = new XYChart.Data<>(data.time[i], data.vx[i]);
                vxSeries.getData().add(vxPoint);

                // Thêm điểm vào series vy(t)
                XYChart.Data<Number, Number> vyPoint = new XYChart.Data<>(data.time[i], data.vy[i]);
                vySeries.getData().add(vyPoint);
            }
        }

        // Thêm series vào biểu đồ
        trajectoryChart.getData().add(trajectorySeries);
        xTimeChart.getData().add(xSeries);
        yTimeChart.getData().add(ySeries);
        vxTimeChart.getData().add(vxSeries);
        vyTimeChart.getData().add(vySeries);

        // Thêm tooltips vào các điểm dữ liệu
        addTooltips(trajectorySeries, data);
        addTooltips(xSeries, data);
        addTooltips(ySeries, data);
        addTooltips(vxSeries, data);
        addTooltips(vySeries, data);
    }

    // Thêm phương thức mới để tạo tooltips
    private void addTooltips(XYChart.Series<Number, Number> series, TrajectoryModel.TrajectoryData data) {
        int dataSize = series.getData().size();

        for (int i = 0; i < dataSize; i++) {
            final int index = i;
            XYChart.Data<Number, Number> dataPoint = series.getData().get(i);

            Tooltip tooltip = new Tooltip(
                    String.format("Time: %.2f s\nX: %.2f m\nY: %.2f m\nVx: %.2f m/s\nVy: %.2f m/s",
                            data.time[index], data.x[index], data.y[index],
                            data.vx[index], data.vy[index])
            );

            // Cải thiện hiệu suất của tooltip
            tooltip.setShowDelay(Duration.millis(100));
            tooltip.setHideDelay(Duration.millis(200));

            // Đảm bảo đã import: javafx.scene.Node
            Node node = dataPoint.getNode();

            // Bắt buộc phải thêm vào sau khi node được render
            if (node != null) {
                Tooltip.install(node, tooltip);
            } else {
                // Sử dụng Platform.runLater nếu node chưa được render
                Platform.runLater(() -> {
                    Node newNode = dataPoint.getNode();
                    if (newNode != null) {
                        Tooltip.install(newNode, tooltip);
                    }
                });
            }
        }
    }
}
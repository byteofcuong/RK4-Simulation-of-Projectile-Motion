package com.npc.rk4.Models;

public class TrajectoryModel {
    private static final double G = 9.81;
    private static final double TIME_TOLERANCE = 0.1; // Dung sai cho thời gian
    private static final int MAX_BINARY_SEARCH_ITERATIONS = 20; // Số lần lặp tối đa cho tìm kiếm nhị phân

    public static class TrajectoryData {
        public double[] time, x, y, vx, vy;

        public TrajectoryData(double[] time, double[] x, double[] y, double[] vx, double[] vy) {
            this.time = time;
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
        }
    }

    public static TrajectoryData calculateTrajectory(
            double alpha, double mass, int nSteps,
            double x0, double y0, double vx0, double vy0,
            ODESolver solver) {

        // Ước tính thời gian ban đầu
        double initialEstimatedTime = estimateInitialTime(vy0);

        // Tìm thời gian tốt hơn bằng tìm kiếm nhị phân
        double optimalTime = findOptimalTime(alpha, mass, nSteps, x0, y0, vx0, vy0, solver, initialEstimatedTime);

        // Tính toán quỹ đạo với thời gian tối ưu
        return calculateWithEstimatedTime(alpha, mass, nSteps, x0, y0, vx0, vy0, optimalTime, solver);
    }

    private static double estimateInitialTime(double vy0) {
        // Ước tính thời gian ban đầu dựa trên chuyển động thẳng đứng
        double estimatedTime = 2 * vy0 / G;
        return estimatedTime <= 0 ? 10 : estimatedTime;
    }

    private static double findOptimalTime(
            double alpha, double mass, int nSteps,
            double x0, double y0, double vx0, double vy0,
            ODESolver solver, double initialEstimatedTime) {

        double left = initialEstimatedTime * 0.1; // Giới hạn dưới
        double right = initialEstimatedTime * 5; // Giới hạn trên
        double optimalTime = initialEstimatedTime;

        for (int i = 0; i < MAX_BINARY_SEARCH_ITERATIONS; i++) {
            TrajectoryData result = calculateWithEstimatedTime(
                    alpha, mass, nSteps, x0, y0, vx0, vy0, optimalTime, solver);

            // Lấy chỉ số cuối cùng của mảng
            int lastIndex = result.y.length - 1;

            if (Math.abs(result.y[lastIndex]) < TIME_TOLERANCE) {
                // Đã tìm thấy thời gian tốt
                break;
            } else if (result.y[lastIndex] > 0) {
                // Vật chưa chạm đất, cần tăng thời gian
                left = optimalTime;
            } else {
                // Vật đã chạm đất, cần giảm thời gian
                right = optimalTime;
            }

            // Tính thời gian mới ở giữa khoảng
            optimalTime = (left + right) / 2;
        }

        return optimalTime;
    }

    private static TrajectoryData calculateWithEstimatedTime(
            double alpha, double mass, int nSteps,
            double x0, double y0, double vx0, double vy0, double estimatedTime,
            ODESolver solver) {

        ODESolver.TriFunction<Double, Double, Double, Double> ax = (t, x, vx) -> -alpha * vx / mass;
        ODESolver.TriFunction<Double, Double, Double, Double> ay = (t, y, vy) -> -G - alpha * vy / mass;

        double[][] xRes = solver.solve(ax, 0, estimatedTime, x0, vx0, nSteps);
        double[][] yRes = solver.solve(ay, 0, estimatedTime, y0, vy0, nSteps);

        double[] x = xRes[0], vx = xRes[1], y = yRes[0], vy = yRes[1];
        double[] time = solver.linspace(0, estimatedTime, nSteps + 1);

        int last = nSteps;
        boolean found = false;
        for (int i = 0; i <= nSteps; i++) {
            if (y[i] < 0) {
                last = i;
                found = true;
                break;
            }
        }
        if (found) {
            int i0 = last - 1, i1 = last;
            double t = (0 - y[i0]) / (y[i1] - y[i0]);
            if (Double.isNaN(t) || Double.isInfinite(t) || Math.abs(y[i1] - y[i0]) < 1e-10)
                t = 0.5;
            time[i1] = time[i0] + t * (time[i1] - time[i0]);
            x[i1] = x[i0] + t * (x[i1] - x[i0]);
            y[i1] = 0;
            vx[i1] = vx[i0] + t * (vx[i1] - vx[i0]);
            vy[i1] = vy[i0] + t * (vy[i1] - vy[i0]);
            double[] tTime = new double[i1 + 1], tX = new double[i1 + 1], tY = new double[i1 + 1],
                    tVx = new double[i1 + 1], tVy = new double[i1 + 1];
            System.arraycopy(time, 0, tTime, 0, i1 + 1);
            System.arraycopy(x, 0, tX, 0, i1 + 1);
            System.arraycopy(y, 0, tY, 0, i1 + 1);
            System.arraycopy(vx, 0, tVx, 0, i1 + 1);
            System.arraycopy(vy, 0, tVy, 0, i1 + 1);
            return new TrajectoryData(tTime, tX, tY, tVx, tVy);
        }
        return new TrajectoryData(time, x, y, vx, vy);
    }
}
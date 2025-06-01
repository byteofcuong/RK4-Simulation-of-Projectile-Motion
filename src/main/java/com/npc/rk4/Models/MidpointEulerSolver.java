package com.npc.rk4.Models;

public class MidpointEulerSolver implements ODESolver {
    @Override
    public double[][] solve(TriFunction<Double, Double, Double, Double> d2y,
                            double xMin, double xMax, double y0, double dy0, int nSteps) {
        double[] x = linspace(xMin, xMax, nSteps + 1);
        double h = x[1] - x[0];
        double[] y = new double[nSteps + 1];
        double[] u = new double[nSteps + 1]; // u = dy/dx

        y[0] = y0;
        u[0] = dy0;

        for (int i = 0; i < nSteps; i++) {
            double k1y = h * u[i];
            double k1u = h * d2y.apply(x[i], y[i], u[i]);

            double yMid = y[i] + k1y / 2;
            double uMid = u[i] + k1u / 2;
            double xMid = x[i] + h / 2;

            double k2y = h * uMid;
            double k2u = h * d2y.apply(xMid, yMid, uMid);

            y[i + 1] = y[i] + k2y;
            u[i + 1] = u[i] + k2u;
        }

        return new double[][]{y, u};
    }
    @Override
    public double[] linspace(double min, double max, int n) {
        double[] result = new double[n];
        double step = (max - min) / (n - 1);
        for (int i = 0; i < n; i++) result[i] = min + i * step;
        return result;
    }
}
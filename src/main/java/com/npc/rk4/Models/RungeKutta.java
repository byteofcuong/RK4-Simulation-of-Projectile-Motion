package com.npc.rk4.Models;

public class RungeKutta {

    @FunctionalInterface
    public interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }

    public static double[][] secondOrderRungeKutta(
            TriFunction<Double, Double, Double, Double> d2y,
            double xMin, double xMax,
            double y0, double dy0,
            int nSteps) {

        double[] x = linspace(xMin, xMax, nSteps + 1);
        double h = x[1] - x[0];
        double[] y = new double[nSteps + 1];
        double[] u = new double[nSteps + 1];  // u = dy/dx

        y[0] = y0;
        u[0] = dy0;

        for (int i = 0; i < nSteps; i++) {
            double K1 = h * u[i];
            double L1 = h * d2y.apply(x[i], y[i], u[i]);

            double K2 = h * (u[i] + L1/2);
            double L2 = h * d2y.apply(x[i] + h/2, y[i] + K1/2, u[i] + L1/2);

            double K3 = h * (u[i] + L2/2);
            double L3 = h * d2y.apply(x[i] + h/2, y[i] + K2/2, u[i] + L2/2);

            double K4 = h * (u[i] + L3);
            double L4 = h * d2y.apply(x[i] + h, y[i] + K3, u[i] + L3);

            y[i+1] = y[i] + (K1 + 2*K2 + 2*K3 + K4) / 6;
            u[i+1] = u[i] + (L1 + 2*L2 + 2*L3 + L4) / 6;
        }

        return new double[][]{y, u};
    }

    public static double[] linspace(double min, double max, int n) {
        double[] result = new double[n];
        double step = (max - min) / (n - 1);

        for (int i = 0; i < n; i++) {
            result[i] = min + i * step;
        }

        return result;
    }
}
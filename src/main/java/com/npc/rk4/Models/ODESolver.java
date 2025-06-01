package com.npc.rk4.Models;

public interface ODESolver {
    double[][] solve(
            TriFunction<Double, Double, Double, Double> d2y,
            double xMin, double xMax, double y0, double dy0, int nSteps
    );
    double[] linspace(double min, double max, int n);

    @FunctionalInterface
    interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }
}
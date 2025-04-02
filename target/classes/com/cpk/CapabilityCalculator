package com.cpk;

import java.util.List;

public class CapabilityCalculator {

    public static double calculateMean(List<Double> data) {
        double sum = 0;
        for (double value : data) {
            sum += value;
        }
        return Math.round((sum / data.size()) * 100.0) / 100.0;
    }

    public static double calculateMin(List<Double> data) {
        return data.stream().min(Double::compareTo).orElse(Double.NaN);
    }

    public static double calculateMax(List<Double> data) {
        return data.stream().max(Double::compareTo).orElse(Double.NaN);
    }

    public static double calculateCPStandardDeviation(List<Double> data, double mean) {
        double sum = 0;
        for (double value : data) {
            sum += Math.pow(value - mean, 2);
        }
        return Math.sqrt(sum / data.size());
    }

    public static double calculatePPStandardDeviation(List<Double> data, double mean) {
        double sum = 0;
        for (double value : data) {
            sum += Math.pow(value - mean, 2);
        }
        return Math.sqrt(sum / (data.size() - 1));
    }

    public static double calculateCp(double usl, double lsl, double stdDev) {
        return Math.round(((usl - lsl) / (6 * stdDev)) * 100.0) / 100.0;
    }

    public static double calculateCpk(double usl, double lsl, double mean, double stdDev) {
        double cpu = (usl - mean) / (3 * stdDev);
        double cpl = (mean - lsl) / (3 * stdDev);
        return Math.round(Math.min(cpu, cpl) * 100.0) / 100.0;
    }

    public static double calculatePp(double usl, double lsl, double stdDev) {
        return Math.round(((usl - lsl) / (6 * stdDev)) * 100.0) / 100.0;
    }

    public static double calculatePpk(double usl, double lsl, double mean, double stdDev) {
        double cpu = (usl - mean) / (3 * stdDev);
        double cpl = (mean - lsl) / (3 * stdDev);
        return Math.round(Math.min(cpu, cpl) * 100.0) / 100.0;
    }
}
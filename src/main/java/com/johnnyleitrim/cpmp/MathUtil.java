package com.johnnyleitrim.cpmp;

public class MathUtil {
  public static Details calcDetails(double[] vals) {
    double best = Double.MAX_VALUE;
    int sum = 0;
    for (double val : vals) {
      best = Math.min(best, val);
      sum += val;
    }

    double mean = sum / (double) vals.length;

    double sqDiff = 0;
    for (double val : vals) {
      sqDiff += (val - mean) * (val - mean);
    }

    double stdDev = Math.sqrt(sqDiff / vals.length);
    return new Details(best, mean, stdDev);
  }

  public static class Details {
    private final double best;
    private final double mean;
    private final double stdDev;

    public Details(double best, double mean, double stdDev) {
      this.best = best;
      this.mean = mean;
      this.stdDev = stdDev;
    }

    public double getBest() {
      return best;
    }

    public double getMean() {
      return mean;
    }

    public double getStdDev() {
      return stdDev;
    }
  }
}

package com.johnnyleitrim.cpmp;

public class MathUtil {
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

  public static Details calcDetails(double[] vals) {
    double best = Double.MAX_VALUE;
    int sum = 0;
    for (int i = 0; i < vals.length; i++) {
      best = Math.min(best, vals[i]);
      sum += vals[i];
    }

    double mean = sum / (double) vals.length;

    double sqDiff = 0;
    for (int i = 0; i < vals.length; i++) {
      sqDiff += (vals[i] - mean) * (vals[i] - mean);
    }

    double stdDev = Math.sqrt(sqDiff / vals.length);
    return new Details(best, mean, stdDev);
  }
}

package com.johnnyleitrim.cpmp.stats;

public class DecimalSolutionStats {
  private double nMoves;
  private double nLocalSearchMoves;
  private double nPerturbationMoves;
  private double durationMs;

  public DecimalSolutionStats(double nMoves, double nLocalSearchMoves, double nPerturbationMoves, double durationMs) {
    this.nMoves = nMoves;
    this.nLocalSearchMoves = nLocalSearchMoves;
    this.nPerturbationMoves = nPerturbationMoves;
    this.durationMs = durationMs;
  }

  public double getNMoves() {
    return nMoves;
  }

  public double getNLocalSearchMoves() {
    return nLocalSearchMoves;
  }

  public double getNPerturbationMoves() {
    return nPerturbationMoves;
  }

  public double getDurationMs() {
    return durationMs;
  }
}

package com.johnnyleitrim.cpmp.stats;

public class SolutionStats {
  private int nMoves;
  private int nLocalSearchMoves;
  private int nPerturbationMoves;
  private long durationMs;

  public SolutionStats(int nMoves, int nLocalSearchMoves, int nPerturbationMoves, long durationMs) {
    this.nMoves = nMoves;
    this.nLocalSearchMoves = nLocalSearchMoves;
    this.nPerturbationMoves = nPerturbationMoves;
    this.durationMs = durationMs;
  }

  public int getNMoves() {
    return nMoves;
  }

  public int getNLocalSearchMoves() {
    return nLocalSearchMoves;
  }

  public int getNPerturbationMoves() {
    return nPerturbationMoves;
  }

  public long getDurationMs() {
    return durationMs;
  }
}

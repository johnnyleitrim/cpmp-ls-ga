package com.johnnyleitrim.cpmp.stats;

import java.io.Closeable;

import com.johnnyleitrim.cpmp.Problem;

public interface StatsWriter extends Closeable {
  void writeSeed(long seed);

  void writeProblemName(Problem problem);

  void writeSolution(int nMoves, int nLocalSearchMoves, int nPerturbationMoves, long durationMs);
}

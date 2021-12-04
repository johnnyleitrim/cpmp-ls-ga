package com.johnnyleitrim.cpmp.stats;

import java.io.IOException;

import com.johnnyleitrim.cpmp.Problem;

public class StatsNullWriter implements StatsWriter {
  @Override
  public void writeSeed(long seed) {

  }

  @Override
  public void writeProblemName(Problem problem) {

  }

  @Override
  public void writeSolution(int nMoves, int nLocalSearchMoves, int nPerturbationMoves, long durationMs) {

  }

  @Override
  public void close() throws IOException {

  }
}

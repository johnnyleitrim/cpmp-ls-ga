package com.johnnyleitrim.cpmp.stats;

import java.io.BufferedReader;
import java.io.IOException;

public class StatsFileReader {

  public static ProblemStats read(BufferedReader reader) throws IOException {
    String line = reader.readLine();
    ProblemFileStats problemFileStats = null;
    ProblemStats problemStats = new ProblemStats();
    while (line != null) {
      char tag = line.charAt(0);
      String[] values = line.substring(2).split(StatsFileWriter.VALUE_DELIMITER);
      switch (tag) {
        case StatsFileWriter.CONFIG_TAG:
          assert values.length == 2;
          problemStats.addConfig(values[0], values[1]);
          break;
        case StatsFileWriter.SEED_TAG:
          break;
        case StatsFileWriter.FILE_TAG:
          assert values.length == 1;
          problemFileStats = new ProblemFileStats(values[0]);
          problemStats.addProblemFileStats(problemFileStats);
          break;
        case StatsFileWriter.SOLUTION_TAG:
          assert values.length == 4;
          int nMoves = Integer.valueOf(values[0]);
          int nLocalSearchMoves = Integer.valueOf(values[1]);
          int nPerturbationMoves = Integer.valueOf(values[2]);
          long durationMs = Integer.valueOf(values[3]);
          problemFileStats.addStats(new SolutionStats(nMoves, nLocalSearchMoves, nPerturbationMoves, durationMs));
          break;
        default:
          throw new IllegalArgumentException("Unexpected tag " + tag);
      }
      line = reader.readLine();
    }
    return problemStats;
  }
}

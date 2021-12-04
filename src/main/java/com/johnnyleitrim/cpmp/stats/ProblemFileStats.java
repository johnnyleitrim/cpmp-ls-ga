package com.johnnyleitrim.cpmp.stats;

import java.util.LinkedList;
import java.util.List;

public class ProblemFileStats {
  private String filename;
  private List<SolutionStats> allSolutionStats;

  public ProblemFileStats(String filename) {
    this.filename = filename;
    this.allSolutionStats = new LinkedList<>();
  }

  public void addStats(SolutionStats solutionStats) {
    allSolutionStats.add(solutionStats);
  }

  public String getFilename() {
    return filename;
  }

  public List<SolutionStats> getAllSolutionStats() {
    return allSolutionStats;
  }
}

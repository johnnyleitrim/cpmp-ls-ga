package com.johnnyleitrim.cpmp.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProblemStats {
  private Map<String, String> config;
  private List<ProblemFileStats> allProblemFileStats;

  public ProblemStats() {
    config = new HashMap<>();
    allProblemFileStats = new ArrayList<>(20);
  }

  public void addConfig(String key, String value) {
    config.put(key, value);
  }

  public void addProblemFileStats(ProblemFileStats problemFileStats) {
    allProblemFileStats.add(problemFileStats);
  }

  public Map<String, String> getConfig() {
    return config;
  }

  public List<ProblemFileStats> getAllProblemFileStats() {
    return allProblemFileStats;
  }
}

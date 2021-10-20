package com.johnnyleitrim.cpmp.stats;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.johnnyleitrim.cpmp.strategy.BestNeighbourTieBreakingStrategies;
import com.johnnyleitrim.cpmp.strategy.BestNeighbourTieBreakingStrategy;
import com.johnnyleitrim.cpmp.strategy.ClearStackSelectionStrategies;
import com.johnnyleitrim.cpmp.strategy.ClearStackSelectionStrategy;
import com.johnnyleitrim.cpmp.strategy.StackClearingStrategies;
import com.johnnyleitrim.cpmp.strategy.StackClearingStrategy;
import com.johnnyleitrim.cpmp.strategy.StackFillingStrategies;
import com.johnnyleitrim.cpmp.strategy.StackFillingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatsOverview {
  private static final Logger LOGGER = LoggerFactory.getLogger(StatsOverview.class);
  private static final int MAX_SOLUTIONS = 100;

  private static final Map<String, String> BASELINE_CONFIG = Map.of(
      "Fill Stack After Clearing", "false",
      "Clear Stack Strategy", "Random",
      "Fill Stack Strategy", "Largest container",
      "Best Neighbour Tie Breaking Strategy", "Random",
      "Clear Stack Selection Strategy", "Random"
  );

  public static void main(String[] args) throws IOException {
    List<ProblemStats> allProblemStats = new ArrayList<>();

    try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(Paths.get(args[0]), args[1] + "*.log")) {
      dirStream.forEach(path -> {
        try {
          allProblemStats.add(StatsFileReader.read(Files.newBufferedReader(path)));
        } catch (Exception e) {
          LOGGER.error("Problem reading: {}", path, e);
        }
      });
    }

    if (allProblemStats.isEmpty()) {
      throw new IllegalArgumentException("No stat files found");
    }

    System.out.println(" | Baseline | Fill Stack Strategy | | | | Clear Stack Strategy | | Best Neighbour Tie Breaking Strategy | | | Clear Stack Selection Strategy | | |");

    List<Map<String, String>> sortOrder = new ArrayList<>(allProblemStats.size());
    sortOrder.add(BASELINE_CONFIG);
    for (boolean fillStackAfterClearing : List.of(true, false)) {
      for (StackFillingStrategy stackFillingStrategy : StackFillingStrategies.ALL) {
        Map<String, String> config = new HashMap<>(BASELINE_CONFIG);
        config.put("Fill Stack After Clearing", String.valueOf(fillStackAfterClearing));
        config.put("Fill Stack Strategy", stackFillingStrategy.getName());
        sortOrder.add(config);
      }
    }

    for (StackClearingStrategy stackClearingStrategy : StackClearingStrategies.ALL) {
      Map<String, String> config = new HashMap<>(BASELINE_CONFIG);
      config.put("Clear Stack Strategy", stackClearingStrategy.getName());
      sortOrder.add(config);
    }

    for (BestNeighbourTieBreakingStrategy bestNeighbourTieBreakingStrategy : BestNeighbourTieBreakingStrategies.ALL) {
      Map<String, String> config = new HashMap<>(BASELINE_CONFIG);
      config.put("Best Neighbour Tie Breaking Strategy", bestNeighbourTieBreakingStrategy.getName());
      sortOrder.add(config);
    }

    for (ClearStackSelectionStrategy clearStackSelectionStrategy : ClearStackSelectionStrategies.ALL) {
      Map<String, String> config = new HashMap<>(BASELINE_CONFIG);
      config.put("Clear Stack Selection Strategy", clearStackSelectionStrategy.getName());
      sortOrder.add(config);
    }

    List<ProblemStats> sortedProblemStats = sort(sortOrder, allProblemStats);

    Set<String> configKeys = BASELINE_CONFIG.keySet();
    // Print all Config Values as a Header
    for (String configKey : configKeys) {
      System.out.print(formatRowName(configKey));
      for (ProblemStats problemStats : sortedProblemStats) {
        System.out.print(" | ");
        System.out.print(formatRowValue(problemStats.getConfig().get(configKey)));
      }
      System.out.println();
    }

    Map<ProblemStats, ExperimentResults> allExperimentResults = getAllExperimentResults(allProblemStats, Optional.of(MAX_SOLUTIONS));

    // Print all stats
    for (int statNo = 0; statNo < 5; statNo++) {
      System.out.print(formatRowName(getStatName(statNo)));
      for (ProblemStats problemStats : sortedProblemStats) {
        System.out.print(" | ");
        ExperimentResults experimentResults = allExperimentResults.get(problemStats);
        System.out.print(formatRowValue(getStat(statNo, experimentResults)));
      }
      System.out.println();
    }
  }

  private static List<ProblemStats> sort(List<Map<String, String>> sortOrder, List<ProblemStats> allProblemStats) {
    List<ProblemStats> sortedProblemStats = new ArrayList<>();
    List<ProblemStats> remainingProblemStats = new ArrayList<>(allProblemStats);
    for (Map<String, String> config : sortOrder) {
      for (ProblemStats problemStats : allProblemStats) {
        boolean match = true;
        for (Map.Entry<String, String> configEntry : config.entrySet()) {
          if (!configEntry.getValue().equals(problemStats.getConfig().get(configEntry.getKey()))) {
            match = false;
          }
        }
        if (match) {
          sortedProblemStats.add(problemStats);
          remainingProblemStats.remove(problemStats);
          break;
        }
      }
    }
    sortedProblemStats.addAll(remainingProblemStats);
    return sortedProblemStats;
  }

  private static String formatRowName(String rowName) {
    return String.format("%-40s", rowName);
  }

  private static String formatRowValue(Object rowValue) {
    return String.format("%-40s", rowValue);
  }

  private static String formatRowValue(double rowValue) {
    return String.format("%-40.2f", rowValue);
  }

  private static String getStatName(int statNo) {
    switch (statNo) {
      case 0:
        return "Best Moves";
      case 1:
        return "Avg Moves";
      case 2:
        return "Best Duration (ms)";
      case 3:
        return "Avg Duration (ms)";
      case 4:
        return "Problems Solved";
      default:
        throw new IllegalArgumentException();
    }
  }

  private static double getStat(int statNo, ExperimentResults experimentResults) {
    switch (statNo) {
      case 0:
        return experimentResults.getMeanBestMoves();
      case 1:
        return experimentResults.getMeanAvgMoves();
      case 2:
        return experimentResults.getMeanBestDurationMs();
      case 3:
        return experimentResults.getMeanAvgDurationMs();
      case 4:
        return experimentResults.getNFilesSolved();
      default:
        throw new IllegalArgumentException();
    }
  }

  private static Map<ProblemStats, ExperimentResults> getAllExperimentResults(List<ProblemStats> allProblemStats, Optional<Integer> maxSolutions) {
    Map<ProblemStats, ExperimentResults> meanStats = new HashMap<>(allProblemStats.size());
    for (ProblemStats problemStats : allProblemStats) {
      meanStats.put(problemStats, getExperimentResults(problemStats, maxSolutions));
    }
    return meanStats;
  }

  private static ExperimentResults getExperimentResults(ProblemStats problemStats, Optional<Integer> maxSolutions) {
    List<SolutionStats> bestSolutionStats = new ArrayList<>(problemStats.getAllProblemFileStats().size());
//    LOGGER.info("{}", problemStats.getConfig());
    for (ProblemFileStats problemFileStats : problemStats.getAllProblemFileStats()) {
      int originalListSize = problemFileStats.getAllSolutionStats().size();
      int listSize = maxSolutions.map(max -> Math.min(max, originalListSize)).orElse(originalListSize);
      if (listSize != maxSolutions.orElse(originalListSize)) {
//        LOGGER.error("Problem {}: {} != {}", problemFileStats.getFilename(), originalListSize, maxSolutions);
      }
      getBestSolution(problemFileStats.getAllSolutionStats().subList(0, listSize)).ifPresent(bestSolutionStats::add);
    }
    DecimalSolutionStats bestMeanStats = calculateMeanStats(bestSolutionStats);
    DecimalSolutionStats avgMeanStats = calculateMeanStats(problemStats.getAllProblemFileStats().stream().flatMap(stats -> stats.getAllSolutionStats().stream()).collect(Collectors.toList()));
    return new ExperimentResults(bestMeanStats.getNMoves(), avgMeanStats.getNMoves(), bestMeanStats.getDurationMs(), avgMeanStats.getDurationMs(), bestSolutionStats.size());
  }

  private static Optional<SolutionStats> getBestSolution(List<SolutionStats> allStats) {
    Optional<SolutionStats> bestStats = Optional.empty();
    for (SolutionStats stats : allStats) {
      if (stats.getNMoves() < bestStats.map(SolutionStats::getNMoves).orElse(Integer.MAX_VALUE)) {
        bestStats = Optional.of(stats);
      }
    }
    return bestStats;
  }

  private static DecimalSolutionStats calculateMeanStats(List<SolutionStats> allSolutionStats) {
    int totalNMoves = 0;
    int totalNLocalSearchMoves = 0;
    int totalNPerturbationMoves = 0;
    long totalDurationMs = 0L;

    for (SolutionStats solutionStats : allSolutionStats) {
      totalNMoves += solutionStats.getNMoves();
      totalNLocalSearchMoves += solutionStats.getNLocalSearchMoves();
      totalNPerturbationMoves += solutionStats.getNPerturbationMoves();
      totalDurationMs += solutionStats.getDurationMs();
    }

    return new DecimalSolutionStats(
        (double) totalNMoves / allSolutionStats.size(),
        (double) totalNLocalSearchMoves / allSolutionStats.size(),
        (double) totalNPerturbationMoves / allSolutionStats.size(),
        (double) totalDurationMs / allSolutionStats.size()
    );
  }

  private static class ExperimentResults {
    private double meanBestMoves;
    private double meanAvgMoves;
    private double meanBestDurationMs;
    private double meanAvgDurationMs;
    private int nFilesSolved;

    public ExperimentResults(double meanBestMoves, double meanAvgMoves, double meanBestDurationMs, double meanAvgDurationMs, int nFilesSolved) {
      this.meanBestMoves = meanBestMoves;
      this.meanAvgMoves = meanAvgMoves;
      this.meanBestDurationMs = meanBestDurationMs;
      this.meanAvgDurationMs = meanAvgDurationMs;
      this.nFilesSolved = nFilesSolved;
    }

    public double getMeanBestMoves() {
      return meanBestMoves;
    }

    public double getMeanAvgMoves() {
      return meanAvgMoves;
    }

    public double getMeanBestDurationMs() {
      return meanBestDurationMs;
    }

    public double getMeanAvgDurationMs() {
      return meanAvgDurationMs;
    }

    public int getNFilesSolved() {
      return nFilesSolved;
    }
  }
}

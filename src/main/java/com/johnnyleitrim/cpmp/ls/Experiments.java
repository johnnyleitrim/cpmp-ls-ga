package com.johnnyleitrim.cpmp.ls;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.johnnyleitrim.cpmp.problem.BFProblemProvider;
import com.johnnyleitrim.cpmp.problem.ProblemProvider;
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

public class Experiments {

  private static final Logger LOGGER = LoggerFactory.getLogger(Experiments.class);

  public static void main(String[] args) {
    // Create a thread pool executor that can run all the threads
    int nThreads = Runtime.getRuntime().availableProcessors() - 1;
    LOGGER.info("Running experiments with {} threads", nThreads);
    ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

    long baseSeed = System.currentTimeMillis();
    int runs = 1;
    int maxSolutions = -1;

    int bfStart = 32;
    int bfEnd = 32;

    List<Future<Void>> experimentFutures = new LinkedList<>();
    for (ClearStackSelectionStrategy clearStackSelectionStrategy : ClearStackSelectionStrategies.ALL) {
      for (BestNeighbourTieBreakingStrategy bestNeighbourTieBreakingStrategy : BestNeighbourTieBreakingStrategies.ALL) {
        for (boolean fillStackAfterClearing : List.of(true, false)) {
          for (StackFillingStrategy stackFillingStrategy : StackFillingStrategies.ALL) {
            for (StackClearingStrategy stackClearingStrategy : StackClearingStrategies.ALL) {

              IterativeLocalSearchStrategyConfig strategyConfig = new IterativeLocalSearchStrategyConfig();
              strategyConfig.setMinSearchMoves(1);
              strategyConfig.setMaxSearchMoves(2);
              strategyConfig.setMaxSearchDuration(Duration.ofMinutes(1));
              strategyConfig.setClearStackSelectionStrategy(clearStackSelectionStrategy);
              strategyConfig.setBestNeighbourTieBreakingStrategy(bestNeighbourTieBreakingStrategy);
              strategyConfig.setFillStackAfterClearing(fillStackAfterClearing);
              strategyConfig.setFillStackStrategy(stackFillingStrategy);
              strategyConfig.setClearStackStrategy(stackClearingStrategy);

              for (int bfNo = bfStart; bfNo <= bfEnd; bfNo++) {
                Experiment experiment = new Experiment("BF" + bfNo, new BFProblemProvider(bfNo), strategyConfig, runs, maxSolutions, baseSeed);
                experimentFutures.add(executorService.submit(experiment));
              }
            }
          }
        }
      }
    }

    for (Future<Void> experimentFuture : experimentFutures) {
      try {
        experimentFuture.get();
      } catch (Exception e) {
        LOGGER.error("Problem executing experiment", e);
      }
    }

    executorService.shutdown();
  }

  private static class Experiment implements Callable<Void> {
    private final String problemCategory;
    private final ProblemProvider problemProvider;
    private final IterativeLocalSearchStrategyConfig strategyConfig;
    private final int runs;
    private final int maxSolutions;
    private final long baseSeed;

    private Experiment(String problemCategory, ProblemProvider problemProvider, IterativeLocalSearchStrategyConfig strategyConfig, int runs, int maxSolutions, long baseSeed) {
      this.problemCategory = problemCategory;
      this.problemProvider = problemProvider;
      this.strategyConfig = strategyConfig;
      this.runs = runs;
      this.maxSolutions = maxSolutions;
      this.baseSeed = baseSeed;
    }

    @Override
    public Void call() throws Exception {
      LOGGER.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
      LOGGER.info(":                               Base Seed: {}", baseSeed);
      LOGGER.info(":                                    Runs: {}", runs);
      LOGGER.info(":                       Maximum Solutions: {}", maxSolutions);
      LOGGER.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
      for (Map.Entry<String, Object> fieldValue : strategyConfig.getFieldValues().entrySet()) {
        String fieldName = String.format("%40s", fieldValue.getKey());
        LOGGER.info(":{}: {}", fieldName, fieldValue.getValue());
      }
      LOGGER.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
      Main.run(problemCategory, problemProvider, strategyConfig, runs, maxSolutions, baseSeed);
      return null;
    }
  }
}

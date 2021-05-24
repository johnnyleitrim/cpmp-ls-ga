package com.johnnyleitrim.cpmp.ls;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.johnnyleitrim.cpmp.CommandOptions;
import com.johnnyleitrim.cpmp.MathUtil;
import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.Random;
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

public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    IterativeLocalSearchStrategyConfig strategyConfig = new IterativeLocalSearchStrategyConfig();

    CommandOptions commandOptions = new CommandOptions(args);
    commandOptions.getIntArg("minSearchMoves").ifPresent(strategyConfig::setMinSearchMoves);
    commandOptions.getIntArg("maxSearchMoves").ifPresent(strategyConfig::setMaxSearchMoves);
    commandOptions.getIntArg("-maxSearchDuration").ifPresent(duration -> strategyConfig.setMaxSearchDuration(Duration.ofMinutes(duration)));
    commandOptions.getArg("-clearStackSelectionStrategy").ifPresent(strategy -> strategyConfig.setClearStackSelectionStrategy(
        getStrategy(strategy, ClearStackSelectionStrategies.class, ClearStackSelectionStrategy.class)));
    commandOptions.getArg("-bestNeighbourTieBreakingStrategy").ifPresent(strategy -> strategyConfig.setBestNeighbourTieBreakingStrategy(
        getStrategy(strategy, BestNeighbourTieBreakingStrategies.class, BestNeighbourTieBreakingStrategy.class)));
    commandOptions.getBoolArg("-fillStackAfterClearing").ifPresent(strategyConfig::setFillStackAfterClearing);
    commandOptions.getArg("-fillStackStrategy").ifPresent(strategy -> strategyConfig.setFillStackStrategy(
        getStrategy(strategy, StackFillingStrategies.class, StackFillingStrategy.class)));
    commandOptions.getArg("-clearStackStrategy").ifPresent(strategy -> strategyConfig.setClearStackStrategy(
        getStrategy(strategy, StackClearingStrategies.class, StackClearingStrategy.class)));

    long baseSeed = commandOptions.getLongArg("-seed").orElse(System.currentTimeMillis());
    int runs = commandOptions.getIntArg("-runs").orElse(10);
    int bfStart = commandOptions.getIntArg("-bfStart").orElse(1);
    int bfEnd = commandOptions.getIntArg("-bfEnd").orElse(1);
    int maxSolutions = commandOptions.getIntArg("-maxSolutions").orElse(-1);

    LOGGER.info(":::::::::::::::::::::::::::::::::::::");
    LOGGER.info(":           Base Seed: {}", baseSeed);
    LOGGER.info(":                Runs: {}", runs);
    LOGGER.info(":   Start BF Category: {}", bfStart);
    LOGGER.info(":     End BF Category: {}", bfEnd);
    LOGGER.info(":   Maximum Solutions: {}", maxSolutions);
    LOGGER.info(":    Min Search Moves: {}", strategyConfig.getMinSearchMoves());
    LOGGER.info(":    Max Search Moves: {}", strategyConfig.getMaxSearchDuration());
    LOGGER.info(": Max Search Duration: {}", strategyConfig.getMaxSearchDuration());
    LOGGER.info(":::::::::::::::::::::::::::::::::::::");

    if (!commandOptions.hasArg("-execute")) {
      LOGGER.info("Exiting...");
      System.exit(0);
    }

    List<ProblemProvider> problemProviders = IntStream.range(bfStart, bfEnd + 1).mapToObj(BFProblemProvider::new).collect(Collectors.toList());

    IterativeLocalSearch iterativeLocalSearch = new IterativeLocalSearch(strategyConfig);

    for (ProblemProvider problemProvider : problemProviders) {
      for (Problem problem : problemProvider.getProblems()) {

        LOGGER.info("==================================");
        LOGGER.info(problem.getName());
        LOGGER.info("==================================");

        double[] nMoves = new double[runs];
        for (int i = 0; i < runs; i++) {
          LOGGER.info("RUN {}", i);
          long randomSeed = baseSeed + (i * 10_000);
          LOGGER.info("Setting random seed to: {}", randomSeed);
          Random.setRandomSeed(randomSeed);
          long startTime = System.currentTimeMillis();
          Optional<List<Move>> moves = iterativeLocalSearch.search(problem.getInitialState(), maxSolutions);
          long duration = System.currentTimeMillis() - startTime;
          if (moves.isPresent()) {
            LOGGER.info("Found solution in {} moves ", moves.get().size());
            nMoves[i] = moves.get().size();
          } else {
            LOGGER.info("No solution found");
          }
          LOGGER.info("Runtime duration {}ms", duration);
        }
        MathUtil.Details details = MathUtil.calcDetails(nMoves);
        LOGGER.info("Best: {}", details.getBest());
        LOGGER.info("Mean: {}", details.getMean());
        LOGGER.info(" Std: {}", details.getStdDev());
      }
    }
  }

  private static <T> T getStrategy(String strategyName, Class<?> strategies, Class<T> strategyType) {
    try {
      return strategyType.cast(strategies.getField(strategyName).get(strategies));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

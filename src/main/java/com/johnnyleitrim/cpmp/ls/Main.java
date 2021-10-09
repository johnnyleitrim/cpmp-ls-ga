package com.johnnyleitrim.cpmp.ls;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.johnnyleitrim.cpmp.strategy.FitnessStrategies;
import com.johnnyleitrim.cpmp.strategy.FitnessStrategy;
import com.johnnyleitrim.cpmp.strategy.StackClearingStrategies;
import com.johnnyleitrim.cpmp.strategy.StackClearingStrategy;
import com.johnnyleitrim.cpmp.strategy.StackFillingStrategies;
import com.johnnyleitrim.cpmp.strategy.StackFillingStrategy;
import com.johnnyleitrim.cpmp.utils.StatsFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws IOException {
    IterativeLocalSearchStrategyConfig strategyConfig = new IterativeLocalSearchStrategyConfig();

    CommandOptions commandOptions = new CommandOptions(args);
    commandOptions.getIntArg("-minSearchMoves").ifPresent(strategyConfig::setMinSearchMoves);
    commandOptions.getIntArg("-maxSearchMoves").ifPresent(strategyConfig::setMaxSearchMoves);
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
    commandOptions.getArg("-fitnessStrategy").ifPresent(strategy -> strategyConfig.setFitnessStrategy(
        getStrategy(strategy, FitnessStrategies.class, FitnessStrategy.class)));

    long baseSeed = commandOptions.getLongArg("-seed").orElse(System.currentTimeMillis());
    int runs = commandOptions.getIntArg("-runs").orElse(10);
    int bfStart = commandOptions.getIntArg("-bfStart").orElse(1);
    int bfEnd = commandOptions.getIntArg("-bfEnd").orElse(1);
    int maxSolutions = commandOptions.getIntArg("-maxSolutions").orElse(-1);

    LOGGER.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
    LOGGER.info(":                               Base Seed: {}", baseSeed);
    LOGGER.info(":                                    Runs: {}", runs);
    LOGGER.info(":                       Start BF Category: {}", bfStart);
    LOGGER.info(":                         End BF Category: {}", bfEnd);
    LOGGER.info(":                       Maximum Solutions: {}", maxSolutions);
    LOGGER.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
    for (Map.Entry<String, Object> fieldValue : strategyConfig.getFieldValues().entrySet()) {
      String fieldName = String.format("%40s", fieldValue.getKey());
      LOGGER.info(":{}: {}", fieldName, fieldValue.getValue());
    }
    LOGGER.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

    if (!commandOptions.hasArg("-execute")) {
      LOGGER.info("Exiting...");
      System.exit(0);
    }

    for (int bfNo = bfStart; bfNo <= bfEnd; bfNo++) {
      run("BF" + bfNo, new BFProblemProvider(bfNo), strategyConfig, runs, maxSolutions, baseSeed);
    }
  }

  public static void run(String problemCategory, ProblemProvider problemProvider, IterativeLocalSearchStrategyConfig strategyConfig, int runs, int maxSolutions, long baseSeed) throws IOException {
    try (StatsFileWriter statsWriter = new StatsFileWriter(problemCategory, strategyConfig, runs, maxSolutions, baseSeed)) {
      IterativeLocalSearch iterativeLocalSearch = new IterativeLocalSearch(strategyConfig, statsWriter);
      for (Problem problem : problemProvider.getProblems()) {

        LOGGER.info("==================================");
        LOGGER.info(problem.getName());
        LOGGER.info("==================================");
        statsWriter.writeProblemName(problem);

        double[] nMoves = new double[runs];
        for (int i = 0; i < runs; i++) {
          LOGGER.debug("RUN {}", i);
          long randomSeed = baseSeed + (i * 10_000);
          LOGGER.debug("Setting random seed to: {}", randomSeed);
          Random.setRandomSeed(randomSeed);
          statsWriter.writeSeed(randomSeed);
          long startTime = System.currentTimeMillis();
          Optional<List<Move>> moves = iterativeLocalSearch.search(problem.getInitialState(), maxSolutions);
          long duration = System.currentTimeMillis() - startTime;
          if (moves.isPresent()) {
            LOGGER.debug("Found solution in {} moves ", moves.get().size());
            nMoves[i] = moves.get().size();
          } else {
            LOGGER.debug("No solution found");
          }
          LOGGER.debug("Runtime duration {}ms", duration);
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

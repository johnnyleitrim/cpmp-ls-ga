package com.johnnyleitrim.cpmp.ls;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.johnnyleitrim.cpmp.CommandOptions;
import com.johnnyleitrim.cpmp.MathUtil;
import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.fitness.FitnessAlgorithm;
import com.johnnyleitrim.cpmp.ls.IterativeLocalSearch.Perturbation;
import com.johnnyleitrim.cpmp.problem.BFProblemProvider;
import com.johnnyleitrim.cpmp.problem.CVProblemProvider;
import com.johnnyleitrim.cpmp.problem.ProblemProvider;

public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws Exception {
    CommandOptions commandOptions = new CommandOptions(args);

    long baseSeed = commandOptions.getLongArg("maxSolutions").orElse(System.currentTimeMillis());
    int runs = commandOptions.getIntArg("-runs").orElse(10);
    int bfStart = commandOptions.getIntArg("-bfStart").orElse(1);
    int bfEnd = commandOptions.getIntArg("-bfEnd").orElse(1);
    Optional<String> cvPrefix = commandOptions.getArg("-cvPrefix");
    int maxSolutions = commandOptions.getIntArg("-maxSolutions").orElse(-1);
    Duration maxSearchDuration = Duration.of(commandOptions.getLongArg("-maxSearchDuration").orElse(5L), ChronoUnit.MINUTES);
    String neighbourhoodMoves = commandOptions.getArg("-neighbourhoods").orElse("1-1,1-2,2-2");
    Perturbation perturbation = Perturbation.valueOf(commandOptions.getArg("-perturbation").orElse("RANDOM_MOVE"));

    String lowerBound = commandOptions.getArg("-lowerBound").orElse("BF");
    FitnessAlgorithm lowerBoundAlgorithm = (FitnessAlgorithm) Class.forName("com.johnnyleitrim.cpmp.fitness." + lowerBound + "LowerBoundFitness").getDeclaredConstructor().newInstance();

    int[][] neighbourhoodMoveOptions = parseSearchMoves(neighbourhoodMoves);

    LOGGER.info(":::::::::::::::::::::::::::::::::::::");
    LOGGER.info(":           Base Seed: {}", baseSeed);
    LOGGER.info(":                Runs: {}", runs);
    LOGGER.info(":   Start BF Category: {}", bfStart);
    LOGGER.info(":     End BF Category: {}", bfEnd);
    LOGGER.info(":      CV File Prefix: {}", cvPrefix.orElse(""));
    LOGGER.info(": Max Search Duration: {}", maxSearchDuration);
    LOGGER.info(": Neighbourhood Moves: {}", neighbourhoodMoves);
    LOGGER.info(":        Perturbation: {}", perturbation);
    LOGGER.info(":         Lower Bound: {}", lowerBoundAlgorithm.getClass().getSimpleName());
    LOGGER.info(":   Maximum Solutions: {}", maxSolutions);
    LOGGER.info(":            Features: {}", Features.instance);
    LOGGER.info(":::::::::::::::::::::::::::::::::::::");

    if (!commandOptions.hasArg("-execute")) {
      LOGGER.info("Exiting...");
      System.exit(0);
    }

    List<ProblemProvider> problemProviders;
    if (cvPrefix.isPresent()) {
      problemProviders = Collections.singletonList(new CVProblemProvider(cvPrefix.get()));
    } else {
      problemProviders = IntStream.range(bfStart, bfEnd + 1).mapToObj(i -> new BFProblemProvider(i)).collect(Collectors.toList());
    }

    for (ProblemProvider problemProvider : problemProviders) {
      for (int[] searchMoves : neighbourhoodMoveOptions) {
        int minSearchMoves = searchMoves[0];
        int maxSearchMoves = searchMoves[1];

        LOGGER.info(String.format("Min Search Moves: %d, Max Search Moves: %d", minSearchMoves, maxSearchMoves));

        for (Problem problem : problemProvider.getProblems()) {

          LOGGER.info("==================================");
          LOGGER.info(problem.getName());
          LOGGER.info("==================================");

          double[] nMoves = new double[runs];
          for (int i = 0; i < runs; i++) {
            LOGGER.info("RUN {}", i);
            Problem.setRandomSeed(baseSeed + (i * 10_000));
            IterativeLocalSearch localSearch = new IterativeLocalSearch(problem.getInitialState(), minSearchMoves, maxSearchMoves, lowerBoundAlgorithm, maxSearchDuration);
            long startTime = System.currentTimeMillis();
            List<Move> moves = localSearch.search(perturbation, maxSolutions);
            long duration = System.currentTimeMillis() - startTime;
            LOGGER.info("Found solution in {} moves ", moves.size());
            LOGGER.info("Runtime duration {}ms", duration);
            nMoves[i] = moves.size();
          }
          MathUtil.Details details = MathUtil.calcDetails(nMoves);
          LOGGER.info("Best: {}", details.getBest());
          LOGGER.info("Mean: {}", details.getMean());
          LOGGER.info(" Std: {}", details.getStdDev());
        }
      }
    }
  }

  private static int[][] parseSearchMoves(String searchMoves) {
    String[] moves = searchMoves.split(",");
    int[][] searchMovesOptions = new int[moves.length][2];
    for (int i = 0; i < moves.length; i++) {
      String[] minMax = moves[i].split("-");
      searchMovesOptions[i] = new int[]{
          Integer.parseInt(minMax[0]),
          Integer.parseInt(minMax[1]),
      };
    }
    return searchMovesOptions;
  }
}

package com.johnnyleitrim.cpmp.ls;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.johnnyleitrim.cpmp.CommandOptions;
import com.johnnyleitrim.cpmp.MathUtil;
import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.fitness.FitnessAlgorithm;
import com.johnnyleitrim.cpmp.ls.IterativeLocalSearch.Perturbation;
import com.johnnyleitrim.cpmp.problem.BFProblemProvider;
import com.johnnyleitrim.cpmp.problem.ProblemProvider;

public class Main {

  public static void main(String[] args) throws Exception {
    CommandOptions commandOptions = new CommandOptions(args);

    long baseSeed = commandOptions.getLongArg("-seed").orElse(System.currentTimeMillis());
    int runs = commandOptions.getIntArg("-runs").orElse(10);
    int bfStart = commandOptions.getIntArg("-bfStart").orElse(1);
    int bfEnd = commandOptions.getIntArg("-bfEnd").orElse(1);
    boolean returnFirstSolution = commandOptions.getArg("-returnFirstSolution").map(Boolean::parseBoolean).orElse(false);
    Duration maxSearchDuration = Duration.of(commandOptions.getLongArg("-maxSearchDuration").orElse(5L), ChronoUnit.MINUTES);
    String neighbourhoodMoves = commandOptions.getArg("-neighbourhoods").orElse("1-1,1-2,2-2");
    Perturbation perturbation = Perturbation.valueOf(commandOptions.getArg("-perturbation").orElse("RANDOM_MOVE"));

    String lowerBound = commandOptions.getArg("-lowerBound").orElse("BF");
    FitnessAlgorithm lowerBoundAlgorithm = (FitnessAlgorithm) Class.forName("com.johnnyleitrim.cpmp.fitness." + lowerBound + "LowerBoundFitness").getDeclaredConstructor().newInstance();

    int[][] neighbourhoodMoveOptions = parseSearchMoves(neighbourhoodMoves);

    System.out.println(":::::::::::::::::::::::::::::::::::::");
    System.out.println(":           Base Seed: " + baseSeed);
    System.out.println(":                Runs: " + runs);
    System.out.println(":   Start BF Category: " + bfStart);
    System.out.println(":     End BF Category: " + bfEnd);
    System.out.println(": Max Search Duration: " + maxSearchDuration);
    System.out.println(": Neighbourhood Moves: " + neighbourhoodMoves);
    System.out.println(":        Perturbation: " + perturbation);
    System.out.println(":         Lower Bound: " + lowerBoundAlgorithm.getClass().getSimpleName());
    System.out.println(":::::::::::::::::::::::::::::::::::::");

    if (!commandOptions.hasArg("-execute")) {
      System.out.println("Exiting...");
      System.exit(0);
    }


    for (ProblemProvider problemProvider : IntStream.range(bfStart, bfEnd + 1).mapToObj(i -> new BFProblemProvider(i)).collect(Collectors.toList())) {
      for (int[] searchMoves : neighbourhoodMoveOptions) {
        int minSearchMoves = searchMoves[0];
        int maxSearchMoves = searchMoves[1];

        System.out.println(String.format("Min Search Moves: %d, Max Search Moves: %d", minSearchMoves, maxSearchMoves));

        for (Problem problem : problemProvider.getProblems()) {

          System.out.println("==================================");
          System.out.println(problem.getName());
          System.out.println("==================================");

          double[] nMoves = new double[runs];
          for (int i = 0; i < runs; i++) {
            System.out.println("RUN " + i + " " + new Date());
            Problem.setRandomSeed(baseSeed + (i * 10_000));
            IterativeLocalSearch localSearch = new IterativeLocalSearch(problem.getInitialState(), minSearchMoves, maxSearchMoves, lowerBoundAlgorithm, maxSearchDuration);
            long startTime = System.currentTimeMillis();
            List<Move> moves = localSearch.search(perturbation, returnFirstSolution);
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Found solution in " + moves.size() + " moves " + new Date());
            System.out.println("Runtime duration " + duration + "ms");
            nMoves[i] = moves.size();
          }
          MathUtil.Details details = MathUtil.calcDetails(nMoves);
          System.out.println("Best: " + details.getBest());
          System.out.println("Mean: " + details.getMean());
          System.out.println(" Std: " + details.getStdDev());
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

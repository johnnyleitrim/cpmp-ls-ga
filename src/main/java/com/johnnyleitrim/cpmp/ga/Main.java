package com.johnnyleitrim.cpmp.ga;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.johnnyleitrim.cpmp.CommandOptions;
import com.johnnyleitrim.cpmp.MathUtil;
import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.fitness.BFLowerBoundFitness;
import com.johnnyleitrim.cpmp.fitness.FitnessAlgorithm;
import com.johnnyleitrim.cpmp.ga.crossover.CrossoverAlgorithm;
import com.johnnyleitrim.cpmp.ga.evaluation.EvaluationResult;
import com.johnnyleitrim.cpmp.ga.generator.ChromosomeGenerator;
import com.johnnyleitrim.cpmp.ga.generator.ExcellentMoveChromosomeGenerator;
import com.johnnyleitrim.cpmp.ga.generator.IterativeLocalSearchChromosomeGenerator;
import com.johnnyleitrim.cpmp.ga.selection.SelectionAlgorithm;
import com.johnnyleitrim.cpmp.ga.variation.MutationAlgorithm;
import com.johnnyleitrim.cpmp.ls.IterativeLocalSearch;
import com.johnnyleitrim.cpmp.problem.BFProblemProvider;
import com.johnnyleitrim.cpmp.problem.ProblemProvider;
import com.johnnyleitrim.cpmp.state.InvalidMoveException;
import com.johnnyleitrim.cpmp.state.MutableState;

public class Main {

  public static void main(String[] args) throws Exception {
    CommandOptions commandOptions = new CommandOptions(args);

    long baseSeed = commandOptions.getLongArg("-seed").orElse(System.currentTimeMillis());
    int runs = commandOptions.getIntArg("-runs").orElse(1);
    int bf = commandOptions.getIntArg("-bf").orElseThrow();
    double mutationDelta = commandOptions.getDoubleArg("-mutationDelta").orElse(0.0);
    Duration maxSearchDuration = Duration.of(commandOptions.getLongArg("-maxSearchDuration").orElse(5L), ChronoUnit.MINUTES);
    boolean performLocalSearch = Boolean.parseBoolean(commandOptions.getArg("-performLocalSearch").orElse("false"));

    String mutation = commandOptions.getArg("-mutation").orElseThrow();
    MutationAlgorithm mutationAlgorithm = (MutationAlgorithm) Class.forName("com.johnnyleitrim.cpmp.ga.variation." + mutation + "Mutation").getDeclaredConstructor().newInstance();

    String crossover = commandOptions.getArg("-crossover").orElseThrow();
    CrossoverAlgorithm crossoverAlgorithm = (CrossoverAlgorithm) Class.forName("com.johnnyleitrim.cpmp.ga.crossover." + crossover + "Crossover").getDeclaredConstructor().newInstance();

    String selection = commandOptions.getArg("-selection").orElseThrow();
    SelectionAlgorithm selectionAlgorithm = (SelectionAlgorithm) Class.forName("com.johnnyleitrim.cpmp.ga.selection." + selection + "Selection").getDeclaredConstructor().newInstance();

    String lowerBound = commandOptions.getArg("-lowerBound").orElse("BF");
    FitnessAlgorithm lowerBoundAlgorithm = (FitnessAlgorithm) Class.forName("com.johnnyleitrim.cpmp.fitness." + lowerBound + "LowerBoundFitness").getDeclaredConstructor().newInstance();

    System.out.println(":::::::::::::::::::::::::::::::::::::");
    System.out.println(":           Base Seed: " + baseSeed);
    System.out.println(":                Runs: " + runs);
    System.out.println(": Max Search Duration: " + maxSearchDuration);
    System.out.println(":           Crossover: " + crossoverAlgorithm.getClass().getSimpleName());
    System.out.println(":            Mutation: " + mutationAlgorithm.getClass().getSimpleName());
    System.out.println(":      Mutation Delta: " + mutationDelta);
    System.out.println(":         Lower Bound: " + lowerBoundAlgorithm.getClass().getSimpleName());
    System.out.println(":           Selection: " + selectionAlgorithm);
    System.out.println(":Perform Local Search: " + performLocalSearch);
    System.out.println(":                  BF: " + bf);
    System.out.println(":::::::::::::::::::::::::::::::::::::");

    if (!commandOptions.hasArg("-execute")) {
      System.out.println("Exiting...");
      System.exit(0);
    }

    int nChromosomes = 200;
    int nGenes = 1000;

    for (ProblemProvider problemProvider : Arrays.asList(
        new BFProblemProvider(bf)
    )) {

      for (Problem problem : problemProvider.getProblems()) {

        System.out.println("==================================");
        System.out.println(problem.getName());
        System.out.println("==================================");

        List<ChromosomeGenerator> chromosomeGenerators = new ArrayList<>(2);
        chromosomeGenerators.add(new IterativeLocalSearchChromosomeGenerator(problem.getInitialState(), 1, IterativeLocalSearch.Perturbation.LOWEST_MISOVERLAID_STACK_CLEARING, new BFLowerBoundFitness()));
        chromosomeGenerators.add(new ExcellentMoveChromosomeGenerator(nGenes, problem.getInitialState()));

        double[] nMoves = new double[runs];
        for (int i = 0; i < runs; i++) {
          System.out.println("RUN " + i + " " + new Date());
          Problem.setRandomSeed(baseSeed + (i * 10_000));
          GenericAlgorithm ga = new GenericAlgorithm(nGenes, nChromosomes, chromosomeGenerators, crossoverAlgorithm, lowerBoundAlgorithm, selectionAlgorithm, mutationAlgorithm, mutationDelta, performLocalSearch, problem);
          EvaluationResult bestIterationSolution = ga.search(maxSearchDuration);
          System.out.println("Found solution in " + bestIterationSolution.getNSolutionGenes() + " moves " + new Date());
          nMoves[i] = bestIterationSolution.getNSolutionGenes();
        }
        MathUtil.Details details = MathUtil.calcDetails(nMoves);
        System.out.println("Best: " + details.getBest());
        System.out.println("Mean: " + details.getMean());
        System.out.println("Mean: " + details.getStdDev());
      }
    }
//    printSolution(problem, bestSolution);
  }

  private static void printSolution(Problem problem, EvaluationResult solution) throws InvalidMoveException {
    if (solution.isSolution()) {
      MutableState state = problem.getInitialState().copy();
      if (solution.isSolution()) {
        System.out.println("0 --------\n" + state);
        for (int j = 0; j < solution.getNSolutionGenes(); j++) {
          Gene move = solution.getChromosome().getGene(j);
          state.applyMove(move.getSourceStack(), move.getDestinationStack());
          System.out.println((j + 1) + " -------- " + move + "\n" + state);
        }
      }
    }
  }
}

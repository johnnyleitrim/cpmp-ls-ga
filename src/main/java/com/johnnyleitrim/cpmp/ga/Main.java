package com.johnnyleitrim.cpmp.ga;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

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

    LOGGER.info(":::::::::::::::::::::::::::::::::::::");
    LOGGER.info(":           Base Seed: {}", baseSeed);
    LOGGER.info(":                Runs: {}", runs);
    LOGGER.info(": Max Search Duration: {}", maxSearchDuration);
    LOGGER.info(":           Crossover: {}", crossoverAlgorithm.getClass().getSimpleName());
    LOGGER.info(":            Mutation: {}", mutationAlgorithm.getClass().getSimpleName());
    LOGGER.info(":      Mutation Delta: {}", mutationDelta);
    LOGGER.info(":         Lower Bound: {}", lowerBoundAlgorithm.getClass().getSimpleName());
    LOGGER.info(":           Selection: {}", selectionAlgorithm);
    LOGGER.info(":Perform Local Search: {}", performLocalSearch);
    LOGGER.info(":                  BF: {}", bf);
    LOGGER.info(":::::::::::::::::::::::::::::::::::::");

    if (!commandOptions.hasArg("-execute")) {
      LOGGER.info("Exiting...");
      System.exit(0);
    }

    int nChromosomes = 200;
    int nGenes = 1000;

    for (ProblemProvider problemProvider : Collections.singletonList(
        new BFProblemProvider(bf)
    )) {

      for (Problem problem : problemProvider.getProblems()) {

        LOGGER.info("==================================");
        LOGGER.info(problem.getName());
        LOGGER.info("==================================");

        List<ChromosomeGenerator> chromosomeGenerators = new ArrayList<>(2);
        chromosomeGenerators.add(new IterativeLocalSearchChromosomeGenerator(problem.getInitialState(), 1, IterativeLocalSearch.Perturbation.LOWEST_MISOVERLAID_STACK_CLEARING, new BFLowerBoundFitness()));
        chromosomeGenerators.add(new ExcellentMoveChromosomeGenerator(nGenes, problem.getInitialState()));

        double[] nMoves = new double[runs];
        for (int i = 0; i < runs; i++) {
          LOGGER.info("RUN {}", i);
          Problem.setRandomSeed(baseSeed + (i * 10_000));
          GenericAlgorithm ga = new GenericAlgorithm(nGenes, nChromosomes, chromosomeGenerators, crossoverAlgorithm, lowerBoundAlgorithm, selectionAlgorithm, mutationAlgorithm, mutationDelta, performLocalSearch, problem);
          long startTime = System.currentTimeMillis();
          EvaluationResult bestIterationSolution = ga.search(maxSearchDuration);
          long duration = System.currentTimeMillis() - startTime;
          LOGGER.info("Found solution in {} moves", bestIterationSolution.getNSolutionGenes());
          LOGGER.info("Runtime duration {}ms", duration);
          nMoves[i] = bestIterationSolution.getNSolutionGenes();
        }
        MathUtil.Details details = MathUtil.calcDetails(nMoves);
        LOGGER.info("Best: {}", details.getBest());
        LOGGER.info("Mean: {}", details.getMean());
        LOGGER.info("Mean: {}", details.getStdDev());
      }
    }
  }
}

package com.johnnyleitrim.cpmp.ga;

import java.time.Duration;
import java.util.List;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.fitness.BFLowerBoundFitness;
import com.johnnyleitrim.cpmp.fitness.FitnessAlgorithm;
import com.johnnyleitrim.cpmp.ga.crossover.CrossoverAlgorithm;
import com.johnnyleitrim.cpmp.ga.evaluation.EvaluationResult;
import com.johnnyleitrim.cpmp.ga.evaluation.EvaluationUtils;
import com.johnnyleitrim.cpmp.ga.generator.ChromosomeGenerator;
import com.johnnyleitrim.cpmp.ga.replacement.EliteSurvivalReplacement;
import com.johnnyleitrim.cpmp.ga.replacement.ReplacementAlgorithm;
import com.johnnyleitrim.cpmp.ga.selection.SelectionAlgorithm;
import com.johnnyleitrim.cpmp.ga.variation.IterativeLocalSearchMutation;
import com.johnnyleitrim.cpmp.ga.variation.MutationAlgorithm;
import com.johnnyleitrim.cpmp.ls.IterativeLocalSearch;

public class GenericAlgorithm {

  private static final double MUTATE_PROBABILITY = 0.05;

  private static final double CROSSOVER_PROBABILITY = 0.8;

  private final int nChromosomes;

  private final Chromosome[] population;

  private final SelectionAlgorithm selectionAlgorithm;

  private final CrossoverAlgorithm crossoverAlgorithm;

  private final ReplacementAlgorithm replacementAlgorithm = new EliteSurvivalReplacement();

  private final MutationAlgorithm mutationAlgorithm;

  private final MutationAlgorithm localSearchAlgorithm = new IterativeLocalSearchMutation(1, IterativeLocalSearch.Perturbation.LOWEST_MISOVERLAID_STACK_CLEARING, new BFLowerBoundFitness());

  private final FitnessAlgorithm fitnessAlgorithm;

  private final double mutationDelta;

  private final boolean performLocalSearch;

  private final Problem problem;

  private int nGenes;

  private EvaluationResult bestSolution;

  private EvaluationResult[] evaluationResults;

  private long startTime;

  private double mutationProbability = MUTATE_PROBABILITY;

  public GenericAlgorithm(int nGenes, int nChromosomes, List<ChromosomeGenerator> chromosomeGenerators, CrossoverAlgorithm crossoverAlgorithm, FitnessAlgorithm fitnessAlgorithm, SelectionAlgorithm selectionAlgorithm, MutationAlgorithm mutationAlgorithm, double mutationDelta, boolean performLocalSearch, Problem problem) {
    this.nGenes = nGenes;
    this.nChromosomes = nChromosomes;
    this.crossoverAlgorithm = crossoverAlgorithm;
    this.fitnessAlgorithm = fitnessAlgorithm;
    this.selectionAlgorithm = selectionAlgorithm;
    this.mutationAlgorithm = mutationAlgorithm;
    this.mutationDelta = mutationDelta;
    this.performLocalSearch = performLocalSearch;
    this.problem = problem;

    population = new Chromosome[nChromosomes];

    for (int i = 0; i < nChromosomes; i++) {
      int generatorIndex = 1;
      if (i % 10 == 0) {
        generatorIndex = 0;
      }
      population[i] = chromosomeGenerators.get(generatorIndex).generateChromosome();
    }

    evaluationResults = evaluate(population);
    updateBestSolution();
  }

  public EvaluationResult search(Duration maxDuration) {
    startTime = System.currentTimeMillis();
    long endTime = startTime + maxDuration.toMillis();
    int generation = 1;
    while (System.currentTimeMillis() < endTime) {
      searchStep();
      generation++;
    }
    System.out.println("\tPerformed " + generation + " generations");
    return bestSolution;
  }

  private void searchStep() {
    generateNewPopulation();
    updateBestSolution();
  }

  private EvaluationResult[] evaluate(Chromosome[] populationToEvaluate) {
    EvaluationResult[] populationEvaluationResults = new EvaluationResult[populationToEvaluate.length];
    for (int i = 0; i < nChromosomes; i++) {
      EvaluationResult result = EvaluationUtils.evaluate(problem, nGenes, populationToEvaluate[i], fitnessAlgorithm);
      populationEvaluationResults[i] = result;
    }
    return populationEvaluationResults;
  }

  private void updateBestSolution() {
    if (bestSolution == null) {
      bestSolution = evaluationResults[0];
    }

    boolean foundImprovement = false;
    for (int i = 0; i < evaluationResults.length; i++) {
      EvaluationResult result = evaluationResults[i];
      if (result.isBetter(bestSolution)) {
        bestSolution = result;
        foundImprovement = true;
      }
    }

    bestSolution = bestSolution.copy();
    nGenes = Math.min(nGenes, bestSolution.getNSolutionGenes());

    if (foundImprovement) {
      long secondsElapsed = 0;
      if (startTime > 0) {
        secondsElapsed = (System.currentTimeMillis() - startTime) / 1000;
      }
      System.out.println(String.format("\t[%s] Found better solution: %s", secondsElapsed, bestSolution));
    }
  }

  private void generateNewPopulation() {
    Chromosome[] matingPool = selectionAlgorithm.generateMatingPool(evaluationResults);
    Chromosome[] newPopulation = new Chromosome[nChromosomes];
    int newPopulationIndex = 0;
    while (newPopulationIndex < nChromosomes) {
      Chromosome parentA = getRandomParent(matingPool);
      Chromosome parentB = getRandomParent(matingPool);

      Chromosome[] children;
      if (Problem.getRandom().nextFloat() < CROSSOVER_PROBABILITY) {
        children = crossoverAlgorithm.crossover(parentA, parentB, nGenes, problem.getInitialState());
      } else {
        children = new Chromosome[]{parentA.copy(), parentB.copy()};
      }

      for (int i = 0; i < children.length; newPopulationIndex++, i++) {
        Chromosome child = children[i];
        if (Problem.getRandom().nextFloat() < mutationProbability) {
          mutationAlgorithm.mutate(child, nGenes, problem.getInitialState());
        }
        if (performLocalSearch) {
          localSearchAlgorithm.mutate(child, nGenes, problem.getInitialState());
        }
        newPopulation[newPopulationIndex] = child;
      }
    }

    EvaluationResult[] newPopulationEvaluationResults = evaluate(newPopulation);

    int repaired = 0;
    for (EvaluationResult result : newPopulationEvaluationResults) {
      if (result.isRepaired()) {
        repaired++;
      }
    }
    System.out.println(String.format("\tRepaired [%d]/[%d]", repaired, nChromosomes));

    evaluationResults = replacementAlgorithm.generateNewPopulation(evaluationResults, newPopulationEvaluationResults);

    double hammingMetric = getHammingMetric(evaluationResults, nGenes);
    double clustering = getClustering(evaluationResults, nGenes);
    System.out.println(String.format("\tHamming: [%f], Clustering: [%f], Mutation Rate: [%f]", hammingMetric, clustering, mutationProbability));

    double threshold = nGenes / 3;
    if (clustering < threshold) {
      mutationProbability = Math.min(mutationProbability + mutationDelta, 90.0);
    } else {
      mutationProbability = Math.max(mutationProbability - mutationDelta, MUTATE_PROBABILITY);
    }
  }

  private static Chromosome getRandomParent(Chromosome[] matingPool) {
    int parentIndex = Problem.getRandom().nextInt(matingPool.length);
    return matingPool[parentIndex];
  }

  private static int getBestResultIndex(EvaluationResult[] evaluationResults) {
    int bestIndex = 0;
    EvaluationResult bestResult = null;
    for (int i = 0; i < evaluationResults.length; i++) {
      if (bestResult == null || evaluationResults[i].isBetter(bestResult)) {
        bestResult = evaluationResults[i];
        bestIndex = i;
      }
    }
    return bestIndex;
  }

  private static double getHammingMetric(EvaluationResult[] results, int nGenes) {
    long hammingDistance = 0;
    int bestIndex = getBestResultIndex(results);
    EvaluationResult bestResult = results[bestIndex];
    for (int i = 0; i < results.length; i++) {
      if (bestIndex != i) {
        hammingDistance += getHammingDistance(bestResult, results[i], nGenes);
      }
    }
    return (double) hammingDistance / results.length;
  }

  private static long getHammingDistance(EvaluationResult individualA, EvaluationResult individualB, int nGenes) {
    long hammingDistance = 0;
    int nGenesToCompare = individualA.isSolution() ? Math.min(nGenes, individualA.getNSolutionGenes()) : nGenes;
    nGenesToCompare = individualB.isSolution() ? Math.min(nGenesToCompare, individualB.getNSolutionGenes()) : nGenesToCompare;
    for (int i = 0; i < nGenesToCompare; i++) {
      if (!individualA.getChromosome().getGene(i).equals(individualB.getChromosome().getGene(i))) {
        hammingDistance++;
      }
    }
    return hammingDistance;
  }

  private static double getClustering(EvaluationResult[] results, int nGenes) {
    long totalHammingDistance = 0;
    int nPairs = 0;
    for (int i = 0; i < results.length - 1; i++) {
      for (int j = i + 1; j < results.length; j++) {
        totalHammingDistance += getHammingDistance(results[i], results[j], nGenes);
        nPairs++;
      }
    }
    return (double) totalHammingDistance / nPairs;
  }
}

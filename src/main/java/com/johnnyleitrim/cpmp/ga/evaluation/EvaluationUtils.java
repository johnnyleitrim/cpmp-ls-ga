package com.johnnyleitrim.cpmp.ga.evaluation;

import java.util.ArrayList;
import java.util.List;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.fitness.FitnessAlgorithm;
import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.ga.Gene;
import com.johnnyleitrim.cpmp.ga.generator.GeneGenerator;
import com.johnnyleitrim.cpmp.ga.generator.IterativeLocalSearchGeneGenerator;
import com.johnnyleitrim.cpmp.ls.IterativeLocalSearch;
import com.johnnyleitrim.cpmp.state.InvalidMoveException;
import com.johnnyleitrim.cpmp.state.MutableState;

public class EvaluationUtils {

  public static EvaluationResult evaluate(Problem problem, int nGenes, Chromosome chromosome, FitnessAlgorithm fitnessAlgorithm) {
    MutableState newState = problem.getInitialState().copy();

    List<Gene> repairedGenes = new ArrayList<>(nGenes);
    List<Gene> badMoves = new ArrayList<>(nGenes);

    int lowestFitness = Integer.MAX_VALUE;
    for (int i = 0; i < nGenes; i++) {
      Gene move = chromosome.getGene(i);

      try {
        newState.applyMove(move.getSourceStack(), move.getDestinationStack());
        repairedGenes.add(move);
      } catch (InvalidMoveException e) {
        badMoves.add(move);
      }

      int fitness = fitnessAlgorithm.calculateFitness(newState);
      if (fitness == 0) {
        return toSolutionResult(repairedGenes, false);
      } else {
        lowestFitness = Math.min(lowestFitness, fitness);
      }
    }

    if (!badMoves.isEmpty()) {
      Gene[] newMoves = fillChromosome(badMoves, newState);
      for (Gene move : newMoves) {
        try {
          newState.applyMove(move.getSourceStack(), move.getDestinationStack());
          repairedGenes.add(move);
        } catch (InvalidMoveException e) {
          throw new RuntimeException(e);
        }
        int fitness = fitnessAlgorithm.calculateFitness(newState);
        if (fitness == 0) {
          return toSolutionResult(repairedGenes, true);
        } else {
          lowestFitness = Math.min(lowestFitness, fitness);
        }
      }
      if (repairedGenes.size() != nGenes) {
        throw new RuntimeException(repairedGenes.size() + " != " + nGenes);
      }
      chromosome = toChromosome(repairedGenes);
    }

    return EvaluationResult.forFitness(lowestFitness, chromosome, !badMoves.isEmpty());
  }

  private static Gene[] fillChromosome(List<Gene> badMoves, MutableState state) {
    GeneGenerator geneGenerator = new IterativeLocalSearchGeneGenerator(1, IterativeLocalSearch.Perturbation.LOWEST_MISOVERLAID_STACK_CLEARING);
    return geneGenerator.generateGenes(state, badMoves.size());
  }

  private static Chromosome toChromosome(List<Gene> genes) {
    return new Chromosome(genes.toArray(new Gene[genes.size()]));
  }

  private static EvaluationResult toSolutionResult(List<Gene> genes, boolean repaired) {
    Chromosome chromosome = new Chromosome(genes.toArray(new Gene[genes.size()]));
    return EvaluationResult.forSolution(genes.size(), chromosome, repaired);
  }
}

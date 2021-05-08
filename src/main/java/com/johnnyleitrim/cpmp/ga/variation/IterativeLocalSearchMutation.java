package com.johnnyleitrim.cpmp.ga.variation;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.fitness.BFLowerBoundFitness;
import com.johnnyleitrim.cpmp.fitness.FitnessAlgorithm;
import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.ga.Gene;
import com.johnnyleitrim.cpmp.ls.IterativeLocalSearch;
import com.johnnyleitrim.cpmp.ls.IterativeLocalSearch.Perturbation;
import com.johnnyleitrim.cpmp.ls.Move;
import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.state.State;
import com.johnnyleitrim.cpmp.utils.MoveUtils;

public class IterativeLocalSearchMutation implements MutationAlgorithm {

  private static final double MUTATE_PROBABILITY = 0.10;

  private final int nSearchMoves;

  private final Perturbation perturbation;

  private final FitnessAlgorithm fitnessAlgorithm;

  public IterativeLocalSearchMutation() {
    this(1, Perturbation.LOWEST_MISOVERLAID_STACK_CLEARING, new BFLowerBoundFitness());
  }

  public IterativeLocalSearchMutation(int nSearchMoves, Perturbation perturbation, FitnessAlgorithm fitnessAlgorithm) {
    this.nSearchMoves = nSearchMoves;
    this.perturbation = perturbation;
    this.fitnessAlgorithm = fitnessAlgorithm;
  }

  @Override
  public void mutate(Chromosome chromosome, int nGenes, State initialState) {
    if (Problem.getRandom().nextFloat() < MUTATE_PROBABILITY) {
      return;
    }

    MutableState currentState = initialState.copy();
    int originalMoves = 0;
    for (int i = 0; i < (nGenes / 2); i++) {
      Gene gene = chromosome.getGene(i);
      try {
        MoveUtils.applyMove(currentState, gene.getSourceStack(), gene.getDestinationStack());
        originalMoves++;
      } catch (Exception e) {
        // We ignore the error, and ignore the move.
      }
    }

    IterativeLocalSearch iterativeLocalSearch = new IterativeLocalSearch(currentState, 1, nSearchMoves, fitnessAlgorithm, Duration.of(2, ChronoUnit.SECONDS));
    List<Move> moves = iterativeLocalSearch.search(perturbation, true);
    for (int i = originalMoves; i < Math.min(originalMoves + moves.size(), nGenes); i++) {
      Move move = moves.get(i - originalMoves);
      chromosome.setGene(i, new Gene(move.getSrcStack(), move.getDstStack()));
    }
  }
}

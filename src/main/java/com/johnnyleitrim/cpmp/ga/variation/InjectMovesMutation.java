package com.johnnyleitrim.cpmp.ga.variation;

import java.util.OptionalInt;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.ga.Gene;
import com.johnnyleitrim.cpmp.random.RandomStackGenerator;
import com.johnnyleitrim.cpmp.state.InvalidMoveException;
import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.state.State;

public class InjectMovesMutation implements MutationAlgorithm {

  private static final double MUTATE_PROBABILITY = 0.02;

  private static final int N_MOVES = 3;

  @Override
  public void mutate(Chromosome chromosome, int nGenes, State initialState) {
    for (int geneIndex = 0; geneIndex < nGenes - N_MOVES; geneIndex++) {
      if (Problem.getRandom().nextFloat() < MUTATE_PROBABILITY) {
        injectNewMoves(chromosome, nGenes, geneIndex, initialState);
        geneIndex += N_MOVES;
      }
    }
  }

  void injectNewMoves(Chromosome chromosome, int nGenes, int newMoveStartIndex, State initialState) {
    MutableState currentState = initialState.copy();
    int moveIndex = 0;
    for (; moveIndex < newMoveStartIndex; moveIndex++) {
      try {
        Gene move = chromosome.getGene(moveIndex);
        currentState.applyMove(move.getSourceStack(), move.getDestinationStack());
      } catch (InvalidMoveException e) {
        break;
      }
    }
    RandomStackGenerator stackGenerator = new RandomStackGenerator(currentState.getStackStates(), OptionalInt.empty());
    int firstBlockSource = stackGenerator.getNextNonEmptyStack();
    int firstBlockDestination = stackGenerator.getNextNonFullStack();
    int secondBlockSource = stackGenerator.getNextNonEmptyStack();

    if (firstBlockSource == -1 || firstBlockDestination == -1 || secondBlockSource == -1) {
      // Unable to perform the move.
      return;
    }

    chromosome.copyTo(moveIndex, chromosome, moveIndex + N_MOVES, nGenes - moveIndex - N_MOVES);
    chromosome.setGene(moveIndex, new Gene(firstBlockSource, firstBlockDestination));
    chromosome.setGene(moveIndex + 1, new Gene(secondBlockSource, firstBlockSource));
    chromosome.setGene(moveIndex + 2, new Gene(firstBlockDestination, secondBlockSource));
  }

}

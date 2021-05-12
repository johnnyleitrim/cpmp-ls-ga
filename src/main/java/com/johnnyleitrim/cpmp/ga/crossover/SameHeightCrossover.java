package com.johnnyleitrim.cpmp.ga.crossover;

import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.ga.Gene;
import com.johnnyleitrim.cpmp.state.InvalidMoveException;
import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.state.State;

public class SameHeightCrossover implements CrossoverAlgorithm {

  private static final int MIN_CROSSOVER_INDEX = 2;

  private static final int MIN_CROSSOVER_LENGTH = 3;

  private static boolean isSameHeight(State stateA, State stateB) {
    for (int stack = 0; stack < stateA.getNumberOfStacks(); stack++) {
      if (stateA.getHeight(stack) != stateB.getHeight(stack)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Chromosome[] crossover(Chromosome parentA, Chromosome parentB, int nGenes, State initialState) {

    MutableState parentAState = initialState.copy();
    MutableState parentBState = initialState.copy();

    Chromosome childA = parentA.copy(nGenes);
    Chromosome childB = parentB.copy(nGenes);

    int countdown = 0;

    for (int moveIndex = 0; moveIndex < nGenes - 1; moveIndex++, countdown--) {
      try {
        Gene parentAMove = parentA.getGene(moveIndex);
        Gene parentBMove = parentB.getGene(moveIndex);
        parentAState.applyMove(parentAMove.getSourceStack(), parentAMove.getDestinationStack());
        parentBState.applyMove(parentBMove.getSourceStack(), parentBMove.getDestinationStack());
        if (countdown <= 0 && moveIndex >= MIN_CROSSOVER_INDEX && isSameHeight(parentAState, parentBState)) {
          int nextMoveIndex = moveIndex + 1;
          parentA.copyTo(nextMoveIndex, childB, nextMoveIndex, nGenes - nextMoveIndex);
          parentB.copyTo(nextMoveIndex, childA, nextMoveIndex, nGenes - nextMoveIndex);
          countdown = MIN_CROSSOVER_LENGTH + 1;
        }
      } catch (InvalidMoveException e) {
        throw new RuntimeException(e);
      }
    }
    return new Chromosome[]{childA, childB};
  }

}

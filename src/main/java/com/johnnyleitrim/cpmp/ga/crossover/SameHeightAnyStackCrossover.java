package com.johnnyleitrim.cpmp.ga.crossover;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.ga.Gene;
import com.johnnyleitrim.cpmp.state.InvalidMoveException;
import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.state.State;
import com.johnnyleitrim.cpmp.utils.StackUtils;

public class SameHeightAnyStackCrossover implements CrossoverAlgorithm {

  @Override
  public Chromosome[] crossover(Chromosome parentA, Chromosome parentB, int nGenes, State initialState) {

    MutableState parentAState = initialState.copy();
    MutableState parentBState = initialState.copy();

    Chromosome childA = parentA.copy(nGenes);
    Chromosome childB = parentB.copy(nGenes);

    StackUtils.StackMapping stackMapping = new StackUtils.StackMapping(initialState.getNumberOfStacks());

    int minCrossoverIndex = Problem.getRandom().nextInt(nGenes / 3);
    for (int moveIndex = 0; moveIndex < nGenes - 1; moveIndex++) {
      try {
        Gene parentAMove = parentA.getGene(moveIndex);
        Gene parentBMove = parentB.getGene(moveIndex);
        parentAState.applyMove(parentAMove.getSourceStack(), parentAMove.getDestinationStack());
        parentBState.applyMove(parentBMove.getSourceStack(), parentBMove.getDestinationStack());
        if (moveIndex >= minCrossoverIndex && StackUtils.mapStacksSameHeight(parentAState, parentBState, stackMapping)) {
          swapRemainingMoves(parentA, childB, moveIndex + 1, nGenes, stackMapping.getAToB());
          swapRemainingMoves(parentB, childA, moveIndex + 1, nGenes, stackMapping.getBToA());
          break;
        }
      } catch (InvalidMoveException e) {
        throw new RuntimeException(e);
      }
    }
    return new Chromosome[]{childA, childB};
  }

  private static void swapRemainingMoves(Chromosome parent, Chromosome child, int startMoveIndex, int nGenes, int[] mapping) {
    for (int moveIndex = startMoveIndex; moveIndex < nGenes; moveIndex++) {
      Gene parentMove = parent.getGene(moveIndex);
      child.setGene(moveIndex, mapGene(parentMove, mapping));
    }
  }

  private static Gene mapGene(Gene originalGene, int[] mapping) {
    int srcStack = originalGene.getSourceStack();
    int dstStack = originalGene.getDestinationStack();
    return new Gene(mapping[srcStack], mapping[dstStack]);
  }
}

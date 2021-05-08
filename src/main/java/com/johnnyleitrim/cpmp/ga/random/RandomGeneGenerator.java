package com.johnnyleitrim.cpmp.ga.random;

import java.util.OptionalInt;

import com.johnnyleitrim.cpmp.ga.Gene;
import com.johnnyleitrim.cpmp.ga.generator.GeneGenerator;
import com.johnnyleitrim.cpmp.random.RandomStackGenerator;
import com.johnnyleitrim.cpmp.state.InvalidMoveException;
import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.state.State;

public class RandomGeneGenerator implements GeneGenerator {

  public Gene[] generateGenes(State initialState, int nGenes) {
    MutableState newState = initialState.copy();
    Gene[] genes = new Gene[nGenes];
    for (int i = 0; i < nGenes; i++) {
      genes[i] = generateGene(newState);
      try {
        newState.applyMove(genes[i].getSourceStack(), genes[i].getDestinationStack());
      } catch (InvalidMoveException e) {
        throw new RuntimeException(e);
      }
    }
    return genes;
  }

  public Gene generateGene(State state) {
    RandomStackGenerator stackGenerator = new RandomStackGenerator(state.getStackStates(), OptionalInt.empty());
    int srcStack = stackGenerator.getNextNonEmptyStack();
    int dstStack = stackGenerator.getNextNonFullStack();
    return new Gene(srcStack, dstStack);
  }
}

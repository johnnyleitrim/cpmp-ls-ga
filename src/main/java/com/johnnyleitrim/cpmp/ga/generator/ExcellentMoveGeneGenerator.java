package com.johnnyleitrim.cpmp.ga.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.ga.Gene;
import com.johnnyleitrim.cpmp.ga.random.RandomGeneGenerator;
import com.johnnyleitrim.cpmp.state.InvalidMoveException;
import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.state.State;

public class ExcellentMoveGeneGenerator implements GeneGenerator {

  private final RandomGeneGenerator randomGeneGenerator = new RandomGeneGenerator();

  @Override
  public Gene generateGene(State state) {
    return findExcellentMove(state).orElseGet(() -> randomGeneGenerator.generateGene(state));
  }

  @Override
  public Gene[] generateGenes(State initialState, int nGenes) {
    MutableState state = initialState.copy();
    Gene[] genes = new Gene[nGenes];

    for (int i = 0; i < nGenes; i++) {
      genes[i] = generateGene(state);
      try {
        state.applyMove(genes[i].getSourceStack(), genes[i].getDestinationStack());
      } catch (InvalidMoveException e) {
        throw new RuntimeException(e);
      }
    }
    return genes;
  }

  private Optional<Gene> findExcellentMove(State state) {
    int nStacks = state.getNumberOfStacks();
    int nTiers = state.getNumberOfTiers();
    int[] topGroups = new int[nStacks];
    for (int stack = 0; stack < nStacks; stack++) {
      topGroups[stack] = state.getTopGroup(stack);
    }

    List<Integer> srcStacks = new ArrayList<>(nStacks);
    List<Integer> dstStacks = new ArrayList<>(nStacks);

    for (int group : state.getGroups()) {
      for (int stack = 0; stack < nStacks; stack++) {
        if (topGroups[stack] == group && state.isMisOverlaid(stack)) {
          srcStacks.add(stack);
        }

        if (topGroups[stack] >= group && !state.isMisOverlaid(stack) && state.getHeight(stack) < nTiers) {
          dstStacks.add(stack);
        }
      }
      if (!srcStacks.isEmpty() && !dstStacks.isEmpty()) {
        int srcStackIdx = Problem.getRandom().nextInt(srcStacks.size());
        int dstStackIdx = Problem.getRandom().nextInt(dstStacks.size());
        return Optional.of(new Gene(srcStacks.get(srcStackIdx), dstStacks.get(dstStackIdx)));
      }

      srcStacks.clear();
      dstStacks.clear();
    }
    return Optional.empty();
  }
}

package com.johnnyleitrim.cpmp.fitness;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.state.State;

public class JFFitness implements FitnessAlgorithm {
  @Override
  public int calculateFitness(State state) {
    int totalFitness = 0;
    int nTiers = state.getNumberOfTiers();
    int nStacks = state.getNumberOfStacks();
    for (int stack = 0; stack < nStacks; stack++) {
      int stackHeight = state.getHeight(stack);
      int lastGroup = Problem.EMPTY;
      int fitness = 0;
      for (int tier = nTiers - 1; tier >= 0; tier--) {
        int group = state.getGroup(stack, tier);
        if (group < lastGroup) {
          fitness = stackHeight - tier - 1;
        }
        lastGroup = group;
      }
      totalFitness += fitness;
    }
    return totalFitness;
  }
}

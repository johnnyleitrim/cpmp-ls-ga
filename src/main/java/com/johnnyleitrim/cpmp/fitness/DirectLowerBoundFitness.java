package com.johnnyleitrim.cpmp.fitness;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.state.State;

public class DirectLowerBoundFitness implements FitnessAlgorithm {
  @Override
  public int calculateFitness(State state) {
    int nTiers = state.getNumberOfTiers();
    int nStacks = state.getNumberOfStacks();

    int nb = 0;

    for (int stack = 0; stack < nStacks; stack++) {
      int highestPriorityGroup = Problem.EMPTY;
      boolean misOverlaid = false;
      for (int tier = 0; tier < nTiers; tier++) {
        int priorityGroup = state.getGroup(stack, tier);
        if (priorityGroup != Problem.EMPTY) {
          if (priorityGroup > highestPriorityGroup && highestPriorityGroup != Problem.EMPTY) {
            misOverlaid = true;
          } else if (highestPriorityGroup == Problem.EMPTY || priorityGroup < highestPriorityGroup) {
            highestPriorityGroup = priorityGroup;
          }
          if (misOverlaid) {
            nb += 1;
          }
        }
      }
    }
    return nb;
  }
}

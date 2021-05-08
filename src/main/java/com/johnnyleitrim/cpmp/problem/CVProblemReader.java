package com.johnnyleitrim.cpmp.problem;

import java.util.List;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.state.State;

public class CVProblemReader {

  public static Problem fromLines(String name, List<String> lines) {
    String[] dimensions = lines.get(0).split(" ");
    int nStacks = Integer.parseInt(dimensions[0]);
    int stackHeight = (Integer.parseInt(dimensions[1]) / nStacks);
    int nTiers = stackHeight + 2;

    int[][] initialState = new int[nTiers][nStacks];
    for (int stack = 0, lineIndex = 1; stack < nStacks; stack++, lineIndex++) {
      String[] stackDetails = lines.get(lineIndex).split(" ");
      if (Integer.parseInt(stackDetails[0]) != stackHeight) {
        throw new RuntimeException("Stack was not the expected height when reading " + name);
      }
      int[] tierGroups = toGroups(stackDetails, stackHeight);
      for (int tier = 0; tier < nTiers; tier++) {
        initialState[tier][stack] = tierGroups[tier];
      }
    }

    State state = new State(initialState, nStacks, nTiers);
    return new Problem(name, state);
  }

  private static int[] toGroups(String[] stackDetails, int stackHeight) {
    int[] groups = new int[stackHeight + 2];
    for (int i = 0; i < stackHeight; i++) {
      groups[i] = Integer.parseInt(stackDetails[i + 1]);
    }
    groups[stackHeight] = Problem.EMPTY;
    groups[stackHeight + 1] = Problem.EMPTY;
    return groups;
  }
}

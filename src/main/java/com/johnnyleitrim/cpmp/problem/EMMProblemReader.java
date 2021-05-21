package com.johnnyleitrim.cpmp.problem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.state.State;

public class EMMProblemReader {

  public static Problem fromLines(String name, List<String> lines) {
    Map<String, String> details = new HashMap<>(lines.size());
    for (String line : lines) {
      if (line.contains(":")) {
        StringTokenizer st = new StringTokenizer(line, ":");
        details.put(st.nextToken().trim(), st.nextToken().trim());
      }
    }

    int nStacks = Integer.parseInt(details.get("Stacks"));
    int nTiers = Integer.parseInt(details.get("Tiers")) + 2;

    int[][] initialState = new int[nTiers][nStacks];
    for (int stack = 0; stack < nStacks; stack++) {
      String stackDetails = details.get("Stack " + stack).trim();
      int[] tierGroups = toGroups(stackDetails, nTiers);
      for (int tier = 0; tier < nTiers; tier++) {
        initialState[tier][stack] = tierGroups[tier];
      }
    }

    State state = new State(initialState, nStacks, nTiers);
    return new Problem(name, state);
  }

  private static int[] toGroups(String stackDetails, int nTiers) {
    int[] groups = new int[nTiers];
    StringTokenizer st = new StringTokenizer(stackDetails);
    for (int i = 0; st.hasMoreTokens(); i++) {
      groups[i] = Integer.parseInt(st.nextToken()) + 1;
    }
    return groups;
  }
}

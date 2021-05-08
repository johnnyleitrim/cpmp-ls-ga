package com.johnnyleitrim.cpmp.fitness;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.state.State;

public class IBFLowerBoundFitness implements FitnessAlgorithm {
  @Override
  public int calculateFitness(State state) {
    int nTiers = state.getNumberOfTiers();
    int nStacks = state.getNumberOfStacks();
    SortedSet<Integer> groups = state.getGroups();
    int[] demandByGroup = new int[groups.iterator().next() + 1];

    int nb = 0; // Number of all badly placed items
    int[] nbs = new int[nStacks]; // Number of all badly placed items in stack s
    boolean[] reverseOrder = new boolean[nStacks];
    StackPosition[] highestWellPlacedGroup = new StackPosition[nStacks];

    for (int stack = 0; stack < nStacks; stack++) {
      int highestPriorityGroup = Problem.EMPTY;
      int nbStack = 0;
      boolean misOverlaid = false;
      int previousPriorityGroup = Problem.EMPTY;
      boolean isReverseOrder = state.getHeight(stack) > 1;
      for (int tier = 0; tier < nTiers; tier++) {
        int priorityGroup = state.getGroup(stack, tier);
        if (priorityGroup != Problem.EMPTY) {
          if (priorityGroup > highestPriorityGroup && highestPriorityGroup != Problem.EMPTY) {
            misOverlaid = true;
          } else if (highestPriorityGroup == Problem.EMPTY || priorityGroup < highestPriorityGroup) {
            highestPriorityGroup = priorityGroup;
          }
          if (misOverlaid) {
            demandByGroup[priorityGroup] += 1;
            nbStack += 1;
          } else {
            highestWellPlacedGroup[stack] = new StackPosition(tier + 1, priorityGroup);
          }
        }
        if (priorityGroup != Problem.EMPTY && previousPriorityGroup != Problem.EMPTY && previousPriorityGroup > priorityGroup) {
          isReverseOrder = false;
        }
        previousPriorityGroup = priorityGroup;
      }
      nb += nbStack;
      nbs[stack] = nbStack;
      reverseOrder[stack] = isReverseOrder;
    }
    int[] cumulativeDemand = getCumulative(demandByGroup, groups, 0);
    int[] cumulativeSupply = getCumulativeSupply(highestWellPlacedGroup, groups, state);
    int[] cumulativeSurplus = getSurplus(cumulativeDemand, cumulativeSupply, groups);
    int biggestSurplusGroup = getHighestSurplusGroup(cumulativeSurplus);
    int gxStacks = Math.max(0, (int) Math.ceil(cumulativeSurplus[biggestSurplusGroup] / (double) nTiers));

    // Number of all well placed items of all groups g′ < g in stack s
    int[] ngs = new int[nStacks];
    boolean[] potentialGxStacks = new boolean[nStacks];
    for (int stack = 0; stack < nStacks; stack++) {
      int ngsStack = 0;
      if (highestWellPlacedGroup[stack] != null) {
        if (highestWellPlacedGroup[stack].group < biggestSurplusGroup) {
          potentialGxStacks[stack] = true;
        }
        for (int tier = 0; tier < highestWellPlacedGroup[stack].height; tier++) {
          int priorityGroup = state.getGroup(stack, tier);
          if (priorityGroup < biggestSurplusGroup) {
            ngsStack++;
          }
        }
      }
      ngs[stack] = ngsStack;
    }
    List<Integer> sortedGxStacks = argsort(potentialGxStacks, ngs);
    int nGxMoves = 0;
    for (int i = 0; i < gxStacks; i++) {
      nGxMoves += ngs[sortedGxStacks.get(i)];
    }

    int ibf0 = 0;
    for (int stack = 0; stack < nStacks; stack++) {
      int misOverlaid = nbs[stack];
      if (nbs[stack] > 0) {
        if (ibf0 == 0 || misOverlaid < ibf0) {
          ibf0 = misOverlaid;
        }
      } else if (state.getHeight(stack) != nTiers) {
        ibf0 = 0;
        break;
      }
    }

    int minMisoverlaid = min(nbs);

    // IBF1: All stacks are misoverlaid
    int ibf1 = 0;
    if (minMisoverlaid > 0) {
      int minGroupInReverseStacks = Integer.MAX_VALUE;
      int maxWellPlacedGroupInLeastMisoverlaidStacks = 0;
      for (int stack = 0; stack < nStacks; stack++) {
        if (reverseOrder[stack]) {
          minGroupInReverseStacks = Math.min(minGroupInReverseStacks, state.getTopGroup(stack));
        }
        if (nbs[stack] == minMisoverlaid) {
          maxWellPlacedGroupInLeastMisoverlaidStacks = Math.max(maxWellPlacedGroupInLeastMisoverlaidStacks, highestWellPlacedGroup[stack].group);
        }
      }
      if (minGroupInReverseStacks > maxWellPlacedGroupInLeastMisoverlaidStacks) {
        ibf1 = 1;
      }
    } else {
      // If only one stack if non-misoverlaid
      int minGroupInReverseStacks = Integer.MAX_VALUE;
      List<Integer> nonMisoverlaidStacks = new ArrayList<>(nStacks);
      for (int stack = 0; stack < nStacks; stack++) {
        if (reverseOrder[stack]) {
          minGroupInReverseStacks = Math.min(minGroupInReverseStacks, state.getTopGroup(stack));
        }
        int stackHeight = state.getHeight(stack);
        if (nbs[stack] == 0 && stackHeight > 0 && stackHeight < nTiers) {
          nonMisoverlaidStacks.add(stack);
        }
      }
      if (nonMisoverlaidStacks.size() == 1) {
        int group = state.getTopGroup(nonMisoverlaidStacks.get(0));
        if (minGroupInReverseStacks != Integer.MAX_VALUE && minGroupInReverseStacks > group) {
          ibf1 = 1;
        }
      }
    }

    return nb + minMisoverlaid + nGxMoves + ibf0 + ibf1;
  }

  private static int min(int[] arr) {
    int min = Integer.MAX_VALUE;
    for (int val : arr) {
      if (val < min) {
        min = val;
      }
    }
    return min;
  }

  private static int[] getCumulative(int[] countByGroup, SortedSet<Integer> groups, int initialValue) {
    int[] cumulativeByGroup = new int[countByGroup.length];
    int cumulativeCount = initialValue;
    for (int group : groups) {
      cumulativeCount += countByGroup[group];
      cumulativeByGroup[group] = cumulativeCount;
    }
    return cumulativeByGroup;
  }

  private static int[] getCumulativeSupply(StackPosition[] highestWellPlacedGroups, SortedSet<Integer> groups, State state) {
    int[] supplyByGroup = new int[groups.iterator().next() + 1];
    int emptyStackSupply = 0;
    int nTiers = state.getNumberOfTiers();
    for (int stack = 0; stack < state.getNumberOfStacks(); stack++) {
      if (highestWellPlacedGroups[stack] == null) {
        emptyStackSupply += nTiers;
      } else {
        supplyByGroup[highestWellPlacedGroups[stack].group] += nTiers - highestWellPlacedGroups[stack].height;
      }
    }
    return getCumulative(supplyByGroup, groups, emptyStackSupply);
  }

  private static int[] getSurplus(int[] demandByGroup, int[] supplyByGroup, Set<Integer> groups) {
    int[] surplusByGroup = new int[demandByGroup.length];
    for (int group : groups) {
      surplusByGroup[group] = demandByGroup[group] - supplyByGroup[group];
    }
    return surplusByGroup;
  }

  private static int getHighestSurplusGroup(int[] surplusByGroup) {
    int highestSurplusGroup = 0;
    int highestSurplus = Integer.MIN_VALUE;
    for (int group = 1; group < surplusByGroup.length; group++) {
      if (surplusByGroup[group] > highestSurplus) {
        highestSurplusGroup = group;
        highestSurplus = surplusByGroup[group];
      }
    }
    return highestSurplusGroup;
  }

  private static List<Integer> argsort(boolean[] potentialStack, int[] values) {
    List<Integer> indexes = new ArrayList<>(values.length);
    for (int i = 0; i < values.length; i++) {
      if (potentialStack[i]) {
        indexes.add(i);
      }
    }
    Collections.sort(indexes, new Comparator<Integer>() {
      @Override
      public int compare(Integer index1, Integer index2) {
        return values[index1] - values[index2];
      }
    });
    return indexes;
  }

  private static class StackPosition {
    private final int height;
    private final int group;

    private StackPosition(int height, int group) {
      this.height = height;
      this.group = group;
    }
  }

}

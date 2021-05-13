package com.johnnyleitrim.cpmp.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.ls.Move;
import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.state.State;

public class StackUtils {

  public static List<Move> clearStack(MutableState state, int stackToClear) {
    List<Move> moves = new LinkedList<>();

    int nStacks = state.getNumberOfStacks();
    int nTiers = state.getNumberOfTiers();

    while (state.getHeight(stackToClear) > 0) {
      // Find a stack with a value lower than ours at the top
      List<Integer> candidateDestinationStacks = new ArrayList<>(nStacks);
      int srcGroup = state.getTopGroup(stackToClear);

      for (int s = 0; s < nStacks; s++) {
        if (stackToClear != s && state.getHeight(s) < nTiers) {
          candidateDestinationStacks.add(s);
        }
      }

      int dstStack = -1;
      if (candidateDestinationStacks.size() > 0) {
        candidateDestinationStacks.sort(Comparator.comparingInt(state::getTopGroup).reversed());
        for (int candidateDestStack : candidateDestinationStacks) {
          if (state.getTopGroup(candidateDestStack) <= srcGroup) {
            dstStack = candidateDestStack;
            break;
          }
        }
        if (dstStack == -1) {
          dstStack = candidateDestinationStacks.get(Problem.getRandom().nextInt(candidateDestinationStacks.size()));
        }
      }

      if (dstStack == -1) {
        return moves;
      } else {
        moves.add(MoveUtils.applyMove(state, stackToClear, dstStack));
      }
    }
    return moves;
  }

  public static List<Move> fillStack(MutableState state, int stackToFill) {
    List<Move> moves = new LinkedList<>();

    int nTiers = state.getNumberOfTiers();
    int nStacks = state.getNumberOfStacks();

    while (state.getHeight(stackToFill) < nTiers) {
      int stackToFillTopGroup = state.getTopGroup(stackToFill);
      if (stackToFillTopGroup == Problem.EMPTY) {
        stackToFillTopGroup = Integer.MAX_VALUE;
      }

      int highestGroup = Problem.EMPTY;
      int bestSourceStack = -1;

      for (int stack = 0; stack < nStacks; stack++) {
        if (stack != stackToFill) {
          int topGroup = state.getTopGroup(stack);
          if (topGroup != Problem.EMPTY && topGroup <= stackToFillTopGroup && topGroup > highestGroup) {
            highestGroup = topGroup;
            bestSourceStack = stack;
          }
        }
      }
      // If no source stack could be identified, exit
      if (bestSourceStack < 0) {
        return moves;
      } else {
        moves.add(MoveUtils.applyMove(state, bestSourceStack, stackToFill));
      }
    }
    return moves;
  }

  public static List<Integer> getLowestStacks(State state) {
    return getLowestStacks(state, ignored -> true);
  }

  public static List<Integer> getLowestStacks(State state, Predicate<Integer> filter) {
    int nStacks = state.getNumberOfStacks();
    int lowestStackHeight = Integer.MAX_VALUE;
    List<Integer> lowestStacks = new ArrayList<>(nStacks);
    for (int stack = 0; stack < nStacks; stack++) {
      if (filter.test(stack)) {
        int stackHeight = state.getHeight(stack);
        if (stackHeight < lowestStackHeight) {
          lowestStackHeight = stackHeight;
          lowestStacks.clear();
          lowestStacks.add(stack);
        } else if (stackHeight == lowestStackHeight) {
          lowestStacks.add(stack);
        }
      }
    }
    return lowestStacks;
  }

  public static boolean isMisOverlaid(State state, int stack) {
    int nTiers = state.getNumberOfTiers();
    int highestPriorityGroup = Problem.EMPTY;
    for (int tier = 0; tier < nTiers; tier++) {
      int priorityGroup = state.getGroup(stack, tier);
      if (priorityGroup != Problem.EMPTY) {
        if (priorityGroup > highestPriorityGroup && highestPriorityGroup != Problem.EMPTY) {
          return true;
        } else if (highestPriorityGroup == Problem.EMPTY || priorityGroup < highestPriorityGroup) {
          highestPriorityGroup = priorityGroup;
        }
      }
    }
    return false;
  }

  public static boolean mapStacksSameHeight(State stateA, State stateB, StackMapping stackMapping) {
    int nStacks = stateA.getNumberOfStacks();
    Set<Integer> usedBStacks = new HashSet<>(nStacks);
    for (int stackA = 0; stackA < nStacks; stackA++) {
      boolean foundMatch = false;
      for (int stackB = 0; stackB < nStacks; stackB++) {
        if (!usedBStacks.contains(stackB) &&
            stateA.getHeight(stackA) == stateB.getHeight(stackB)) {
          stackMapping.mapAToB(stackA, stackB);
          usedBStacks.add(stackB);
          foundMatch = true;
          break;
        }
      }
      if (!foundMatch) {
        return false;
      }
    }
    return true;
  }

  public static class StackMapping {
    private final int[] aToB;
    private final int[] bToA;

    public StackMapping(int nStacks) {
      this.aToB = new int[nStacks];
      this.bToA = new int[nStacks];
    }

    public void mapAToB(int stackA, int stackB) {
      aToB[stackA] = stackB;
      bToA[stackB] = stackA;
    }

    public int[] getAToB() {
      return aToB;
    }

    public int[] getBToA() {
      return bToA;
    }
  }
}

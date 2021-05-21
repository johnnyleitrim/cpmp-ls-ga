package com.johnnyleitrim.cpmp.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.ls.Features;
import com.johnnyleitrim.cpmp.ls.Move;
import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.state.State;

public class StackUtils {

  public static List<Move> clearStack(MutableState state, int stackToClear) {
    List<Move> moves = new LinkedList<>();

    while (state.getHeight(stackToClear) > 0) {
      // Find a stack with a value lower than ours at the top
      List<Integer> candidateDestinationStacks = getNonFullStacks(state, s -> stackToClear != s);

      int dstStack = -1;
      if (candidateDestinationStacks.size() > 0) {
        if (Features.instance.isClearToBestStackEnabled()) {
          int srcGroup = state.getTopGroup(stackToClear);
          candidateDestinationStacks.sort(Comparator.comparingInt(state::getTopGroup).reversed());
          for (int candidateDestStack : candidateDestinationStacks) {
            if (state.getTopGroup(candidateDestStack) <= srcGroup) {
              dstStack = candidateDestStack;
              break;
            }
          }
          if (dstStack == -1) {
            // If there are no stacks with a smaller value container on top,
            // Move to the stack with the lowest value container.
            dstStack = candidateDestinationStacks.get(candidateDestinationStacks.size() - 1);
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

      Predicate<Integer> srcStackFilter = Features.instance.isDontFillFromGoodStackEnabled() ? state::isMisOverlaid : s -> true;

      for (int stack = 0; stack < nStacks; stack++) {
        if (stack != stackToFill && srcStackFilter.test(stack)) {
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

  public static List<Integer> getNonFullStacks(State state, Predicate<Integer> stackFilter) {
    int nStacks = state.getNumberOfStacks();
    int nTiers = state.getNumberOfTiers();

    List<Integer> nonFullStacks = new ArrayList<>(nStacks);

    for (int s = 0; s < nStacks; s++) {
      if (state.getHeight(s) < nTiers && stackFilter.test(s)) {
        nonFullStacks.add(s);
      }
    }
    return nonFullStacks;
  }

  public static List<Integer> getLowestStacks(State state) {
    return getLowestStacks(state, ignored -> true);
  }

  public static List<Integer> getLowestStacks(State state, Predicate<Integer> stackFilter) {
    int nStacks = state.getNumberOfStacks();
    int lowestStackHeight = Integer.MAX_VALUE;
    List<Integer> lowestStacks = new ArrayList<>(nStacks);
    for (int stack = 0; stack < nStacks; stack++) {
      if (stackFilter.test(stack)) {
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

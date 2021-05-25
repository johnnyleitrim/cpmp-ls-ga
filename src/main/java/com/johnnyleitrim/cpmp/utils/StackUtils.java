package com.johnnyleitrim.cpmp.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.johnnyleitrim.cpmp.ls.Move;
import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.state.State;
import com.johnnyleitrim.cpmp.strategy.StackClearingStrategy;
import com.johnnyleitrim.cpmp.strategy.StackFillingStrategy;

public class StackUtils {

  public static List<Move> clearStack(MutableState state, int stackToClear, StackClearingStrategy strategy) {
    List<Move> moves = new LinkedList<>();
    while (state.getHeight(stackToClear) > 0) {
      Optional<Integer> dstStack = strategy.selectStack(state, stackToClear);
      if (dstStack.isEmpty()) {
        return moves;
      } else {
        moves.add(MoveUtils.applyMove(state, stackToClear, dstStack.get()));
      }
    }
    return moves;
  }

  public static List<Move> fillStack(MutableState state, int stackToFill, StackFillingStrategy strategy) {
    List<Move> moves = new LinkedList<>();
    int nTiers = state.getNumberOfTiers();
    while (state.getHeight(stackToFill) < nTiers) {
      Optional<Integer> srcStack = strategy.selectStack(state, stackToFill);
      if (srcStack.isEmpty()) {
        return moves;
      } else {
        moves.add(MoveUtils.applyMove(state, srcStack.get(), stackToFill));
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
    int nTiers = state.getNumberOfTiers();
    LargestValueItems<Integer> lowestStacks = new LargestValueItems<>(nStacks);
    for (int stack = 0; stack < nStacks; stack++) {
      if (stackFilter.test(stack)) {
        lowestStacks.add(nTiers - state.getHeight(stack), stack);
      }
    }
    return lowestStacks.getItems();
  }
}

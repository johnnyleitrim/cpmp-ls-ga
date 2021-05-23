package com.johnnyleitrim.cpmp.strategy;

import java.util.Optional;
import java.util.function.Predicate;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.state.State;

public class StackFillingStrategies {

  public static final StackFillingStrategy LARGEST_CONTAINER = new BaseStrategy(false);

  public static final StackFillingStrategy LARGEST_MIS_OVERLAID_CONTAINER = new BaseStrategy(true);

  private static class BaseStrategy implements StackFillingStrategy {

    private final boolean misOverlaidOnly;

    private BaseStrategy(boolean misOverlaidOnly) {
      this.misOverlaidOnly = misOverlaidOnly;
    }

    @Override
    public Optional<Integer> apply(State state, Integer stackToFill) {
      int nStacks = state.getNumberOfStacks();

      int highestGroup = Problem.EMPTY;
      int bestSourceStack = -1;

      int stackToFillTopGroup = state.getTopGroup(stackToFill);
      if (stackToFillTopGroup == Problem.EMPTY) {
        stackToFillTopGroup = Integer.MAX_VALUE;
      }

      Predicate<Integer> srcStackFilter = misOverlaidOnly ? state::isMisOverlaid : s -> true;

      for (int stack = 0; stack < nStacks; stack++) {
        if (stack != stackToFill && srcStackFilter.test(stack)) {
          int topGroup = state.getTopGroup(stack);
          if (topGroup != Problem.EMPTY && topGroup <= stackToFillTopGroup && topGroup > highestGroup) {
            highestGroup = topGroup;
            bestSourceStack = stack;
          }
        }
      }
      return bestSourceStack == -1 ? Optional.empty() : Optional.of(bestSourceStack);
    }
  }
}

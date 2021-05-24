package com.johnnyleitrim.cpmp.strategy;

import com.johnnyleitrim.cpmp.Random;
import com.johnnyleitrim.cpmp.state.State;
import com.johnnyleitrim.cpmp.utils.StackUtils;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StackClearingStrategies {

  public static final StackClearingStrategy RANDOM = new Strategy("Random") {
    @Override
    public Optional<Integer> selectStack(State state, int stackToClear) {
      List<Integer> candidateDestinationStacks = StackUtils.getNonFullStacks(state, s -> s != stackToClear);
      if (candidateDestinationStacks.isEmpty()) {
        return Optional.empty();
      }
      return Optional.of(Random.getRandomItem(candidateDestinationStacks));
    }
  };

  public static final StackClearingStrategy CLEAR_TO_BEST = new Strategy("Clear to best") {
    @Override
    public Optional<Integer> selectStack(State state, int stackToClear) {
      List<Integer> candidateDestinationStacks = StackUtils.getNonFullStacks(state, s -> s != stackToClear);
      int srcGroup = state.getTopGroup(stackToClear);
      candidateDestinationStacks.sort(Comparator.comparingInt(state::getTopGroup).reversed());
      for (int candidateDestStack : candidateDestinationStacks) {
        if (state.getTopGroup(candidateDestStack) <= srcGroup) {
          return Optional.of(candidateDestStack);
        }
      }
      return Optional.of(Random.getRandomItem(candidateDestinationStacks));
    }
  };

  private static abstract class Strategy extends BaseStrategy implements StackClearingStrategy {
    public Strategy(String name) {
      super(name);
    }
  }
}

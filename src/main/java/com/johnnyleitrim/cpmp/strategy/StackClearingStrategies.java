package com.johnnyleitrim.cpmp.strategy;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.johnnyleitrim.cpmp.Random;
import com.johnnyleitrim.cpmp.utils.StackUtils;

public class StackClearingStrategies {

  public static final StackClearingStrategy RANDOM = (state, stackToClear) -> {
    List<Integer> candidateDestinationStacks = StackUtils.getNonFullStacks(state, s -> s != stackToClear);
    if (candidateDestinationStacks.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(Random.getRandomItem(candidateDestinationStacks));
  };

  public static final StackClearingStrategy CLEAR_TO_BEST = (state, stackToClear) -> {
    List<Integer> candidateDestinationStacks = StackUtils.getNonFullStacks(state, s -> s != stackToClear);
    int srcGroup = state.getTopGroup(stackToClear);
    candidateDestinationStacks.sort(Comparator.comparingInt(state::getTopGroup).reversed());
    for (int candidateDestStack : candidateDestinationStacks) {
      if (state.getTopGroup(candidateDestStack) <= srcGroup) {
        return Optional.of(candidateDestStack);
      }
    }
    return Optional.of(Random.getRandomItem(candidateDestinationStacks));
  };

}

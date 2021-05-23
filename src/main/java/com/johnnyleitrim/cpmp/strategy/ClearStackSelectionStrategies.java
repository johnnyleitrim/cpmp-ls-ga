package com.johnnyleitrim.cpmp.strategy;

import java.util.List;
import java.util.OptionalInt;

import com.johnnyleitrim.cpmp.Random;
import com.johnnyleitrim.cpmp.random.RandomStackGenerator;
import com.johnnyleitrim.cpmp.utils.StackUtils;

public class ClearStackSelectionStrategies {

  public static final ClearStackSelectionStrategy RANDOM_STACK = (state) -> {
    RandomStackGenerator stackGenerator = new RandomStackGenerator(state.getStackStates(), OptionalInt.empty());
    return stackGenerator.getNextNonEmptyStack();
  };

  public static final ClearStackSelectionStrategy LOWEST_STACK = (state) -> {
    List<Integer> lowestStacks = StackUtils.getLowestStacks(state);
    return Random.getRandomItem(lowestStacks);
  };

  public static final ClearStackSelectionStrategy LOWEST_MIS_OVERLAID_STACK = (state) -> {
    List<Integer> lowestStacks = StackUtils.getLowestStacks(state, state::isMisOverlaid);
    return Random.getRandomItem(lowestStacks);
  };

}

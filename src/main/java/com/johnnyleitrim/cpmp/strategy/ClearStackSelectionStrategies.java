package com.johnnyleitrim.cpmp.strategy;

import com.johnnyleitrim.cpmp.Random;
import com.johnnyleitrim.cpmp.random.RandomStackGenerator;
import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.utils.StackUtils;
import java.util.List;
import java.util.OptionalInt;

public class ClearStackSelectionStrategies {

  public static final ClearStackSelectionStrategy RANDOM_STACK = new Strategy("Random") {
    @Override
    public int selectStack(MutableState state) {
      RandomStackGenerator stackGenerator = new RandomStackGenerator(state.getStackStates(), OptionalInt.empty());
      return stackGenerator.getNextNonEmptyStack();
    }
  };

  public static final ClearStackSelectionStrategy LOWEST_STACK = new Strategy("Lowest stack") {
    @Override
    public int selectStack(MutableState state) {
      List<Integer> lowestStacks = StackUtils.getLowestStacks(state);
      return Random.getRandomItem(lowestStacks);
    }
  };

  public static final ClearStackSelectionStrategy LOWEST_MIS_OVERLAID_STACK = new Strategy("Lowest mis-overlaid stack") {
    @Override
    public int selectStack(MutableState state) {
      List<Integer> lowestStacks = StackUtils.getLowestStacks(state, state::isMisOverlaid);
      return Random.getRandomItem(lowestStacks);
    }
  };

  private static abstract class Strategy extends BaseStrategy implements ClearStackSelectionStrategy {
    public Strategy(String name) {
      super(name);
    }
  }
}

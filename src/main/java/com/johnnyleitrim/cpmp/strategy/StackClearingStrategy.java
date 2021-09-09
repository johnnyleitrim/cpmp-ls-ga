package com.johnnyleitrim.cpmp.strategy;

import java.util.Optional;

import com.johnnyleitrim.cpmp.state.State;

public interface StackClearingStrategy extends Strategy {
  Optional<Integer> selectStack(State state, int stackToClear);
}

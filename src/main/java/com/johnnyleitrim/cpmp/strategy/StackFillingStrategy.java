package com.johnnyleitrim.cpmp.strategy;

import com.johnnyleitrim.cpmp.state.State;
import java.util.Optional;

public interface StackFillingStrategy extends Strategy {
  Optional<Integer> selectStack(State state, int stackToFill);
}

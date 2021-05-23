package com.johnnyleitrim.cpmp.strategy;

import java.util.Optional;
import java.util.function.BiFunction;

import com.johnnyleitrim.cpmp.state.State;

public interface StackClearingStrategy extends BiFunction<State, Integer, Optional<Integer>> {
}

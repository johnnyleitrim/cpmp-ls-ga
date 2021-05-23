package com.johnnyleitrim.cpmp.strategy;

import java.util.function.Function;

import com.johnnyleitrim.cpmp.state.MutableState;

public interface ClearStackSelectionStrategy extends Function<MutableState, Integer> {
}

package com.johnnyleitrim.cpmp.strategy;

import com.johnnyleitrim.cpmp.state.MutableState;

public interface ClearStackSelectionStrategy extends Strategy {
  int selectStack(MutableState state);
}

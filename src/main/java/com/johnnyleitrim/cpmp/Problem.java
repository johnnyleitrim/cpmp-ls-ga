package com.johnnyleitrim.cpmp;

import com.johnnyleitrim.cpmp.state.State;

public class Problem {

  public static final int EMPTY = 0;

  private final String name;

  private final State initialState;

  public Problem(String name, State initialState) {
    this.name = name;
    this.initialState = initialState;
  }

  public String getName() {
    return name;
  }

  public State getInitialState() {
    return initialState;
  }

  @Override
  public String toString() {
    return initialState.toString();
  }
}

package com.johnnyleitrim.cpmp;

import java.util.Random;

import com.johnnyleitrim.cpmp.state.State;

public class Problem {

  public static final int EMPTY = 0;

  private static Random RANDOM = new Random();

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

  public static Random getRandom() {
    return RANDOM;
  }

  public static void setRandom(Random random) {
    Problem.RANDOM = random;
  }

  public static void setRandomSeed(long seed) {
    System.out.println("Setting random seed: " + seed);
    Problem.RANDOM = new Random(seed);
  }

  @Override
  public String toString() {
    return initialState.toString();
  }
}

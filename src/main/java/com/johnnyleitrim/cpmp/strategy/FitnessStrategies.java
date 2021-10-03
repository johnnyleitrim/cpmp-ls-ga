package com.johnnyleitrim.cpmp.strategy;

import com.johnnyleitrim.cpmp.fitness.BFLowerBoundFitness;
import com.johnnyleitrim.cpmp.fitness.NewBFLowerBoundFitness;
import com.johnnyleitrim.cpmp.state.State;

public class FitnessStrategies {

  public static final FitnessStrategy ORIGINAL = new Strategy("Original") {
    private final BFLowerBoundFitness fitness = new BFLowerBoundFitness();

    @Override
    public int calculateFitness(State state) {
      return fitness.calculateFitness(state);
    }
  };

  public static final FitnessStrategy NEW = new Strategy("New") {
    private final NewBFLowerBoundFitness fitness = new NewBFLowerBoundFitness();

    @Override
    public int calculateFitness(State state) {
      return fitness.calculateFitness(state);
    }
  };

  private static abstract class Strategy extends BaseStrategy implements FitnessStrategy {
    public Strategy(String name) {
      super(name);
    }
  }
}

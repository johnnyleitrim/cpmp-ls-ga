package com.johnnyleitrim.cpmp.fitness;

import com.johnnyleitrim.cpmp.state.State;

public interface FitnessAlgorithm {
  int calculateFitness(State state);
}

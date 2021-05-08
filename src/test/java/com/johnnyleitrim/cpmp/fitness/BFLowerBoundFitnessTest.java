package com.johnnyleitrim.cpmp.fitness;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.johnnyleitrim.cpmp.state.State;

public class BFLowerBoundFitnessTest {

  private final FitnessAlgorithm fitnessAlgorithm = new BFLowerBoundFitness();

  @Test
  public void test1() {
    int nTiers = 4;
    int nStacks = 6;
    int[][] internalState = new int[nTiers][nStacks];
    internalState[3] = new int[]{1, 0, 0, 0, 0, 3};
    internalState[2] = new int[]{4, 0, 0, 0, 3, 3};
    internalState[1] = new int[]{2, 4, 3, 2, 4, 3};
    internalState[0] = new int[]{2, 3, 1, 1, 1, 1};
    State state = new State(internalState, nStacks, nTiers);
    int actualFitness = fitnessAlgorithm.calculateFitness(state);
    assertThat(actualFitness).isEqualTo(13);
  }

  @Test
  public void test2() {
    int nTiers = 6;
    int nStacks = 4;
    int[][] internalState = new int[nTiers][nStacks];
    internalState[5] = new int[]{0, 1, 0, 0};
    internalState[4] = new int[]{0, 2, 0, 0};
    internalState[3] = new int[]{0, 3, 9, 0};
    internalState[2] = new int[]{11, 4, 15, 13};
    internalState[1] = new int[]{7, 10, 8, 16};
    internalState[0] = new int[]{6, 12, 14, 5};
    State state = new State(internalState, nStacks, nTiers);
    int actualFitness = fitnessAlgorithm.calculateFitness(state);
    assertThat(actualFitness).isEqualTo(7);
  }

  @Test
  public void test3() {
    int nTiers = 6;
    int nStacks = 4;
    int[][] internalState = new int[nTiers][nStacks];
    internalState[5] = new int[]{0, 0, 0, 0};
    internalState[4] = new int[]{0, 0, 0, 0};
    internalState[3] = new int[]{5, 1, 2, 12};
    internalState[2] = new int[]{10, 11, 16, 15};
    internalState[1] = new int[]{7, 14, 3, 9};
    internalState[0] = new int[]{6, 13, 8, 4};
    State state = new State(internalState, nStacks, nTiers);
    int actualFitness = fitnessAlgorithm.calculateFitness(state);
    assertThat(actualFitness).isEqualTo(14);
  }
}

package com.johnnyleitrim.cpmp.fitness;

import com.johnnyleitrim.cpmp.state.State;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NewBFLowerBoundFitnessTest {

  private final FitnessAlgorithm fitnessAlgorithm = new NewBFLowerBoundFitness();

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


  @Test
  public void test4() {
    int nTiers = 8;
    int nStacks = 20;
    int[][] internalState = new int[nTiers][nStacks];
    internalState[7] = new int[]{0, 22, 0, 0, 0, 0, 0, 0, 0, 0, 0, 22, 0, 0, 0, 0, 25, 0, 0, 0};
    internalState[6] = new int[]{18, 31, 0, 0, 0, 0, 0, 10, 0, 0, 0, 24, 0, 0, 0, 0, 36, 0, 0, 0};
    internalState[5] = new int[]{6, 29, 0, 0, 0, 0, 0, 24, 23, 0, 32, 22, 0, 7, 0, 0, 37, 0, 2, 20};
    internalState[4] = new int[]{10, 27, 0, 26, 0, 0, 0, 13, 33, 0, 23, 23, 29, 24, 0, 0, 16, 29, 15, 1};
    internalState[3] = new int[]{19, 35, 0, 39, 0, 0, 1, 38, 4, 0, 29, 1, 6, 4, 0, 0, 16, 10, 35, 5};
    internalState[2] = new int[]{19, 27, 21, 11, 0, 26, 34, 20, 3, 0, 20, 7, 30, 34, 0, 0, 14, 14, 26, 27};
    internalState[1] = new int[]{10, 18, 33, 38, 0, 29, 28, 31, 36, 0, 31, 9, 30, 25, 0, 0, 36, 27, 13, 12};
    internalState[0] = new int[]{20, 22, 15, 12, 0, 26, 32, 11, 17, 38, 5, 3, 2, 9, 39, 17, 17, 8, 25, 37};
    State state = new State(internalState, nStacks, nTiers);
    int actualFitness = fitnessAlgorithm.calculateFitness(state);
    assertThat(actualFitness).isEqualTo(73);
  }
}

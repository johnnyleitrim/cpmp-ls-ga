package com.johnnyleitrim.cpmp.state;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MutableStateTest {

  @Test
  public void itRejectsMoveIfNoContainerInStack() {
    int nStacks = 3;
    int nTiers = 5;
    int[][] internalState = new int[nTiers][nStacks];
    internalState[2] = new int[]{0, 5, 4};
    internalState[1] = new int[]{0, 6, 9};
    internalState[0] = new int[]{0, 2, 8};

    MutableState state = new MutableState(internalState, nStacks, nTiers);

    Assertions.assertThrows(InvalidMoveException.class, () -> state.applyMove(0, 2));
  }

  @Test
  public void itUpdatesMisOverlaid() throws Exception {
    int nStacks = 3;
    int nTiers = 5;
    int[][] internalState = new int[nTiers][nStacks];
    internalState[2] = new int[]{0, 5, 4};
    internalState[1] = new int[]{0, 6, 9};
    internalState[0] = new int[]{0, 2, 8};

    MutableState state = new MutableState(internalState, nStacks, nTiers);

    state.applyMove(1, 0);
    assertThat(state.isMisOverlaid(0)).isFalse();
    assertThat(state.isMisOverlaid(1)).isTrue();
    assertThat(state.isMisOverlaid(2)).isTrue();

    state.applyMove(1, 0);
    assertThat(state.isMisOverlaid(0)).isTrue();
    assertThat(state.isMisOverlaid(1)).isFalse();
    assertThat(state.isMisOverlaid(2)).isTrue();
  }

  @Test
  public void itRejectsMoveIfStackFull() {
    int nStacks = 3;
    int nTiers = 3;
    int[][] internalState = new int[nTiers][nStacks];
    internalState[2] = new int[]{0, 5, 4};
    internalState[1] = new int[]{0, 6, 9};
    internalState[0] = new int[]{1, 2, 8};

    MutableState state = new MutableState(internalState, nStacks, nTiers);

    Assertions.assertThrows(InvalidMoveException.class, () -> state.applyMove(0, 2));
  }

  @Test
  public void itAppliesMoveCorrectly() throws InvalidMoveException {
    int nStacks = 3;
    int nTiers = 5;
    int[][] internalState = new int[nTiers][nStacks];
    internalState[2] = new int[]{1, 5, 4};
    internalState[1] = new int[]{7, 6, 9};
    internalState[0] = new int[]{3, 2, 8};

    int[][] expectedInternalState = new int[nTiers][nStacks];
    expectedInternalState[3] = new int[]{0, 0, 1};
    expectedInternalState[2] = new int[]{0, 5, 4};
    expectedInternalState[1] = new int[]{7, 6, 9};
    expectedInternalState[0] = new int[]{3, 2, 8};

    MutableState state = new MutableState(internalState, nStacks, nTiers);

    state.applyMove(0, 2);

    assertThat(state).isEqualTo(new State(expectedInternalState, nStacks, nTiers));
  }
}

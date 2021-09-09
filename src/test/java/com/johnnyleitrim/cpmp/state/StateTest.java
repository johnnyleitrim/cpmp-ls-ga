package com.johnnyleitrim.cpmp.state;

import com.johnnyleitrim.cpmp.state.State.StackState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StateTest {

  private static final int N_STACKS = 5;
  private static final int N_TIERS = 3;
  private State state;

  @BeforeEach
  public void setup() {
    int[][] internalState = new int[N_TIERS][N_STACKS];
    internalState[2] = new int[]{0, 0, 4, 0, 1};
    internalState[1] = new int[]{0, 6, 9, 2, 3};
    internalState[0] = new int[]{0, 2, 8, 3, 5};
    state = new State(internalState, N_STACKS, N_TIERS);
  }

  @Test
  public void itGetsStackStates() {
    State.StackState[] expectedStates = new StackState[]{StackState.EMPTY, StackState.PARTIAL, StackState.FULL, StackState.PARTIAL, StackState.FULL};
    assertThat(state.getStackStates()).isEqualTo(expectedStates);
  }

  @Test
  public void itGetsStackHeights() {
    assertThat(state.getHeight(0)).isEqualTo(0);
    assertThat(state.getHeight(1)).isEqualTo(2);
    assertThat(state.getHeight(2)).isEqualTo(3);
    assertThat(state.getHeight(3)).isEqualTo(2);
    assertThat(state.getHeight(4)).isEqualTo(3);
  }

  @Test
  public void itGetsGroups() {
    assertThat(state.getGroup(0, 1)).isEqualTo(0);
    assertThat(state.getGroup(1, 0)).isEqualTo(2);
    assertThat(state.getGroup(2, 1)).isEqualTo(9);
  }

  @Test
  public void itGetsTopGroup() {
    assertThat(state.getTopGroup(0)).isEqualTo(0);
    assertThat(state.getTopGroup(1)).isEqualTo(6);
    assertThat(state.getTopGroup(2)).isEqualTo(4);
    assertThat(state.getTopGroup(3)).isEqualTo(2);
    assertThat(state.getTopGroup(4)).isEqualTo(1);
  }

  @Test
  public void itReturnsMisOverlaid() {
    assertThat(state.isMisOverlaid(0)).isFalse();
    assertThat(state.isMisOverlaid(1)).isTrue();
    assertThat(state.isMisOverlaid(2)).isTrue();
    assertThat(state.isMisOverlaid(3)).isFalse();
    assertThat(state.isMisOverlaid(4)).isFalse();
  }

}

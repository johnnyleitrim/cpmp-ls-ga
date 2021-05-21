package com.johnnyleitrim.cpmp.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.johnnyleitrim.cpmp.ls.Features;
import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.state.State;

public class StackUtilsTest {

  @BeforeAll
  public static void setup() {
    Features.instance.setDontFillFromGoodStackEnabled(true);
  }

  @Test
  public void itFillsStack() {
    int nStacks = 3;
    int nTiers = 3;
    int[][] internalState = new int[nTiers][nStacks];
    internalState[2] = new int[]{0, 5, 4};
    internalState[1] = new int[]{0, 6, 9};
    internalState[0] = new int[]{0, 2, 8};

    MutableState state = new MutableState(internalState, nStacks, nTiers);
    StackUtils.fillStack(state, 0);

    assertThat(state.getGroup(0, 0)).isEqualTo(5);
    assertThat(state.getGroup(0, 1)).isEqualTo(4);
    assertThat(state.getGroup(0, 2)).isEqualTo(0);
  }

  @Test
  public void itFillsNonEmptyStack() {
    int nStacks = 3;
    int nTiers = 3;
    int[][] internalState = new int[nTiers][nStacks];
    internalState[2] = new int[]{0, 5, 4};
    internalState[1] = new int[]{0, 6, 9};
    internalState[0] = new int[]{4, 2, 8};

    MutableState state = new MutableState(internalState, nStacks, nTiers);
    StackUtils.fillStack(state, 0);

    assertThat(state.getGroup(0, 0)).isEqualTo(4);
    assertThat(state.getGroup(0, 1)).isEqualTo(4);
    assertThat(state.getGroup(0, 2)).isEqualTo(0);
  }

  @Test
  public void itFillsStackCompletely() {
    int nStacks = 3;
    int nTiers = 3;
    int[][] internalState = new int[nTiers][nStacks];
    internalState[2] = new int[]{0, 9, 6};
    internalState[1] = new int[]{0, 8, 5};
    internalState[0] = new int[]{0, 7, 4};

    MutableState state = new MutableState(internalState, nStacks, nTiers);
    StackUtils.fillStack(state, 0);

    assertThat(state.getGroup(0, 0)).isEqualTo(9);
    assertThat(state.getGroup(0, 1)).isEqualTo(8);
    assertThat(state.getGroup(0, 2)).isEqualTo(6);
  }

  @Test
  public void itFillsWideStack() {
    int nStacks = 5;
    int nTiers = 3;
    int[][] internalState = new int[nTiers][nStacks];
    internalState[2] = new int[]{0, 0, 0, 0, 3};
    internalState[1] = new int[]{0, 0, 0, 0, 2};
    internalState[0] = new int[]{0, 0, 0, 0, 1};

    MutableState state = new MutableState(internalState, nStacks, nTiers);
    StackUtils.fillStack(state, 0);

    assertThat(state.getGroup(0, 0)).isEqualTo(3);
    assertThat(state.getGroup(0, 1)).isEqualTo(2);
    assertThat(state.getGroup(4, 0)).isEqualTo(1);
  }

  @Test
  public void itGetsLowestStack() {
    int nStacks = 5;
    int nTiers = 3;
    int[][] internalState = new int[nTiers][nStacks];
    internalState[2] = new int[]{0, 0, 0, 0, 1};
    internalState[1] = new int[]{1, 0, 1, 0, 1};
    internalState[0] = new int[]{1, 1, 1, 1, 1};

    State state = new State(internalState, nStacks, nTiers);
    List<Integer> lowestStacks = StackUtils.getLowestStacks(state);

    assertThat(lowestStacks).containsExactly(1, 3);
  }

  @Test
  public void itGetsLowestStackWithFilter() {
    int nStacks = 5;
    int nTiers = 3;
    int[][] internalState = new int[nTiers][nStacks];
    internalState[2] = new int[]{0, 0, 0, 0, 1};
    internalState[1] = new int[]{1, 0, 1, 0, 1};
    internalState[0] = new int[]{1, 2, 1, 2, 1};

    State state = new State(internalState, nStacks, nTiers);
    List<Integer> lowestStacks = StackUtils.getLowestStacks(state, stack -> state.getTopGroup(stack) == 1);

    assertThat(lowestStacks).containsExactly(0, 2);
  }

  @Test
  public void itMapsStacksWithSameHeight() {
    int nStacks = 5;
    int nTiers = 3;

    int[][] stateA = new int[nTiers][nStacks];
    stateA[2] = new int[]{0, 0, 0, 0, 1};
    stateA[1] = new int[]{1, 0, 1, 0, 1};
    stateA[0] = new int[]{1, 1, 1, 1, 1};

    int[][] stateB = new int[nTiers][nStacks];
    stateB[2] = new int[]{0, 0, 0, 1, 0};
    stateB[1] = new int[]{0, 1, 1, 1, 0};
    stateB[0] = new int[]{1, 1, 1, 1, 1};

    StackUtils.StackMapping stackMapping = new StackUtils.StackMapping(nStacks);

    boolean actual = StackUtils.mapStacksSameHeight(new State(stateA, nStacks, nTiers), new State(stateB, nStacks, nTiers), stackMapping);

    assertThat(actual).isTrue();
    assertThat(stackMapping.getAToB()).containsExactly(1, 0, 2, 4, 3);
    assertThat(stackMapping.getBToA()).containsExactly(1, 0, 2, 4, 3);
  }

  @Test
  public void itDoesntMapsStacksWithDifferentHeight() {
    int nStacks = 5;
    int nTiers = 3;

    int[][] stateA = new int[nTiers][nStacks];
    stateA[2] = new int[]{0, 0, 0, 0, 0};
    stateA[1] = new int[]{1, 0, 1, 0, 1};
    stateA[0] = new int[]{1, 1, 1, 1, 1};

    int[][] stateB = new int[nTiers][nStacks];
    stateB[2] = new int[]{1, 1, 1, 1, 1};
    stateB[1] = new int[]{1, 1, 1, 1, 1};
    stateB[0] = new int[]{1, 1, 1, 1, 1};

    StackUtils.StackMapping stackMapping = new StackUtils.StackMapping(nStacks);

    boolean actual = StackUtils.mapStacksSameHeight(new State(stateA, nStacks, nTiers), new State(stateB, nStacks, nTiers), stackMapping);

    assertThat(actual).isFalse();
  }
}

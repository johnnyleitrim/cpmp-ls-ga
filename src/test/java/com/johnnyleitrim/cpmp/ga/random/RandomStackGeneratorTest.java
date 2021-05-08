package com.johnnyleitrim.cpmp.ga.random;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import org.junit.jupiter.api.Test;

import com.johnnyleitrim.cpmp.random.RandomStackGenerator;
import com.johnnyleitrim.cpmp.state.State;

public class RandomStackGeneratorTest {

  @Test
  public void itWorks() {
    State.StackState[] states = new State.StackState[]{
        State.StackState.EMPTY,
        State.StackState.FULL,
        State.StackState.PARTIAL,
        State.StackState.PARTIAL,
        State.StackState.FULL,
        State.StackState.EMPTY,
        State.StackState.EMPTY,
        State.StackState.FULL,
        State.StackState.PARTIAL,
        State.StackState.FULL,
    };

    RandomStackGenerator generatorUnderTest = new RandomStackGenerator(states, OptionalInt.empty());

    {
      List<Integer> actualNonEmptyStacks = new ArrayList<>(states.length);
      for (int i = 0; i < 7; i++) {
        actualNonEmptyStacks.add(generatorUnderTest.getNextNonEmptyStack());
      }
      assertThat(actualNonEmptyStacks).containsExactlyInAnyOrder(1, 2, 3, 4, 7, 8, 9);
      assertThat(generatorUnderTest.getNextNonEmptyStack()).isEqualTo(-1);
    }

    {
      List<Integer> actualNonFullStacks = new ArrayList<>(states.length);
      for (int i = 0; i < 3; i++) {
        actualNonFullStacks.add(generatorUnderTest.getNextNonFullStack());
      }
      assertThat(actualNonFullStacks).containsExactlyInAnyOrder(0, 5, 6);
      assertThat(generatorUnderTest.getNextNonFullStack()).isEqualTo(-1);
    }
  }

  @Test
  public void itWorksWithExclusion() {
    State.StackState[] states = new State.StackState[]{
        State.StackState.EMPTY,
        State.StackState.FULL,
        State.StackState.PARTIAL,
        State.StackState.PARTIAL,
        State.StackState.FULL,
        State.StackState.EMPTY,
        State.StackState.EMPTY,
        State.StackState.FULL,
        State.StackState.PARTIAL,
        State.StackState.FULL,
    };

    RandomStackGenerator generatorUnderTest = new RandomStackGenerator(states, OptionalInt.of(3));

    {
      List<Integer> actualNonEmptyStacks = new ArrayList<>(states.length);
      for (int i = 0; i < 6; i++) {
        actualNonEmptyStacks.add(generatorUnderTest.getNextNonEmptyStack());
      }
      assertThat(actualNonEmptyStacks).containsExactlyInAnyOrder(1, 2, 4, 7, 8, 9);
      assertThat(generatorUnderTest.getNextNonEmptyStack()).isEqualTo(-1);
    }

    {
      List<Integer> actualNonFullStacks = new ArrayList<>(states.length);
      for (int i = 0; i < 3; i++) {
        actualNonFullStacks.add(generatorUnderTest.getNextNonFullStack());
      }
      assertThat(actualNonFullStacks).containsExactlyInAnyOrder(0, 5, 6);
      assertThat(generatorUnderTest.getNextNonFullStack()).isEqualTo(-1);
    }
  }

}

package com.johnnyleitrim.cpmp.random;

import java.util.OptionalInt;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.state.State;

public class RandomStackGenerator {

  private final int[] randomStacks;

  private final State.StackState[] states;

  public RandomStackGenerator(State.StackState[] states, OptionalInt excludedStack) {
    this.states = states;

    randomStacks = new int[states.length - (excludedStack.isPresent() ? 1 : 0)];
    for (int stack = 0, i = 0; stack < states.length; stack++) {
      if (excludedStack.isEmpty() || stack != excludedStack.getAsInt()) {
        randomStacks[i] = stack;
        i++;
      }
    }
    for (int i = randomStacks.length - 1; i > 0; i--) {
      int index = Problem.getRandom().nextInt(i + 1);
      // Simple swap
      int a = randomStacks[index];
      randomStacks[index] = randomStacks[i];
      randomStacks[i] = a;
    }
  }

  public int getNextNonEmptyStack() {
    return getRandomStack(State.StackState.EMPTY);
  }

  public int getNextNonFullStack() {
    return getRandomStack(State.StackState.FULL);
  }

  private int getRandomStack(State.StackState excludedStack) {
    for (int i = 0; i < randomStacks.length; i++) {
      int stack = randomStacks[i];
      if (stack != -1 && states[stack] != excludedStack) {
        randomStacks[i] = -1;
        return stack;
      }
    }
    return -1;
  }
}

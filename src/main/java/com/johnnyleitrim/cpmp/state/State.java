package com.johnnyleitrim.cpmp.state;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import com.johnnyleitrim.cpmp.Problem;

public class State {

  protected final int[] state;

  protected final int nStacks;

  protected final int nTiers;

  protected final int[] stackHeights;

  private final SortedSet<Integer> groups;

  private final String groupFormat;

  private final char[] emptyBay;

  public enum StackState {
    EMPTY, PARTIAL, FULL,
  }

  public State(int[][] state, int nStacks, int nTiers) {
    this(flattenState(state), nStacks, nTiers);
  }

  public State(int[] state, int nStacks, int nTiers) {
    this.state = state;
    this.nStacks = nStacks;
    this.nTiers = nTiers;

    stackHeights = new int[nStacks];
    calculateStackHeights();

    groups = new TreeSet<>(Comparator.reverseOrder());

    int longestGroup = 0;
    for (int group : state) {
      if (group != Problem.EMPTY) {
        groups.add(group);
        longestGroup = Math.max(longestGroup, (int) (Math.log10(group) + 1));
      }
    }
    groupFormat = "%" + longestGroup + "d";
    emptyBay = new char[longestGroup];
    Arrays.fill(emptyBay, ' ');
  }

  public int getNumberOfStacks() {
    return nStacks;
  }

  public int getNumberOfTiers() {
    return nTiers;
  }

  public SortedSet<Integer> getGroups() {
    return groups;
  }

  public int getGroup(int stack, int tier) {
    return state[getIndex(stack, tier)];
  }

  public int getTopGroup(int stack) {
    int stackHeight = stackHeights[stack];
    if (stackHeight == 0) {
      return Problem.EMPTY;
    }
    return state[getIndex(stack, stackHeights[stack] - 1)];
  }

  public boolean isMisOverlaid(int stack) {
    for (int tier = 0; tier < nTiers - 1; tier++) {
      int group = getGroup(stack, tier);
      if (group == Problem.EMPTY) {
        return false;
      }
      if (group < getGroup(stack, tier + 1)) {
        return true;
      }
    }
    return false;
  }

  public int getHeight(int stack) {
    return stackHeights[stack];
  }

  public StackState[] getStackStates() {
    StackState[] states = new StackState[nStacks];
    for (int stack = 0; stack < nStacks; stack++) {
      int height = getHeight(stack);
      if (height == 0) {
        states[stack] = StackState.EMPTY;
      } else if (height == nTiers) {
        states[stack] = StackState.FULL;
      } else {
        states[stack] = StackState.PARTIAL;
      }
    }
    return states;
  }

  public MutableState copy() {
    int[] newState = new int[state.length];
    System.arraycopy(state, 0, newState, 0, state.length);
    return new MutableState(newState, nStacks, nTiers);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof State) {
      State other = (State) o;
      return nStacks == other.nStacks && nTiers == other.nTiers && Arrays.equals(state, other.state);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(nStacks, nTiers);
    result = 31 * result + Arrays.hashCode(state);
    return result;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    for (int tier = nTiers - 1; tier >= 0; tier--) {
      str.append("|");
      for (int stack = 0; stack < nStacks; stack++) {
        int group = getGroup(stack, tier);
        str.append(" ");
        if (group == Problem.EMPTY) {
          str.append(emptyBay);
        } else {
          str.append(String.format(groupFormat, group));
        }
        str.append(" |");
      }
      str.append("\n");
    }
    return str.toString();
  }

  protected int getIndex(int stack, int tier) {
    return (tier * nStacks) + stack;
  }

  private void calculateStackHeights() {
    for (int stack = 0; stack < nStacks; stack++) {
      int height = 0;
      for (int tier = nTiers; tier > 0; tier--) {
        if (getGroup(stack, tier - 1) != Problem.EMPTY) {
          height = tier;
          break;
        }
      }
      stackHeights[stack] = height;
    }
  }


  private static final int[] flattenState(int[][] state) {
    int nTiers = state.length;
    int nStacks = state[0].length;
    int[] flatState = new int[nStacks * nTiers];
    for (int tier = 0; tier < nTiers; tier++) {
      System.arraycopy(state[tier], 0, flatState, tier * nStacks, nStacks);
    }
    return flatState;
  }
}

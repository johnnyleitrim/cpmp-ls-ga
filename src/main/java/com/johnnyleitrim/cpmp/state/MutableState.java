package com.johnnyleitrim.cpmp.state;

import com.johnnyleitrim.cpmp.Problem;

public class MutableState extends State {
  public MutableState(int[][] state, int nStacks, int nTiers) {
    super(state, nStacks, nTiers);
  }

  public MutableState(int[] state, int nStacks, int nTiers) {
    super(state, nStacks, nTiers);
  }

  public void applyMove(int srcStack, int dstStack) throws InvalidMoveException {
    int srcStackHeight = getHeight(srcStack);
    int dstStackHeight = getHeight(dstStack);
    if (srcStackHeight == 0 || dstStackHeight == nTiers) {
      throw new InvalidMoveException(srcStack, dstStack);
    }

    // Update the stack heights
    stackHeights[srcStack] -= 1;
    stackHeights[dstStack] += 1;

    // Perform move
    state[getIndex(dstStack, dstStackHeight)] = state[getIndex(srcStack, srcStackHeight - 1)];
    state[getIndex(srcStack, srcStackHeight - 1)] = Problem.EMPTY;

    // Update the stack misOverlaid states
    calculateMisOverlaidStack(srcStack);
    if (dstStackHeight > 0 && getGroup(dstStack, dstStackHeight) > getGroup(dstStack, dstStackHeight - 1)) {
      stackMisOverlaid[dstStack] = true;
    }
  }
}

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
    stackHeights[srcStack] -= 1;
    stackHeights[dstStack] += 1;
    state[getIndex(dstStack, dstStackHeight)] = state[getIndex(srcStack, srcStackHeight - 1)];
    state[getIndex(srcStack, srcStackHeight - 1)] = Problem.EMPTY;
  }
}

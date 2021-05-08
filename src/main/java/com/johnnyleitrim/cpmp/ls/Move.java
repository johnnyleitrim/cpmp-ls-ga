package com.johnnyleitrim.cpmp.ls;

public class Move {
  private final int srcStack;
  private final int dstStack;

  public Move(int srcStack, int dstStack) {
    this.srcStack = srcStack;
    this.dstStack = dstStack;
  }

  public int getSrcStack() {
    return srcStack;
  }

  public int getDstStack() {
    return dstStack;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append(srcStack);
    str.append("->");
    str.append(dstStack);
    return str.toString();
  }
}

package com.johnnyleitrim.cpmp.state;

public class InvalidMoveException extends Exception {
  public InvalidMoveException(int srcStack, int dstStack) {
    super("Invalid move: Either stack " + srcStack + " is empty or stack " + dstStack + " is full");
  }
}

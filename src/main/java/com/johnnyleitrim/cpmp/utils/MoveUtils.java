package com.johnnyleitrim.cpmp.utils;

import com.johnnyleitrim.cpmp.ls.Move;
import com.johnnyleitrim.cpmp.state.InvalidMoveException;
import com.johnnyleitrim.cpmp.state.MutableState;

public class MoveUtils {

  public static MutableState applyMove(MutableState state, Iterable<Move> moves) {
    try {
      for (Move move : moves) {
        state.applyMove(move.getSrcStack(), move.getDstStack());
      }
    } catch (InvalidMoveException e) {
      throw new RuntimeException(e);
    }
    return state;
  }

  public static MutableState applyMove(MutableState state, Move... moves) {
    try {
      for (Move move : moves) {
        state.applyMove(move.getSrcStack(), move.getDstStack());
      }
    } catch (InvalidMoveException e) {
      throw new RuntimeException(e);
    }
    return state;
  }

  public static Move applyMove(MutableState state, int srcStack, int dstStack) {
    Move move = new Move(srcStack, dstStack);
    applyMove(state, move);
    return move;
  }
}

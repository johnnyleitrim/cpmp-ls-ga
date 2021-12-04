package com.johnnyleitrim.cpmp.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import com.johnnyleitrim.cpmp.ls.Move;
import com.johnnyleitrim.cpmp.state.InvalidMoveException;
import com.johnnyleitrim.cpmp.state.MutableState;

public class MoveUtils {

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

  public static List<List<Move>> findTransientMoves(List<Move> moves, int nStacks) {
    List<List<Move>> transientMoves = new LinkedList<>();
    MoveInfo[] moveTurns = new MoveInfo[nStacks];
    Arrays.fill(moveTurns, new MoveInfo(-1, false));
    for (int i = 0; i < moves.size(); i++) {
      Move move = moves.get(i);
      int src = move.getSrcStack();
      int dst = move.getDstStack();

      MoveInfo previousMoveTurn = moveTurns[src];
      if (previousMoveTurn.turn > -1) {
        int previousSrc = -1;
        for (int s = 0; s < nStacks; s++) {
          if (s != src && moveTurns[s].turn == previousMoveTurn.turn && !previousMoveTurn.src) {
            previousSrc = s;
          }
        }

        if (previousSrc > -1 && moveTurns[dst].turn < previousMoveTurn.turn) {
          transientMoves.add(Arrays.asList(new Move(previousSrc, src), new Move(src, dst)));
        }
      }

      moveTurns[src] = new MoveInfo(i, true);
      moveTurns[dst] = new MoveInfo(i, false);
    }
    return transientMoves;
  }

  public static List<Move> removeTransientMoves(List<Move> moves, int nStacks) {
    List<Move> newMoves = new ArrayList<>(moves.size());
    MoveInfo[] moveTurns = new MoveInfo[nStacks + 1];
    Arrays.fill(moveTurns, new MoveInfo(-1, false));
    for (Move move : moves) {
      int src = move.getSrcStack();
      int dst = move.getDstStack();

      MoveInfo previousMoveTurn = moveTurns[src];
      boolean keepMove = true;
      if (previousMoveTurn.turn > -1) {
        int previousSrc = -1;
        for (int s = 0; s < nStacks; s++) {
          if (s != src && moveTurns[s].turn == previousMoveTurn.turn && !previousMoveTurn.src) {
            previousSrc = s;
            break;
          }
        }

        if (previousSrc > -1 && moveTurns[dst].turn < previousMoveTurn.turn) {
          newMoves.set(previousMoveTurn.turn, new Move(previousSrc, dst));
          src = previousSrc;
          keepMove = false;
        }
      }

      int moveTurn = previousMoveTurn.turn;
      if (keepMove) {
        moveTurn = newMoves.size();
        newMoves.add(move);
      }
      moveTurns[src] = new MoveInfo(moveTurn, true);
      moveTurns[dst] = new MoveInfo(moveTurn, false);
    }
    return newMoves;
  }

  public static List<Move> parseMoves(String movesString) {
    List<Move> moves = new LinkedList<>();
    StringTokenizer stringTokenizer = new StringTokenizer(movesString.substring(1, movesString.length() - 1), ",");
    while (stringTokenizer.hasMoreTokens()) {
      String moveString = stringTokenizer.nextToken().trim();
      int arrowIndex = moveString.indexOf("->");
      int src = Integer.parseInt(moveString.substring(0, arrowIndex));
      int dst = Integer.parseInt(moveString.substring(arrowIndex + 2));
      moves.add(new Move(src, dst));
    }
    return moves;
  }

  private static class MoveInfo {
    private final int turn;
    private final boolean src;

    private MoveInfo(int turn, boolean src) {
      this.turn = turn;
      this.src = src;
    }

    @Override
    public String toString() {
      StringBuilder str = new StringBuilder();
      str.append(turn);
      str.append(":");
      str.append(src);
      return str.toString();
    }
  }
}

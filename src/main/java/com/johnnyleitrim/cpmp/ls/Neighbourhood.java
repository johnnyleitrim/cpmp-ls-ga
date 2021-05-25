package com.johnnyleitrim.cpmp.ls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.fitness.FitnessAlgorithm;
import com.johnnyleitrim.cpmp.state.InvalidMoveException;
import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.state.State;

public class Neighbourhood implements Iterable<Neighbour>, Iterator<Neighbour> {

  private final List<Queue<Move>> moveLevels;
  private final FitnessAlgorithm fitnessAlgorithm;
  private final int nMoves;
  private final State initialState;
  private MutableState currentState;

  public Neighbourhood(FitnessAlgorithm fitnessAlgorithm, State initialState, int nMoves) {
    this.fitnessAlgorithm = fitnessAlgorithm;
    this.nMoves = nMoves;
    this.initialState = initialState;

    moveLevels = new ArrayList<>(nMoves);
    moveLevels.add(generateLevel(initialState, -1));
    for (int level = 1; level < nMoves; level++) {
      moveLevels.add(new LinkedList<>());
    }

    generate();
  }

  private static Queue<Move> generateLevel(State state, int previousDstStack) {
    int nStacks = state.getNumberOfStacks();
    State.StackState[] stackStates = state.getStackStates();
    Queue<Move> moves = new LinkedList<>();

    for (int srcStack = 0; srcStack < nStacks; srcStack++) {
      for (int dstStack = 0; dstStack < nStacks; dstStack++) {
        if (srcStack != dstStack && srcStack != previousDstStack && stackStates[srcStack] != State.StackState.EMPTY && stackStates[dstStack] != State.StackState.FULL) {
          moves.add(new Move(srcStack, dstStack));
        }
      }
    }

    return moves;
  }

  @Override
  public Iterator<Neighbour> iterator() {
    return this;
  }

  @Override
  public boolean hasNext() {
    return !moveLevels.get(0).isEmpty();
  }

  @Override
  public Neighbour next() {
    Move[] moves = new Move[nMoves];
    Neighbour.Containers[] movedContainers = new Neighbour.Containers[nMoves];
    int lastLevel = nMoves - 1;
    for (int level = 0; level < lastLevel; level++) {
      moves[level] = moveLevels.get(level).peek();
      int dstStack = moves[level].getDstStack();
      int dstStackHeight = currentState.getHeight(dstStack);
      int movedContainer = currentState.getTopGroup(dstStack);
      int overlaidContainer = Problem.EMPTY;
      if (dstStackHeight > 1) {
        overlaidContainer = currentState.getGroup(dstStack, dstStackHeight - 1);
      }
      movedContainers[level] = new Neighbour.Containers(movedContainer, overlaidContainer);
    }
    Move lastMove = moveLevels.get(lastLevel).remove();
    moves[lastLevel] = lastMove;
    int movedContainer = currentState.getTopGroup(lastMove.getSrcStack());
    int overlaidContainer = currentState.getTopGroup(lastMove.getDstStack());
    movedContainers[lastLevel] = new Neighbour.Containers(movedContainer, overlaidContainer);
    applyMove(lastMove);
    int fitness = fitnessAlgorithm.calculateFitness(currentState);
    undoMove(lastMove);
    checkAndGenerate();
    return new Neighbour(fitness, movedContainers, moves);
  }

  private void checkAndGenerate() {
    boolean generate = false;
    for (int level = nMoves - 1; level > 0; level--) {
      if (moveLevels.get(level).isEmpty()) {
        moveLevels.get(level - 1).poll();
        generate = true;
      }
    }

    if (generate && hasNext()) {
      generate();
    }
  }

  private void generate() {
    currentState = initialState.copy();
    for (int level = 1; level < nMoves; level++) {
      Move move = moveLevels.get(level - 1).peek();
      applyMove(move);
      if (moveLevels.get(level).isEmpty()) {
        moveLevels.set(level, generateLevel(currentState, move.getDstStack()));
      }
    }
  }

  private void applyMove(Move move) {
    try {
      currentState.applyMove(move.getSrcStack(), move.getDstStack());
    } catch (InvalidMoveException e) {
      throw new RuntimeException(e);
    }
  }

  private void undoMove(Move move) {
    applyMove(new Move(move.getDstStack(), move.getSrcStack()));
  }
}

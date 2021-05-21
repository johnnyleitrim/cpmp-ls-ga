package com.johnnyleitrim.cpmp.ls;

public class Neighbour {
  private final int cost;
  private final int lastMovedContainer;
  private final Move[] moves;

  public Neighbour(int cost, int lastMovedContainer, Move[] moves) {
    this.lastMovedContainer = lastMovedContainer;
    this.moves = moves;
    this.cost = cost;
  }

  public int getCost() {
    return cost;
  }

  public int getLastMovedContainer() {
    return lastMovedContainer;
  }

  public Move[] getMoves() {
    return moves;
  }

}

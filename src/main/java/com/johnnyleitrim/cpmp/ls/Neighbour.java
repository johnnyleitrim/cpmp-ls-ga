package com.johnnyleitrim.cpmp.ls;

public class Neighbour {
  private final Move[] moves;
  private final int cost;

  public Neighbour(int cost, Move... moves) {
    this.moves = moves;
    this.cost = cost;
  }

  public Move[] getMoves() {
    return moves;
  }

  public int getCost() {
    return cost;
  }
}

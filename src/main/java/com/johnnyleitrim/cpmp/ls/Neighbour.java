package com.johnnyleitrim.cpmp.ls;

public class Neighbour {
  private final int cost;
  private final int[] movedContainer;
  private final Move[] moves;
  private final int sumContainerGroups;

  public Neighbour(int cost, int[] movedContainer, Move[] moves) {
    this.movedContainer = movedContainer;
    this.moves = moves;
    this.cost = cost;
    sumContainerGroups = movedContainer[movedContainer.length - 1]; // IntStream.of(movedContainer).sum();
  }

  public int getCost() {
    return cost;
  }

  public int[] getMovedContainer() {
    return movedContainer;
  }

  public int getSumContainerGroups() {
    return sumContainerGroups;
  }

  public Move[] getMoves() {
    return moves;
  }

}

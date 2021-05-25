package com.johnnyleitrim.cpmp.ls;

public class Neighbour {
  private final int cost;
  private final Containers[] movedContainers;
  private final Move[] moves;

  public Neighbour(int cost, Containers[] movedContainers, Move[] moves) {
    this.movedContainers = movedContainers;
    this.moves = moves;
    this.cost = cost;
  }

  public int getCost() {
    return cost;
  }

  public Move[] getMoves() {
    return moves;
  }

  public Containers[] getMovedContainers() {
    return movedContainers;
  }

  public static class Containers {
    private final int moved;
    private final int overlaid;

    public Containers(int moved, int overlaid) {
      this.moved = moved;
      this.overlaid = overlaid;
    }

    public int getMoved() {
      return moved;
    }

    public int getOverlaid() {
      return overlaid;
    }
  }
}

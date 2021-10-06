package com.johnnyleitrim.cpmp.strategy;

import java.util.List;

import com.johnnyleitrim.cpmp.Random;
import com.johnnyleitrim.cpmp.ls.Neighbour;
import com.johnnyleitrim.cpmp.utils.LargestValueItems;

public class BestNeighbourTieBreakingStrategies {

  public static final BestNeighbourTieBreakingStrategy RANDOM = new Strategy("Random") {

    @Override
    public Neighbour getBestNeighbour(List<Neighbour> neighbours) {
      return Random.getRandomItem(neighbours);
    }
  };

  public static final BestNeighbourTieBreakingStrategy HIGHEST_LAST_CONTAINER = new Strategy("Highest Last Container") {

    @Override
    public Neighbour getBestNeighbour(List<Neighbour> neighbours) {
      LargestValueItems<Neighbour> bestNeighbours = new LargestValueItems<>(neighbours.size());
      for (Neighbour neighbour : neighbours) {
        bestNeighbours.add(getLastMovedContainer(neighbour), neighbour);
      }
      return Random.getRandomItem(bestNeighbours.getItems());
    }

    private int getLastMovedContainer(Neighbour neighbour) {
      Neighbour.Containers[] movedContainers = neighbour.getMovedContainers();
      return movedContainers[movedContainers.length - 1].getMoved();
    }
  };

  public static final BestNeighbourTieBreakingStrategy SMALLEST_CONTAINER_DIFFERENCE = new Strategy("Smallest Container Difference") {

    @Override
    public Neighbour getBestNeighbour(List<Neighbour> neighbours) {
      LargestValueItems<Neighbour> bestNeighbours = new LargestValueItems<>(neighbours.size());
      for (Neighbour neighbour : neighbours) {
        bestNeighbours.add(-getContainerDifference(neighbour), neighbour);
      }
      return Random.getRandomItem(bestNeighbours.getItems());
    }

    private int getContainerDifference(Neighbour neighbour) {
      int diff = 0;
      for (Neighbour.Containers movedContainers : neighbour.getMovedContainers()) {
        diff += movedContainers.getOverlaid() - movedContainers.getMoved();
      }
      return diff;
    }
  };

  public static final List<BestNeighbourTieBreakingStrategy> ALL = List.of(RANDOM, HIGHEST_LAST_CONTAINER, SMALLEST_CONTAINER_DIFFERENCE);

  private static abstract class Strategy extends BaseStrategy implements BestNeighbourTieBreakingStrategy {
    public Strategy(String name) {
      super(name);
    }
  }

}

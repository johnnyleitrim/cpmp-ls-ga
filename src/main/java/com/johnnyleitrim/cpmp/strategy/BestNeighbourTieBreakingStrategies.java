package com.johnnyleitrim.cpmp.strategy;

import com.johnnyleitrim.cpmp.Random;
import com.johnnyleitrim.cpmp.ls.Neighbour;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BestNeighbourTieBreakingStrategies {

  public static final BestNeighbourTieBreakingStrategy RANDOM = new Strategy("Random") {

    @Override
    public Neighbour getBestNeighbour(List<Neighbour> neighbours) {
      return Random.getRandomItem(neighbours);
    }
  };

  public static final BestNeighbourTieBreakingStrategy HIGHEST_CONTAINER = new Strategy("Highest Container") {

    @Override
    public Neighbour getBestNeighbour(List<Neighbour> neighbours) {
      neighbours.sort(Comparator.comparingInt(Neighbour::getSumContainerGroups).reversed());
      List<Neighbour> bestNeighbours = new ArrayList<>(neighbours.size());
      int highestContainer = neighbours.get(0).getSumContainerGroups();
      for (Neighbour neighbour : neighbours) {
        if (neighbour.getSumContainerGroups() == highestContainer) {
          bestNeighbours.add(neighbour);
        }
      }
      return Random.getRandomItem(bestNeighbours);
    }
  };

  private static abstract class Strategy extends BaseStrategy implements BestNeighbourTieBreakingStrategy {
    public Strategy(String name) {
      super(name);
    }
  }
}

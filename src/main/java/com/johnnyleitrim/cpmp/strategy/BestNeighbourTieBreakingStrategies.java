package com.johnnyleitrim.cpmp.strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.johnnyleitrim.cpmp.Random;
import com.johnnyleitrim.cpmp.ls.Neighbour;

public class BestNeighbourTieBreakingStrategies {

  public static final BestNeighbourTieBreakingStrategy RANDOM = Random::getRandomItem;

  public static final BestNeighbourTieBreakingStrategy HIGHEST_CONTAINER = neighbours -> {
    neighbours.sort(Comparator.comparingInt(Neighbour::getSumContainerGroups).reversed());
    List<Neighbour> bestNeighbours = new ArrayList<>(neighbours.size());
    int highestContainer = neighbours.get(0).getSumContainerGroups();
    for (Neighbour neighbour : neighbours) {
      if (neighbour.getSumContainerGroups() == highestContainer) {
        bestNeighbours.add(neighbour);
      }
    }
    return Random.getRandomItem(bestNeighbours);
  };
}

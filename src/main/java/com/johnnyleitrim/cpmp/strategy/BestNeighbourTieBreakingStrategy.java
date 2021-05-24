package com.johnnyleitrim.cpmp.strategy;

import com.johnnyleitrim.cpmp.ls.Neighbour;
import java.util.List;

public interface BestNeighbourTieBreakingStrategy extends Strategy {
  Neighbour getBestNeighbour(List<Neighbour> neighbours);
}

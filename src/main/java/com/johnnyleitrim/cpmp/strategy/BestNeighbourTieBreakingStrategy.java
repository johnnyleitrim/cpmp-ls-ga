package com.johnnyleitrim.cpmp.strategy;

import java.util.List;

import com.johnnyleitrim.cpmp.ls.Neighbour;

public interface BestNeighbourTieBreakingStrategy extends Strategy {
  Neighbour getBestNeighbour(List<Neighbour> neighbours);
}

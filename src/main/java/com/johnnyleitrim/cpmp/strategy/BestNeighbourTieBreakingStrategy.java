package com.johnnyleitrim.cpmp.strategy;

import java.util.List;
import java.util.function.Function;

import com.johnnyleitrim.cpmp.ls.Neighbour;

public interface BestNeighbourTieBreakingStrategy extends Function<List<Neighbour>, Neighbour> {
}

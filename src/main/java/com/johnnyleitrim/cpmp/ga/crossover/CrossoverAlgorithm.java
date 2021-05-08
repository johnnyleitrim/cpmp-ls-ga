package com.johnnyleitrim.cpmp.ga.crossover;

import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.state.State;

public interface CrossoverAlgorithm {
  Chromosome[] crossover(Chromosome parentA, Chromosome parentB, int nGenes, State initialState);
}

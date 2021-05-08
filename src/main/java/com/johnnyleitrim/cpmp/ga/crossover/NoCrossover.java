package com.johnnyleitrim.cpmp.ga.crossover;

import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.state.State;

public class NoCrossover implements CrossoverAlgorithm {

  @Override
  public Chromosome[] crossover(Chromosome parentA, Chromosome parentB, int nGenes, State initialState) {
    return new Chromosome[]{parentA, parentB};
  }
}

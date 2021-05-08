package com.johnnyleitrim.cpmp.ga.generator;

import com.johnnyleitrim.cpmp.ga.Gene;
import com.johnnyleitrim.cpmp.state.State;

public interface GeneGenerator {
  Gene generateGene(State state);

  Gene[] generateGenes(State initialState, int nGenes);
}

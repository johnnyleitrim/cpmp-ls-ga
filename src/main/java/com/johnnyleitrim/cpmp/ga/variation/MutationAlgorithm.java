package com.johnnyleitrim.cpmp.ga.variation;

import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.state.State;

public interface MutationAlgorithm {
  void mutate(Chromosome chromosome, int nGenes, State initialState);
}

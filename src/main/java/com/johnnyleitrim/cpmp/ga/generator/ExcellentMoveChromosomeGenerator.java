package com.johnnyleitrim.cpmp.ga.generator;

import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.ga.Gene;
import com.johnnyleitrim.cpmp.state.State;

public class ExcellentMoveChromosomeGenerator implements ChromosomeGenerator {

  private final int nGenes;

  private final State initialState;

  private final GeneGenerator geneGenerator;

  public ExcellentMoveChromosomeGenerator(int nGenes, State initialState) {
    this.nGenes = nGenes;
    this.initialState = initialState;
    geneGenerator = new ExcellentMoveGeneGenerator();
  }

  @Override
  public Chromosome generateChromosome() {
    Gene[] genes = geneGenerator.generateGenes(initialState, nGenes);
    return new Chromosome(genes);
  }
}

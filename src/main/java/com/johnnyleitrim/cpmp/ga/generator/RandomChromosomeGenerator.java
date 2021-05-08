package com.johnnyleitrim.cpmp.ga.generator;

import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.ga.Gene;
import com.johnnyleitrim.cpmp.ga.random.RandomGeneGenerator;
import com.johnnyleitrim.cpmp.state.State;

public class RandomChromosomeGenerator implements ChromosomeGenerator {

  private final int nGenes;

  private final State initialState;

  private final GeneGenerator geneGenerator = new RandomGeneGenerator();

  public RandomChromosomeGenerator(int nGenes, State initialState) {
    this.nGenes = nGenes;
    this.initialState = initialState;
  }

  @Override
  public Chromosome generateChromosome() {
    Gene[] genes = geneGenerator.generateGenes(initialState, nGenes);
    return new Chromosome(genes);
  }
}

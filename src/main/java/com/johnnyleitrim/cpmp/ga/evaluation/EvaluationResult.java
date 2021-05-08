package com.johnnyleitrim.cpmp.ga.evaluation;

import com.johnnyleitrim.cpmp.ga.Chromosome;

public class EvaluationResult {
  private final int nSolutionGenes;
  private final int fitness;
  private final Chromosome chromosome;
  private final boolean repaired;

  private EvaluationResult(int nSolutionGenes, int fitness, Chromosome chromosome, boolean repaired) {
    this.nSolutionGenes = nSolutionGenes;
    this.fitness = fitness;
    this.chromosome = chromosome;
    this.repaired = repaired;
  }

  public static EvaluationResult forSolution(int nSolutionGenes, Chromosome chromosome, boolean repaired) {
    return new EvaluationResult(nSolutionGenes, 0, chromosome, repaired);
  }

  public static EvaluationResult forFitness(int fitness, Chromosome chromosome, boolean repaired) {
    return new EvaluationResult(Integer.MAX_VALUE, fitness, chromosome, repaired);
  }

  public boolean isSolution() {
    return nSolutionGenes < Integer.MAX_VALUE;
  }

  public int getNSolutionGenes() {
    return nSolutionGenes;
  }

  public int getFitness() {
    return fitness;
  }

  public Chromosome getChromosome() {
    return chromosome;
  }

  public boolean isRepaired() {
    return repaired;
  }

  public boolean isBetter(EvaluationResult other) {
    return (fitness < other.fitness || nSolutionGenes < other.nSolutionGenes);
  }

  public EvaluationResult copy() {
    return new EvaluationResult(nSolutionGenes, fitness, copyChromosome(), repaired);
  }

  private Chromosome copyChromosome() {
    if (isSolution()) {
      return chromosome.copy(nSolutionGenes);
    }
    return chromosome.copy();
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("EvaluationResult{");
    sb.append("isSolution=").append(isSolution());
    sb.append(", nSolutionGenes=").append(nSolutionGenes);
    sb.append(", fitness=").append(fitness);
    sb.append(", repaired=").append(repaired);
    sb.append('}');
    return sb.toString();
  }
}

package com.johnnyleitrim.cpmp.ga;

import java.util.Arrays;

public class Chromosome {

  private final Gene[] genes;

  public Chromosome(Gene[] genes) {
    this.genes = genes;
  }

  public Chromosome(int nGenes) {
    this.genes = new Gene[nGenes];
  }

  public Gene getGene(int i) {
    return genes[i];
  }

  public void setGene(int i, Gene gene) {
    genes[i] = gene;
  }

  public Chromosome copy() {
    return copy(genes.length);
  }

  public Chromosome copy(int nGenes) {
    Gene[] copy = new Gene[nGenes];
    for (int i = 0; i < nGenes; i++) {
      copy[i] = genes[i].copy();
    }
    return new Chromosome(copy);
  }

  public void copyTo(int srcPos, Chromosome dest, int dstPos, int length) {
    copyTo(srcPos, dest.genes, dstPos, length);
  }

  public void copyTo(int srcPos, Gene[] dest, int dstPos, int length) {
    System.arraycopy(genes, srcPos, dest, dstPos, length);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o != null && getClass() == o.getClass()) {
      Chromosome other = (Chromosome) o;
      return Arrays.equals(genes, other.genes);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(genes);
  }

  @Override
  public String toString() {
    return Arrays.toString(genes);
  }
}

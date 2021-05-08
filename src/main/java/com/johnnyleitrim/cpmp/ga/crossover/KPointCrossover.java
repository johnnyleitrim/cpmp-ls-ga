package com.johnnyleitrim.cpmp.ga.crossover;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.state.State;

public class KPointCrossover implements CrossoverAlgorithm {

  private final int kPoints;

  public KPointCrossover() {
    this(1);
  }

  public KPointCrossover(int kPoints) {
    this.kPoints = kPoints;
  }

  @Override
  public Chromosome[] crossover(Chromosome parentA, Chromosome parentB, int nGenes, State initialState) {
    Chromosome childA = new Chromosome(nGenes);
    Chromosome childB = new Chromosome(nGenes);

    int lastPoint = 0;
    for (int kPoint = 0; kPoint < kPoints && lastPoint < nGenes; kPoint++) {
      int point = Problem.getRandom().nextInt(nGenes - lastPoint);
      point += lastPoint;
      int nextPoint = point + 1;

      // Copy the start of the children from the parents
      parentA.copyTo(lastPoint, childA, lastPoint, nextPoint - lastPoint);
      parentB.copyTo(lastPoint, childB, lastPoint, nextPoint - lastPoint);

      // Copy the rest of the children from the other parent
      parentB.copyTo(nextPoint, childA, nextPoint, nGenes - nextPoint);
      parentA.copyTo(nextPoint, childB, nextPoint, nGenes - nextPoint);

      lastPoint = nextPoint;

      // Swap the parents for the next iteration
      Chromosome tmp = parentA;
      parentA = parentB;
      parentB = tmp;
    }

    return new Chromosome[]{childA, childB};
  }
}

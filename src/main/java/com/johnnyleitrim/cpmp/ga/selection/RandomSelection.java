package com.johnnyleitrim.cpmp.ga.selection;

import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.ga.evaluation.EvaluationResult;

public class RandomSelection implements SelectionAlgorithm {

  @Override
  public Chromosome[] generateMatingPool(EvaluationResult[] evaluationResults) {
    Chromosome[] matingPool = new Chromosome[evaluationResults.length];
    for (int i = 0; i < evaluationResults.length; i++) {
      matingPool[i] = evaluationResults[i].getChromosome();
    }
    return matingPool;
  }
}

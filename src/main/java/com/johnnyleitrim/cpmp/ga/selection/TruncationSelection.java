package com.johnnyleitrim.cpmp.ga.selection;

import java.util.Arrays;
import java.util.Comparator;

import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.ga.evaluation.EvaluationResult;

public class TruncationSelection implements SelectionAlgorithm {

  private static final Comparator<EvaluationResult> RESULT_COMPARATOR = Comparator.comparing(EvaluationResult::getFitness).thenComparing(EvaluationResult::getNSolutionGenes);

  private final double topNPercentage;

  public TruncationSelection(double topNPercentage) {
    this.topNPercentage = topNPercentage;
  }

  @Override
  public Chromosome[] generateMatingPool(EvaluationResult[] evaluationResults) {
    Chromosome[] matingPool = new Chromosome[evaluationResults.length];

    Arrays.sort(evaluationResults, RESULT_COMPARATOR);

    int topIndividuals = (int) Math.ceil(evaluationResults.length * topNPercentage);
    int nPartitions = evaluationResults.length / topIndividuals;
    for (int partition = 0; partition < nPartitions; partition++) {
      for (int ind = 0; ind < topIndividuals; ind++) {
        int idx = (partition * topIndividuals) + ind;
        matingPool[idx] = evaluationResults[ind].getChromosome();
      }
    }

    int remaining = evaluationResults.length - (nPartitions * topIndividuals);
    for (int ind = 0; ind < remaining; ind++) {
      int idx = (nPartitions * topIndividuals) + ind;
      matingPool[idx] = evaluationResults[ind].getChromosome();
    }

    return matingPool;
  }
}

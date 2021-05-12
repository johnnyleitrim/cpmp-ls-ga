package com.johnnyleitrim.cpmp.ga.selection;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.ga.evaluation.EvaluationResult;

public class StochasticUniversalSamplingSelection implements SelectionAlgorithm {

  private static double adjustFitness(EvaluationResult evaluationResult, int maxFitness) {
    return maxFitness - evaluationResult.getFitness() + 1;
  }

  @Override
  public Chromosome[] generateMatingPool(EvaluationResult[] evaluationResults) {
    Chromosome[] matingPool = new Chromosome[evaluationResults.length];
    double[] adjustedFitness = new double[evaluationResults.length];

    int maxFitness = 0;
    for (EvaluationResult result : evaluationResults) {
      maxFitness = Math.max(maxFitness, result.getFitness());
    }

    double totalFitness = 0.0;
    for (int i = 0; i < evaluationResults.length; i++) {
      double newFitness = adjustFitness(evaluationResults[i], maxFitness);
      adjustedFitness[i] = newFitness;
      totalFitness += newFitness;
    }

    // Determine the distance between marks
    double distanceBetweenMarks = totalFitness / evaluationResults.length;
    // First Mark is randomly generated. All future marks will be `distanceBetweenMarks`away
    double currentMark = Problem.getRandom().nextDouble() * distanceBetweenMarks;

    double previousIndividualFitnessBoundary = 0;
    int currentParentIndex = 0;

    for (int i = 0; i < evaluationResults.length; i++) {
      Chromosome selectedParent = null;
      while (selectedParent == null) {
        // The current individual's boundary is its (inverse) fitness, plus all the
        // previously considered individual 's (inverse) fitnesses.
        double currentIndividualFitnessBoundary = previousIndividualFitnessBoundary + adjustedFitness[currentParentIndex];
        if (currentMark < currentIndividualFitnessBoundary) {
          // If the current mark is in the current individual range,
          // we select the individual and move the current mark onto the next mark
          selectedParent = evaluationResults[i].getChromosome();
          currentMark += distanceBetweenMarks;
        } else {
          // If the current mark is after the current individual's range,
          // we move on to the next individual
          currentParentIndex += 1;
          previousIndividualFitnessBoundary = currentIndividualFitnessBoundary;
        }
      }
      matingPool[i] = selectedParent;
    }
    return matingPool;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}

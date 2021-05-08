package com.johnnyleitrim.cpmp.ga.replacement;

import java.util.Arrays;
import java.util.Comparator;

import com.johnnyleitrim.cpmp.ga.evaluation.EvaluationResult;

public class EliteSurvivalReplacement implements ReplacementAlgorithm {
  private static final double ELITE_SURVIVAL_PERCENTAGE = 0.10;

  private static final Comparator<EvaluationResult> RESULT_COMPARATOR = Comparator.comparing(EvaluationResult::getFitness);

  @Override
  public EvaluationResult[] generateNewPopulation(EvaluationResult[] oldPopulation, EvaluationResult[] newPopulation) {
    Arrays.sort(oldPopulation, RESULT_COMPARATOR);
    Arrays.sort(newPopulation, RESULT_COMPARATOR);

    int topN = (int) Math.round(ELITE_SURVIVAL_PERCENTAGE * oldPopulation.length);

    for (int oldPopIndex = 0; oldPopIndex < topN; oldPopIndex++) {
      int newPopIndex = newPopulation.length - topN + oldPopIndex;
      if (oldPopulation[oldPopIndex].isBetter(newPopulation[newPopIndex])) {
        newPopulation[newPopIndex] = oldPopulation[oldPopIndex];
      }
    }

    return newPopulation;
  }
}

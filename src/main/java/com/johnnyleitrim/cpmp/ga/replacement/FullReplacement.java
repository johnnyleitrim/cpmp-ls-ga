package com.johnnyleitrim.cpmp.ga.replacement;

import com.johnnyleitrim.cpmp.ga.evaluation.EvaluationResult;

public class FullReplacement implements ReplacementAlgorithm {
  @Override
  public EvaluationResult[] generateNewPopulation(EvaluationResult[] oldPopulation, EvaluationResult[] newPopulation) {
    return newPopulation;
  }
}

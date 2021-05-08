package com.johnnyleitrim.cpmp.ga.replacement;

import com.johnnyleitrim.cpmp.ga.evaluation.EvaluationResult;

public interface ReplacementAlgorithm {
  EvaluationResult[] generateNewPopulation(EvaluationResult[] oldPopulation, EvaluationResult[] newPopulation);
}

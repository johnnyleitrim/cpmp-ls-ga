package com.johnnyleitrim.cpmp.ga.selection;

import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.ga.evaluation.EvaluationResult;

public interface SelectionAlgorithm {
  Chromosome[] generateMatingPool(EvaluationResult[] evaluationResults);
}

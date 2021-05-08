package com.johnnyleitrim.cpmp.problem;

import com.johnnyleitrim.cpmp.Problem;

public interface ProblemProvider {
  Iterable<Problem> getProblems();
}

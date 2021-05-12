package com.johnnyleitrim.cpmp.problem;

import java.util.ArrayList;
import java.util.List;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.state.State;

public class ToyProblemProvider implements ProblemProvider {

  private final int nProblems;

  public ToyProblemProvider(int nProblems) {
    this.nProblems = nProblems;
  }

  private static Problem generateHardProblem() {
    int nTiers = 6;
    int nStacks = 4;
    int[][] initialState = new int[nTiers][nStacks];
    initialState[5] = new int[]{Problem.EMPTY, Problem.EMPTY, Problem.EMPTY, Problem.EMPTY};
    initialState[4] = new int[]{Problem.EMPTY, Problem.EMPTY, Problem.EMPTY, Problem.EMPTY};
    initialState[3] = new int[]{5, 1, 2, 12};
    initialState[2] = new int[]{10, 11, 16, 15};
    initialState[1] = new int[]{7, 14, 3, 9};
    initialState[0] = new int[]{6, 13, 8, 4};
    return new Problem("Toy Problem - Hard", new State(initialState, nStacks, nTiers));
  }

  private static Problem generateMediumProblem() {
    int nStacks = 3;
    int nTiers = 5;
    int[][] initialState = new int[nTiers][nStacks];
    initialState[2] = new int[]{1, 5, 4};
    initialState[1] = new int[]{7, 6, 9};
    initialState[0] = new int[]{3, 2, 8};
    return new Problem("Toy Problem - Medium", new State(initialState, nStacks, nTiers));
  }

  @Override
  public Iterable<Problem> getProblems() {
    Problem problem = generateMediumProblem();
    List<Problem> problems = new ArrayList<>(nProblems);
    for (int i = 0; i < nProblems; i++) {
      problems.add(problem);
    }
    return problems;
  }
}

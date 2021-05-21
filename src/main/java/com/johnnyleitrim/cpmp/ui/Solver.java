package com.johnnyleitrim.cpmp.ui;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.fitness.BFLowerBoundFitness;
import com.johnnyleitrim.cpmp.ls.IterativeLocalSearch;
import com.johnnyleitrim.cpmp.ls.IterativeLocalSearch.Perturbation;
import com.johnnyleitrim.cpmp.ls.Move;
import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.state.State;
import com.johnnyleitrim.cpmp.utils.MoveUtils;

public class Solver implements Callable<List<State>> {

  private final SolverParams solverParams;

  public Solver(SolverParams solverParams) {
    this.solverParams = solverParams;
  }

  @Override
  public List<State> call() {
    State initialState = solverParams.problem.getInitialState();
    IterativeLocalSearch cpmpSolver = new IterativeLocalSearch(
        initialState,
        solverParams.minSearchMoves, solverParams.maxSearchMoves,
        new BFLowerBoundFitness(),
        Duration.ofSeconds(20));

    List<Move> moves = cpmpSolver.search(solverParams.perturbation, 1);
    MutableState state = initialState.copy();
    List<State> problemStates = new ArrayList<>(moves.size() + 1);
    problemStates.add(state.copy());
    for (Move move : moves) {
      MoveUtils.applyMove(state, move);
      problemStates.add(state.copy());
    }
    return problemStates;
  }

  public static class SolverParams {
    private Problem problem;
    private int minSearchMoves = 1;
    private int maxSearchMoves = 2;
    private IterativeLocalSearch.Perturbation perturbation = Perturbation.LOWEST_MISOVERLAID_STACK_CLEARING;

    public Problem getProblem() {
      return problem;
    }

    public void setProblem(Problem problem) {
      this.problem = problem;
    }

    public int getMinSearchMoves() {
      return minSearchMoves;
    }

    public void setMinSearchMoves(int minSearchMoves) {
      this.minSearchMoves = minSearchMoves;
    }

    public int getMaxSearchMoves() {
      return maxSearchMoves;
    }

    public void setMaxSearchMoves(int maxSearchMoves) {
      this.maxSearchMoves = maxSearchMoves;
    }

    public IterativeLocalSearch.Perturbation getPerturbation() {
      return perturbation;
    }

    public void setPerturbation(IterativeLocalSearch.Perturbation perturbation) {
      this.perturbation = perturbation;
    }
  }
}

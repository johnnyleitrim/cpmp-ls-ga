package com.johnnyleitrim.cpmp.ui.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import com.johnnyleitrim.cpmp.ls.IterativeLocalSearch;
import com.johnnyleitrim.cpmp.ls.Move;
import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.state.State;
import com.johnnyleitrim.cpmp.utils.MoveUtils;

public class Solver implements Callable<List<State>> {

  private final IterativeLocalSearch iterativeLocalSearch;

  private final State initialState;

  public Solver(IterativeLocalSearch iterativeLocalSearch, State initialState) {
    this.iterativeLocalSearch = iterativeLocalSearch;
    this.initialState = initialState;
  }

  @Override
  public List<State> call() {
    Optional<List<Move>> moves = iterativeLocalSearch.search(initialState, 1);
    MutableState state = initialState.copy();
    List<State> problemStates = new ArrayList<>(moves.map(List::size).orElse(0) + 1);
    problemStates.add(state.copy());
    for (Move move : moves.orElse(Collections.emptyList())) {
      MoveUtils.applyMove(state, move);
      problemStates.add(state.copy());
    }
    return problemStates;
  }
}

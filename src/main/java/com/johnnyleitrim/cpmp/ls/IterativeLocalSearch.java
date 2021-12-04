package com.johnnyleitrim.cpmp.ls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.state.State;
import com.johnnyleitrim.cpmp.stats.StatsWriter;
import com.johnnyleitrim.cpmp.strategy.ClearStackSelectionStrategy;
import com.johnnyleitrim.cpmp.utils.MoveUtils;
import com.johnnyleitrim.cpmp.utils.StackUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IterativeLocalSearch {
  private static final Logger LOGGER = LoggerFactory.getLogger(IterativeLocalSearch.class);

  private final IterativeLocalSearchStrategyConfig strategyConfig;
  private final StatsWriter statsWriter;

  public IterativeLocalSearch(IterativeLocalSearchStrategyConfig strategyConfig, StatsWriter statsWriter) {
    this.strategyConfig = strategyConfig;
    this.statsWriter = statsWriter;
  }

  public Optional<List<Move>> search(State initialState, int maxSolutions) {

    Optional<List<Move>> bestSolution = Optional.empty();
    long startTime = System.currentTimeMillis();
    int solutionCount = 0;

    while (hasNotFoundMaxSolutions(maxSolutions, solutionCount) && hasNotExceededMaxDuration(startTime)) {
      int localSearchMoves = 0;
      int perturbationMoves = 0;
      State state = initialState;
      int acceptanceCriteria = 0;
      long solutionStartTime = System.currentTimeMillis();

      int currentCost = calculateFitness(state);

      StepResult localSearchResult = performLocalSearchStep(state, currentCost);
      currentCost = localSearchResult.cost;
      state = localSearchResult.state;
      localSearchMoves += localSearchResult.moves.size();

      List<Move> moves = new LinkedList<>(localSearchResult.moves);

      while (currentCost != 0 && hasNotExceededMaxDuration(startTime)) {
        StepResult perturbationResult = performPerturbationStep(state);
        localSearchResult = performLocalSearchStep(perturbationResult.state, perturbationResult.cost);

        if (localSearchResult.cost - currentCost < acceptanceCriteria) {
          currentCost = localSearchResult.cost;
          moves.addAll(perturbationResult.moves);
          moves.addAll(localSearchResult.moves);
          state = localSearchResult.state;

          perturbationMoves += perturbationResult.moves.size();
          localSearchMoves += localSearchResult.moves.size();
          acceptanceCriteria = 0;
        } else {
          acceptanceCriteria++;
        }
      }
      LOGGER.trace("Local search moves: {}, Perturbation moves: {}", localSearchMoves, perturbationMoves);
      if (currentCost == 0) {
        LOGGER.trace("Found solution in {} moves", moves.size());
        solutionCount++;
        moves = removeTransientMoves(moves, state.getNumberOfStacks());
        statsWriter.writeSolution(moves.size(), localSearchMoves, perturbationMoves, System.currentTimeMillis() - solutionStartTime);
        bestSolution = getBestSolution(bestSolution, moves);
      } else {
        LOGGER.debug("No solution found");
      }
    }
    return bestSolution;
  }

  private List<Move> removeTransientMoves(List<Move> moves, int nStacks) {
    int nMovesBeforeRemoval = moves.size();
//    moves = MoveUtils.removeTransientMoves(moves, nStacks);
    if (moves.size() < nMovesBeforeRemoval) {
      LOGGER.trace("Removed {} transient moves", nMovesBeforeRemoval - moves.size());
    }
    return moves;
  }

  private void printDuplicateStates(State initialState, List<Move> moves) {
    Map<State, Integer> states = new HashMap<>(moves.size() + 1);
    states.put(initialState, 0);
    MutableState currentState = initialState.copy();
    for (int i = 1; i < moves.size(); i++) {
      Move move = moves.get(i - 1);
      MoveUtils.applyMove(currentState, move.getSrcStack(), move.getDstStack());
      if (states.containsKey(currentState)) {
        int initialLocation = states.get(currentState);
        LOGGER.debug("Found a duplicate state, distance is {}", i - initialLocation);
      } else {
        states.put(currentState.copy(), i);
      }
    }
  }

  private Optional<List<Move>> getBestSolution(Optional<List<Move>> currentBest, List<Move> moves) {
    if (currentBest.isEmpty() || moves.size() < currentBest.get().size()) {
      LOGGER.debug("Found better solution in {} moves", moves.size());
      return Optional.of(moves);
    }
    return currentBest;
  }

  private StepResult performLocalSearchStep(State state, int currentCost) {
    boolean localOptimumFound = false;
    int nSearchMoves = strategyConfig.getMinSearchMoves();
    List<Move> moves = new LinkedList<>();

    while (!localOptimumFound) {
      Neighbour bestNeighbour = getBestNeighbour(state, nSearchMoves);
      if (bestNeighbour != null && bestNeighbour.getCost() < currentCost) {
        moves.addAll(Arrays.asList(bestNeighbour.getMoves()));
        state = MoveUtils.applyMove(state.copy(), bestNeighbour.getMoves());
        currentCost = bestNeighbour.getCost();
        nSearchMoves = strategyConfig.getMinSearchMoves();
      } else if (nSearchMoves < strategyConfig.getMaxSearchMoves()) {
        nSearchMoves++;
      } else {
        localOptimumFound = true;
      }
    }
    return new StepResult(moves, currentCost, state);
  }

  private StepResult performPerturbationStep(State state) {

    MutableState newState = state.copy();
    ClearStackSelectionStrategy clearStackSelectionStrategy = strategyConfig.getClearStackSelectionStrategy();
    int stackToClear = clearStackSelectionStrategy.selectStack(newState);
    List<Move> moves = StackUtils.clearStack(newState, stackToClear, strategyConfig.getClearStackStrategy());
    if (strategyConfig.isFillStackAfterClearing()) {
      moves.addAll(StackUtils.fillStack(newState, stackToClear, strategyConfig.getFillStackStrategy()));
    }
    int newCost = calculateFitness(newState);
    return new StepResult(moves, newCost, newState);
  }

  private Neighbour getBestNeighbour(State state, int nSearchMoves) {
    int nStacks = state.getNumberOfStacks();

    Neighbour bestNeighbour = null;
    List<Neighbour> bestNeighbours = new ArrayList<>(nStacks);

    Neighbourhood neighbourhood = new Neighbourhood(strategyConfig.getFitnessStrategy(), state, nSearchMoves);

    for (Neighbour neighbour : neighbourhood) {
      if (bestNeighbour == null || neighbour.getCost() < bestNeighbour.getCost()) {
        bestNeighbour = neighbour;
        bestNeighbours.clear();
        bestNeighbours.add(neighbour);
      } else if (neighbour.getCost() == bestNeighbour.getCost()) {
        bestNeighbours.add(neighbour);
      }
    }

    return bestNeighbour == null ? null : strategyConfig.getBestNeighbourTieBreakingStrategy().getBestNeighbour(bestNeighbours);
  }

  private boolean hasNotExceededMaxDuration(long startTime) {
    return System.currentTimeMillis() < (startTime + strategyConfig.getMaxSearchDuration().toMillis());
  }

  private boolean hasNotFoundMaxSolutions(int maxSolutions, int solutionsFound) {
    return maxSolutions < 0 || solutionsFound < maxSolutions;
  }

  private int calculateFitness(State state) {
    return strategyConfig.getFitnessStrategy().calculateFitness(state);
  }

  private static class StepResult {
    private final List<Move> moves;
    private final int cost;
    private final State state;

    private StepResult(List<Move> moves, int cost, State state) {
      this.moves = moves;
      this.cost = cost;
      this.state = state;
    }
  }
}

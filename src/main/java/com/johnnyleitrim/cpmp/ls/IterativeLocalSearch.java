package com.johnnyleitrim.cpmp.ls;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalInt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.fitness.FitnessAlgorithm;
import com.johnnyleitrim.cpmp.random.RandomStackGenerator;
import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.state.State;
import com.johnnyleitrim.cpmp.utils.MoveUtils;
import com.johnnyleitrim.cpmp.utils.StackUtils;

public class IterativeLocalSearch {
  private static final Logger LOGGER = LoggerFactory.getLogger(IterativeLocalSearch.class);

  private final State initialState;

  private final int minSearchMoves;
  private final int maxSearchMoves;
  private final FitnessAlgorithm fitnessAlgorithm;
  private final long maxSearchDurationMillis;

  public IterativeLocalSearch(State initialState, int minSearchMoves, int maxSearchMoves, FitnessAlgorithm fitnessAlgorithm, Duration maxSearchDuration) {
    this.initialState = initialState;
    this.minSearchMoves = minSearchMoves;
    this.maxSearchMoves = maxSearchMoves;
    this.fitnessAlgorithm = fitnessAlgorithm;
    this.maxSearchDurationMillis = maxSearchDuration.toMillis();
  }

  public List<Move> search(Perturbation perturbation, int maxSolutions) {

    List<Move> bestSolution = Collections.emptyList();
    long startTime = System.currentTimeMillis();
    int solutionCount = 0;

    while (isStillRunning(startTime)) {
      int localSearchMoves = 0;
      int perturbationMoves = 0;
      State state = initialState;
      int acceptanceCriteria = 0;

      int currentCost = fitnessAlgorithm.calculateFitness(state);

      StepResult localSearchResult = performLocalSearchStep(state, currentCost);
      currentCost = localSearchResult.cost;
      state = localSearchResult.state;
      localSearchMoves += localSearchResult.moves.size();

      List<Move> moves = new LinkedList<>(localSearchResult.moves);

      int iteration = 0;
      while (currentCost != 0 && iteration < 1000 && isStillRunning(startTime)) {
        StepResult perturbationResult = performPerturbationStep(perturbation, state);
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

        iteration += maxSearchMoves * (maxSearchMoves - minSearchMoves + 1); // For the perturbation
        iteration += 1; // For the perturbation
      }
      LOGGER.debug("Local search moves: {}, Perturbation moves: {}", localSearchMoves, perturbationMoves);
      if (currentCost == 0) {
        LOGGER.debug("Found solution in {} moves, iterations: {}", moves.size(), iteration);
        solutionCount++;
        moves = removeTransientMoves(moves);
        if (bestSolution.isEmpty() || moves.size() < bestSolution.size()) {
          LOGGER.debug("Found better solution in {} moves", moves.size());
          bestSolution = moves;
        }
        if (maxSolutions > 0 && solutionCount >= maxSolutions) {
          return bestSolution;
        }
      } else {
        LOGGER.debug("No solution found in iterations: {}", iteration);
      }
    }
    return bestSolution;
  }

  private List<Move> removeTransientMoves(List<Move> moves) {
    int nMovesBeforeRemoval = moves.size();
    moves = MoveUtils.removeTransientMoves(moves, initialState.getNumberOfStacks());
    if (moves.size() < nMovesBeforeRemoval) {
      LOGGER.info("Removed {} transient moves", nMovesBeforeRemoval - moves.size());
    }
    return moves;
  }

  private StepResult performLocalSearchStep(State state, int currentCost) {
    boolean localOptimumFound = false;
    int nSearchMoves = minSearchMoves;
    List<Move> moves = new LinkedList<>();

    while (!localOptimumFound) {
      Neighbour bestNeighbour = getBestNeighbour(state, nSearchMoves);
      if (bestNeighbour != null && bestNeighbour.getCost() < currentCost) {
        moves.addAll(Arrays.asList(bestNeighbour.getMoves()));
        state = MoveUtils.applyMove(state.copy(), bestNeighbour.getMoves());
        currentCost = bestNeighbour.getCost();
        nSearchMoves = minSearchMoves;
      } else if (nSearchMoves < maxSearchMoves) {
        nSearchMoves++;
      } else {
        localOptimumFound = true;
      }
    }
    return new StepResult(moves, currentCost, state);
  }

  private StepResult performPerturbationStep(Perturbation perturbation, State state) {
    switch (perturbation) {
      case RANDOM_MOVE:
        return performRandomMoveStep(state);
      case STACK_CLEARING:
        return performStackClearingStep(state);
      case LOWEST_STACK_CLEARING:
        return performClearAndFillLowestStackStep(state);
      case LOWEST_MISOVERLAID_STACK_CLEARING:
        return performClearAndFillLowestMisOverlaidStackStep(state);
      default:
        throw new IllegalArgumentException("Unknown perturbation: " + perturbation);
    }
  }

  private StepResult performRandomMoveStep(State state) {
    RandomStackGenerator stackGenerator = new RandomStackGenerator(state.getStackStates(), OptionalInt.empty());
    int srcStack = stackGenerator.getNextNonEmptyStack();
    int dstStack = stackGenerator.getNextNonFullStack();
    Move move = new Move(srcStack, dstStack);
    State newState = MoveUtils.applyMove(state.copy(), move);
    int newCost = fitnessAlgorithm.calculateFitness(newState);
    return new StepResult(Collections.singletonList(move), newCost, newState);
  }

  private StepResult performStackClearingStep(State state) {
    // Randomly pick a stack.
    RandomStackGenerator stackGenerator = new RandomStackGenerator(state.getStackStates(), OptionalInt.empty());
    int stackToClear = stackGenerator.getNextNonEmptyStack();
    return performClearAndFillStackStep(state, stackToClear);
  }

  private StepResult performClearAndFillLowestStackStep(State state) {

    // Find the lowest stack. If there is more than one, pick one at random.
    List<Integer> lowestStacks = StackUtils.getLowestStacks(state);
    int stackToClear = lowestStacks.get(Problem.getRandom().nextInt(lowestStacks.size()));
    return performClearAndFillStackStep(state, stackToClear);
  }

  private StepResult performClearAndFillLowestMisOverlaidStackStep(State state) {
    // Find the lowest stack. If there is more than one, pick one at random.
    List<Integer> lowestStacks = StackUtils.getLowestStacks(state, state::isMisOverlaid);
    lowestStacks.sort(Comparator.comparingInt(stack -> state.getGroup(stack, 0)));
    int stackToClear = lowestStacks.get(0);
    return performClearAndFillStackStep(state, stackToClear);
  }

  private StepResult performClearAndFillStackStep(State state, int stackToClear) {
    MutableState newState = state.copy();
    List<Move> moves = StackUtils.clearStack(newState, stackToClear);
    if (Features.instance.isFillStackEnabled()) {
      moves.addAll(StackUtils.fillStack(newState, stackToClear));
    }

    int newCost = fitnessAlgorithm.calculateFitness(newState);

    return new StepResult(moves, newCost, newState);
  }

  private Neighbour getBestNeighbour(State state, int nSearchMoves) {
    int nStacks = state.getNumberOfStacks();

    Neighbour bestNeighbour = null;
    List<Neighbour> bestNeighbours = new ArrayList<>(nStacks);

    Neighbourhood neighbourhood = new Neighbourhood(fitnessAlgorithm, state, nSearchMoves);

    for (Neighbour neighbour : neighbourhood) {
      if (bestNeighbour == null || neighbour.getCost() < bestNeighbour.getCost()) {
        bestNeighbour = neighbour;
        bestNeighbours.clear();
        bestNeighbours.add(neighbour);
      } else if (neighbour.getCost() == bestNeighbour.getCost()) {
        bestNeighbours.add(neighbour);
      }
    }

    return bestNeighbour == null ? null : getBestNeighbour(bestNeighbours);
  }

  private Neighbour getBestNeighbour(List<Neighbour> neighbours) {
    int bestNeighbours = neighbours.size();
    if (Features.instance.isImprovedTieBreakingEnabled()) {
      neighbours.sort(Comparator.comparingInt(Neighbour::getSumContainerGroups).reversed());
      bestNeighbours = 0;
      int highestContainer = neighbours.get(0).getSumContainerGroups();
      for (Neighbour neighbour : neighbours) {
        if (neighbour.getSumContainerGroups() == highestContainer) {
          bestNeighbours++;
        }
      }
    }
    return neighbours.get(Problem.getRandom().nextInt(bestNeighbours));
  }

  private boolean isStillRunning(long startTime) {
    return System.currentTimeMillis() < (startTime + maxSearchDurationMillis);
  }

  public enum Perturbation {
    RANDOM_MOVE, STACK_CLEARING, LOWEST_STACK_CLEARING, LOWEST_MISOVERLAID_STACK_CLEARING
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

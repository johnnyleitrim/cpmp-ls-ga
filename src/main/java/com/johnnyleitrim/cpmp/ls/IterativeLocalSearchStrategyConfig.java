package com.johnnyleitrim.cpmp.ls;

import java.time.Duration;

import com.johnnyleitrim.cpmp.fitness.BFLowerBoundFitness;
import com.johnnyleitrim.cpmp.fitness.FitnessAlgorithm;
import com.johnnyleitrim.cpmp.strategy.BestNeighbourTieBreakingStrategies;
import com.johnnyleitrim.cpmp.strategy.BestNeighbourTieBreakingStrategy;
import com.johnnyleitrim.cpmp.strategy.ClearStackSelectionStrategies;
import com.johnnyleitrim.cpmp.strategy.ClearStackSelectionStrategy;
import com.johnnyleitrim.cpmp.strategy.StackClearingStrategies;
import com.johnnyleitrim.cpmp.strategy.StackClearingStrategy;
import com.johnnyleitrim.cpmp.strategy.StackFillingStrategies;
import com.johnnyleitrim.cpmp.strategy.StackFillingStrategy;

public class IterativeLocalSearchStrategyConfig {

  private int minSearchMoves = 1;

  private int maxSearchMoves = 2;

  private FitnessAlgorithm fitnessAlgorithm = new BFLowerBoundFitness();

  private Duration maxSearchDuration = Duration.ofMinutes(1);

  private ClearStackSelectionStrategy clearStackSelectionStrategy = ClearStackSelectionStrategies.RANDOM_STACK;

  private BestNeighbourTieBreakingStrategy bestNeighbourTieBreakingStrategy = BestNeighbourTieBreakingStrategies.HIGHEST_CONTAINER;

  private boolean fillStackAfterClearing = false;

  private StackFillingStrategy fillStackStrategy = StackFillingStrategies.LARGEST_CONTAINER;

  private StackClearingStrategy clearStackStrategy = StackClearingStrategies.RANDOM;

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

  public FitnessAlgorithm getFitnessAlgorithm() {
    return fitnessAlgorithm;
  }

  public void setFitnessAlgorithm(FitnessAlgorithm fitnessAlgorithm) {
    this.fitnessAlgorithm = fitnessAlgorithm;
  }

  public Duration getMaxSearchDuration() {
    return maxSearchDuration;
  }

  public void setMaxSearchDuration(Duration maxSearchDuration) {
    this.maxSearchDuration = maxSearchDuration;
  }

  public ClearStackSelectionStrategy getClearStackSelectionStrategy() {
    return clearStackSelectionStrategy;
  }

  public void setClearStackSelectionStrategy(ClearStackSelectionStrategy clearStackSelectionStrategy) {
    this.clearStackSelectionStrategy = clearStackSelectionStrategy;
  }

  public BestNeighbourTieBreakingStrategy getBestNeighbourTieBreakingStrategy() {
    return bestNeighbourTieBreakingStrategy;
  }

  public void setBestNeighbourTieBreakingStrategy(BestNeighbourTieBreakingStrategy bestNeighbourTieBreakingStrategy) {
    this.bestNeighbourTieBreakingStrategy = bestNeighbourTieBreakingStrategy;
  }

  public boolean isFillStackAfterClearing() {
    return fillStackAfterClearing;
  }

  public void setFillStackAfterClearing(boolean fillStackAfterClearing) {
    this.fillStackAfterClearing = fillStackAfterClearing;
  }

  public StackFillingStrategy getFillStackStrategy() {
    return fillStackStrategy;
  }

  public void setFillStackStrategy(StackFillingStrategy fillStackStrategy) {
    this.fillStackStrategy = fillStackStrategy;
  }

  public StackClearingStrategy getClearStackStrategy() {
    return clearStackStrategy;
  }

  public void setClearStackStrategy(StackClearingStrategy clearStackStrategy) {
    this.clearStackStrategy = clearStackStrategy;
  }
}

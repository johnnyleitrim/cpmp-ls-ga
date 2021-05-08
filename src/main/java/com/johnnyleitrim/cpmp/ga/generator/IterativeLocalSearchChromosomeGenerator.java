package com.johnnyleitrim.cpmp.ga.generator;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.johnnyleitrim.cpmp.fitness.FitnessAlgorithm;
import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.ga.Gene;
import com.johnnyleitrim.cpmp.ls.IterativeLocalSearch;
import com.johnnyleitrim.cpmp.ls.IterativeLocalSearch.Perturbation;
import com.johnnyleitrim.cpmp.ls.Move;
import com.johnnyleitrim.cpmp.state.State;

public class IterativeLocalSearchChromosomeGenerator implements ChromosomeGenerator {

  private final IterativeLocalSearch iterativeLocalSearch;

  private final Perturbation perturbation;

  public IterativeLocalSearchChromosomeGenerator(State initialState, int nSearchMoves, Perturbation perturbation, FitnessAlgorithm fitnessAlgorithm) {
    this.iterativeLocalSearch = new IterativeLocalSearch(initialState, 1, nSearchMoves, fitnessAlgorithm, Duration.of(1, ChronoUnit.MINUTES));
    this.perturbation = perturbation;
  }

  @Override
  public Chromosome generateChromosome() {
    List<Move> moves = iterativeLocalSearch.search(perturbation, true);
    Gene[] genes = new Gene[moves.size()];
    for (int i = 0; i < moves.size(); i++) {
      Move move = moves.get(i);
      genes[i] = new Gene(move.getSrcStack(), move.getDstStack());
    }
    return new Chromosome(genes);
  }
}

package com.johnnyleitrim.cpmp.ga.generator;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.johnnyleitrim.cpmp.fitness.BFLowerBoundFitness;
import com.johnnyleitrim.cpmp.ga.Gene;
import com.johnnyleitrim.cpmp.ls.IterativeLocalSearch;
import com.johnnyleitrim.cpmp.ls.IterativeLocalSearch.Perturbation;
import com.johnnyleitrim.cpmp.ls.Move;
import com.johnnyleitrim.cpmp.state.State;

public class IterativeLocalSearchGeneGenerator implements GeneGenerator {

  private static final ExcellentMoveGeneGenerator excellentMoveGeneGenerator = new ExcellentMoveGeneGenerator();
  private final int nMaxSearchMoves;
  private final Perturbation perturbation;

  public IterativeLocalSearchGeneGenerator(int nMaxSearchMoves, Perturbation perturbation) {
    this.nMaxSearchMoves = nMaxSearchMoves;
    this.perturbation = perturbation;
  }

  @Override
  public Gene generateGene(State state) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Gene[] generateGenes(State initialState, int nGenes) {
    IterativeLocalSearch iterativeLocalSearch = new IterativeLocalSearch(initialState, 1, nMaxSearchMoves, new BFLowerBoundFitness(), Duration.of(2, ChronoUnit.SECONDS));
    List<Move> moves = iterativeLocalSearch.search(perturbation, 1);
    Gene[] genes = new Gene[nGenes];
    for (int i = 0; i < nGenes; i++) {
      if (i < moves.size()) {
        Move move = moves.get(i);
        genes[i] = new Gene(move.getSrcStack(), move.getDstStack());
      } else {
        return excellentMoveGeneGenerator.generateGenes(initialState, nGenes);
      }
    }
    return genes;
  }
}

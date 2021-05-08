package com.johnnyleitrim.cpmp.ga.variation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.ga.Gene;
import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.state.State;

public class InjectMovesMutationTest {

  private final InjectMovesMutation mutationUnderTest = new InjectMovesMutation();

  @Test
  public void itPerformsMutationCorrectly() throws Exception {
    int nStacks = 3;
    int nTiers = 5;
    int[][] internalState = new int[nTiers][nStacks];
    internalState[2] = new int[]{0, 5, 4};
    internalState[1] = new int[]{0, 6, 9};
    internalState[0] = new int[]{0, 2, 8};

    State state = new State(internalState, nStacks, nTiers);

    int nGenes = 5;

    Chromosome chromosome = new Chromosome(new Gene[]{
        new Gene(1, 0),
        // {0, 0, 4};
        // {0, 6, 9};
        // {5, 2, 8};
        new Gene(2, 0),
        // {0, 0, 0};
        // {4, 6, 9};
        // {5, 2, 8};
        new Gene(2, 0),
        // {9, 0, 0};
        // {4, 6, 0};
        // {5, 2, 8};
        new Gene(1, 2),
        // {9, 0, 0};
        // {4, 0, 6};
        // {5, 2, 8};
        new Gene(0, 3),
        // {0, 0, 9};
        // {4, 0, 6};
        // {5, 2, 8};
    });

    int geneAfterInsert = 4;
    mutationUnderTest.injectNewMoves(chromosome, nGenes, 1, state);
    assertThat(chromosome.getGene(geneAfterInsert)).isEqualTo(new Gene(2, 0));

    MutableState actualState = state.copy();
    for (int i = 0; i < geneAfterInsert; i++) {
      Gene move = chromosome.getGene(i);
      actualState.applyMove(move.getSourceStack(), move.getDestinationStack());
    }
    assertThat(actualState.getHeight(0)).isEqualTo(1);
    assertThat(actualState.getHeight(1)).isEqualTo(2);
    assertThat(actualState.getHeight(2)).isEqualTo(3);
  }

}

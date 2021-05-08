package com.johnnyleitrim.cpmp.ga.crossover;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.ga.Gene;
import com.johnnyleitrim.cpmp.state.State;

public class SameHeightCrossoverTest {

  private final SameHeightCrossover algorithmUnderTest = new SameHeightCrossover();

  @Test
  public void itPerformsCrossoverCorrectly() {
    int nStacks = 3;
    int nTiers = 5;
    int[][] internalState = new int[nTiers][nStacks];
    internalState[2] = new int[]{0, 5, 4};
    internalState[1] = new int[]{0, 6, 9};
    internalState[0] = new int[]{0, 2, 8};

    State state = new State(internalState, nStacks, nTiers);

    int nGenes = 4;

    Chromosome parentA = new Chromosome(new Gene[]{
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
    });
    Chromosome parentB = new Chromosome(new Gene[]{
        new Gene(2, 0),
        // {0, 5, 0};
        // {0, 6, 9};
        // {4, 2, 8};
        new Gene(2, 0),
        // {0, 5, 0};
        // {9, 6, 0};
        // {4, 2, 8};
        new Gene(1, 0),
        // {5, 0, 0};
        // {9, 6, 0};
        // {4, 2, 8};
        new Gene(0, 2),
        // {0, 0, 0};
        // {9, 6, 5};
        // {4, 2, 8};
    });

    Chromosome expectedChildA = parentA.copy();
    expectedChildA.setGene(3, parentB.getGene(3));

    Chromosome expectedChildB = parentB.copy();
    expectedChildB.setGene(3, parentA.getGene(3));

    Chromosome[] actualChildren = algorithmUnderTest.crossover(parentA, parentB, nGenes, state);
    assertThat(actualChildren).containsExactlyInAnyOrder(expectedChildA, expectedChildB);
  }
}

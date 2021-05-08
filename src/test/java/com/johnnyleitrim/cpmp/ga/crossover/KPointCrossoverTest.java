package com.johnnyleitrim.cpmp.ga.crossover;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.ga.Gene;

@ExtendWith(MockitoExtension.class)
public class KPointCrossoverTest {

  private final Chromosome parentA = new Chromosome(new Gene[]{
      new Gene(0, 2),
      new Gene(1, 2),
      new Gene(3, 2),
  });

  private final Chromosome parentB = new Chromosome(new Gene[]{
      new Gene(0, 1),
      new Gene(2, 1),
      new Gene(3, 1),
  });

  private final int nGenes = 3;

  @Mock
  private Random random;

  @Test
  public void itPerformsOnePointCrossoverIfMaxIndex() {
    when(random.nextInt(anyInt())).thenReturn(nGenes - 1);
    Problem.setRandom(random);
    KPointCrossover crossover = new KPointCrossover(1);

    Chromosome[] children = crossover.crossover(parentA, parentB, nGenes, null);
    assertThat(children[0]).isEqualTo(parentA);
    assertThat(children[1]).isEqualTo(parentB);
  }

  @Test
  public void itPerformOnePointCrossover() {
    when(random.nextInt(anyInt())).thenReturn(1);
    Problem.setRandom(random);
    KPointCrossover crossover = new KPointCrossover(1);

    Chromosome[] children = crossover.crossover(parentA, parentB, nGenes, null);
    assertThat(children[0]).isEqualTo(new Chromosome(new Gene[]{
        parentA.getGene(0),
        parentA.getGene(1),
        parentB.getGene(2),
    }));
    assertThat(children[1]).isEqualTo(new Chromosome(new Gene[]{
        parentB.getGene(0),
        parentB.getGene(1),
        parentA.getGene(2),
    }));
  }

  @Test
  public void itPerformTwoPointCrossover() {
    when(random.nextInt(nGenes)).thenReturn(0);
    when(random.nextInt(nGenes - 1)).thenReturn(0);
    Problem.setRandom(random);
    KPointCrossover crossover = new KPointCrossover(2);

    Chromosome[] children = crossover.crossover(parentA, parentB, nGenes, null);
    assertThat(children[0]).isEqualTo(new Chromosome(new Gene[]{
        parentA.getGene(0),
        parentB.getGene(1),
        parentA.getGene(2),
    }));
    assertThat(children[1]).isEqualTo(new Chromosome(new Gene[]{
        parentB.getGene(0),
        parentA.getGene(1),
        parentB.getGene(2),
    }));
  }

}

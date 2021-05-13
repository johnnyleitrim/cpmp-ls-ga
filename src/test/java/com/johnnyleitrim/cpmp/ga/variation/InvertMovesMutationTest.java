package com.johnnyleitrim.cpmp.ga.variation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;

import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.ga.Gene;

@ExtendWith(MockitoExtension.class)
public class InvertMovesMutationTest {

  private final Chromosome individual = new Chromosome(new Gene[]{
      new Gene(0, 10),
      new Gene(1, 10),
      new Gene(2, 10),
      new Gene(3, 10),
      new Gene(4, 10),
      new Gene(5, 10),
      new Gene(6, 10),
  });
  private final InvertMovesMutation mutation = new InvertMovesMutation();
  @Mock
  private Random random;

  @BeforeEach
  public void setup() {
    Problem.setRandom(random);
  }

  @Test
  public void itInvertsGenes() {
    doReturn(2, 3).when(random).nextInt(anyInt());

    mutation.mutate(individual, 7, null);
    assertThat(individual).isEqualTo(new Chromosome(new Gene[]{
        new Gene(0, 10),
        new Gene(1, 10),
        new Gene(5, 10),
        new Gene(4, 10),
        new Gene(3, 10),
        new Gene(2, 10),
        new Gene(6, 10),
    }));
  }
}

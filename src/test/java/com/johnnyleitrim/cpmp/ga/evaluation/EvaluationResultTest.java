package com.johnnyleitrim.cpmp.ga.evaluation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.ga.Gene;

public class EvaluationResultTest {

  private static final Chromosome CHROMOSOME = new Chromosome(new Gene[0]);

  @Test
  public void itComparesSolutionsCorrectly() {
    EvaluationResult bestSolution = EvaluationResult.forSolution(1, CHROMOSOME, false);
    EvaluationResult worstSolution = EvaluationResult.forSolution(10, CHROMOSOME, false);

    assertThat(bestSolution.isBetter(worstSolution)).isTrue();
    assertThat(worstSolution.isBetter(bestSolution)).isFalse();
  }

  @Test
  public void itComparesSameSolutionCorrectly() {
    EvaluationResult solution = EvaluationResult.forSolution(1, CHROMOSOME, false);

    assertThat(solution.isBetter(solution)).isFalse();
  }

  @Test
  public void itComparesNonSolutionsCorrectly() {
    EvaluationResult bestSolution = EvaluationResult.forFitness(1, CHROMOSOME, false);
    EvaluationResult worstSolution = EvaluationResult.forFitness(10, CHROMOSOME, false);

    assertThat(bestSolution.isBetter(worstSolution)).isTrue();
    assertThat(worstSolution.isBetter(bestSolution)).isFalse();
  }

  @Test
  public void itComparesSameNonSolutionCorrectly() {
    EvaluationResult solution = EvaluationResult.forFitness(1, CHROMOSOME, false);

    assertThat(solution.isBetter(solution)).isFalse();
  }

  @Test
  public void itComparesSolutionsAndNonSolutionsCorrectly() {
    EvaluationResult bestSolution = EvaluationResult.forSolution(30, CHROMOSOME, false);
    EvaluationResult worstSolution = EvaluationResult.forFitness(10, CHROMOSOME, false);

    assertThat(bestSolution.isBetter(worstSolution)).isTrue();
    assertThat(worstSolution.isBetter(bestSolution)).isFalse();
  }

}

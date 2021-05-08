package com.johnnyleitrim.cpmp.ga.variation;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.ga.Gene;
import com.johnnyleitrim.cpmp.state.State;

public class InvertMovesMutation implements MutationAlgorithm {

  @Override
  public void mutate(Chromosome chromosome, int nGenes, State initialState) {
    int index1 = Problem.getRandom().nextInt(nGenes);
    int index2 = index1 + Problem.getRandom().nextInt(nGenes - index1);

    for (int left = index1, right = index2; left < right; left++, right--) {
      // swap the values at the left and right indices
      Gene temp = chromosome.getGene(left);
      chromosome.setGene(left, chromosome.getGene(right));
      chromosome.setGene(right, temp);
    }
  }
}

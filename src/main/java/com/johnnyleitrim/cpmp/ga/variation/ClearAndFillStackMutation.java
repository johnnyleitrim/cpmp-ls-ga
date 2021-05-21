package com.johnnyleitrim.cpmp.ga.variation;

import java.util.List;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.ga.Gene;
import com.johnnyleitrim.cpmp.ls.Move;
import com.johnnyleitrim.cpmp.state.InvalidMoveException;
import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.state.State;
import com.johnnyleitrim.cpmp.utils.StackUtils;

public class ClearAndFillStackMutation implements MutationAlgorithm {


  @Override
  public void mutate(Chromosome chromosome, int nGenes, State initialState) {
    int clearStackStartIndex = Problem.getRandom().nextInt(nGenes / 2);
    MutableState currentState = initialState.copy();
    for (int i = 0; i < clearStackStartIndex; i++) {
      try {
        Gene move = chromosome.getGene(i);
        currentState.applyMove(move.getSourceStack(), move.getDestinationStack());
      } catch (InvalidMoveException e) {
        // The crossover operator may have created invalid solutions
        // So we will start the stack clearing from the first invalid move if needed
        clearStackStartIndex = i;
        break;
      }
    }

    // Find the lowest stack. If there is more than one, pick one at random.
    List<Integer> lowestStacks = StackUtils.getLowestStacks(currentState, stack -> currentState.isMisOverlaid(stack));
    int stackToClear = lowestStacks.get(Problem.getRandom().nextInt(lowestStacks.size()));

    List<Move> moves = StackUtils.clearStack(currentState, stackToClear);
    moves.addAll(StackUtils.fillStack(currentState, stackToClear));

    if (moves.isEmpty()) {
      return;
    }

    int moveIndex = clearStackStartIndex;
    for (Move move : moves) {
      if (moveIndex < nGenes - 1) {
        chromosome.setGene(moveIndex, new Gene(move.getSrcStack(), move.getDstStack()));
        moveIndex++;
      }
    }
  }
}

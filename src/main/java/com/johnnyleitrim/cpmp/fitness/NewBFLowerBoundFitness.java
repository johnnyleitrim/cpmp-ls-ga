package com.johnnyleitrim.cpmp.fitness;

import java.util.ArrayList;
import java.util.Collections;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.state.State;

public class NewBFLowerBoundFitness implements FitnessAlgorithm {
  @Override
  public int calculateFitness(State state) {
    int bayWidth = state.getNumberOfStacks();
    int bayHeight = state.getNumberOfTiers();

    int overstowage = 0;
    int lowestPriority = state.getGroups().first();
    var lbDemand = new int[lowestPriority + 1];
    var lbSupply = new int[lowestPriority + 1];
    for (int i = 0; i < lowestPriority + 1; i++) {
      lbDemand[i] = 0;
      lbSupply[i] = 0;

    }
    var empty = new boolean[bayWidth];
    var lbHighestWplaced = new int[bayWidth];
    var lbStackHasOverstowage = new boolean[bayWidth];
    for (int i = 0; i < bayWidth; i++) {
      lbHighestWplaced[i] = Problem.EMPTY;
      lbStackHasOverstowage[i] = false;
    }
    for (int i = 0; i < bayWidth; i++) {
      if (state.getGroup(i, 0) == Problem.EMPTY) {
        empty[i] = true;
      } else {
        empty[i] = false;
      }
    }
    int stackOverstowed;
    int minBadlyPlaced = Integer.MAX_VALUE;
    int supplyValue = 0;

    for (int ss = 0; ss < bayWidth; ++ss) {
      var afterHighestWp = false;
      var overstowed = false;
      stackOverstowed = 0;
      int bottom = state.getGroup(ss, 0);
      if (bottom > Problem.EMPTY) {
        lbHighestWplaced[ss] = bottom;
      }

      for (int tt = 1; tt < bayHeight; tt++) {
        int current = state.getGroup(ss, tt);
        int below = state.getGroup(ss, tt - 1);


        if (!overstowed && (below < current)) {
          supplyValue = below;
          lbHighestWplaced[ss] = below;
          overstowed = true;
          lbStackHasOverstowage[ss] = true;
          afterHighestWp = true;
        }
        if (!afterHighestWp && bottom > Problem.EMPTY && current == Problem.EMPTY) {
          afterHighestWp = true;
          supplyValue = below;
        }
        if (afterHighestWp) {
          lbSupply[supplyValue]++;
        }
        if (overstowed) {
          if (current != Problem.EMPTY) {
            lbDemand[current]++;
            stackOverstowed++;
          }
        } else if (current > Problem.EMPTY) {
          lbHighestWplaced[ss] = current;
        }
      }
      overstowage += stackOverstowed;
      minBadlyPlaced = Math.min(minBadlyPlaced, stackOverstowed);
    }
    overstowage += minBadlyPlaced;
    // Compute the maximum cumulative demand surplus g*
    // Note that D(g) is computed on the fly by accumulating the values in the demand vector
    int cmDemand = 0;
    int numEmptyStacks = 0;
    for (int i = 0; i < empty.length; i++) {
      if (empty[i]) {
        numEmptyStacks++;
      }
    }

    int cmSupply = numEmptyStacks * bayHeight;
    int maxCmDmdSup = 0;
    int gstar = 0;
    for (int gg = lowestPriority; gg > Problem.EMPTY; --gg) {
      cmDemand += lbDemand[gg];
      cmSupply += lbSupply[gg];
      int tmpCmDmdSup = cmDemand - cmSupply;
      if (tmpCmDmdSup > maxCmDmdSup) {
        maxCmDmdSup = tmpCmDmdSup;
        gstar = gg;
      }
    }
    int nsgx = Math.max(0, (int) Math.ceil(maxCmDmdSup / (double) bayHeight));
    var ngstar = new ArrayList<Integer>();
    for (int ss = 0; ss < bayWidth; ++ss) {
      if (lbHighestWplaced[ss] < gstar) {
        ngstar.add(0);
        if (!empty[ss]) {
          // iterate up stack ss until the current tier is overstowing
          for (int tt = 0;
               tt < bayHeight && state.getGroup(ss, tt) > Problem.EMPTY &&
                   (tt == 0 || state.getGroup(ss, tt) <= state.getGroup(ss, tt - 1));
               tt++) {
            if (state.getGroup(ss, tt) < gstar) {
              ngstar.set(ngstar.size() - 1, ngstar.get(ngstar.size() - 1) + 1);
            }
          }
        }
      }
    }
    Collections.sort(ngstar);
    for (int ii = 0; ii < nsgx; ++ii) {
      overstowage += ngstar.get(ii);
    }
    return overstowage;
  }
}

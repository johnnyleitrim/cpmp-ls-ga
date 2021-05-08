package com.johnnyleitrim.cpmp.ga.selection;

import java.util.ArrayList;
import java.util.List;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.ga.Chromosome;
import com.johnnyleitrim.cpmp.ga.evaluation.EvaluationResult;

public class TournamentSelection implements SelectionAlgorithm {

  private final int nParticipants;

  public TournamentSelection() {
    this(2);
  }

  public TournamentSelection(int nParticipants) {
    this.nParticipants = nParticipants;
  }

  @Override
  public Chromosome[] generateMatingPool(EvaluationResult[] evaluationResults) {
    Chromosome[] matingPool = new Chromosome[evaluationResults.length];
    for (int i = 0; i < evaluationResults.length; i++) {
      matingPool[i] = runTournament(evaluationResults);
    }
    return matingPool;
  }

  private Chromosome runTournament(EvaluationResult[] evaluationResults) {
    List<Integer> participants = new ArrayList<>(nParticipants);
    for (int i = 0; i < nParticipants; i++) {
      int randomParticipant = Problem.getRandom().nextInt(evaluationResults.length);
      while (participants.contains(randomParticipant)) {
        randomParticipant = Problem.getRandom().nextInt(evaluationResults.length);
      }
      participants.add(randomParticipant);
    }
    EvaluationResult winner = null;
    for (int i = 0; i < nParticipants; i++) {
      EvaluationResult participant = evaluationResults[participants.get(i)];
      if (winner == null || participant.isBetter(winner)) {
        winner = participant;
      }
    }

    return winner.getChromosome();
  }

  @Override
  public String toString() {
    return String.format("%s (nParticipants: %d)", getClass().getSimpleName(), nParticipants);
  }
}

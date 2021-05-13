package com.johnnyleitrim.cpmp.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.time.Duration;
import java.util.List;

import javax.swing.JFrame;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.fitness.BFLowerBoundFitness;
import com.johnnyleitrim.cpmp.ls.IterativeLocalSearch;
import com.johnnyleitrim.cpmp.ls.Move;
import com.johnnyleitrim.cpmp.problem.BFProblemProvider;
import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.state.State;
import com.johnnyleitrim.cpmp.utils.MoveUtils;

public class CpmpApp {

  private StatePanel statePanel;

  public static void main(String[] args) {
    Problem problem = new BFProblemProvider(32).getProblems().iterator().next();
    CpmpApp cpmpApp = new CpmpApp(problem.getInitialState());

    cpmpApp.solveProblem(problem, 400, 400);
  }

  public CpmpApp(State state) {
    JFrame frame = new JFrame("Container Pre-Marshalling Problem");
    statePanel = new StatePanel(state);

    frame.setLayout(new BorderLayout());
    frame.add(statePanel, BorderLayout.CENTER);

    // Show our window
    frame.setVisible(true);
  }

  private void solveProblem(Problem problem, int width, int height) {
    IterativeLocalSearch cpmpSolver = new IterativeLocalSearch(problem.getInitialState(), 1, 2, new BFLowerBoundFitness(), Duration.ofMinutes(1));
    List<Move> moves = cpmpSolver.search(IterativeLocalSearch.Perturbation.LOWEST_MISOVERLAID_STACK_CLEARING, true);
    MutableState state = problem.getInitialState().copy();
    for (Move move : moves) {
      MoveUtils.applyMove(state, move);
      EventQueue.invokeLater(() -> statePanel.setState(state));
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}

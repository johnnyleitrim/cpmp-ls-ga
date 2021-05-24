package com.johnnyleitrim.cpmp.ui.solver;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.johnnyleitrim.cpmp.fitness.BFLowerBoundFitness;
import com.johnnyleitrim.cpmp.ls.IterativeLocalSearch;
import com.johnnyleitrim.cpmp.ls.IterativeLocalSearchStrategyConfig;
import com.johnnyleitrim.cpmp.state.State;

public class SolverPanel extends JPanel {

  private static final Logger LOGGER = LoggerFactory.getLogger(SolverPanel.class);

  private static final BFLowerBoundFitness FITNESS = new BFLowerBoundFitness();

  private final int nSolvers = 6;
  private final StatePanel statePanel;
  private final JLabel statusLine;
  private final ExecutorService executorService = Executors.newFixedThreadPool(nSolvers);
  private final IterativeLocalSearch iterativeLocalSearch;
  private final State initialState;

  private List<State> problemStates;
  private int currentProblemState = 0;

  public SolverPanel(State initialState, IterativeLocalSearchStrategyConfig strategyConfig) {
    statePanel = new StatePanel(initialState);
    this.initialState = initialState;

    iterativeLocalSearch = new IterativeLocalSearch(strategyConfig);

    setLayout(new BorderLayout());
    add(statePanel, BorderLayout.CENTER);

    JButton solveButton = new JButton("Solve");
    solveButton.addActionListener(e -> solveProblem((JButton) e.getSource()));

    add(solveButton, BorderLayout.NORTH);

    statusLine = new JLabel("No problem loaded", SwingConstants.CENTER);
    statusLine.setBorder(new BevelBorder(BevelBorder.LOWERED));
    add(statusLine, BorderLayout.SOUTH);

    statePanel.registerKeyboardAction((e) -> {
          if (problemStates != null) {
            currentProblemState = Math.min(currentProblemState + 1, problemStates.size() - 1);
            showState(currentProblemState);
          }
        },
        "Next Move",
        KeyStroke.getKeyStroke("RIGHT"),
        JComponent.WHEN_IN_FOCUSED_WINDOW);

    statePanel.registerKeyboardAction((e) -> {
          if (problemStates != null) {
            currentProblemState = Math.max(currentProblemState - 1, 0);
            showState(currentProblemState);
          }
        },
        "Previous Move",
        KeyStroke.getKeyStroke("LEFT"),
        JComponent.WHEN_IN_FOCUSED_WINDOW);

    int fitness = FITNESS.calculateFitness(initialState);
    statusLine.setText(String.format("Fitness: %d", fitness));
  }

  private void showState(int stateIndex) {
    if (problemStates != null) {
      State state = problemStates.get(stateIndex);
      int fitness = FITNESS.calculateFitness(state);

      statusLine.setText(String.format("Move %d out of %d [Fitness: %d]", stateIndex, problemStates.size() - 1, fitness));
      statePanel.setState(state);
      statePanel.repaint();
    }
  }

  private void solveProblem(JButton solveButton) {
    solveButton.setText("Solving...");
    problemStates = null;
    Thread solver = new Thread(() -> {
      List<Future<List<State>>> solvers = new ArrayList<>(nSolvers);
      for (int tNo = 0; tNo < nSolvers; tNo++) {
        solvers.add(executorService.submit(new Solver(iterativeLocalSearch, initialState)));
      }
      for (Future<List<State>> solverFuture : solvers) {
        try {
          List<State> solverStates = solverFuture.get();
          if (problemStates == null || problemStates.size() > solverStates.size()) {
            problemStates = solverStates;
          }
          LOGGER.info("Found solution with {} states", solverStates.size());
        } catch (Exception e) {
          LOGGER.error("Problem finding solution", e);
        }
      }
      currentProblemState = 0;
      statePanel.setState(initialState);
      statusLine.setText(String.format("Solved in %d moves", problemStates.size() - 1));
      solveButton.setText("Solve");
    });
    solver.start();
  }
}

package com.johnnyleitrim.cpmp.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.fitness.BFLowerBoundFitness;
import com.johnnyleitrim.cpmp.ls.IterativeLocalSearch;
import com.johnnyleitrim.cpmp.ls.Move;
import com.johnnyleitrim.cpmp.problem.BFProblemReader;
import com.johnnyleitrim.cpmp.problem.CVProblemReader;
import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.state.State;
import com.johnnyleitrim.cpmp.utils.MoveUtils;

public class CpmpApp {
  private static final Logger LOGGER = LoggerFactory.getLogger(CpmpApp.class);

  private StatePanel statePanel;
  private Problem problem;
  private List<State> problemStates;
  private int currentProblemState = 0;
  private JLabel statusLine;
  private int nSolvers = 6;
  private ExecutorService executorService = Executors.newFixedThreadPool(nSolvers);

  private static final BFLowerBoundFitness FITNESS = new BFLowerBoundFitness();

  public static void main(String[] args) {
    long seed = System.currentTimeMillis();
    Problem.getRandom().setSeed(seed);
    LOGGER.info("Setting solver seed to {}", seed);
    CpmpApp cpmpApp = new CpmpApp();
  }

  public CpmpApp() {
    JFrame frame = new JFrame("Container Pre-Marshalling Problem");
    statePanel = new StatePanel(null);

    frame.setLayout(new BorderLayout());
    frame.add(statePanel, BorderLayout.CENTER);

    JButton solveButton = new JButton("Solve");
    solveButton.addActionListener(e -> solveProblem(problem, (JButton) e.getSource()));
    frame.add(solveButton, BorderLayout.NORTH);

    statusLine = new JLabel("No problem loaded", SwingConstants.CENTER);
    statusLine.setBorder(new BevelBorder(BevelBorder.LOWERED));
    frame.add(statusLine, BorderLayout.SOUTH);

    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");

    JMenuItem bfProblem = new JMenuItem("Open BF Problem");
    fileMenu.add(bfProblem);
    addOpenFileListener(frame, bfProblem, "BF Problem Files", "bay", (lines, filename) -> BFProblemReader.fromLines(filename, lines));

    JMenuItem cvProblem = new JMenuItem("Open CV Problem");
    fileMenu.add(cvProblem);
    addOpenFileListener(frame, cvProblem, "CV Problem Files", "dat", (lines, filename) -> CVProblemReader.fromLines(filename, lines));

    menuBar.add(fileMenu);
    frame.setJMenuBar(menuBar);

    statePanel.registerKeyboardAction((e) -> {
          currentProblemState = Math.min(currentProblemState + 1, problemStates.size() - 1);
          showState(currentProblemState);
        },
        "Next Move",
        KeyStroke.getKeyStroke("RIGHT"),
        JComponent.WHEN_IN_FOCUSED_WINDOW);

    statePanel.registerKeyboardAction((e) -> {
          currentProblemState = Math.max(currentProblemState - 1, 0);
          showState(currentProblemState);
        },
        "Previous Move",
        KeyStroke.getKeyStroke("LEFT"),
        JComponent.WHEN_IN_FOCUSED_WINDOW);

    // Show our window
    frame.setMinimumSize(new Dimension(400, 400));
    frame.setVisible(true);
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

  private void solveProblem(Problem problem, JButton solveButton) {
    if (problem != null) {
      solveButton.setText("Solving...");
      problemStates = null;
      Thread solver = new Thread(() -> {
        List<Future<List<State>>> solvers = new ArrayList<>(nSolvers);
        for (int tNo = 0; tNo < nSolvers; tNo++) {
          solvers.add(executorService.submit(new Solver(problem)));
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
        statePanel.setState(problem.getInitialState());
        statusLine.setText(String.format("Solved in %d moves", problemStates.size() - 1));
        solveButton.setText("Solve");
      });
      solver.start();
    }
  }

  private void addOpenFileListener(JFrame frame, JMenuItem menuItem, String fileDesc, String fileExt, BiFunction<List<String>, String, Problem> problemReader) {
    menuItem.addActionListener(
        enterPress -> {
          JFileChooser chooser = new JFileChooser();
          chooser.setCurrentDirectory(new File("."));
          chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
          chooser.setFileFilter(new FileNameExtensionFilter(fileDesc, fileExt));
          if (chooser.showOpenDialog(frame) == JFileChooser.OPEN_DIALOG) {
            File file = chooser.getSelectedFile();
            try {
              statusLine.setText("Loaded " + file.getName());
              List<String> lines = Files.readAllLines(Paths.get(file.toURI()));
              problem = problemReader.apply(lines, file.getName());
              statePanel.setState(problem.getInitialState());
            } catch (Exception e) {
              JOptionPane.showMessageDialog(frame, "Cannot open file: " + e.getMessage());
            }
          }
        });
  }

  private static class Solver implements Callable<List<State>> {

    private final Problem problem;

    private Solver(Problem problem) {
      this.problem = problem;
    }

    @Override
    public List<State> call() {
      IterativeLocalSearch cpmpSolver = new IterativeLocalSearch(problem.getInitialState(), 1, 2, new BFLowerBoundFitness(), Duration.ofMinutes(1));
      List<Move> moves = cpmpSolver.search(IterativeLocalSearch.Perturbation.LOWEST_MISOVERLAID_STACK_CLEARING, 1);
      MutableState state = problem.getInitialState().copy();
      List<State> problemStates = new ArrayList<>(moves.size() + 1);
      problemStates.add(state.copy());
      for (int i = 0; i < moves.size(); i++) {
        MoveUtils.applyMove(state, moves.get(i));
        problemStates.add(state.copy());
      }
      return problemStates;
    }
  }
}

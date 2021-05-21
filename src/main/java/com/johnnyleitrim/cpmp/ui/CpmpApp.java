package com.johnnyleitrim.cpmp.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.fitness.BFLowerBoundFitness;
import com.johnnyleitrim.cpmp.problem.BFProblemReader;
import com.johnnyleitrim.cpmp.problem.CVProblemReader;
import com.johnnyleitrim.cpmp.problem.EMMProblemReader;
import com.johnnyleitrim.cpmp.state.State;

public class CpmpApp {
  private static final Logger LOGGER = LoggerFactory.getLogger(CpmpApp.class);

  private final StatePanel statePanel;
  private Solver.SolverParams solverParams = new Solver.SolverParams();
  private List<State> problemStates;
  private int currentProblemState = 0;
  private final JLabel statusLine;
  private final int nSolvers = 6;
  private final ExecutorService executorService = Executors.newFixedThreadPool(nSolvers);

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
    solveButton.addActionListener(e -> solveProblem((JButton) e.getSource()));

    JPanel topSection = new JPanel(new GridLayout(3, 1));
    topSection.add(solveButton);

    List<JToggleButton> featureToggleButtons = FeatureToggleButtons.createToggleButtons();
    JPanel featuresSection = new JPanel(new GridLayout(1, featureToggleButtons.size()));
    for (JToggleButton button : featureToggleButtons) {
      featuresSection.add(button);
    }
    topSection.add(featuresSection);

    List<JComboBox> parameterBoxes = new SolverParamComboBoxes(solverParams).getComboBoxes();
    JPanel parameterSection = new JPanel(new GridLayout(1, parameterBoxes.size()));
    for (JComboBox box : parameterBoxes) {
      parameterSection.add(box);
    }
    topSection.add(parameterSection);

    frame.add(topSection, BorderLayout.NORTH);

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

    JMenuItem emmProblem = new JMenuItem("Open EMM Problem");
    fileMenu.add(emmProblem);
    addOpenFileListener(frame, emmProblem, "EMM Problem Files", "bay", (lines, filename) -> EMMProblemReader.fromLines(filename, lines));

    menuBar.add(fileMenu);
    frame.setJMenuBar(menuBar);

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

  private void solveProblem(JButton solveButton) {
    if (solverParams.getProblem() != null) {
      solveButton.setText("Solving...");
      problemStates = null;
      Thread solver = new Thread(() -> {
        List<Future<List<State>>> solvers = new ArrayList<>(nSolvers);
        for (int tNo = 0; tNo < nSolvers; tNo++) {
          solvers.add(executorService.submit(new Solver(solverParams)));
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
        statePanel.setState(solverParams.getProblem().getInitialState());
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
              solverParams.setProblem(problemReader.apply(lines, file.getName()));
              statePanel.setState(solverParams.getProblem().getInitialState());
            } catch (Exception e) {
              LOGGER.error("Error opening file {}", file.getName(), e);
              JOptionPane.showMessageDialog(frame, "Cannot open file: " + e.getMessage());
            }
          }
        });
  }
}

package com.johnnyleitrim.cpmp.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.fitness.BFLowerBoundFitness;
import com.johnnyleitrim.cpmp.ls.IterativeLocalSearch;
import com.johnnyleitrim.cpmp.ls.Move;
import com.johnnyleitrim.cpmp.problem.BFProblemReader;
import com.johnnyleitrim.cpmp.problem.CVProblemReader;
import com.johnnyleitrim.cpmp.state.MutableState;
import com.johnnyleitrim.cpmp.utils.MoveUtils;

public class CpmpApp {

  private StatePanel statePanel;
  private Problem problem;

  public static void main(String[] args) {
    CpmpApp cpmpApp = new CpmpApp();
  }

  public CpmpApp() {
    JFrame frame = new JFrame("Container Pre-Marshalling Problem");
    statePanel = new StatePanel(null);

    frame.setLayout(new BorderLayout());
    frame.add(statePanel, BorderLayout.CENTER);

    JButton solveButton = new JButton("Solve");
    solveButton.addActionListener(e -> solveProblem(problem));
    frame.add(solveButton, BorderLayout.NORTH);

    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");

    JMenuItem bfProblem = new JMenuItem("Open BF Problem");
    fileMenu.add(bfProblem);
    addOpenFileListener(frame, bfProblem, lines -> BFProblemReader.fromLines("BF Problem", lines));

    JMenuItem cvProblem = new JMenuItem("Open CV Problem");
    fileMenu.add(cvProblem);
    addOpenFileListener(frame, cvProblem, lines -> CVProblemReader.fromLines("CV Problem", lines));

    menuBar.add(fileMenu);
    frame.setJMenuBar(menuBar);

    // Show our window
    frame.setMinimumSize(new Dimension(400, 400));
    frame.setVisible(true);
  }

  private void solveProblem(Problem problem) {
    if (problem != null) {
      Thread t = new Thread(() -> {
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
      });
      t.start();
    }
  }

  private void addOpenFileListener(JFrame frame, JMenuItem menuItem, Function<List<String>, Problem> problemReader) {
    menuItem.addActionListener(
        enterPress -> {
          JFileChooser chooser = new JFileChooser();
          chooser.setCurrentDirectory(new File("."));
          chooser.setSelectedFile(new File(""));
          chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
          if (chooser.showOpenDialog(frame) == JFileChooser.OPEN_DIALOG) {
            File file = chooser.getSelectedFile();
            try {
              List<String> lines = Files.readAllLines(Paths.get(file.toURI()));
              problem = problemReader.apply(lines);
              statePanel.setState(problem.getInitialState());
            } catch (Exception e) {
              JOptionPane.showMessageDialog(frame, "Cannot open file: " + e.getMessage());
            }
          }
        });
  }
}

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
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

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
  private JLabel statusLine;

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

    statusLine = new JLabel("No problem loaded", SwingConstants.CENTER);
    statusLine.setBorder(new BevelBorder(BevelBorder.LOWERED));
    frame.add(statusLine, BorderLayout.SOUTH);

    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");

    JMenuItem bfProblem = new JMenuItem("Open BF Problem");
    fileMenu.add(bfProblem);
    addOpenFileListener(frame, bfProblem, "BF Problem Files", "bay", lines -> BFProblemReader.fromLines("BF Problem", lines));

    JMenuItem cvProblem = new JMenuItem("Open CV Problem");
    fileMenu.add(cvProblem);
    addOpenFileListener(frame, cvProblem, "CV Problem Files", "dat", lines -> CVProblemReader.fromLines("CV Problem", lines));

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
        for (int i = 0; i < moves.size(); i++) {
          MoveUtils.applyMove(state, moves.get(i));
          statusLine.setText(String.format("Move %d out of %d", i + 1, moves.size()));
          EventQueue.invokeLater(() -> {
            statePanel.setState(state);
          });
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

  private void addOpenFileListener(JFrame frame, JMenuItem menuItem, String fileDesc, String fileExt, Function<List<String>, Problem> problemReader) {
    menuItem.addActionListener(
        enterPress -> {
          JFileChooser chooser = new JFileChooser();
          chooser.setCurrentDirectory(new File("."));
          chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
          chooser.setFileFilter(new FileNameExtensionFilter(fileDesc, fileExt));
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

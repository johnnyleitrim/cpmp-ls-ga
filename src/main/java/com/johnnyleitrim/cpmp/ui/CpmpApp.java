package com.johnnyleitrim.cpmp.ui;

import java.awt.Dimension;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.BiFunction;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.Random;
import com.johnnyleitrim.cpmp.ls.IterativeLocalSearchStrategyConfig;
import com.johnnyleitrim.cpmp.problem.BFProblemReader;
import com.johnnyleitrim.cpmp.problem.CVProblemReader;
import com.johnnyleitrim.cpmp.problem.EMMProblemReader;
import com.johnnyleitrim.cpmp.ui.solver.SolverPanel;

public class CpmpApp {
  private static final Logger LOGGER = LoggerFactory.getLogger(CpmpApp.class);

  private final IterativeLocalSearchStrategyConfig strategyConfig = new IterativeLocalSearchStrategyConfig();
  private final JTabbedPane tabbedPane;

  public static void main(String[] args) {
    long seed = System.currentTimeMillis();
    Random.setRandomSeed(seed);
    LOGGER.info("Setting solver seed to {}", seed);
    CpmpApp cpmpApp = new CpmpApp();
  }

  public CpmpApp() {
    JFrame frame = new JFrame("Container Pre-Marshalling Problem");
    tabbedPane = new JTabbedPane();
    frame.add(tabbedPane);

    tabbedPane.add("ILS Settings", new StrategyConfigPanel(strategyConfig));

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

    // Show our window
    frame.setMinimumSize(new Dimension(400, 400));
    frame.setVisible(true);
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
              List<String> lines = Files.readAllLines(Paths.get(file.toURI()));
              Problem problem = problemReader.apply(lines, file.getName());
              SolverPanel solverPanel = new SolverPanel(problem.getInitialState(), strategyConfig);
              tabbedPane.add(problem.getName(), solverPanel);
              tabbedPane.setSelectedComponent(solverPanel);
            } catch (Exception e) {
              LOGGER.error("Error opening file {}", file.getName(), e);
              JOptionPane.showMessageDialog(frame, "Cannot open file: " + e.getMessage());
            }
          }
        });
  }
}

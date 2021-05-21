package com.johnnyleitrim.cpmp.ui;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JComboBox;

import com.johnnyleitrim.cpmp.ls.IterativeLocalSearch;

public class SolverParamComboBoxes {

  private final Solver.SolverParams solverParams;

  public SolverParamComboBoxes(Solver.SolverParams solverParams) {
    this.solverParams = solverParams;
  }

  public List<JComboBox> getComboBoxes() {
    JComboBox perturbationBox = new JComboBox(IterativeLocalSearch.Perturbation.values());
    perturbationBox.addActionListener(e -> {
      JComboBox cb = (JComboBox) e.getSource();
      IterativeLocalSearch.Perturbation perturbation = (IterativeLocalSearch.Perturbation) cb.getSelectedItem();
      solverParams.setPerturbation(perturbation);
    });
    JComboBox<Integer> minMovesBox = createMovesComboBox(solverParams.getMinSearchMoves(), solverParams::setMinSearchMoves);
    JComboBox<Integer> maxMovesBox = createMovesComboBox(solverParams.getMaxSearchMoves(), solverParams::setMaxSearchMoves);
    return Arrays.asList(perturbationBox, minMovesBox, maxMovesBox);
  }

  private JComboBox<Integer> createMovesComboBox(int selected, Consumer<Integer> paramSetter) {
    JComboBox<Integer> movesBox = new JComboBox<>();
    movesBox.addItem(1);
    movesBox.addItem(2);
    movesBox.setSelectedItem(selected);
    movesBox.addActionListener(e -> {
      JComboBox cb = (JComboBox) e.getSource();
      paramSetter.accept((Integer) cb.getSelectedItem());
    });
    return movesBox;
  }
}

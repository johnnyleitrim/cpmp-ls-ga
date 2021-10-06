package com.johnnyleitrim.cpmp.ui;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.johnnyleitrim.cpmp.ls.IterativeLocalSearchStrategyConfig;
import com.johnnyleitrim.cpmp.strategy.BestNeighbourTieBreakingStrategies;
import com.johnnyleitrim.cpmp.strategy.ClearStackSelectionStrategies;
import com.johnnyleitrim.cpmp.strategy.FitnessStrategies;
import com.johnnyleitrim.cpmp.strategy.StackClearingStrategies;
import com.johnnyleitrim.cpmp.strategy.StackFillingStrategies;
import com.johnnyleitrim.cpmp.strategy.Strategy;

public class StrategyConfigPanel extends JPanel {

  private final IterativeLocalSearchStrategyConfig strategyConfig;

  public StrategyConfigPanel(IterativeLocalSearchStrategyConfig strategyConfig) {
    this.strategyConfig = strategyConfig;

    setLayout(new GridLayout(2, 1, 10, 10));
    add(addLocalSearchConfigPanel());
    add(addPerturbationConfigPanel());
  }

  private JPanel addLocalSearchConfigPanel() {
    JPanel localSearchConfig = new JPanel();
    localSearchConfig.setBorder(BorderFactory.createTitledBorder("Local Search"));
    localSearchConfig.setLayout(new GridLayout(4, 2, 10, 10));

    addIntegerField(localSearchConfig, "Minimum search moves:", strategyConfig::getMinSearchMoves, strategyConfig::setMinSearchMoves);
    addIntegerField(localSearchConfig, "Maximum search moves:", strategyConfig::getMaxSearchMoves, strategyConfig::setMaxSearchMoves);
    addStrategyField(localSearchConfig, "Neighbour Tie Breaking Strategy:", strategyConfig::getBestNeighbourTieBreakingStrategy, strategyConfig::setBestNeighbourTieBreakingStrategy, BestNeighbourTieBreakingStrategies.ALL);
    addStrategyField(localSearchConfig, "Fitness Strategy:", strategyConfig::getFitnessStrategy, strategyConfig::setFitnessStrategy, List.of(
        FitnessStrategies.ORIGINAL,
        FitnessStrategies.NEW
    ));

    return localSearchConfig;
  }

  private JPanel addPerturbationConfigPanel() {
    JPanel perturbationConfig = new JPanel();
    perturbationConfig.setBorder(BorderFactory.createTitledBorder("Perturbation"));
    perturbationConfig.setLayout(new GridLayout(4, 2, 10, 10));

    addStrategyField(perturbationConfig, "Clear Stack Selection Strategy:", strategyConfig::getClearStackSelectionStrategy, strategyConfig::setClearStackSelectionStrategy, ClearStackSelectionStrategies.ALL);
    addStrategyField(perturbationConfig, "Stack Clearing Strategy:", strategyConfig::getClearStackStrategy, strategyConfig::setClearStackStrategy, StackClearingStrategies.ALL);
    addBooleanField(perturbationConfig, "Fill Stack after Clearing:", strategyConfig::isFillStackAfterClearing, strategyConfig::setFillStackAfterClearing);
    addStrategyField(perturbationConfig, "Stack Filling Strategy:", strategyConfig::getFillStackStrategy, strategyConfig::setFillStackStrategy, StackFillingStrategies.ALL);

    return perturbationConfig;
  }

  private void addIntegerField(JPanel parent, String fieldName, Supplier<Integer> getter, Consumer<Integer> setter) {
    JLabel fieldLabel = new JLabel(fieldName, SwingConstants.RIGHT);
    JComboBox<Integer> fieldComboBox = new JComboBox<>(new Integer[]{1, 2, 3});
    fieldLabel.setLabelFor(fieldComboBox);

    fieldComboBox.setSelectedItem(getter.get());
    fieldComboBox.addActionListener(e -> {
      JComboBox<Integer> cb = (JComboBox<Integer>) e.getSource();
      setter.accept((Integer) cb.getSelectedItem());
    });
    parent.add(fieldLabel);
    parent.add(fieldComboBox);
  }

  private void addBooleanField(JPanel parent, String fieldName, Supplier<Boolean> getter, Consumer<Boolean> setter) {
    JLabel fieldLabel = new JLabel(fieldName, SwingConstants.RIGHT);
    JCheckBox enabledButton = new JCheckBox("Enabled", getter.get());
    fieldLabel.setLabelFor(enabledButton);

    enabledButton.addActionListener(e -> setter.accept(enabledButton.isSelected()));

    parent.add(fieldLabel);
    parent.add(enabledButton);
  }

  private <T extends Strategy> void addStrategyField(JPanel parent, String fieldName, Supplier<Strategy> getter, Consumer<T> setter, List<T> strategies) {
    JLabel fieldLabel = new JLabel(fieldName, SwingConstants.RIGHT);
    JComboBox<T> fieldComboBox = new JComboBox<>(new Vector<>(strategies));
    fieldLabel.setLabelFor(fieldComboBox);

    fieldComboBox.setSelectedItem(getter.get());
    fieldComboBox.addActionListener(e -> {
      JComboBox<T> cb = (JComboBox<T>) e.getSource();
      setter.accept(cb.getItemAt(cb.getSelectedIndex()));
    });
    parent.add(fieldLabel);
    parent.add(fieldComboBox);
  }
}

package com.johnnyleitrim.cpmp.ui;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.johnnyleitrim.cpmp.ls.IterativeLocalSearchStrategyConfig;
import com.johnnyleitrim.cpmp.strategy.BestNeighbourTieBreakingStrategies;
import com.johnnyleitrim.cpmp.strategy.BestNeighbourTieBreakingStrategy;
import com.johnnyleitrim.cpmp.strategy.ClearStackSelectionStrategies;
import com.johnnyleitrim.cpmp.strategy.ClearStackSelectionStrategy;
import com.johnnyleitrim.cpmp.strategy.FitnessStrategies;
import com.johnnyleitrim.cpmp.strategy.FitnessStrategy;
import com.johnnyleitrim.cpmp.strategy.StackClearingStrategies;
import com.johnnyleitrim.cpmp.strategy.StackClearingStrategy;
import com.johnnyleitrim.cpmp.strategy.StackFillingStrategies;
import com.johnnyleitrim.cpmp.strategy.StackFillingStrategy;
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

    addStrategyField(localSearchConfig, "Neighbour Tie Breaking Strategy:", strategyConfig::getBestNeighbourTieBreakingStrategy, strategyConfig::setBestNeighbourTieBreakingStrategy, new BestNeighbourTieBreakingStrategy[]{
        BestNeighbourTieBreakingStrategies.RANDOM,
        BestNeighbourTieBreakingStrategies.HIGHEST_LAST_CONTAINER,
        BestNeighbourTieBreakingStrategies.SMALLEST_CONTAINER_DIFFERENCE,
    });

    addStrategyField(localSearchConfig, "Fitness Strategy:", strategyConfig::getFitnessStrategy, strategyConfig::setFitnessStrategy, new FitnessStrategy[]{
        FitnessStrategies.ORIGINAL,
        FitnessStrategies.NEW
    });

    return localSearchConfig;
  }

  private JPanel addPerturbationConfigPanel() {
    JPanel perturbationConfig = new JPanel();
    perturbationConfig.setBorder(BorderFactory.createTitledBorder("Perturbation"));
    perturbationConfig.setLayout(new GridLayout(4, 2, 10, 10));

    addStrategyField(perturbationConfig, "Clear Stack Selection Strategy:", strategyConfig::getClearStackSelectionStrategy, strategyConfig::setClearStackSelectionStrategy, new ClearStackSelectionStrategy[]{
        ClearStackSelectionStrategies.RANDOM_STACK,
        ClearStackSelectionStrategies.LOWEST_STACK,
        ClearStackSelectionStrategies.LOWEST_MIS_OVERLAID_STACK,
    });

    addStrategyField(perturbationConfig, "Stack Clearing Strategy:", strategyConfig::getClearStackStrategy, strategyConfig::setClearStackStrategy, new StackClearingStrategy[]{
        StackClearingStrategies.RANDOM,
        StackClearingStrategies.CLEAR_TO_BEST,
    });

    addBooleanField(perturbationConfig, "Fill Stack after Clearing:", strategyConfig::isFillStackAfterClearing, strategyConfig::setFillStackAfterClearing);

    addStrategyField(perturbationConfig, "Stack Filling Strategy:", strategyConfig::getFillStackStrategy, strategyConfig::setFillStackStrategy, new StackFillingStrategy[]{
        StackFillingStrategies.LARGEST_CONTAINER,
        StackFillingStrategies.LARGEST_MIS_OVERLAID_CONTAINER,
    });

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

  private <T extends Strategy> void addStrategyField(JPanel parent, String fieldName, Supplier<Strategy> getter, Consumer<T> setter, T[] strategies) {
    JLabel fieldLabel = new JLabel(fieldName, SwingConstants.RIGHT);
    JComboBox<T> fieldComboBox = new JComboBox<>(strategies);
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

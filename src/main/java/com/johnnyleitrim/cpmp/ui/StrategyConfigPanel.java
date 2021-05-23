package com.johnnyleitrim.cpmp.ui;

import javax.swing.JPanel;

import com.johnnyleitrim.cpmp.ls.IterativeLocalSearchStrategyConfig;

public class StrategyConfigPanel extends JPanel {

  private final IterativeLocalSearchStrategyConfig strategyConfig;

  public StrategyConfigPanel(IterativeLocalSearchStrategyConfig strategyConfig) {
    this.strategyConfig = strategyConfig;
  }
}

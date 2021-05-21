package com.johnnyleitrim.cpmp.ls;

import java.beans.BeanProperty;

public class Features {

  public static final Features instance = new Features();

  private boolean improvedTieBreakingEnabled = Boolean.getBoolean("improvedTieBreaking");
  private boolean clearToBestStackEnabled = Boolean.getBoolean("clearToBestStack");
  private boolean dontFillFromGoodStackEnabled = Boolean.getBoolean("dontFillFromGoodStack");
  private boolean fillStackEnabled = Boolean.getBoolean("fillStack");

  @BeanProperty(description = "Improved Tie Breaking")
  public boolean isImprovedTieBreakingEnabled() {
    return improvedTieBreakingEnabled;
  }

  public void setImprovedTieBreakingEnabled(boolean improvedTieBreakingEnabled) {
    this.improvedTieBreakingEnabled = improvedTieBreakingEnabled;
  }

  @BeanProperty(description = "Clear to Best Stack")
  public boolean isClearToBestStackEnabled() {
    return clearToBestStackEnabled;
  }

  public void setClearToBestStackEnabled(boolean clearToBestStackEnabled) {
    this.clearToBestStackEnabled = clearToBestStackEnabled;
  }

  @BeanProperty(description = "Dont Fill from Good Stacks")
  public boolean isDontFillFromGoodStackEnabled() {
    return dontFillFromGoodStackEnabled;
  }

  public void setDontFillFromGoodStackEnabled(boolean dontFillFromGoodStackEnabled) {
    this.dontFillFromGoodStackEnabled = dontFillFromGoodStackEnabled;
  }

  public boolean isFillStackEnabled() {
    return fillStackEnabled;
  }

  @BeanProperty(description = "Fill Stack after Clearing")
  public void setFillStackEnabled(boolean fillStackEnabled) {
    this.fillStackEnabled = fillStackEnabled;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("improvedTieBreakingEnabled: ");
    str.append(improvedTieBreakingEnabled);
    str.append(", clearToBestStackEnabled: ");
    str.append(clearToBestStackEnabled);
    str.append(", dontFillFromGoodStackEnabled: ");
    str.append(dontFillFromGoodStackEnabled);
    str.append(", fillStackEnabled: ");
    str.append(fillStackEnabled);
    return str.toString();
  }
}

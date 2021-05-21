package com.johnnyleitrim.cpmp.ui;

import java.beans.PropertyDescriptor;

import javax.swing.JToggleButton;

import com.johnnyleitrim.cpmp.ls.Features;

public class FeatureToggleButtonModel extends JToggleButton.ToggleButtonModel {

  private final PropertyDescriptor featureDescriptor;

  public FeatureToggleButtonModel(PropertyDescriptor featureDescriptor) {
    this.featureDescriptor = featureDescriptor;
  }

  @Override
  public boolean isSelected() {
    try {
      return ((Boolean) featureDescriptor.getReadMethod().invoke(Features.instance)).booleanValue();
    } catch (Exception e) {
      throw new RuntimeException("Problem getting value for " + featureDescriptor.getDisplayName(), e);
    }
  }

  @Override
  public void setSelected(boolean enabled) {
    try {
      featureDescriptor.getWriteMethod().invoke(Features.instance, enabled);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

package com.johnnyleitrim.cpmp.ui;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JToggleButton;

import com.johnnyleitrim.cpmp.ls.Features;

public class FeatureToggleButtons {

  public static List<JToggleButton> createToggleButtons() {
    List<JToggleButton> toggleButtons = new ArrayList<>();
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(Features.class);
      for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
        if (propertyDescriptor.getDisplayName().endsWith("Enabled")) {
          JToggleButton button = new JToggleButton(propertyDescriptor.getShortDescription());
          FeatureToggleButtonModel model = new FeatureToggleButtonModel(propertyDescriptor);
          button.setModel(model);
          toggleButtons.add(button);
        }
      }
      return toggleButtons;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

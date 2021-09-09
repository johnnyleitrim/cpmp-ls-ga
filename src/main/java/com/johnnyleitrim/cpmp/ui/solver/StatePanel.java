package com.johnnyleitrim.cpmp.ui.solver;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.johnnyleitrim.cpmp.state.State;

public class StatePanel extends JPanel {

  private State state;

  private BufferedImage image;

  public StatePanel(State state) {
    this.state = state;
  }

  @Override
  public void setBounds(int x, int y, int width, int height) {
    super.setBounds(x, y, width, height);
    image = createBufferedImage(width, height);
  }

  public void setState(State state) {
    this.state = state;
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    if (image != null && state != null) {
      DrawUtils.drawState(state, image.getGraphics(), getWidth(), getHeight());
      g.drawImage(image, 0, 0, null);
    } else {
      super.paintComponent(g);
    }
  }

  private BufferedImage createBufferedImage(int width, int height) {
    if (width > 0 && height > 0) {
      return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }
    return null;
  }
}

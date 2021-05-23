package com.johnnyleitrim.cpmp.ui.solver;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.state.State;

public class DrawUtils {

  private static final Color SLOT_SPACES = new Color(240, 240, 240);

  private static final Color MIS_OVERLAID_BG = new Color(220, 220, 220);

  public static void drawState(State state, Graphics g, int screenWidth, int screenHeight) {

    int horizontalMargin = calculateMargin(screenWidth, state.getNumberOfStacks());
    int verticalMargin = calculateMargin(screenHeight, state.getNumberOfTiers());

    int bottomY = screenHeight - verticalMargin;
    int topY = verticalMargin;
    int leftX = horizontalMargin;
    int rightX = screenWidth - horizontalMargin;

    Graphics2D graphics = (Graphics2D) g;

    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, screenWidth, screenHeight);

    // Containing Square
    graphics.setColor(SLOT_SPACES);

    BasicStroke dashed = new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL,
        0, new float[]{2, 4}, 0);
    graphics.setStroke(dashed);

    graphics.drawLine(leftX, bottomY, rightX, bottomY);
    graphics.drawLine(leftX, topY, rightX, topY);
    graphics.drawLine(leftX, bottomY, leftX, topY);
    graphics.drawLine(rightX, bottomY, rightX, topY);

    int containerWidth = (rightX - leftX) / state.getNumberOfStacks();
    int containerHeight = (bottomY - topY) / state.getNumberOfTiers();

    for (int s = 1; s < state.getNumberOfStacks(); s++) {
      int lineX = leftX + (s * containerWidth);
      graphics.drawLine(lineX, bottomY, lineX, topY);
    }

    for (int t = 1; t < state.getNumberOfTiers(); t++) {
      int lineY = bottomY - (t * containerHeight);
      graphics.drawLine(leftX, lineY, rightX, lineY);
    }

    Font font = new Font(Font.SANS_SERIF, Font.BOLD, Math.min(containerWidth / 2, containerHeight / 2));
    graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
    graphics.setFont(font);

    for (int s = 0; s < state.getNumberOfStacks(); s++) {
      int smallestGroup = Integer.MAX_VALUE;
      boolean misOverlaid = false;
      for (int t = 0; t < state.getNumberOfTiers(); t++) {
        BasicStroke solid = new BasicStroke(2);
        graphics.setStroke(solid);

        int group = state.getGroup(s, t);
        if (group != Problem.EMPTY) {
          if (group > smallestGroup) {
            misOverlaid = true;
          }
          drawContainer(graphics, leftX, bottomY, containerWidth, containerHeight, s, t, group, misOverlaid);
          smallestGroup = Math.min(smallestGroup, group);
        }
      }
    }
  }

  private static void drawContainer(Graphics2D graphics, int leftX, int bottomY, int containerWidth, int containerHeight, int stack, int tier, int group, boolean misOverlaid) {
    int containerLeftX = leftX + (stack * containerWidth);
    int containerBottomY = bottomY - (tier * containerHeight);
    int containerTopY = containerBottomY - containerHeight;

    if (misOverlaid) {
      graphics.setColor(MIS_OVERLAID_BG);
      graphics.fillRect(containerLeftX, containerTopY, containerWidth, containerHeight);
    }

    graphics.setColor(Color.BLACK);

    FontMetrics metrics = graphics.getFontMetrics();
    int textY = ((containerHeight - metrics.getHeight()) / 2) + metrics.getAscent();
    int textX = ((containerWidth - metrics.stringWidth(String.valueOf(group))) / 2);
    graphics.drawString(String.valueOf(group), containerLeftX + textX, containerTopY + textY);

    graphics.drawRect(containerLeftX, containerTopY, containerWidth, containerHeight);
  }

  private static int calculateMargin(int screenDimension, int nContainers) {
    // We want to enforce a minimum margin
    int potentialScreenDimension = screenDimension - 20;
    int containerDimension = potentialScreenDimension / nContainers;
    return (screenDimension - (containerDimension * nContainers)) / 2;
  }
}

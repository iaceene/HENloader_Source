package org.bdj;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

public class Screen
  extends Container {
  private static final long serialVersionUID = 4761178503523947426L;
  private ArrayList messages;
  private Font font;
  public int top = 40;
  
  public Screen(ArrayList paramArrayList) {
    this.messages = paramArrayList;
    this.font = new Font(null, 0, 36);
  }
  
  public void paint(Graphics paramGraphics) {
    paramGraphics.setColor(new Color(100, 110, 160));
    paramGraphics.fillRect(0, 0, getWidth(), getHeight());
    paramGraphics.setFont(this.font);
    paramGraphics.setColor(new Color(255, 255, 255));
    for (byte b = 0; b < this.messages.size(); b++) {
      
      String str = this.messages.get(b);
      int i = paramGraphics.getFontMetrics().stringWidth(str);
      paramGraphics.drawString(str, 0, this.top + b * 40);
    } 
  }
}

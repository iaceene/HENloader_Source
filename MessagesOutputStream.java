package org.bdj;

import java.io.OutputStream;
import java.util.ArrayList;
import org.havi.ui.HScene;

public class MessagesOutputStream
  extends OutputStream {
  ArrayList messages;
  HScene scene;
  String cur;
  
  public MessagesOutputStream(ArrayList paramArrayList, HScene paramHScene) {
    this.messages = paramArrayList;
    this.scene = paramHScene;
    this.cur = "";
    this.messages.add(this.cur);
  }
  
  public synchronized void write(int paramInt) {
    if (paramInt == 10) {
      
      this.scene.repaint();
      this.cur = "";
      this.messages.add(this.cur);
    }
    else if (paramInt != 179) {
      
      this.cur += (char)paramInt;
      this.messages.set(this.messages.size() - 1, this.cur);
    } 
  }
}

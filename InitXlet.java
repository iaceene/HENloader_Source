package org.bdj;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.tv.xlet.XletContext;
import org.bdj.external.KernelOffset;
import org.bdj.external.Poops;
import org.dvb.event.UserEvent;
import org.dvb.event.UserEventRepository;
import org.havi.ui.HScene;

public class InitXlet implements Xlet, UserEventListener {
  public static final int BUTTON_X = 10;
  public static final int BUTTON_O = 19;
  public static final int BUTTON_U = 38;
  public static final int BUTTON_D = 40;
  private static InitXlet instance;
  private EventQueue eq;
  private HScene scene;
  private Screen gui;
  private XletContext context;
  private static PrintStream console;
  
  public static class EventQueue {
    int cnt = 0; private LinkedList l;
    
    EventQueue() {
      this.l = new LinkedList();
    }
    
    public synchronized void put(Object param1Object) {
      this.l.addLast(param1Object);
      this.cnt++;
    }
    
    public synchronized Object get() {
      if (this.cnt == 0)
        return null; 
      Object object = this.l.getFirst();
      this.l.removeFirst();
      this.cnt--;
      return object;
    }
  }




  
  private static final ArrayList messages = new ArrayList();

  
  public void initXlet(XletContext paramXletContext) {
    try {
      DisableSecurityManagerAction.execute();
    } catch (Exception exception) {}
    
    instance = this;
    this.context = paramXletContext;
    this.eq = new EventQueue();
    this.scene = HSceneFactory.getInstance().getDefaultHScene();
    
    try {
      this.gui = new Screen(messages);
      this.gui.setSize(1920, 1080);
      this.scene.add(this.gui, "Center");
      UserEventRepository userEventRepository = new UserEventRepository("input");
      userEventRepository.addKey(10);
      userEventRepository.addKey(19);
      userEventRepository.addKey(38);
      userEventRepository.addKey(40);
      EventManager.getInstance().addUserEventListener(this, userEventRepository);
      (new Thread(this)
        {
          private final InitXlet this$0;
          
          public void run() {
            try {
              this.this$0.scene.repaint();
              InitXlet.console = new PrintStream(new MessagesOutputStream(InitXlet.messages, this.this$0.scene));


              
              InitXlet.console.println("Hen Loader LP v1.0, based on:");
              InitXlet.console.println("- GoldHEN 2.4b18.7 by SiSTR0");
              InitXlet.console.println("- poops code by theflow0");
              InitXlet.console.println("- lapse code by Gezine");
              InitXlet.console.println("- BDJ build environment by kimariin");
              InitXlet.console.println("- java console by sleirsgoevy");
              InitXlet.console.println("");
              System.gc();
              if (System.getSecurityManager() != null) {
                InitXlet.console.println("Priviledge escalation failure, unsupported firmware?");
              } else {
                Kernel.initializeKernelOffsets();
                String str = Helper.getCurrentFirmwareVersion();
                InitXlet.console.println("Firmware: " + str);
                if (!KernelOffset.hasPS4Offsets()) {
                  
                  InitXlet.console.println("Unsupported Firmware");
                } else {
                  
                  while (true) {
                    byte b = 0; int i = 0;
                    boolean bool = (!str.equals("12.50") && !str.equals("12.52")) ? true : false;
                    InitXlet.console.println("\nSelect the mode to run:");
                    if (bool) {
                      InitXlet.console.println("* X = Lapse");
                      InitXlet.console.println("* O = Poops");
                    } else {
                      InitXlet.console.println("* X = Poops");
                    } 
                    
                    while ((i != 19 || !bool) && i != 10)
                    {
                      i = InitXlet.pollInput();
                    }
                    if (i == 10 && bool) {
                      
                      int k = Lapse.main(InitXlet.console);
                      if (k == 0) {
                        
                        InitXlet.console.println("Success");
                        break;
                      } 
                      if (k <= -6 || b++ >= 3) {
                        
                        InitXlet.console.println("Fatal fail(" + k + "), please REBOOT PS4");
                        break;
                      } 
                      InitXlet.console.println("Failed (" + k + "), but you can try again");
                      continue;
                    } 
                    int j = Poops.main(InitXlet.console);
                    if (j == 0) {
                      
                      InitXlet.console.println("Success");
                      break;
                    } 
                    InitXlet.console.println("Fatal fail(" + j + "), please REBOOT PS4");


                    
                    break;
                  } 
                } 
              } 
            } catch (Throwable throwable) {
              
              this.this$0.scene.repaint();
            } 
          }
        }).start();
    }
    catch (Throwable throwable) {
      
      printStackTrace(throwable);
    } 
    this.scene.validate();
  }
  
  public void startXlet() {
    this.gui.setVisible(true);
    this.scene.setVisible(true);
    this.gui.requestFocus();
  }
  
  public void pauseXlet() {
    this.gui.setVisible(false);
  }
  
  public void destroyXlet(boolean paramBoolean) {
    this.scene.remove(this.gui);
    this.scene = null;
  }
  
  private void printStackTrace(Throwable paramThrowable) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    paramThrowable.printStackTrace(printWriter);
    if (console != null)
      console.print(stringWriter.toString()); 
  }
  
  public void userEventReceived(UserEvent paramUserEvent) {
    boolean bool = false;
    if (paramUserEvent.getType() == 401) {
      
      bool = true;
      if (paramUserEvent.getCode() == 38) {
        this.gui.top += 270;
      } else if (paramUserEvent.getCode() == 40) {
        this.gui.top -= 270;
      } else {
        bool = false;
      }  this.scene.repaint();
    } 
    if (bool)
      return; 
    if (paramUserEvent.getType() == 401)
      this.eq.put(new Integer(paramUserEvent.getCode())); 
  }
  
  public static void repaint() {
    instance.scene.repaint();
  }
  
  public static int pollInput() {
    Object object = instance.eq.get();
    if (object == null)
      return 0; 
    return ((Integer)object).intValue();
  }
}

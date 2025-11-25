package org.bdj.external;

import java.util.Vector;
import org.bdj.api.API;
import org.bdj.api.Buffer;









public class Helper
{
  public static final int AF_INET = 2;
  public static final int AF_INET6 = 28;
  public static final int AF_UNIX = 1;
  public static final int SOCK_DGRAM = 2;
  public static final int SOCK_STREAM = 1;
  public static final int IPPROTO_UDP = 17;
  public static final int IPPROTO_TCP = 6;
  public static final int IPPROTO_IPV6 = 41;
  public static final int SOL_SOCKET = 65535;
  public static final int SO_REUSEADDR = 4;
  public static final int SO_LINGER = 128;
  public static final int TCP_INFO = 32;
  public static final int TCPS_ESTABLISHED = 4;
  public static final int IPV6_RTHDR = 51;
  public static final int IPV6_TCLASS = 61;
  public static final int IPV6_2292PKTOPTIONS = 25;
  public static final int IPV6_PKTINFO = 46;
  public static final int IPV6_NEXTHOP = 48;
  public static final int AIO_CMD_READ = 1;
  public static final int AIO_CMD_WRITE = 2;
  public static final int AIO_CMD_FLAG_MULTI = 4096;
  public static final int AIO_CMD_MULTI_READ = 4097;
  public static final int AIO_CMD_MULTI_WRITE = 4098;
  public static final int AIO_STATE_COMPLETE = 3;
  public static final int AIO_STATE_ABORTED = 4;
  public static final int AIO_PRIORITY_HIGH = 3;
  public static final int SCE_KERNEL_ERROR_ESRCH = -2147352573;
  public static final int MAX_AIO_IDS = 128;
  public static final int CPU_LEVEL_WHICH = 3;
  public static final int CPU_WHICH_TID = 1;
  public static final int RTP_SET = 1;
  public static final int RTP_PRIO_REALTIME = 2;
  public static final int SYS_READ = 3;
  public static final int SYS_WRITE = 4;
  public static final int SYS_OPEN = 5;
  public static final int SYS_CLOSE = 6;
  public static final int SYS_GETPID = 20;
  public static final int SYS_GETUID = 24;
  public static final int SYS_ACCEPT = 30;
  public static final int SYS_PIPE = 42;
  public static final int SYS_MPROTECT = 74;
  public static final int SYS_SOCKET = 97;
  public static final int SYS_CONNECT = 98;
  public static final int SYS_BIND = 104;
  public static final int SYS_SETSOCKOPT = 105;
  public static final int SYS_LISTEN = 106;
  public static final int SYS_GETSOCKOPT = 118;
  public static final int SYS_NETGETIFLIST = 125;
  public static final int SYS_SOCKETPAIR = 135;
  public static final int SYS_SYSCTL = 202;
  public static final int SYS_NANOSLEEP = 240;
  public static final int SYS_SIGACTION = 416;
  public static final int SYS_THR_SELF = 432;
  public static final int SYS_CPUSET_GETAFFINITY = 487;
  public static final int SYS_CPUSET_SETAFFINITY = 488;
  public static final int SYS_RTPRIO_THREAD = 466;
  public static final int SYS_EVF_CREATE = 538;
  public static final int SYS_EVF_DELETE = 539;
  public static final int SYS_EVF_SET = 544;
  public static final int SYS_EVF_CLEAR = 545;
  public static final int SYS_IS_IN_SANDBOX = 585;
  public static final int SYS_DLSYM = 591;
  public static final int SYS_DYNLIB_LOAD_PRX = 594;
  public static final int SYS_DYNLIB_UNLOAD_PRX = 595;
  public static final int SYS_AIO_MULTI_DELETE = 662;
  public static final int SYS_AIO_MULTI_WAIT = 663;
  public static final int SYS_AIO_MULTI_POLL = 664;
  public static final int SYS_AIO_MULTI_CANCEL = 666;
  public static final int SYS_AIO_SUBMIT_CMD = 669;
  public static final int SYS_MUNMAP = 73;
  public static final int SYS_MMAP = 477;
  public static final int SYS_JITSHM_CREATE = 533;
  public static final int SYS_JITSHM_ALIAS = 534;
  public static final int SYS_KEXEC = 661;
  public static final int SYS_SETUID = 23;
  public static API api;
  private static long libkernelBase;
  private static long[] syscallWrappers;
  public static Buffer AIO_ERRORS;
  private static String firmwareVersion;
  
  static {
    try {
      api = API.getInstance();
      syscallWrappers = new long[1024];
      AIO_ERRORS = new Buffer(512);
      initSyscalls();
      detectFirmwareVersion();
    } catch (Exception exception) {
      throw new ExceptionInInitializerError(exception);
    } 
  }
  
  public static long getLibkernelBase() {
    return libkernelBase;
  }
  
  private static void initSyscalls() throws Exception {
    collectInfo();
    findSyscallWrappers();
    
    int[] arrayOfInt = { 669, 662, 663, 664, 666, 97, 104, 106, 98, 30, 105, 118, 135, 3, 4, 6, 5, 538, 539, 544, 545, 20, 24, 202, 585, 487, 488, 466, 73, 477, 533, 534, 661, 23 };










    
    boolean bool = true;
    for (byte b = 0; b < arrayOfInt.length; b++) {
      int i = arrayOfInt[b];
      if (syscallWrappers[i] == 0L) {
        bool = false;
      }
    } 
    
    if (!bool) {
      throw new RuntimeException("Required syscalls not found");
    }
  }
  
  private static void detectFirmwareVersion() {
    firmwareVersion = sysctlByName("kern.sdk_version");
  }
  
  public static String getCurrentFirmwareVersion() {
    return firmwareVersion;
  }
  
  private static String sysctlByName(String paramString) {
    Buffer buffer1 = new Buffer(8);
    Buffer buffer2 = new Buffer(112);
    Buffer buffer3 = new Buffer(8);
    Buffer buffer4 = new Buffer(8);
    Buffer buffer5 = new Buffer(8);

    
    buffer1.putLong(0, 12884901888L);
    buffer3.putLong(0, 112L);

    
    byte[] arrayOfByte = new byte[paramString.length() + 1];
    for (byte b = 0; b < paramString.length(); b++) {
      arrayOfByte[b] = (byte)paramString.charAt(b);
    }
    arrayOfByte[paramString.length()] = 0;
    Buffer buffer6 = new Buffer(arrayOfByte.length);
    buffer6.put(0, arrayOfByte);

    
    long l = syscall(202, buffer1.address(), 2L, buffer2
        .address(), buffer3.address(), buffer6
        .address(), arrayOfByte.length);
    if (l < 0L) {
      throw new RuntimeException("Failed to translate sysctl name to mib: " + paramString);
    }

    
    buffer5.putLong(0, 8L);
    l = syscall(202, buffer2.address(), 2L, buffer4
        .address(), buffer5.address(), 0L, 0L);
    if (l < 0L) {
      throw new RuntimeException("Failed to get sysctl value for: " + paramString);
    }
    
    int i = buffer4.getByte(3) & 0xFF;
    int j = buffer4.getByte(2) & 0xFF;
    
    String str1 = Integer.toHexString(i);
    String str2 = Integer.toHexString(j);
    if (str2.length() == 1) {
      str2 = "0" + str2;
    }
    return str1 + "." + str2;
  }
  
  public static boolean isJailbroken() {
    try {
      long l = syscall(23, 0L);
      if (l == 0L) {
        return true;
      }
      return false;
    }
    catch (Exception exception) {
      return false;
    } 
  }

  
  private static void collectInfo() throws Exception {
    long l1 = api.dlsym(8193L, "sceKernelGetModuleInfoFromAddr");
    if (l1 == 0L) {
      throw new RuntimeException("sceKernelGetModuleInfoFromAddr not found");
    }
    
    long l2 = l1;
    Buffer buffer = new Buffer(768);
    
    long l3 = api.call(l1, l2, 1L, buffer.address());
    if (l3 != 0L) {
      throw new RuntimeException("sceKernelGetModuleInfoFromAddr() error: 0x" + Long.toHexString(l3));
    }
    
    libkernelBase = api.read64(buffer.address() + 352L);
  }

  
  private static void findSyscallWrappers() {
    byte[] arrayOfByte = new byte[262144]; byte b;
    for (b = 0; b < 262144; b++) {
      arrayOfByte[b] = api.read8(libkernelBase + b);
    }
    
    for (b = 0; b <= 262132; b++) {
      if (arrayOfByte[b] == 72 && arrayOfByte[b + 1] == -57 && arrayOfByte[b + 2] == -64 && arrayOfByte[b + 7] == 73 && arrayOfByte[b + 8] == -119 && arrayOfByte[b + 9] == -54 && arrayOfByte[b + 10] == 15 && arrayOfByte[b + 11] == 5) {







        
        int i = arrayOfByte[b + 3] & 0xFF | (arrayOfByte[b + 4] & 0xFF) << 8 | (arrayOfByte[b + 5] & 0xFF) << 16 | (arrayOfByte[b + 6] & 0xFF) << 24;



        
        if (i >= 0 && i < syscallWrappers.length) {
          syscallWrappers[i] = libkernelBase + b;
        }
      } 
    } 
  }

  
  public static long syscall(int paramInt, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) {
    return api.call(syscallWrappers[paramInt], paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6);
  }
  
  public static long syscall(int paramInt, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) {
    return api.call(syscallWrappers[paramInt], paramLong1, paramLong2, paramLong3, paramLong4, paramLong5);
  }
  
  public static long syscall(int paramInt, long paramLong1, long paramLong2, long paramLong3, long paramLong4) {
    return api.call(syscallWrappers[paramInt], paramLong1, paramLong2, paramLong3, paramLong4);
  }
  
  public static long syscall(int paramInt, long paramLong1, long paramLong2, long paramLong3) {
    return api.call(syscallWrappers[paramInt], paramLong1, paramLong2, paramLong3);
  }
  
  public static long syscall(int paramInt, long paramLong1, long paramLong2) {
    return api.call(syscallWrappers[paramInt], paramLong1, paramLong2);
  }
  
  public static long syscall(int paramInt, long paramLong) {
    return api.call(syscallWrappers[paramInt], paramLong);
  }
  
  public static long syscall(int paramInt) {
    return api.call(syscallWrappers[paramInt]);
  }

  
  public static short htons(int paramInt) {
    return (short)((paramInt << 8 | paramInt >>> 8) & 0xFFFF);
  }
  
  public static int aton(String paramString) {
    String[] arrayOfString = split(paramString, "\\.");
    int i = Integer.parseInt(arrayOfString[0]);
    int j = Integer.parseInt(arrayOfString[1]);
    int k = Integer.parseInt(arrayOfString[2]);
    int m = Integer.parseInt(arrayOfString[3]);
    return m << 24 | k << 16 | j << 8 | i;
  }
  
  public static String toHexString(int paramInt1, int paramInt2) {
    String str = Integer.toHexString(paramInt1);
    StringBuffer stringBuffer = new StringBuffer();
    for (int i = str.length(); i < paramInt2; i++) {
      stringBuffer.append("0");
    }
    stringBuffer.append(str);
    return stringBuffer.toString();
  }
  
  public static String[] split(String paramString1, String paramString2) {
    Vector vector = new Vector();
    int i = 0;
    int j = 0;
    
    while ((j = paramString1.indexOf(".", i)) != -1) {
      vector.addElement(paramString1.substring(i, j));
      i = j + 1;
    } 
    vector.addElement(paramString1.substring(i));
    
    String[] arrayOfString = new String[vector.size()];
    for (byte b = 0; b < vector.size(); b++) {
      arrayOfString[b] = vector.elementAt(b);
    }
    return arrayOfString;
  }
  
  public static int createUdpSocket() {
    long l = syscall(97, 28L, 2L, 17L);
    if (l == -1L) {
      throw new RuntimeException("new_socket() error: " + l);
    }
    return (int)l;
  }
  
  public static int createTcpSocket() {
    long l = syscall(97, 2L, 1L, 0L);
    if (l == -1L) {
      throw new RuntimeException("new_tcp_socket() error: " + l);
    }
    return (int)l;
  }
  
  public static void setSockOpt(int paramInt1, int paramInt2, int paramInt3, Buffer paramBuffer, int paramInt4) {
    long l = syscall(105, paramInt1, paramInt2, paramInt3, paramBuffer.address(), paramInt4);
    if (l == -1L) {
      throw new RuntimeException("setsockopt() error: " + l);
    }
  }
  
  public static int getSockOpt(int paramInt1, int paramInt2, int paramInt3, Buffer paramBuffer, int paramInt4) {
    Buffer buffer = new Buffer(8);
    buffer.putInt(0, paramInt4);
    long l = syscall(118, paramInt1, paramInt2, paramInt3, paramBuffer.address(), buffer.address());
    if (l == -1L) {
      throw new RuntimeException("getsockopt() error: " + l);
    }
    return buffer.getInt(0);
  }
  
  public static int getCurrentCore() {
    try {
      Buffer buffer = new Buffer(16);
      buffer.fill((byte)0);
      
      long l = syscall(487, 3L, 1L, -1L, 16L, buffer.address());
      if (l != 0L) {
        return -1;
      }
      
      int i = buffer.getInt(0);
      byte b = 0;
      int j = i;
      
      while (j > 0) {
        j >>>= 1;
        b++;
      } 
      
      return Math.max(0, b - 1);
    } catch (Exception exception) {
      return -1;
    } 
  }
  
  public static boolean pinToCore(int paramInt) {
    try {
      Buffer buffer = new Buffer(16);
      buffer.fill((byte)0);
      
      int i = 1 << paramInt;
      buffer.putShort(0, (short)i);
      
      long l = syscall(488, 3L, 1L, -1L, 16L, buffer.address());
      return (l == 0L);
    } catch (Exception exception) {
      return false;
    } 
  }
  
  public static boolean setRealtimePriority(int paramInt) {
    try {
      Buffer buffer = new Buffer(4);
      buffer.putShort(0, (short)2);
      buffer.putShort(2, (short)paramInt);
      
      long l = syscall(466, 1L, 0L, buffer.address());
      return (l == 0L);
    } catch (Exception exception) {
      return false;
    } 
  }

  
  public static Buffer createAioRequests(int paramInt) {
    Buffer buffer = new Buffer(40 * paramInt);
    for (byte b = 0; b < paramInt; b++) {
      buffer.putInt(b * 40 + 32, -1);
    }
    return buffer;
  }
  
  public static long aioSubmitCmd(int paramInt1, long paramLong1, int paramInt2, int paramInt3, long paramLong2) {
    return syscall(669, paramInt1, paramLong1, paramInt2, paramInt3, paramLong2);
  }
  
  public static long aioMultiCancel(long paramLong1, int paramInt, long paramLong2) {
    return syscall(666, paramLong1, paramInt, paramLong2);
  }
  
  public static long aioMultiPoll(long paramLong1, int paramInt, long paramLong2) {
    return syscall(664, paramLong1, paramInt, paramLong2);
  }
  
  public static long aioMultiDelete(long paramLong1, int paramInt, long paramLong2) {
    return syscall(662, paramLong1, paramInt, paramLong2);
  }
  
  public static long aioMultiWait(long paramLong1, int paramInt1, long paramLong2, int paramInt2, long paramLong3) {
    return syscall(663, paramLong1, paramInt1, paramLong2, paramInt2, paramLong3);
  }

  
  public static void cancelAios(long paramLong, int paramInt) {
    char c = '';
    int i = paramInt % c;
    int j = (paramInt - i) / c;
    
    for (byte b = 0; b < j; b++) {
      aioMultiCancel(paramLong + (b * 4 * c), c, AIO_ERRORS.address());
    }
    
    if (i > 0) {
      aioMultiCancel(paramLong + (j * 4 * c), i, AIO_ERRORS.address());
    }
  }
  
  public static void freeAios(long paramLong, int paramInt, boolean paramBoolean) {
    char c = '';
    int i = paramInt % c;
    int j = (paramInt - i) / c;
    
    for (byte b = 0; b < j; b++) {
      long l = paramLong + (b * 4 * c);
      if (paramBoolean) {
        aioMultiCancel(l, c, AIO_ERRORS.address());
      }
      aioMultiPoll(l, c, AIO_ERRORS.address());
      aioMultiDelete(l, c, AIO_ERRORS.address());
    } 
    
    if (i > 0) {
      long l = paramLong + (j * 4 * c);
      if (paramBoolean) {
        aioMultiCancel(l, i, AIO_ERRORS.address());
      }
      aioMultiPoll(l, i, AIO_ERRORS.address());
      aioMultiDelete(l, i, AIO_ERRORS.address());
    } 
  }
  
  public static void freeAios(long paramLong, int paramInt) {
    freeAios(paramLong, paramInt, true);
  }

  
  public static int buildRoutingHeader(Buffer paramBuffer, int paramInt) {
    int i = (paramInt >>> 3) - 1 & 0xFFFFFFFE;
    paramInt = i + 1 << 3;
    
    paramBuffer.putByte(0, (byte)0);
    paramBuffer.putByte(1, (byte)i);
    paramBuffer.putByte(2, (byte)0);
    paramBuffer.putByte(3, (byte)(i >>> 1));
    
    return paramInt;
  }
  
  public static int getRthdr(int paramInt1, Buffer paramBuffer, int paramInt2) {
    return getSockOpt(paramInt1, 41, 51, paramBuffer, paramInt2);
  }
  
  public static void setRthdr(int paramInt1, Buffer paramBuffer, int paramInt2) {
    setSockOpt(paramInt1, 41, 51, paramBuffer, paramInt2);
  }
  
  public static void freeRthdrs(int[] paramArrayOfint) {
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      if (paramArrayOfint[b] >= 0) {
        setSockOpt(paramArrayOfint[b], 41, 51, new Buffer(1), 0);
      }
    } 
  }

  
  public static int createEvf(long paramLong, int paramInt) {
    long l = syscall(538, paramLong, 0L, paramInt);
    if (l == -1L) {
      throw new RuntimeException("evf_create() error: " + l);
    }
    return (int)l;
  }
  
  public static void setEvfFlags(int paramInt1, int paramInt2) {
    long l1 = syscall(545, paramInt1, 0L);
    if (l1 == -1L) {
      throw new RuntimeException("evf_clear() error: " + l1);
    }
    
    long l2 = syscall(544, paramInt1, paramInt2);
    if (l2 == -1L) {
      throw new RuntimeException("evf_set() error: " + l2);
    }
  }
  
  public static void freeEvf(int paramInt) {
    long l = syscall(539, paramInt);
    if (l == -1L) {
      throw new RuntimeException("evf_delete() error: " + l);
    }
  }

  
  public static void removeSocketFromArray(int[] paramArrayOfint, int paramInt) {
    if (paramInt >= 0 && paramInt < paramArrayOfint.length) {
      for (int i = paramInt; i < paramArrayOfint.length - 1; i++) {
        paramArrayOfint[i] = paramArrayOfint[i + 1];
      }
      paramArrayOfint[paramArrayOfint.length - 1] = -1;
    } 
  }
  
  public static void addSocketToArray(int[] paramArrayOfint, int paramInt) {
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      if (paramArrayOfint[b] == -1) {
        paramArrayOfint[b] = paramInt;
        break;
      } 
    } 
  }

  
  public static String extractStringFromBuffer(Buffer paramBuffer) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < 8; ) {
      byte b1 = paramBuffer.getByte(b);
      if (b1 != 0 && 
        b1 >= 32 && b1 <= 126) {
        stringBuffer.append((char)b1);
        
        b++;
      } 
    } 
    return stringBuffer.toString();
  }
}

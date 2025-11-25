package org.bdj.external;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.bdj.api.API;










public class BinLoader
{
  private static final int PROT_READ = 1;
  private static final int PROT_WRITE = 2;
  private static final int PROT_EXEC = 4;
  private static final int MAP_PRIVATE = 2;
  private static final int MAP_ANONYMOUS = 4096;
  private static final int ELF_MAGIC = 1179403647;
  private static final int PT_LOAD = 1;
  private static final int PAGE_SIZE = 4096;
  private static final int MAX_PAYLOAD_SIZE = 4194304;
  private static final int READ_CHUNK_SIZE = 4096;
  private static final String USBPAYLOAD_RESOURCE = "/disc/BDMV/AUXDATA/aiofix_USBpayload.elf";
  private static API api;
  private static byte[] binData;
  private static long mmapBase;
  private static long mmapSize;
  private static long entryPoint;
  private static Thread payloadThread;
  
  static {
    try {
      api = API.getInstance();
    } catch (Exception exception) {
      throw new ExceptionInInitializerError(exception);
    } 
  }
  
  public static void start() {
    Thread thread = new Thread(new Runnable() {
          public void run() {
            BinLoader.startInternal();
          }
        });
    thread.setName("BinLoader");
    thread.start();
  }
  
  private static void startInternal() {
    executeEmbeddedPayload();
  }
  
  private static void executeEmbeddedPayload() {
    try {
      File file = new File("/disc/BDMV/AUXDATA/aiofix_USBpayload.elf");
      FileInputStream fileInputStream = new FileInputStream(file);
      byte[] arrayOfByte = new byte[fileInputStream.available()];
      fileInputStream.read(arrayOfByte);
      fileInputStream.close();
      loadFromData(arrayOfByte);
      run();
      waitForPayloadToExit();
    }
    catch (Exception exception) {}
  }


  
  private static byte[] loadResourcePayload(String paramString) throws Exception {
    InputStream inputStream = BinLoader.class.getResourceAsStream(paramString);
    if (inputStream == null) {
      throw new RuntimeException("Resource not found: " + paramString);
    }
    
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byte[] arrayOfByte = new byte[4096];
    
    int i = 0;
    try {
      int j;
      while ((j = inputStream.read(arrayOfByte)) != -1) {
        byteArrayOutputStream.write(arrayOfByte, 0, j);
        i += j;

        
        if (i > 4194304) {
          throw new RuntimeException("Resource payload exceeds maximum size: 4194304");
        }
      } 
      
      return byteArrayOutputStream.toByteArray();
    } finally {
      
      inputStream.close();
      byteArrayOutputStream.close();
    } 
  }
  public static void loadFromData(byte[] paramArrayOfbyte) throws Exception {
    long l1;
    if (paramArrayOfbyte == null) {
      throw new IllegalArgumentException("Payload data cannot be null");
    }
    
    if (paramArrayOfbyte.length == 0) {
      throw new IllegalArgumentException("Payload data cannot be empty");
    }
    
    if (paramArrayOfbyte.length > 4194304) {
      throw new IllegalArgumentException("Payload too large: " + paramArrayOfbyte.length + " bytes (max: " + 4194304 + ")");
    }
    
    binData = paramArrayOfbyte;


    
    try {
      l1 = roundUp(paramArrayOfbyte.length, 4096L);
      if (l1 <= 0L || l1 > 8388608L) {
        throw new RuntimeException("Invalid mmap size calculation: " + l1);
      }
    } catch (ArithmeticException arithmeticException) {
      throw new RuntimeException("Integer overflow in mmap size calculation");
    } 

    
    byte b = 7;
    char c = 'ဂ';
    
    long l2 = Helper.syscall(477, 0L, l1, b, c, -1L, 0L);
    if (l2 < 0L) {
      int i = api.errno();
      throw new RuntimeException("mmap() failed with error: " + l2 + " (errno: " + i + ")");
    } 

    
    if (l2 == 0L || l2 == -1L) {
      throw new RuntimeException("mmap() returned invalid address: 0x" + Long.toHexString(l2));
    }
    
    mmapBase = l2;
    mmapSize = l1;


    
    try {
      if (paramArrayOfbyte.length >= 4) {
        int i = (paramArrayOfbyte[3] & 0xFF) << 24 | (paramArrayOfbyte[2] & 0xFF) << 16 | (paramArrayOfbyte[1] & 0xFF) << 8 | paramArrayOfbyte[0] & 0xFF;

        
        if (i == 1179403647) {
          entryPoint = loadElfSegments(paramArrayOfbyte);
        } else {
          
          if (paramArrayOfbyte.length > mmapSize) {
            throw new RuntimeException("Payload size exceeds allocated memory");
          }
          api.memcpy(mmapBase, paramArrayOfbyte, paramArrayOfbyte.length);
          entryPoint = mmapBase;
        } 
      } else {
        throw new RuntimeException("Payload too small (< 4 bytes)");
      } 

      
      if (entryPoint == 0L) {
        throw new RuntimeException("Invalid entry point: 0x0");
      }
      if (entryPoint < mmapBase || entryPoint >= mmapBase + mmapSize) {
        throw new RuntimeException("Entry point outside allocated memory range: 0x" + Long.toHexString(entryPoint));
      
      }
    }
    catch (Exception exception) {
      
      long l = Helper.syscall(73, mmapBase, mmapSize);
      if (l < 0L);
      
      mmapBase = 0L;
      mmapSize = 0L;
      entryPoint = 0L;
      throw exception;
    } 
  }

  
  private static long loadElfSegments(byte[] paramArrayOfbyte) throws Exception {
    long l = Helper.syscall(477, 0L, paramArrayOfbyte.length, 3L, 4098L, -1L, 0L);
    
    if (l < 0L) {
      throw new RuntimeException("Failed to allocate temp buffer for ELF parsing");
    }

    
    try {
      api.memcpy(l, paramArrayOfbyte, paramArrayOfbyte.length);

      
      ElfHeader elfHeader = readElfHeader(l);

      
      for (byte b = 0; b < elfHeader.phNum; b++) {
        long l1 = l + elfHeader.phOff + (b * elfHeader.phEntSize);
        ProgramHeader programHeader = readProgramHeader(l1);
        
        if (programHeader.type == 1 && programHeader.memSize > 0L) {
          
          long l2 = mmapBase + programHeader.vAddr % 16777216L;

          
          if (programHeader.fileSize > 0L) {
            byte[] arrayOfByte = new byte[(int)programHeader.fileSize];
            System.arraycopy(paramArrayOfbyte, (int)programHeader.offset, arrayOfByte, 0, (int)programHeader.fileSize);
            api.memcpy(l2, arrayOfByte, arrayOfByte.length);
          } 

          
          if (programHeader.memSize > programHeader.fileSize) {
            api.memset(l2 + programHeader.fileSize, 0, programHeader.memSize - programHeader.fileSize);
          }
        } 
      } 
      
      return mmapBase + elfHeader.entry % 16777216L;
    }
    finally {
      
      Helper.syscall(73, l, paramArrayOfbyte.length);
    } 
  }

  
  public static void run() throws Exception {
    payloadThread = new Thread(new Runnable()
        {
          public void run() {
            try {
              long l = BinLoader.api.call(BinLoader.entryPoint);
            }
            catch (Exception exception) {}
          }
        });

    
    payloadThread.setName("BinPayload");
    payloadThread.start();
  }

  
  public static void waitForPayloadToExit() throws Exception {
    if (payloadThread != null) {
      try {
        payloadThread.join();
      } catch (InterruptedException interruptedException) {
        Thread.currentThread().interrupt();
      } 
    }

    
    if (mmapBase != 0L && mmapSize > 0L) {
      
      try {
        long l = Helper.syscall(73, mmapBase, mmapSize);
        if (l < 0L) {
          int i = api.errno();
        }
      }
      catch (Exception exception) {}


      
      mmapBase = 0L;
      mmapSize = 0L;
      entryPoint = 0L;
      binData = null;
    } 



    
    payloadThread = null;
  }
  private static class ElfHeader {
    long entry;
    long phOff;
    int phEntSize;
    int phNum;
    
    private ElfHeader() {} }
  
  private static class ProgramHeader {
    int type;
    long offset;
    long vAddr;
    long fileSize;
    long memSize;
    
    private ProgramHeader() {} }
  
  private static ElfHeader readElfHeader(long paramLong) {
    ElfHeader elfHeader = new ElfHeader();
    elfHeader.entry = api.read64(paramLong + 24L);
    elfHeader.phOff = api.read64(paramLong + 32L);
    elfHeader.phEntSize = api.read16(paramLong + 54L) & 0xFFFF;
    elfHeader.phNum = api.read16(paramLong + 56L) & 0xFFFF;
    return elfHeader;
  }
  
  private static ProgramHeader readProgramHeader(long paramLong) {
    ProgramHeader programHeader = new ProgramHeader();
    programHeader.type = api.read32(paramLong + 0L);
    programHeader.offset = api.read64(paramLong + 8L);
    programHeader.vAddr = api.read64(paramLong + 16L);
    programHeader.fileSize = api.read64(paramLong + 32L);
    programHeader.memSize = api.read64(paramLong + 40L);
    return programHeader;
  }
  
  private static long roundUp(long paramLong1, long paramLong2) {
    if (paramLong1 < 0L || paramLong2 <= 0L) {
      throw new IllegalArgumentException("Invalid arguments: value=" + paramLong1 + ", boundary=" + paramLong2);
    }

    
    if (paramLong1 > Long.MAX_VALUE - paramLong2) {
      throw new ArithmeticException("Integer overflow in roundUp calculation");
    }
    
    return (paramLong1 + paramLong2 - 1L) / paramLong2 * paramLong2;
  }
}

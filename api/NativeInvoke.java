package org.bdj.api;



public class NativeInvoke
{
  static API api;
  static long sceKernelSendNotificationRequestAddr;
  
  static {
    try {
      api = API.getInstance();
      sceKernelSendNotificationRequestAddr = api.dlsym(8193L, "sceKernelSendNotificationRequest");
    } catch (Exception exception) {}
  }


  
  public static int sendNotificationRequest(String paramString) {
    if (sceKernelSendNotificationRequestAddr == 0L) {
      return -1;
    }
    
    long l1 = 3120L;
    Buffer buffer = new Buffer((int)l1);
    
    buffer.fill((byte)0);
    buffer.putInt(16, -1);
    
    byte[] arrayOfByte = paramString.getBytes();
    for (byte b = 0; b < arrayOfByte.length && b < l1 - 45L - 1L; b++) {
      buffer.putByte(45 + b, arrayOfByte[b]);
    }
    
    buffer.putByte(45 + Math.min(arrayOfByte.length, (int)(l1 - 45L - 1L)), (byte)0);
    
    long l2 = api.call(sceKernelSendNotificationRequestAddr, 0L, buffer.address(), l1, 0L);
    
    return (int)l2;
  }
}
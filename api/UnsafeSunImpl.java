package org.bdj.api;

import java.lang.reflect.Field;
import sun.misc.Unsafe;








class UnsafeSunImpl
  implements UnsafeInterface
{
  private static final String UNSAFE_CLASS_NAME = "sun.misc.Unsafe";
  private static final String THE_UNSAFE_FIELD_NAME = "theUnsafe";
  private final Unsafe unsafe;
  
  UnsafeSunImpl() throws Exception {
    Class.forName("sun.misc.Unsafe");

    
    Field field = Unsafe.class.getDeclaredField("theUnsafe");
    field.setAccessible(true);
    this.unsafe = (Unsafe)field.get(null);
  }
  
  public byte getByte(long paramLong) {
    return this.unsafe.getByte(paramLong);
  }
  
  public short getShort(long paramLong) {
    return this.unsafe.getShort(paramLong);
  }
  
  public int getInt(long paramLong) {
    return this.unsafe.getInt(paramLong);
  }
  
  public long getLong(long paramLong) {
    return this.unsafe.getLong(paramLong);
  }
  
  public long getLong(Object paramObject, long paramLong) {
    return this.unsafe.getLong(paramObject, paramLong);
  }
  
  public void putByte(long paramLong, byte paramByte) {
    this.unsafe.putByte(paramLong, paramByte);
  }
  
  public void putShort(long paramLong, short paramShort) {
    this.unsafe.putShort(paramLong, paramShort);
  }
  
  public void putInt(long paramLong, int paramInt) {
    this.unsafe.putInt(paramLong, paramInt);
  }
  
  public void putLong(long paramLong1, long paramLong2) {
    this.unsafe.putLong(paramLong1, paramLong2);
  }
  
  public void putObject(Object paramObject1, long paramLong, Object paramObject2) {
    this.unsafe.putObject(paramObject1, paramLong, paramObject2);
  }
  
  public long objectFieldOffset(Field paramField) {
    return this.unsafe.objectFieldOffset(paramField);
  }
  
  public long allocateMemory(long paramLong) {
    return this.unsafe.allocateMemory(paramLong);
  }
  
  public long reallocateMemory(long paramLong1, long paramLong2) {
    return this.unsafe.reallocateMemory(paramLong1, paramLong2);
  }
  
  public void freeMemory(long paramLong) {
    this.unsafe.freeMemory(paramLong);
  }
  
  public void setMemory(long paramLong1, long paramLong2, byte paramByte) {
    this.unsafe.setMemory(paramLong1, paramLong2, paramByte);
  }
  
  public void copyMemory(long paramLong1, long paramLong2, long paramLong3) {
    this.unsafe.copyMemory(paramLong1, paramLong2, paramLong3);
  }
}

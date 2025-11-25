package org.bdj.api;

import java.lang.reflect.Field;

interface UnsafeInterface {
  byte getByte(long paramLong);
  
  short getShort(long paramLong);
  
  int getInt(long paramLong);
  
  long getLong(long paramLong);
  
  long getLong(Object paramObject, long paramLong);
  
  void putByte(long paramLong, byte paramByte);
  
  void putShort(long paramLong, short paramShort);
  
  void putInt(long paramLong, int paramInt);
  
  void putLong(long paramLong1, long paramLong2);
  
  void putObject(Object paramObject1, long paramLong, Object paramObject2);
  
  long objectFieldOffset(Field paramField);
  
  long allocateMemory(long paramLong);
  
  long reallocateMemory(long paramLong1, long paramLong2);
  
  void freeMemory(long paramLong);
  
  void setMemory(long paramLong1, long paramLong2, byte paramByte);
  
  void copyMemory(long paramLong1, long paramLong2, long paramLong3);
}

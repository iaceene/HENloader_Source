package org.bdj.api;






public final class Int64
  extends Buffer
{
  public static final int SIZE = 8;
  
  public Int64() {
    super(8);
  }
  
  public Int64(long paramLong) {
    this();
    set(paramLong);
  }
  
  public long get() {
    return getLong(0);
  }
  
  public void set(long paramLong) {
    putLong(0, paramLong);
  }
}
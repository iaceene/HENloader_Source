package org.bdj.api;






public final class Int32
  extends Buffer
{
  public static final int SIZE = 4;
  
  public Int32() {
    super(4);
  }
  
  public Int32(int paramInt) {
    this();
    set(paramInt);
  }
  
  public int get() {
    return getInt(0);
  }
  
  public void set(int paramInt) {
    putInt(0, paramInt);
  }
}
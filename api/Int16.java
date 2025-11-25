package org.bdj.api;






public final class Int16
  extends Buffer
{
  public static final int SIZE = 2;
  
  public Int16() {
    super(2);
  }
  
  public Int16(short paramShort) {
    this();
    set(paramShort);
  }
  
  public short get() {
    return getShort(0);
  }
  
  public void set(short paramShort) {
    putShort(0, paramShort);
  }
}
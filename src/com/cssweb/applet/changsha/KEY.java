/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cssweb.applet.changsha;

import javacard.framework.Util;

/**
 *
 * @author chenh
 */
public class KEY  {
   
   byte keyId;
   byte keyVersion;
   byte algId;
   byte errorCount;
   byte[] key;
           
            
    public KEY(byte[] k)
    {
        keyId = k[0];
        keyVersion = k[1];
        algId = k[2];
        errorCount = k[3];
        
        if (keyId == (byte)0x08)
        {
            key = new byte[6];
            Util.arrayCopy(k, (short)4, key, (short)0, (short)6);
        }
        else
        {
            key = new byte[16];
            Util.arrayCopy(k, (short)4, key, (short)0, (short)16);
        }
    }
    
    public byte[] getKey()
    {
        return key;
    }
    
    public byte getKeyId()
    {
        return keyId;
    }
    
    public byte getAlgId()
    {
        return algId;
        
    }
    
    public byte getKeyVersion()
    {
        return keyVersion;
    }
    
    public byte getErrorCount()
    {
        return errorCount;
    }
    
}

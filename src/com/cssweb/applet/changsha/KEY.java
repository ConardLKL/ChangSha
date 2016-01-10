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
   byte safeStatus;
   byte[] key;
           
            
    public KEY(byte kid, byte[] k)
    {
        keyId = kid; //��Կ����
        
        keyVersion = k[1];//��Կ�汾
        algId = k[2];//�㷨��ʶ
        errorCount = k[3];//���������
        safeStatus = k[4];//������ȫ״̬
        
        
        
        
        if (k.length == 24)
        {
        	key = new byte[16];
            Util.arrayCopy(k, (short)5, key, (short)0, (short)16);
            
        }
        else
        {
        	//PIN����
        	key = new byte[6];
            Util.arrayCopy(k, (short)5, key, (short)0, (short)6);
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
    
    public byte getSafeStatus()
    {
    	return safeStatus;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cssweb.applet.changsha;

import javacard.framework.APDU;
import javacard.framework.ISOException;
import javacard.framework.Util;
import javacard.security.CryptoException;
import javacard.security.DESKey;
import javacard.security.KeyBuilder;
import javacard.security.Signature;
import javacardx.crypto.Cipher;

/**
 *
 * @author chenh
 */
public class ALG {
    
 
        
    
       
    public static final byte[] bytesXOR(byte[] d1, byte[] d2)
    {
        if (d1 == null || d2 == null)
            return null;
        
        short size = (short) d1.length;
        
        byte[] result = new byte[size];
         
        for(short i=(short)0; i<(short)size; i++ )
           result[i] = byteXOR(d1[i], d2[i]);
       
       return result;
    }
    
    private static byte byteXOR(byte src1, byte src2) {
        return (byte) ((src1 & 0xFF) ^ (src2 & 0xFF));
    }

    public static  byte[] padding(byte[] src, short srcLen)
    {
        short x = (short)(srcLen % 8);

        short addLen = 8;

        if (x != 0) {
            addLen = (short)(8 - x);
        }


        byte[] add = new byte[addLen];
        for (short i=0; i<addLen; i++)
        {
            if (i==0)
                add[i] = (byte)0x80;
            else
                add[i] = (byte)0x00;
        }

        short len = (short)(srcLen + addLen);
        byte[] data = new byte[len];
        
        Util.arrayCopy(src, (short)0, data, (short)0, srcLen);
        Util.arrayCopy(add, (short)0, data, srcLen, addLen);
        
        return data;
    }
   
    public static void genSessionKey(byte[] key, byte[] src, short srcLen, byte[] sessionKey)
    {
        //padding data
        byte[] data = padding(src, srcLen);
        short len = (short) data.length;
         
        DESKey dk;
        if (key.length == 8)
        {
            dk = (DESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES, false);
        }
        else  
        {
            dk = (DESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES3_2KEY, false);
        }
         
        dk.setKey(key, (short)0);
        
        Cipher cipher = Cipher.getInstance(Cipher.ALG_DES_ECB_NOPAD, false);
        
        
        cipher.init(dk, Cipher.MODE_ENCRYPT);
       
        cipher.doFinal(data, (short)0, (short)len, sessionKey, (short)0);
    }
    
    
    
    public static void genMACOrTAC(byte[] key, byte[] iv, byte[] src, short srcLen, byte[] mac)
    {
        byte[] left = new byte[8];
        byte[] right = new byte[8];
        Util.arrayCopy(key, (short)0, left, (short)0, (short)8);
        
        if(key.length == 16)
        	Util.arrayCopy(key, (short)8, right, (short)0, (short)8);
        
        //padding data
        byte[] data = padding(src, srcLen);
        short len = (short) data.length;
        
        short pos = 0;
        byte[] block1 = new byte[8];
        Util.arrayCopy(data, pos, block1, (short)0, (short)8);
        pos += 8;

        byte[] input = bytesXOR(iv, block1);

        byte[] output = new byte[8];
        encrypt(left, input, (short)0, (short)8, output, (short)0);
        
        
        short count = (short)(data.length/8);
        byte[] block = new byte[8];
        for (short i=1; i<count; i++)
        {
            
            Util.arrayCopy(data, pos, block, (short)0, (short)8);
            pos += 8;

  
            input = bytesXOR(output, block);

            encrypt(left, input, (short)0, (short)8, output, (short)0);
        }

        if (key.length == 16)
        {
        	byte[] dec = new byte[8];
        	decrypt(right, output, (short)0, (short)8, dec, (short)0);
        	
        	encrypt(left, dec, (short)0, (short)8, mac, (short)0);
        }
        else
        {
        	Util.arrayCopy(output, (short)0, mac, (short)0, (short)8);
        }
    }
    
    
    public static void encrypt(byte[] key, byte[] src, short srcOffset, short srcLen, byte[] out, short outOffset)
    {
    
        
        Cipher cipher = Cipher.getInstance(Cipher.ALG_DES_ECB_NOPAD, false);
         
        DESKey desKey = (DESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES, false);
       
         
        desKey.setKey(key, (short)0);
        
        cipher.init(desKey, Cipher.MODE_ENCRYPT);
       
        cipher.doFinal(src, srcOffset, srcLen, out, outOffset);
                
    }
    
    public static void decrypt(byte[] key, byte[] src, short srcOffset, short srcLen, byte[] out, short outOffset)
    {
        Cipher cipher = Cipher.getInstance(Cipher.ALG_DES_ECB_NOPAD, false);
         
        
        //DESKey desKey = (DESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES, false);
        //DESKey desKey = (DESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES3_2KEY, false);
        DESKey desKey;
        if (key.length == 8)
        {
        	desKey = (DESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES, false);
        }
        else  
        {
        	desKey = (DESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES3_2KEY, false);
        }
        desKey.setKey(key, (short)0);

        cipher.init(desKey, Cipher.MODE_DECRYPT);
           
        cipher.doFinal(src, srcOffset, srcLen, out, outOffset);
    }
    
    
    public static void testDES(MyAPDU apdu) throws ISOException
    {
        byte[] buffer = apdu.getBuffer();
      
        //byte[] key = {0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38};
        byte[] key = {0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38};
        byte[] data = {0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48};
        //byte[] data = {0x41, 0x42, 0x43, 0x44, 0x00, 0x00};
        
        byte[] iv = new byte[8];
                
         byte[] mac1 = new byte[8];
        ALG.encrypt(key, data, (short)0, (short)data.length, mac1, (short)0);
        
        byte[] out = new byte[8];
        ALG.decrypt(key, mac1, (short)0, (short)8, out, (short)0); // notice: length is 16
     
       
       
        Util.arrayCopy(out, (short)0, buffer, (short)0, (short)out.length);
        
       
        apdu.le = (short)out.length;
    }
    
    public static void testMAC(MyAPDU apdu) throws ISOException
    {
        
        byte[] buffer = apdu.getBuffer();
      
        byte[] iv = {(byte)0xFC, (byte)0xF3, 0x04, 0x77, 0x00, 0x00, 0x00, 0x00};
        byte[] k ={0x78,(byte)0xBB,(byte)0xDF,(byte)0xD3,(byte)0xCE,(byte)0xCA,0x48,(byte)0xFD,0x7B,(byte)0xD5,(byte)0xF6,(byte)0xAE,0x69,(byte)0x88,0x2C,0x60};
        byte[] d = {0x04, (byte)0xDC, 0x07, (byte)0xBC, 0x1C, 0x11, 0x16, 0x01, 0x00, 0x20, 0x15, 0x08, 0x27, 0x17, 0x24, 0x23, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
                
        //31323334353637383132333435363738
        
         byte[] mac1 = new byte[512];
        
        ALG.genMACOrTAC(k, iv, d, (short)d.length, mac1);
     
       
        Util.arrayCopy(mac1, (short)0, buffer, (short)0, (short)8);
        
        
        apdu.le = (short)8;
    
    }
    
   
    public static void testTAC(MyAPDU apdu) throws ISOException
    {
        byte[] buffer = apdu.getBuffer();
      
        byte[] key = {0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38};
        byte[] data = {0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48};
        
        byte[] iv = new byte[8];
                
         byte[] mac1 = new byte[512];
         
        byte[] left = new byte[8];
        byte[] right = new byte[8];
        Util.arrayCopy(key, (short)0, left, (short)0, (short)8);
        Util.arrayCopy(key, (short)8, right, (short)0, (short)8);
        byte[] sessionKey = ALG.bytesXOR(left, right);
        
        ALG.genMACOrTAC(sessionKey, iv, data, (short)data.length, mac1);
     
       
        Util.arrayCopy(mac1, (short)0, buffer, (short)0, (short)8);
        
       
        apdu.le = (short)8;
    }
    
}

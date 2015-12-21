/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cssweb.applet.changsha;

/**
 *
 * @author chenh
 */
public class ADF extends DF{
   
    EP ep;
    
    public ADF(short SFI, byte[] name, short len, byte read, byte write)
    {
        super(SFI, len, read, write);
        
        fileType = File.FILE_TYPE_ADF;
        
        fileName = name;
        
        ep = new EP((short)0x02, (short)0x43, (byte)0xf0, (byte)0xf0);
    }

   
    
}

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
public class MF extends DDF{
    
    
    
    public MF(short SFI, short len, byte read, byte write)
    {
        super(SFI, len, read, write);
        
        fileType = File.FILE_TYPE_MF;
        
        //1PAY.SYS.DDF01 315041592E5359532E4444463031
       // fileName = {(byte)0x31, (byte)0x50, (byte)0x41, (byte)0x59, (byte)0x2E, (byte)0x53, (byte)0x59, (byte)0x53, (byte)0x2E, (byte)0x44, (byte)0x44, (byte)0x46, (byte)0x30, (byte)0x31};
    }
}

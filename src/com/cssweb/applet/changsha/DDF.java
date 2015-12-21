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
public class DDF extends DF{
   
    RecordFileTLV DFInfo;
    
    public DDF(short SFI, short len, byte read, byte write)
    {
        super(SFI, len, read, write);
        
        fileType = File.FILE_TYPE_DDF;
        
        DFInfo = new RecordFileTLV((short)0x01, (short)0x0100, (byte)0xF0, (byte)0xF0);
        addFile(DFInfo);
    }
    
}

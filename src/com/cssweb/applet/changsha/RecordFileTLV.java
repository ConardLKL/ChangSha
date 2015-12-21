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
public class RecordFileTLV extends RecordFile{
    
    
    public RecordFileTLV(short SFI, short len, byte read, byte write)
    {
        super(SFI, len, read, write);
        
        fileType = File.FILE_TYPE_RECORD_TLV;
    }
    
       
    
    
}

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
public class RecordFileFixed extends RecordFile{

    

    public RecordFileFixed(short SFI, byte maxRecords, byte recLen, byte read, byte write)
    {
        super(SFI, recLen, read, write);

        
        fileType = File.FILE_TYPE_RECORD_FIXED;
        
        fileSize = (short) (maxRecords * recLen);
        
        maxRecordNum = maxRecords;
        records = new Object[maxRecordNum];
       
    }
    
   
}

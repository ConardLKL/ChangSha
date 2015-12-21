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
public class RecordFileCycler extends RecordFile{
    

  

    
    public RecordFileCycler(short SFI, byte maxRecords, byte recLen, byte read, byte write)
    {
        super(SFI, recLen, read, write);
        
        fileType = File.FILE_TYPE_RECORD_CYCLER;
        
        fileSize = (short) (maxRecords * recLen);
        
        maxRecordNum = maxRecords;
        records = new Object[maxRecordNum];
       
        
    }
    
    
    
    public boolean addRecord(byte[] rec)
    {
        if (recordId > maxRecordNum)
        {
            recordId = 0;
        }
       
        records[recordId++] = rec;
        return true;
    }
    
   
    
    public byte[] getRecord(byte recId)
    {
        if (recId >= maxRecordNum)
            return null;
        
        return (byte[]) records[recId];
    }
    
    
}

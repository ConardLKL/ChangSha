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
public class RecordFile extends File{
    public static final short MAX_RECORD_NUM = (byte)0x20;
    
    Object records[];
    short recordId;
    short maxRecordNum;
    
       

    public RecordFile(short SFI, short len, byte read, byte write) {
        super(SFI, len, read, write);
        
        
        recordId = 0;
        maxRecordNum = MAX_RECORD_NUM;
        records = new Object[maxRecordNum];
    }
    
    public boolean addRecord(byte recId, byte[] rec)
    {
        if (recId >= MAX_RECORD_NUM)
            return false;
            
        records[recId] = rec;
        return true;
    }
    
    public byte[] getRecordById(byte recId)
    {
        if (recId >= MAX_RECORD_NUM)
            return null;
        
        return (byte[]) records[recId];
    }
    
    
    public byte[] getRecordByTag(byte Tag)
    {
        for (short i=0; i<records.length; i++)
        {
            byte[] rec = (byte[]) records[i];
            if (rec != null)
            {
                byte t = rec[0];
            
                if (t == Tag)
                {
                    
                    return rec;
                } 
            }
        }
        
        return null;
    }
  
    
    
    public boolean updateRecordById(byte recId, byte[] record)
    {
        if (recId >= MAX_RECORD_NUM)
            return false;
            
        
        if (records[recId] == null)
            return false;
        
        records[recId] = record;
        
        return true;
    }
    
    
    
    public boolean updateRecordByTag(byte Tag, byte[] record)
    {
        for (short i=0; i<records.length; i++)
        {
            byte[] rec = (byte[]) records[i];
            if (rec != null)
            {
            
                byte tag = rec[0];
            
                if (Tag == tag)
                {
                    records[i] = record;
                    return true;
                } 
            }
        }
        
        return false;
    }    
}

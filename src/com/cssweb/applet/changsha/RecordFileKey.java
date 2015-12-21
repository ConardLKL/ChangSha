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
public class RecordFileKey extends RecordFile{

    public RecordFileKey(short SFI, short len, byte read, byte write) 
    {
        super(SFI, len, read, write);
        
        fileType = File.FILE_TYPE_RECORD_KEY;
    }
    
    public boolean addKey(byte recId, byte[] key)
    {
        if (recordId >= maxRecordNum)
            return false;
        
        KEY k = new KEY(key);
        records[recId] = k;
        
        return true;
    }
    
    public KEY getKeyById(byte recId)
    {
        if (recId >= maxRecordNum)
            return null;
        
        return (KEY) records[recId];
    }
    
    public KEY getKeyByTag(byte tag)
    {
        for (short i=0; i<maxRecordNum; i++)
        {
            KEY key = (KEY) records[i];
            if (key != null)
            {
                if (key.getKeyId() == tag)
                    return key;
            }
        }
        
        return null;
    }
    
    public boolean updateKeyById(byte recId, byte[] key)
    {
        if (recId >= maxRecordNum)
            return false;
        
        KEY k = (KEY) records[recId];
        if (k == null) //record must be exist.
            return false;
        
        KEY tmpKey = new KEY(key);
        records[recId] = tmpKey;
      
        return true;        
    }
    
    public boolean updateKeyByTag(byte tag, byte[] key)
    {
        for (short i=0; i<maxRecordNum; i++)
        {
            KEY k = (KEY) records[i];
            if (k != null)
            {
                if (k.getKeyId() == tag)
                {
                    KEY tmpKey = new KEY(key);
                    records[i] = tmpKey;
                    return true;
                }
                    
            }
        }
        
        return false;
    }
    
}

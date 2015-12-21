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
public class DF extends File{
    public static final short MAX_FILES = 0x20;
    
    RecordFileKey keyFile;
    byte[] fileName;
    
    File files[] = new File[MAX_FILES];
    short pos = 0;
   

    public DF(short SFI, short len, byte read, byte write) 
    {
        super(SFI, len, read, write);
        
        fileType = File.FILE_TYPE_DF;
    }
    
    public byte[] getFileName()
    {
        return fileName;
    }
    
    public void setKeyFile(RecordFileKey key)
    {
        keyFile = key;
    }
    
    public RecordFileKey getKeyFile()
    {
        return keyFile;
    }
    
    
    public boolean addFile(File file)
    {
        if (pos >= MAX_FILES)
            return false;
        
        files[pos++] = file;
        return true;
    }
    
    public File selectFile(short SFI)
    {
        for (short i=0; i<pos; i++)
        {
            File file = files[i];
            if (file != null)
            {
                if (file.getSFI() == SFI)
                {
                    return file;
                }
            }
        }
        
        return null;
    
    }
    
    public File selectFile(byte[] name)
    {
        for (short i=0; i<pos; i++)
        {
            File file = files[i];
            if ( (file != null) && (file.getFileType() == File.FILE_TYPE_DF) )
            {
                
                byte[] fileName = ((DF)file).getFileName();
                
                if (Util.arrayCompare(fileName, (short)0, name, (short)0, (short)name.length) == 0)
                {
                    return file;
                }
            }
        }
        
        return null;
    }
    
}

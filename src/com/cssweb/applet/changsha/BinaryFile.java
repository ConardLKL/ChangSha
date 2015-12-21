/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cssweb.applet.changsha;

import com.cssweb.applet.changsha.File;
import javacard.framework.Util;

/**
 *
 * @author chenh
 */
public class BinaryFile extends File{
    
  
    byte[] content;
    
    public BinaryFile(short SFI, short len, byte read, byte write)
    {
        super(SFI, len, read, write);
        
        fileType = File.FILE_TYPE_BIN;
    }
    
   public void setData(byte[] data)
    {
        content = data;
    }
   
   public byte[] getData()
   {
       return content;
   }
    
   
}

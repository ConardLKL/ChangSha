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
public class File {
    
    
    public static final byte FILE_TYPE_DF = (byte)0x38;
    public static final byte FILE_TYPE_MF = (byte)0x38;
    public static final byte FILE_TYPE_DDF = (byte)0x38;
    public static final byte FILE_TYPE_ADF = (byte)0x38;
    
    
    
    public static final byte FILE_TYPE_BIN = (byte)0x28;
    
    public static final byte FILE_TYPE_RECORD_CYCLER = (byte)0x2E;
    public static final byte FILE_TYPE_RECORD_TLV = (byte)0x2C;
    public static final byte FILE_TYPE_RECORD_FIXED = (byte)0x2A;
    
    public static final byte FILE_TYPE_RECORD_KEY = (byte)0x3F;
    public static final byte FILE_TYPE_RECORD_EP = (byte)0x2F;
    
    byte fileType;
    short sfi;
    short fileSize;
    byte readPermission;
    byte writePermission;
    
    
    public File(short SFI, short len, byte read, byte write)
    {
        sfi = SFI;
        fileSize = len;
        readPermission = read;
        writePermission = write;
    }
    
    public byte getFileType()
    {
        return fileType;
    }
    
    public short getSFI()
    {
        return sfi;
    }
    
    public short getFileSize()
    {
        return fileSize;
    }
}

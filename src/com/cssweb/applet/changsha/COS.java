/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cssweb.applet.changsha;

import javacard.framework.APDU;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.Util;

/**
 *
 * @author chenh
 */
public class COS {
    MF MF = null;
    DF currentDF = null;
    File currentFile = null;
    
    static final byte[] AID = {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x86, (byte)0x98, (byte)0x07, (byte)0x00};
    
    boolean appLock;
    boolean appLockForEver;
    byte[] UID;
    boolean personalEnd;
    
    byte keyId = (byte)0x10;
    
    
    public COS()
    {
        
    }
    
    public void createFile(MyAPDU apdu) throws ISOException
    {
     
        short sfi = 0; 
       
        sfi = Util.makeShort(apdu.p1, apdu.p2);
        
       
          
        byte fileType = apdu.buffer[0];
        
        short fileSize = 0;
        fileSize = Util.getShort(apdu.buffer, (byte)1);
      
        byte create = apdu.buffer[3]; //mf & ddf
        byte erase =  apdu.buffer[4]; //mf & ddf
        byte select = apdu.buffer[5]; //ddf
        byte read = create; // ef
        byte write = erase; // ef
       
        if (fileType == File.FILE_TYPE_DF)
        {
            if (apdu.p1 == (byte)0x3F && (apdu.p2 == (byte)0x00))
            {
                if (MF == null)
                {
                   
                    MF = new MF(sfi, fileSize, create, erase);
                    currentDF = MF;
                    apdu.le = 0;
                }
                else
                {
                        ISOException.throwIt(ISO7816.SW_FILE_INVALID);
                }
            }
            else
            {
                if (apdu.lc != (byte)0x08)
                {
                    byte nameLen = (byte)(apdu.lc - (byte)0x08);
                    byte[] name = new byte[nameLen];
                    Util.arrayCopy(apdu.buffer, (short)0x08, name, (short)0, nameLen);
                    
                    
                    ADF adf = new ADF(sfi, name, fileSize, create, erase); //select permission
                    currentDF.addFile(adf);
                    apdu.le = 0;
                }
                else
                {
                    ISOException.throwIt(ISO7816.SW_DATA_INVALID);
                }
            }
        }
        else if (fileType == File.FILE_TYPE_BIN)
        {
            //if (currentPath == null)
            //    throw;
            
            File file = new BinaryFile(sfi, fileSize, read, write);
            currentDF.addFile(file);
            apdu.le = 0;
        }
        else if(fileType == File.FILE_TYPE_RECORD_CYCLER)
        {
            byte recordNum = apdu.buffer[1];
            byte recordLen = apdu.buffer[2];
            
            File file = new RecordFileCycler(sfi, recordNum, recordLen, read, write);
            currentDF.addFile(file);
            apdu.le = 0;
        }
        else if(fileType ==  File.FILE_TYPE_RECORD_FIXED)
        {
            byte recordNum = apdu.buffer[1];
            byte recordLen = apdu.buffer[2];
            
            File file = new RecordFileFixed(sfi, recordNum, recordLen, read, write);
            currentDF.addFile(file);
            apdu.le = 0;
        }
        else if(fileType == File.FILE_TYPE_RECORD_TLV)
        {
            File file = new RecordFileTLV(sfi, fileSize, read, write);
            currentDF.addFile(file);
            apdu.le = 0;
        }
        else if(fileType == File.FILE_TYPE_RECORD_KEY)
        {
            RecordFileKey keyFile = new RecordFileKey(sfi, fileSize, read, write);
            ((DF)currentDF).setKeyFile(keyFile);
            apdu.le = 0;
        }
        else
        {
            ISOException.throwIt(ISO7816.SW_FILE_INVALID);
        }
    }
    
    public void selectFile(MyAPDU apdu) throws ISOException
    {

        // 0x00 select by SFI
        // 0x04 select by name
        byte[] fileName = new byte[apdu.lc];
        Util.arrayCopy(apdu.buffer, (short)0, fileName, (short)0, apdu.lc);
        
        //select AID
        if (Util.arrayCompare(fileName, (short)0, AID, (short)0, (short)AID.length) == 0)
        {
            //return AID FCI
            apdu.le = 0;
            return;
        }
        
        //select PSE
        
        if (apdu.p1 == (byte)0x00 || apdu.p1 == (byte)0x04)
        {
            //select 3F00
            
            File file = currentDF.selectFile(fileName);
      
            if (file != null)
            {
                if (file.getFileType() == File.FILE_TYPE_DF)
                {
                    currentDF = (DF)file;
                    apdu.le = 0;
                    
                    //return PSE FCI
                    
                    
                    //return ADF FCI
                }
                else
                {
                    //select EF
                    currentFile = file;
                    apdu.le = 0;
                  
                }

            }
            else
            {
               // throw file not found;
                ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);
            }
        }
        else
        {
            //throw not support param
            ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
        }
    }
    
    
    public void writeBinary(MyAPDU apdu) throws ISOException
    {
         //0x7F=0111 1111
          //100X XXXX
        byte sfi = (byte) (apdu.p1 & 0x7F);
        
       
        if (apdu.cla == (byte)0x04)
        {
        	if (!apdu.unwrap(keyId))
        		ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        }
        
        
        BinaryFile file = (BinaryFile) currentDF.selectFile(sfi);
        if (file != null)
        {
            byte[] data = new byte[apdu.lc];
            Util.arrayCopyNonAtomic(apdu.buffer, (short)0, data, (short)0, apdu.lc);
            
            file.setData(data);
        }
        else
        {
            ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);
        }
    }
    
    public void readBinary(MyAPDU apdu) throws ISOException
    {
    	if (apdu.cla == (byte)0x04)
        {
        	if (!apdu.unwrap(keyId))
        		ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        }
    	
         //0x7F=0111 1111
        //100X XXXX
        byte sfi = (byte) (apdu.p1 & (byte)0x7F);
        
       
       
        
        BinaryFile file = (BinaryFile) currentDF.selectFile(sfi);
        if (file != null)
        {
            byte[] out = file.getData();
            
            Util.arrayCopy(out, (byte)0x00, apdu.buffer, (short)0x00, (short)out.length);
            apdu.le = (short)out.length;
            
        }
        else
        {
            ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);
        }
          
    }
    
    public void updateRecord(MyAPDU apdu) throws ISOException
    {
       
        
        byte recordId = 0;
        byte tag = 0;
        
        //sfi
        //XXXX X000 //tag
        //XXXX X100 //id
        
        if (apdu.cla == (byte)0x04)
        {
        	if (!apdu.unwrap(keyId))
        		ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        }
       
        
        byte t = (byte)(apdu.p2 << 5);
        if (t == (byte)0x00) //0000 0000
        {
            tag = apdu.p1;
        }
        else if (t == (byte)0x80) //0000 0100 <<5         1000 0000
        {
            recordId = apdu.p1;
        }
        else
        {
        }
         
        byte sfi = (byte)((apdu.p2 & 0xF8) >> 3);
        
       byte[] record = new byte[apdu.lc];
       Util.arrayCopy(apdu.buffer, (short)0, record, (short)0, apdu.lc);
        
        if (sfi == (byte)0x00)
        {
           RecordFileKey keyFile = ((DF)currentDF).getKeyFile();
            if (keyFile != null)
            {
                if (t == (byte)0x00)
                {
                    keyFile.updateKeyByTag(tag, record);
                }
                else
                {
                    keyFile.updateKeyById(recordId, record);
                }
            }
            else
            {
                 ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);
            }
        }
        else
        {
        
            RecordFile file = (RecordFile) currentDF.selectFile(sfi);
            if (file != null)
            {
                if (t == (byte)0x00)
                {
                    if (!file.updateRecordByTag(tag, record))
                        ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
                }
                else
                {
                    if (!file.updateRecordById(recordId, record))
                        ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
                }
            }
            else
            {
                ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);
            }
        }
         
    }
    
    public void appendRecord(MyAPDU apdu) throws ISOException
    {
        byte recordId = 0;
        byte tag = 0;
        
        //sfi
        //XXXX X000 //tag
        //XXXX X100 //id
        
        if (apdu.cla == (byte)0x04)
        {
        	if (!apdu.unwrap(keyId))
        		ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        }
       
        
        byte t = (byte)(apdu.p2 << 5);
        if (t == (byte)0x00) //0000 0000
        {
            tag = apdu.p1;
            ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);
        }
        else if (t == (byte)0x80) //0000 0100 <<5         1000 0000
        {
            recordId = apdu.p1;
        }
        else
        {
        }
         
        byte sfi = (byte)((apdu.p2 & 0xF8) >> 3);
        
        
        
        byte[] record = new byte[apdu.lc];
        
        Util.arrayCopy(apdu.buffer, (short)0, record, (short)0, apdu.lc);
        
        if (sfi == (byte)0x00)
        {
            RecordFileKey keyFile = ((DF)currentDF).getKeyFile();
            if (keyFile != null)
            {
                keyFile.addKey(recordId, record);
            }
            else
            {
                 ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);
            }
        }
        else
        {
        
            RecordFile file = (RecordFile) currentDF.selectFile(sfi);
            if (file != null)
            {
               if (!file.addRecord(recordId, record))
                   ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
            }
            else
            {
                ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);
            }
        }
    }
    
    
    public void readRecord(MyAPDU apdu) throws ISOException
    {
        byte[] buffer = apdu.getBuffer();
        
        byte recordId = 0;
        byte tag = 0;
        
        if (apdu.cla == (byte)0x04)
        {
        	if (!apdu.unwrap(keyId))
        		ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        }
       
        
        //sfi
        //XXXX X000 //tag
        //XXXX X100 //id
       
        
        byte t = (byte)(apdu.p2 << 5);
        
        
        if (t == (byte)0x00) //0000 0000
        {
            tag = apdu.p1;
        }
        else if (t == (byte)0x80) //0000 0100 <<5         1000 0000
        {
            recordId = apdu.p1;
           
        }
        else
        {
            
        }
        
        //0xF8=1111 1000
        byte sfi = (byte)((apdu.p2 & 0xF8) >> 3);
        
        
        
        
        if (sfi == 0x00)
        {
            
        }
        else
        {
            RecordFile file = (RecordFile) currentDF.selectFile(sfi);
            if (file != null)
            {
                byte[] data = null;
                if (t == (byte)0x00)
                    data = file.getRecordByTag(tag);
                else
                    data = file.getRecordById(recordId);

                if (data != null)
                {
                    Util.arrayCopy(data, (byte)0x00, buffer, (short)0x00, (short)data.length);
                    apdu.le = (short)data.length;
                    
                }
            }
            else
            {
                ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);
            }
        }
    }
    
    
    public void writeKey(MyAPDU apdu)
    {
        
    }
    
  
    
    public void writeUID(MyAPDU apdu) throws ISOException
    {
        if (apdu.cla != (byte)0x00)
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        
        if (apdu.ins != (byte)0x22)
            ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        
        if (apdu.p1 != (byte)0x00)
            ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
        
        if (apdu.p2 != (byte)0x00)
            ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
        
        UID = new byte[apdu.lc];
        Util.arrayCopy(apdu.getBuffer(), (short)0, UID, (short)0, apdu.lc);
        
        apdu.le = 0;
    }
    
    public void getMessage(MyAPDU apdu) throws ISOException
    {
         if (apdu.cla != (byte)0x80)
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
            
        if (apdu.ins != (byte)0xCA)
        {
            ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
        
        if (UID == null)
            ISOException.throwIt(ISO7816.SW_WARNING_STATE_UNCHANGED);
            
        short len = (short)UID.length;
        
        Util.arrayCopy(UID, (short)0, apdu.buffer, (short)0, (short)len);
        
        apdu.le = len;
        
    }
    
    public void cardBlock(MyAPDU apdu) throws ISOException
    {
        if (apdu.cla != (byte)0x84)
        {
        	ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }
        else
        {
        	if (!apdu.unwrap(keyId))
        		ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        }
        
        if (apdu.ins != (byte)0x16)
        	ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        
        if (apdu.p1 != (byte)0x00)
        	ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
        
        if (apdu.p2 != (byte)0x00)
        	ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
    }
    
    public void appBlock(MyAPDU apdu) throws ISOException
    {
        if (apdu.cla != (byte)0x84)
        {
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }
        else
        {
        	if (!apdu.unwrap(keyId))
        		ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        }
        
        if (apdu.ins != (byte)0x1E)
        {
        	ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
        
        if (apdu.p2 == (byte)0x00)
            appLock = true;
        else if (apdu.p2 == (byte)0x01)
            appLockForEver = true;
        else
            ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
        
        apdu.le = 0;
    }
    
    public void appUnBlock(MyAPDU apdu) throws ISOException
    {
        if (apdu.cla != (byte)0x84)
        {
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }
        else
        {
        	if (!apdu.unwrap(keyId))
        		ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        }
        
        if (apdu.ins != (byte)0x18)
        {
        	ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
        
        if(appLockForEver)
        	ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        
        
        
        if(appLock)
             appLock = false;
        else 
             ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        
        apdu.le = (short)0;
    }
    
    
    public void getResponse(MyAPDU apdu)
    {
       
        
        
    }
    
    public KEY loadKey(byte keyIndex)
    {
        RecordFileKey keyFile = currentDF.getKeyFile();
        
        if (keyFile != null)
        {
            KEY key = keyFile.getKeyByTag(keyIndex);
            return key;
        }
        
        return null;
    }
    
    public boolean updateCAPPPurchaseRecord(short sfi, byte tag, byte[] record)
    {
        boolean ret = false;
        
        RecordFileTLV tlv = (RecordFileTLV) currentDF.selectFile(sfi);
        if (tlv != null)
        {
            ret = tlv.updateRecordByTag(tag, record);
        }
        
        
        
        return ret;
    }
    
    //cycler record file
    public boolean appendLog(short sfi, byte[] log)
    {
        boolean ret = false;
        
        RecordFileCycler logFile =  (RecordFileCycler) currentDF.selectFile(sfi);
        if (logFile != null)
        {
            ret = logFile.addRecord(log);
            
        }
        
        return ret;
    }
    
    public void personalEnd(MyAPDU apdu)
    {
        if (apdu.cla != (byte)0x00)
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
            
        if (apdu.ins != (byte)0x08)
        {
            ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
        
        personalEnd = true;
        
        apdu.le = 0;
    }
}

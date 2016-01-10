package com.cssweb.applet.changsha;

import javacard.framework.AID;
import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.Util;
import javacard.security.DESKey;
import javacard.security.Key;
import javacard.security.KeyBuilder;
import javacard.security.RandomData;
import javacard.security.Signature;
import javacardx.crypto.Cipher;


/**
 *
 * @author chenhf
 */
public class Main extends Applet {
    
    MyAPDU  apduin;
   
    Changsha changsha;
    MyRandom random;
    
    
    
    
    public static void install(byte[] bArray, short bOffset, byte bLength) {
        
        
        
        new Main(bArray, bOffset, bLength);
    }

   
    protected Main(byte[] bArray, short bOffset, byte bLength) {
       
    	random = new MyRandom();
    	
     
        changsha = new Changsha(random);
        apduin = new MyAPDU(random);
        
       
       
    
        
        /*
        short aidLen = bArray[bOffset];
        byte[] aid = new byte[aidLen];
        Util.arrayCopy(bArray, (short)(bOffset+1), aid, (short)0, aidLen);
        
        short ctrlInfoLen = bArray[bOffset + aidLen + 1];
        byte[] ctrlInfo = new byte[ctrlInfoLen];
        Util.arrayCopy(bArray, (short)(bOffset + aidLen + 2), ctrlInfo, (short)0, ctrlInfoLen);
        
        short argsLen = bArray[bOffset + aidLen + ctrlInfoLen + 2];
        byte[] args = new byte[argsLen];
        Util.arrayCopy(bArray, (short)(bOffset + aidLen + ctrlInfoLen + 2), args, (short)0, argsLen);
        */
        
        byte aidLen = bArray[bOffset];
        if (aidLen== (byte)0){
            register();
        } else {
            register(bArray, (short)(bOffset+1), aidLen);
        }
    }
    
    
    public boolean select()
    {
        return true;
    }
    
   
    public void deselect()
    {
       
    }

   
    public void process(APDU apdu) throws ISOException {
        short bytesRead;
        short echoOffset;
        short  dl;
        boolean  rc=false;
        
        byte[] apduBuffer = apdu.getBuffer();
        
        apduin.cla = (byte)apduBuffer[ISO7816.OFFSET_CLA];
        apduin.ins = (byte)apduBuffer[ISO7816.OFFSET_INS];
        apduin.p1 = (byte)apduBuffer[ISO7816.OFFSET_P1];
        apduin.p2 = (byte)apduBuffer[ISO7816.OFFSET_P2];
        apduin.lc = (short)(apduBuffer[ISO7816.OFFSET_LC]& 0x0FF);
        

        // select AID (instance id) return AID FCI;
        
        if (Config.appLockForEver)
        	ISOException.throwIt(MyAPDU.SW_E_APPBLK);
        
        if( apduin.APDUContainData(apduin.ins)) 
        {
           bytesRead = apdu.setIncomingAndReceive();
           echoOffset = (short)0;

           while ( bytesRead > 0 ) {
              Util.arrayCopyNonAtomic(apduBuffer, ISO7816.OFFSET_CDATA, apduin.buffer, echoOffset, bytesRead);
              echoOffset += bytesRead;
              bytesRead = apdu.receiveBytes(ISO7816.OFFSET_CDATA);
           }
           apduin.lc = echoOffset;

        }
        else 
        {
           apduin.le = apduin.lc;
           apduin.lc = (short)0;
        }
        
        if (!Config.appLock)
        {
	        if (!Config.personalEnd)
	        {
	        	//尚未个人化，改成switch
	        	
	        	if (apduin.ins == INS.SELECT)
	        	{
	        		changsha.select(apduin);
	        	}
	        	else if (apduin.ins == INS.WRITE_KEY)
	        	{
	        		changsha.writeKey(apduin);
	        	}
	        	else if (apduin.ins == INS.WRITE_UID)
	        	{
	        		changsha.writeUID(apduin);
	        	}
	        	else if (apduin.ins == INS.GET_CHALLENGE)
	        	{
	        		changsha.challenge(apduin);
	        	}
	        	else if (apduin.ins == INS.EXTERNAL_AUTH)
	        	{
	        		changsha.externalAuth(apduin);
	        	}
	        	else if (apduin.ins == INS.WRITE_BINARY)
	        	{
	        		changsha.writeBinary(apduin);
	        	}
	        	else if (apduin.cla == (byte)0x04 && apduin.ins == (byte)0xDC)
	        	{
	                changsha.updateRecord(apduin);
	        	}
	        	else if (apduin.ins == INS.PERSONAL_END)
	        	{
	        		changsha.personalEnd(apduin);
	        	}
	        	else if (apduin.ins == INS.APP_BLOCK)
	        	{
	        		changsha.appBlock(apduin);
	        	}
	        	
	        	else if (apduin.ins == INS.CARD_BLOCK)
	        	{
	        		changsha.cardBlock(apduin);
	        	}
	        	else
	        	{
	        		ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
	        	}
	        }
	        else
	        {
	        	// 已完成个人化，改成switch
	        	if (apduin.ins == INS.SELECT)
	        	{
	        		changsha.select(apduin);
	        	}
	        	else if (apduin.ins == INS.GET_MESSAGE)
	        	{
	        		changsha.getMessage(apduin);
	        	}
	        	else if (apduin.ins == INS.GET_CHALLENGE)
	        	{
	        		changsha.challenge(apduin);
	        	}
	        	else if (apduin.ins == INS.EXTERNAL_AUTH)
	        	{
	        		changsha.externalAuth(apduin);
	        	}
	        	else if (apduin.ins == INS.READ_BINARY)
	        	{
	        		changsha.readBinary(apduin);
	        	}
	        	else if (apduin.ins == INS.READ_RECORD)
	        	{
	        		changsha.readRecord(apduin);
	        	}
	        	else if (apduin.ins == INS.GET_BALANCE)
	        	{
	        		changsha.getBalance(apduin);
	        	}
	        	else if (apduin.ins == INS.VERIFY_PIN)
	        	{
	        		changsha.verifyPIN(apduin);
	        	}
	        	else if (apduin.ins == INS.INIT_PURCHASE_CHARGE)
	        	{
	        		if (apduBuffer[ISO7816.OFFSET_P1] == (byte)0x00)
	                {
	                	changsha.loadInit(apduin);
	                }
	                else if (apduBuffer[ISO7816.OFFSET_P1] == (byte)0x01 || apduBuffer[ISO7816.OFFSET_P1] == (byte)0x03)
	                {
	                	changsha.purchaseInit(apduin);
	                }
	                else
	                {
	                
	                }
	        	}
	        	else if (apduin.ins == INS.LOAD)
	        	{
	        		changsha.load(apduin);
	        	}
	        	else if (apduin.cla == (byte)0x80 && apduin.ins == (byte)0xDC)
	        	{
	                changsha.cappPurchaseUpdate(apduin);
	        	}
	        	else if (apduin.ins == INS.PURCHASE)
	        	{
	        		changsha.purchase(apduin);
	        	}
	        	else if (apduin.ins == INS.GET_TRANSACTION_PROVE)
	        	{
	        		changsha.getTransactionProve(apduin);
	        	}
	        	else if (apduin.ins == INS.APP_BLOCK)
	        	{
	        		changsha.appBlock(apduin);
	        	}
	        	
	        	else if (apduin.ins == INS.CARD_BLOCK)
	        	{
	        		changsha.cardBlock(apduin);
	        	}
	        	else
	        	{
	        		ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
	        	}
	        }
        }
        else
        {
        	if (apduin.ins == INS.GET_CHALLENGE)
        	{
        		changsha.challenge(apduin);
        	}
        	else if (apduin.ins == INS.APP_UNBLOCK)
        	{
        		changsha.appUnBlock(apduin);
        	}
        	else
        	{
        		ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED); 
        	}
        }
    
       // if (rc) {
           dl = apduin.le;
           if(dl>(short)0) {
              Util.arrayCopyNonAtomic(apduin.buffer,(short)0, apduBuffer,(short)0,dl);
              apdu.setOutgoingAndSend((short)0, apduin.le);
           }
      //  }
        
	
    }//end process
    
    
    
}

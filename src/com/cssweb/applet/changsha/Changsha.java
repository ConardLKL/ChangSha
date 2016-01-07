/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cssweb.applet.changsha;


import javacard.framework.APDU;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.Util;
import javacard.security.RandomData;

/**
 *
 * @author chenh
 */
public class Changsha {
    static final byte TRADE_TYPE_PURCHASE = (byte)0x06;
    static final byte TRADE_TYPE_CHARGE = (byte)0x02;
    static final byte TRADE_TYPE_CAPP_PURCHASE = (byte)0x09;
    
    
    short purchaseTradeId = 0;
    short chargeTradeId = 0;
    byte[] money = new byte[4];
    byte[] terminalId = new byte[6]; 
    byte[] purchaseKey = null;
    
    byte[] temp = JCSystem.makeTransientByteArray((short)32, JCSystem.CLEAR_ON_DESELECT);
   
    byte[] MAC2 = JCSystem.makeTransientByteArray((short)8, JCSystem.CLEAR_ON_DESELECT);
    byte[] TAC = JCSystem.makeTransientByteArray((short)8, JCSystem.CLEAR_ON_DESELECT);
    byte[] MAC1 = JCSystem.makeTransientByteArray((short)8, JCSystem.CLEAR_ON_DESELECT);
    byte[] iv = JCSystem.makeTransientByteArray((short)8, JCSystem.CLEAR_ON_DESELECT);
    byte[] purchaseSessionKey = JCSystem.makeTransientByteArray((short)16, JCSystem.CLEAR_ON_DESELECT);
    byte[] chargeSessionKey = JCSystem.makeTransientByteArray((short)8, JCSystem.CLEAR_ON_DESELECT);
    
   
 
    
    boolean appLock;
    boolean appLockForEver;
   
    boolean personalEnd;
    
   
    
   
    MyRandom myRandom;
    byte[] random;
    byte[] UID;
    
    File issue;
    File app, personal;
    EP balance;
    File logLocalPurchase, logCharge, logRemotePurchase;
    File helper, reserved;
    
    File cappPurchase;
    byte[] cappPurchaseRecord = null;
    
    public static final byte PATH_MF = (byte)0x00;
    public static final byte PATH_ADF = (byte)0x01;
    byte path;
    
    //传输密钥
    KEY keyTrans;
    //keyId keyVersion algId errorCount
    public static final byte[] TRANS_KEY = {(byte)0x14, (byte)0x01, (byte)0x00, (byte)0x33, (byte)0x00, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0x80, (byte)0x00, (byte)0x00};
    
    
    KEY MFDCCK, MFDCMK;
    KEY ADFDCCK, ADFDCMK, ADFDCMK01, ADFDCMK02, DPK, DLK, DTK, PIN, DABK, DAUK, DPUK, DPRK;
    
    
    public static final byte KEY_TAG_DCCK = (byte)0x39; //主控密钥
    
    public static final byte KEY_TAG_DCMK = (byte)0x36; //维护密钥
    //ADFDCMK01，ADFDCMK02 0x36
    public static final byte KEY_TAG_APPDCMK = (byte)0x50; //应用维护密钥
  
    
    
    public static final byte KEY_TAG_DPK = (byte)0x06; //消费密钥
    public static final byte KEY_TAG_DLK = (byte)0x07; //充值密钥
    public static final byte KEY_TAG_DTK = (byte)0x08; //TAC密钥
    
    //public static final byte KEY_TAG_PIN = (byte); //PIN密钥
    public static final byte KEY_TAG_DPUK = (byte)0x04;//PIN解锁密钥
    public static final byte KEY_TAG_DPRK = (byte)0x05;//PIN重装密钥
    
    public static final byte KEY_TAG_DABK = (byte)0x33;//应用锁定密钥
    public static final byte KEY_TAG_DAUK = (byte)0x34;//应用解锁密钥
   
    
    
    
    public Changsha(MyRandom rand)
    {
       
        myRandom = rand;
        
        issue = new BinaryFile((short)0x05, (short)0x28, (byte)0xf0, (byte)0xf0);
        app = new BinaryFile((short)0x15, (short)0x1E, (byte)0xf0, (byte)0xf0);
        personal = new BinaryFile((short)0x16, (short)0x5C, (byte)0xf0, (byte)0xf0);
        
        balance = new EP((byte)0x02, (byte)0x43, (byte)0xf0, (byte)0xf0);
        
        
        logLocalPurchase = new RecordFileCycler((short)0x18, (byte)0x0A, (byte)0x17, (byte)0xf0, (byte)0xf0);
        logRemotePurchase = new RecordFileCycler((short)0x10, (byte)0x0A, (byte)0x17, (byte)0xf0, (byte)0xf0);
        logCharge = new RecordFileCycler((short)0x1A, (byte)0x0A, (byte)0x17, (byte)0xf0, (byte)0xf0);
        
        cappPurchase = new RecordFileTLV((short)0x17, (short)0x150, (byte)0xf0, (byte)0xf0);
        
        helper = new BinaryFile((short)0x11, (short)0x20, (byte)0xf0, (byte)0xf0);
        reserved = new BinaryFile((short)0x12, (short)0x20, (byte)0xf0, (byte)0xf0);
        
        keyTrans = new KEY((byte)0x00, TRANS_KEY);
    }
    
    //gen random
    public void challenge(MyAPDU apdu) throws ISOException
    {
        byte[] buffer = apdu.getBuffer();
        
        
        myRandom.genRandom();
        random = myRandom.getRandom();
        
        Util.arrayCopyNonAtomic(random, (short)0, buffer, (short)0, (short)4);
        
        apdu.le = 4;
    }
    
    
    
   
    public void externalAuth(MyAPDU apdu) throws ISOException
    {
        if (apdu.cla != (byte)0x00 )
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        
        if ( apdu.ins != (byte)0x82)
            ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        
        if (apdu.lc != 8)
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        
        
        byte[] key = {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
        
        byte[] in = new byte[8];
        //myRandom.genRandom();
        random = myRandom.getRandom();
        Util.arrayCopy(random, (short)0, in, (short)0, (short)4);
        in[4] = (byte)0x00;
        in[5] = (byte)0x00;
        in[6] = (byte)0x00;
        in[7] = (byte)0x00;
        
        byte[] out = new byte[8];
        ALG.encrypt(key, in, (short)0, (short)8, out, (short)0);
        
       
        
        
        if (Util.arrayCompare(apdu.buffer, (short)0, out, (short)0, (short)8) != 0)
            ISOException.throwIt(ISO7816.SW_WRONG_DATA);
            
        apdu.le = 0;
    }
    
    
    public boolean getBalance(MyAPDU apdu) throws ISOException
    {
        byte[] buffer = apdu.buffer;
      
        byte[] baBalance = balance.toBytes();
        Util.arrayCopy(baBalance, (short)0, buffer, (short)0, (short)4);
        
        apdu.le = (short)4;
        
        
        
        return true;
    }
    

    public void chargeInit(MyAPDU apdu) throws ISOException
    {
    	if (apdu.p1 != (byte)0x00)
    		ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
    	
    	if (apdu.p2 != (byte)0x02)
    		ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
    	
    	
    	byte[] buffer = apdu.getBuffer();
        // buffer = CLA INS P1 P2 LC keyIndex1 money4 terminalId6

        // get key index
        byte keyId = buffer[0];
        
        
        
        // get purchase money
        Util.arrayCopy(buffer, (short)1, money, (short)0, (short)4); 
        // get terminal id
        Util.arrayCopy(buffer, (short)5, terminalId, (short)0, (short)6);
        //end

        
        //gen charge session key
        myRandom.genRandom();
        random = myRandom.getRandom();
        Util.arrayCopy(random, (short)0, temp, (short)0, (short)4);
        Util.setShort(temp, (short)4, chargeTradeId);
        
        byte[] chargeKey = DLK.getKey();
        ALG.genSessionKey(chargeKey, temp, (short)6, chargeSessionKey);
        
        
        // gen MAC1
        // balance4 money4 tradeType1 ternimalId6
        byte[] baBalance = balance.toBytes();
        Util.arrayCopy(baBalance, (short)0, temp, (short)0, (short)4);
        Util.arrayCopy(money, (short)0, temp, (short)4, (short)4);
        temp[8] = TRADE_TYPE_CHARGE;
        Util.arrayCopy(terminalId, (short)0, temp, (short)9, (short)6);
        ALG.genMACOrTAC(chargeSessionKey, iv, temp, (byte)15, MAC1);
        
        
        
        // response buffer = balance4, chargeTradeId2 keyVersion1 AlgId1 random4 MAC(1)4
        Util.arrayCopy(baBalance, (short)0, buffer, (short)0, (short)4);
        Util.setShort(buffer, (short)4, chargeTradeId);
        buffer[6] = DLK.getKeyVersion();
        buffer[7] = DLK.getAlgId();
        Util.arrayCopy(random, (short)0, buffer, (short)8, (short)4); // random
        Util.arrayCopy(MAC1, (short)0, buffer, (short)12, (short)4);
        apdu.le = 16;
    }
   
    public void charge(MyAPDU apdu) throws ISOException
    {
        byte[] buffer = apdu.getBuffer();
        
        // buffer = CLA INS P1 P2 LC ternimalDate4 ternimalTime3 MAC(2)4
        byte[] ternimalDatetime = new byte[7];
        Util.arrayCopy(buffer, (short)0, ternimalDatetime, (short)0, (short)7);
        byte[] mac2 = new byte[4];
        Util.arrayCopy(buffer, (short)7, mac2, (short)0, (short)4);
        
        
        
        // GET MAC2 = money4 tradeType1 ternimalId6 terminalDatetime7
        Util.arrayCopy(money, (short)0, temp, (short)0, (short)4);
        temp[4] = TRADE_TYPE_CHARGE;
        Util.arrayCopy(terminalId, (short)0, temp, (short)5, (short)6);
        Util.arrayCopy(ternimalDatetime, (short)0, temp, (short)11, (short)7);
        
        ALG.genMACOrTAC(chargeSessionKey, iv, temp, (byte)18, MAC2);
        
        if (Util.arrayCompare(MAC2, (short)0, mac2, (short)0, (short)4) != 0)
        {
            ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
        }
        
        
        JCSystem.beginTransaction();
        balance.add(money, (byte)1);
        chargeTradeId++;
        
        byte[] log = new byte[23];
        Util.setShort(log, (short)0, chargeTradeId);
        Util.arrayCopy(money, (short)0, log, (short)5, (short)4);
        log[9] = TRADE_TYPE_CHARGE;
        Util.arrayCopy(terminalId, (short)0, log, (short)10, (short)6);
        Util.arrayCopy(ternimalDatetime, (short)0, log, (short)16, (short)7);
        
        ((RecordFileCycler)logCharge).addRecord(log);
        
        JCSystem.commitTransaction();
        
        
        
        // response buffer = TAC4
        // balance4 chargeTradeId2 money4 tradeType1 ternimalId6 terminalDatetime7
        byte[] baBalance = balance.toBytes();
        Util.arrayCopy(baBalance, (short)0, temp, (short)0, (short)4);
        Util.setShort(temp, (short)4, chargeTradeId);
        Util.arrayCopy(money, (short)0, temp, (short)6, (short)4);
        temp[10] = TRADE_TYPE_CHARGE;
        Util.arrayCopy(terminalId, (short)0, temp, (short)11, (short)6);
        Util.arrayCopy(ternimalDatetime, (short)0, temp, (short)17, (short)7);
        
        byte[] left = new byte[8];
        byte[] right = new byte[8];
        
        
        
        byte[] TACKey = DTK.getKey();
        
        Util.arrayCopy(TACKey, (short)0, left, (short)0, (short)8);
        Util.arrayCopy(TACKey, (short)8, right, (short)0, (short)8);
        byte[] tacKey = ALG.bytesXOR(left, right);
        
        
        
        ALG.genMACOrTAC(tacKey, iv, temp, (byte)24, TAC);
        
        
        Util.arrayCopy(TAC, (short)0, buffer, (short)0, (short)4);
        apdu.le = 4;   
    }
    
    public void purchaseInit(MyAPDU apdu) throws ISOException
    {
        byte[] buffer = apdu.getBuffer();
        
        //buffer = CLA INS P1 P2 LC keyIndex1 money4 terminalId6
        // get key index
        byte keyId = buffer[0];
        
        purchaseKey = DPK.getKey();
        
        // get purchase money
        byte[] temp = new byte[4];
        Util.arrayCopy(buffer, (short)1, temp, (short)0, (short)4);
        if (balance.subtract(temp, (byte)0) != 0)
        {
            ISOException.throwIt(ISO7816.SW_WRONG_DATA);
        }
        else
        {
            money = temp;
        }
        // get terminal number
        Util.arrayCopy(buffer, (short)5, terminalId, (short)0, (short)6);
        //end
        
        
        
        // return buffer = balance4 purchaseTradeId2 keyVersion1 algId1 random4
        // set balance
        byte[] baBalance = balance.toBytes();
        Util.arrayCopy(baBalance, (short)0, buffer, (short)0, (short)4);
        Util.setShort(buffer, (short)4, purchaseTradeId);
        buffer[6] = DPK.getKeyVersion();
        buffer[7] = DPK.getAlgId();
        myRandom.genRandom();
        random = myRandom.getRandom();
        Util.arrayCopy(random, (short)0, buffer, (short)8, (short)4);
        apdu.le = 12;
    }
    
   
    
   
    public void purchase(MyAPDU apdu) throws ISOException
    {
        byte[] buffer = apdu.getBuffer();
        
        
        //buffer = CLA INS P1 P2 LC ternimalTradeId4 terminalDate4 terminalTime3 MAC(1)4
        byte[] ternimalTradeId = new byte[4];
        Util.arrayCopy(buffer, (short)0, ternimalTradeId, (short)0, (short)4);
        byte[] ternimalDatetime = new byte[7];
        Util.arrayCopy(buffer, (short)4, ternimalDatetime, (short)0, (short)7);
        byte[] mac1 = new byte[4];
        Util.arrayCopy(buffer, (short)11, mac1, (short)0, (short)4);
        
        
        // gen session key
        //temp = random4 purchaseTradeId2 ternimalTradeId(right 2)
        Util.arrayCopy(random, (short)0, temp, (short)0, (short)4);
        Util.setShort(temp, (short)4, purchaseTradeId);
        // ternimalTradeId right 2byte???
        Util.arrayCopy(ternimalTradeId, (short)2, temp, (short)6, (short)2); 
        
        ALG.genSessionKey(purchaseKey, temp, (short)8, purchaseSessionKey);//return 32 bytes
        // end
        
        
        
        // gen MAC1
        //temp = money4 tradeType1 ternimalId6 terminalDate4 ternimalTime3
        Util.arrayCopy(money, (short)0, temp, (short)0, (short)4);
        temp[4] = TRADE_TYPE_PURCHASE;
        Util.arrayCopy(terminalId, (short)0, temp, (short)5, (short)6);
        Util.arrayCopy(ternimalDatetime, (short)0, temp, (short)11, (short)7); // terminalDate terminlaTime
        
        ALG.genMACOrTAC(purchaseSessionKey, iv, temp, (byte)18, MAC1);
        
        if (Util.arrayCompare(MAC1, (short)0, mac1, (short)0, (short)4) != 0)
        {
            ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
        }
        //end
        
        
        
        //gen TAC
        //buffer = CLA INS P1 P2 LC ternimalTradeId4 terminalDate4 terminalTime3 MAC(1)4
        //temp copy before money4 tradeType1 ternimalId6 ternimalDatetime7
        //temp copy after  money4 tradeType1 ternimalId6 (ternimalTradeId4 terminalDate4 terminalTime3)
        Util.arrayCopy(buffer, (short)0, temp, (short)11, (short)11);
        
        byte[] left = new byte[8];
        byte[] right = new byte[8];
        
       
        byte[] TACKey = DTK.getKey();
        
        Util.arrayCopy(TACKey, (short)0, left, (short)0, (short)8);
        Util.arrayCopy(TACKey, (short)8, right, (short)0, (short)8);
        byte[] tacKey = ALG.bytesXOR(left, right);
        
        ALG.genMACOrTAC(tacKey, iv, temp, (byte)22, TAC);
        //end
        
        
        
        //gen MAC2
        ALG.genMACOrTAC(purchaseSessionKey, iv, money, (byte)4, MAC2);
        //end
        
        
        
        //
        JCSystem.beginTransaction();
        balance.subtract(money, (byte)1);
        purchaseTradeId++;

        byte[] log = new byte[23];
        Util.setShort(log, (short)0, purchaseTradeId);
        Util.arrayCopy(money, (short)0, log, (short)5, (short)4);
        log[9] = TRADE_TYPE_PURCHASE;
        Util.arrayCopy(terminalId, (short)0, log, (short)10, (short)6);
        Util.arrayCopy(ternimalDatetime, (short)0, log, (short)16, (short)7);
        
        ((RecordFileCycler)logLocalPurchase).addRecord(log);
        

        JCSystem.commitTransaction();
        
        
        
        // return buffer = TAC4 MAC(2)4
        Util.arrayCopy(TAC, (short)0, buffer, (short)0, (short)4);
        Util.arrayCopy(MAC2, (short)0, buffer, (short)4, (short)4);
        
        apdu.le = 8;
    }
    
    
    
    
    public void cappPurchaseInit(MyAPDU apdu) throws ISOException
    {
        byte[] buffer = apdu.getBuffer();
        
        // 80 50 03 02 0B keyIndex1 money4 ternimalId6
        //begin money is 0
        //end money is actual money

        // get key index
        byte keyId = buffer[0];
        
        purchaseKey = DPK.getKey();
        Util.arrayCopy(buffer, (short)1, money, (short)0, (short)4);
        Util.arrayCopy(buffer, (short)5, terminalId, (short)0, (short)6);

        // is allow overflow???
        if (balance.subtract(money, (byte)0) != 0 )
        {
            ISOException.throwIt(ISO7816.SW_WRONG_DATA);
        }
      
        
        // return buffer = balance4 purchaseTradeId2 keyVersion1 algId1 random4
        //pboc response buffer balance4 purchaseTradeId2 overdrawLimit3 keyVersion1 algId1 random4
        // set balance
        byte[] baBalance = balance.toBytes();
        Util.arrayCopy(baBalance, (short)0, buffer, (short)0, (short)4);
        
        
        //set purchase trade num
        Util.setShort(buffer, (short)4, purchaseTradeId);
        
        // get keyVersion, algId by keyIndex
        // set key version
        buffer[6] = DPK.getKeyVersion();
        // set alg type
        buffer[7] = DPK.getAlgId();
        
        // set random
        myRandom.genRandom();
        random = myRandom.getRandom();
        Util.arrayCopy(random, (short)0, buffer, (short)8, (short)4);

        apdu.le = 12;
    }
    
 
    public void cappPurchaseUpdate(MyAPDU apdu) throws ISOException
    {
        byte[] buffer = apdu.getBuffer();
         
        //begin tradeStatus set not 00
        //end tradeStatus set 00
        

        byte tag = apdu.p1;

        
        byte sfi = (byte)((apdu.p2 & 0xFF) >> 3);
        
        if (sfi != 0x17)
             ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
            
        byte[] data = new byte[48];
        Util.arrayCopy(buffer, (short)0, data, (short)0, (short)48);
        
        cappPurchaseRecord = data;
        apdu.le = 0;
    }
    
  
    public void cappPurchase(MyAPDU apdu) throws ISOException
    {
        byte[] buffer = apdu.getBuffer();
        // 80 54 01 00 0f 
        //buffer = CLA INS P1 P2 LC ternimalTradeId4 terminalDate4 terminalTime3 MAC(1)4
        byte[] ternimalTradeId = new byte[4];
        Util.arrayCopy(buffer, (short)0, ternimalTradeId, (short)0, (short)4);
        byte[] terminalDatetime = new byte[7];
        Util.arrayCopy(buffer, (short)4, terminalDatetime, (short)0, (short)7);
        byte[] mac1 = new byte[4];
        Util.arrayCopy(buffer, (short)11, mac1, (short)0, (short)4);
        
        
        // gen session key
        //temp = random4 purchaseTradeId2 ternimalTradeId(right 2)
        Util.arrayCopy(random, (short)0, temp, (short)0, (short)4);
        Util.setShort(temp, (short)4, purchaseTradeId);
        // ternimalTradeId right 2byte???
        Util.arrayCopy(ternimalTradeId, (short)2, temp, (short)6, (short)2); 
        
        ALG.genSessionKey(purchaseKey, temp, (short)8, purchaseSessionKey);
        // end
        
        
        
        // gen MAC1
        //temp = money4 tradeType1 ternimalId6 terminalDate4 ternimalTime3 UID
        Util.arrayCopy(money, (short)0, temp, (short)0, (short)4);
        temp[4] = TRADE_TYPE_CAPP_PURCHASE;
        Util.arrayCopy(terminalId, (short)0, temp, (short)5, (short)6);
        Util.arrayCopy(terminalDatetime, (short)0, temp, (short)11, (short)7); // terminalDate terminlaTime
        Util.arrayCopy(UID, (short)0, temp, (short)18, (short)UID.length);//用户卡安全认证识别码
        
        ALG.genMACOrTAC(purchaseSessionKey, iv, temp, (byte)27, MAC1);
        if (Util.arrayCompare(MAC1, (short)0, mac1, (short)0, (short)4) != 0)
        {
            ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
        }
        //end
        
       
        //gen TAC
        //buffer = CLA INS P1 P2 LC ternimalTradeId4 terminalDate4 terminalTime3 MAC(1)4
        //temp copy before money4 tradeType1 ternimalId6 ternimalDatetime7
        //temp copy after  money4 tradeType1 ternimalId6 (ternimalTradeId4 terminalDate4 terminalTime3)
        Util.arrayCopy(buffer, (short)0, temp, (short)11, (short)11);
        
        byte[] left = new byte[8];
        byte[] right = new byte[8];
        
        byte[] TACKey = DTK.getKey();
        Util.arrayCopy(TACKey, (short)0, left, (short)0, (short)8);
        Util.arrayCopy(TACKey, (short)8, right, (short)0, (short)8);
        byte[] tacKey = ALG.bytesXOR(left, right);
        
        ALG.genMACOrTAC(tacKey, iv, temp, (byte)22, TAC);
        //end
        
        //gen MAC2
        // begin money is 0
        // end money is actual money
        ALG.genMACOrTAC(purchaseSessionKey, iv, money, (byte)4, MAC2);
        //end
        
        
        if (cappPurchaseRecord == null)
            ISOException.throwIt(ISO7816.SW_WRONG_DATA);
        
        if (cappPurchaseRecord[4] == (byte)0x00)
        {
            JCSystem.beginTransaction();
            //step 1
            balance.subtract(money, (byte)1);
            
            //step 2
            purchaseTradeId++;

            //step 3
            // update capp record
            ((RecordFile)cappPurchase).updateRecordByTag(cappPurchaseRecord[0], cappPurchaseRecord);
           
            
            //step 4
            byte[] log = new byte[23];
            Util.setShort(log, (short)0, purchaseTradeId);
            Util.arrayCopy(money, (short)0, log, (short)5, (short)4);
            log[9] = TRADE_TYPE_CAPP_PURCHASE;
            Util.arrayCopy(terminalId, (short)0, log, (short)10, (short)6);
            Util.arrayCopy(terminalDatetime, (short)0, log, (short)16, (short)7);
            
            ((RecordFileCycler)logLocalPurchase).addRecord(log);
           
            JCSystem.commitTransaction();
        }
        else
        {
            // status isn't 0x00 
            //save to file
           
            ((RecordFile)cappPurchase).updateRecordByTag(cappPurchaseRecord[0], cappPurchaseRecord);
        }
        
        
        
        
        // return buffer = TAC4 MAC(2)4
        Util.arrayCopy(TAC, (short)0, buffer, (short)0, (short)4);
        Util.arrayCopy(MAC2, (short)0, buffer, (short)4, (short)4);
        apdu.le = 8;
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
    
    public void TranProof(MyAPDU apdu) throws ISOException
    {
         byte[] buffer = apdu.getBuffer();
        // buffer = 80 5A A1/A2 00 02 Data
        // response buffer = MAC/TAC
    }
    

    public void readBinary(MyAPDU apdu) throws ISOException
    {
    	if (apdu.cla == (byte)0x04)
        {
        	//if (!apdu.unwrap(keyId))
        	//	ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        }
    	
         //0x7F=0111 1111
        //      100X XXXX
        short sfi =  (short) (apdu.p1 & (byte)0x7F);
        
       
       
        
        BinaryFile file = null;
        
        if (sfi == issue.getSFI())
        {
        	file = (BinaryFile) issue;
        }
        else if(sfi == app.getSFI())
        {
        	file = (BinaryFile) app;
        }
        else if(sfi == personal.getSFI())
        {
        	file = (BinaryFile) personal;
        }
        else if(sfi == helper.getSFI())
        {
        	file = (BinaryFile) helper;
        }
        else if(sfi == reserved.getSFI())
        {
        	file = (BinaryFile) reserved;
        }
        else
        {
        	file = null;
        }
        
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
    

    public void writeBinary(MyAPDU apdu) throws ISOException
    {
    	if (apdu.cla != (byte)0x04)
        {
        	ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        	
        }
    	
        //0x7F=0111 1111
        //     100X XXXX
        byte sfi = (byte) (apdu.p1 & 0x7F);
        byte[] key = null;
        
      
        BinaryFile file = null;
        
        //#文件更新密钥1 -密钥接口获取 DCMK01 02-01  用于更新sfi=15\16
        //#文件更新密钥2 -密钥接口获取 DCMK02 03-06  用于更新sfi=17\11\12
        
        if (sfi == issue.getSFI())
        {
        	file = (BinaryFile) issue;
        	key = MFDCMK.getKey();
        }
        else if(sfi == app.getSFI())//0x15
        {
        	
        	file = (BinaryFile) app;
        	key = ADFDCMK01.getKey();
        }
        else if(sfi == personal.getSFI())//0x16
        {
        	file = (BinaryFile) personal;
        	key = ADFDCMK01.getKey();
        }
        else if(sfi == helper.getSFI())//0x11
        {
        	file = (BinaryFile) helper;
        	key = ADFDCMK02.getKey();
        }
        else if(sfi == reserved.getSFI())//0x12
        {
        	file = (BinaryFile) reserved;
        	key = ADFDCMK02.getKey();
        }
        else
        {
        	ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);
        }
        
        
     
        	  
            if (!apdu.unwrap(key))
          		ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        	
            byte[] data = new byte[apdu.lc];
            Util.arrayCopyNonAtomic(apdu.buffer, (short)0, data, (short)0, apdu.lc);
            
            file.setData(data);
            
            apdu.le = 0;
    }
    

    public void readRecord(MyAPDU apdu) throws ISOException
    {
        byte[] buffer = apdu.getBuffer();
        
        byte recordId = 0;
        byte tag = 0;
        
        if (apdu.cla == (byte)0x04)
        {
        	//if (!apdu.unwrap(keyId))
        	//	ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
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
        
        
        
        RecordFile file = null;
        
        if (sfi == logLocalPurchase.getSFI())
        {
        	file = (RecordFile) logLocalPurchase;
        }
        else if(sfi == logCharge.getSFI())
        {
        	file = (RecordFile) logCharge;
        }
        else if(sfi == logRemotePurchase.getSFI())
        {
        	file = (RecordFile) logRemotePurchase;
        }
        else if(sfi == cappPurchase.getSFI())
        {
        	file = (RecordFile) cappPurchase;
        }
        else
        {
        	ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);
        }
        
             
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
    }

    public void updateRecord(MyAPDU apdu) throws ISOException
    {
    	if (apdu.cla != (byte)0x04)
        {
        	ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        	
        }
        
        byte recordId = 0;
        byte tag = 0;
        
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
         
        byte sfi = (byte)((apdu.p2 & 0xF8) >> 3);
        
      
        
        
       RecordFile file = null;
       
       
       if(sfi == cappPurchase.getSFI())
       {
    	   file = (RecordFile) cappPurchase;
       }
       else
       {
    	   ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);
       }
       
       
       	if (!apdu.unwrap(ADFDCMK02.getKey())) //0x17
       		ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
       
       
       byte[] record = new byte[apdu.lc];
       Util.arrayCopy(apdu.buffer, (short)0, record, (short)0, apdu.lc);
           
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
       
       apdu.le = 0;
    }
    /*
    public void appendRecord(MyAPDU apdu) throws ISOException
    {
    	ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
    	
        byte recordId = 0;
        byte tag = 0;
        
        //sfi
        //XXXX X000 //tag
        //XXXX X100 //id
        
        if (apdu.cla == (byte)0x04)
        {
        	//if (!apdu.unwrap(keyId))
        	//	ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
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
        
        RecordFile file = null;
        
        if (sfi == logLocalPurchase.getSFI())
        {
        	file = (RecordFile) logLocalPurchase;
        }
        else if(sfi == logCharge.getSFI())
        {
        	file = (RecordFile) logCharge;
        }
        else if(sfi == logRemotePurchase.getSFI())
        {
        	file = (RecordFile) logRemotePurchase;
        }
        else if(sfi == cappPurchase.getSFI())
        {
        	file = (RecordFile) cappPurchase;
        }
        else
        {
        	ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);
        }
        
            
        if (file != null)
        {
           if (!file.addRecord(recordId, record))
               ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
        }            
    }
    
    */

    
    
    public void cardBlock(MyAPDU apdu) throws ISOException
    {
        if (apdu.cla != (byte)0x84)
        {
        	ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }
        else
        {
        	//if (!apdu.unwrap(keyId))
        	//	ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
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
        	//if (!apdu.unwrap(keyId))
        	//	ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
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
        	//if (!apdu.unwrap(keyId))
        	//	ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
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
    
    
    public void getResponse(MyAPDU apdu) throws ISOException
    {
       
        
        
    }
    
    
    
    public void personalEnd(MyAPDU apdu) throws ISOException
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
    
    public void verify(MyAPDU apdu) throws ISOException
    {
    	if (apdu.cla != (byte)0x00)
    		ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
    	
    	if (apdu.ins != (byte)0x20)
    		ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
    	
    	if (apdu.p1 != (byte)0x00 || apdu.p2 != (byte)0x00)
    		ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
    	
    	//需要实现verifypin0020 0000 03 123456
    }
    
    public void select(MyAPDU apdu) throws ISOException
    {
        //00 a4 04 00 lc filename
        // 0x00 select by SFI
        // 0x04 select by name
    	if (apdu.p1 != 0x04 && apdu.p2 != 0x00)
    		ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
    		
        byte[] fileName = new byte[apdu.lc];
        Util.arrayCopy(apdu.buffer, (short)0, fileName, (short)0, apdu.lc);
        
     
        byte[] AID = {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x86, (byte)0x98, (byte)0x07, (byte)0x00};
        
        //1PAY.SYS.DDF01
        byte[] MF = {(byte)0x31, (byte)0x50, (byte)0x41, (byte)0x59, (byte)0x2E, (byte)0x53, (byte)0x59, (byte)0x53, (byte)0x2E, (byte)0x44, (byte)0x44, (byte)0x46, (byte)0x30, (byte)0x31};
        
        // ADF
        byte[] ADF = {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x86, (byte)0x98, (byte)0x07, (byte)0x01};
        
        //select AID
        if (fileName.length == AID.length && Util.arrayCompare(fileName, (short)0, AID, (short)0, (short)AID.length) == 0)
        {
            //return AID FCI
        	path = PATH_MF;
            apdu.le = 0;
           return;
        }
      //select PSE
        else if(fileName.length == MF.length && Util.arrayCompare(fileName, (short)0, MF, (short)0, (short)MF.length) == 0)
        {
        	//return MF FCI
        	path = PATH_MF;
        	apdu.le = 0;
        	return;
        }
        //select ADF
        else if(fileName.length == ADF.length && Util.arrayCompare(fileName, (short)0, ADF, (short)0, (short)ADF.length) == 0)
        {
            //return ADF FCI
        	path = PATH_ADF;
            apdu.le = 0;
           return;
        }
        else
        {
        	ISOException.throwIt(ISO7816.SW_WRONG_DATA);
        }
    }
    

    public void writeKey(MyAPDU apdu) throws ISOException
    {
    	if (apdu.cla != (byte)0x84)
    	{
    		ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
    	}
    	
    	
    	if (apdu.ins != (byte)0xD4)
    	{
    		ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
    	}
    	
    	byte keyType = apdu.p1; //密钥类型
    	byte keyId = apdu.p2; //密钥索引
    	
    	byte[] out = new byte[24];
    	
    	byte[] temp = null;
    	byte[] key = null;
    	
    	
    	if (keyType == KEY_TAG_DCCK)
    	{
    		if (path == PATH_MF)
    		{
    			key = keyTrans.getKey();
    			
    			//卡片主控密钥
	    		if (!apdu.unwrap(key))
	    			ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
	    		
	    		
	    		ALG.decrypt(key, apdu.buffer, (short)0, apdu.lc, out, (short)0);
	    		
	    		MFDCCK = new KEY(keyId, out);
	    		
    		}
    		else
    		{
    			key = MFDCCK.getKey();
    			
    			//应用主控密钥
    			if (!apdu.unwrap(key))
	    			ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
	    		
	    		
	    		ALG.decrypt(key, apdu.buffer, (short)0, apdu.lc, out, (short)0);
	    		
	    		ADFDCCK = new KEY(keyId, out);
	    		
    		}
    	}
    	else if (keyType == KEY_TAG_DCMK)
    	{
    		if (path == PATH_MF)
    		{
    			key = MFDCCK.getKey();
    			
    			//卡片维护密钥
    			if (!apdu.unwrap(key))
    				ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
    		
    		
    			ALG.decrypt(key, apdu.buffer, (short)0, apdu.lc, out, (short)0);
    		
    			MFDCMK = new KEY(keyId, out);
    		}
    		else
    		{
    			key = ADFDCCK.getKey();
    			
    			//应用维护密钥
    			if (!apdu.unwrap(key))
    				ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
    		
    		
    			ALG.decrypt(key, apdu.buffer, (short)0, apdu.lc, out, (short)0);
    			
    			if (keyId == (byte)0x00)
    			{
    				ADFDCMK01 = new KEY(keyId, out);
    			}
    			else
    			{
    				ADFDCMK02 = new KEY(keyId, out);
    			}
    		}
    	}
    	
    	else if(keyType == KEY_TAG_APPDCMK)
    	{
    		//应用维护密钥
    		key = ADFDCCK.getKey();
    		
			if (!apdu.unwrap(key))
				ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
		
		
			short len = apdu.lc;
			byte[] tmp = new byte[len];
			Util.arrayCopy(apdu.buffer, (short)0, tmp, (short)0, len);
			ALG.decrypt(key, tmp, (short)0, len, out, (short)0);
			
			//ALG.decrypt(key, apdu.buffer, (short)0, apdu.lc, out, (short)0);
		
			ADFDCMK = new KEY(keyId, out);
    	}
    	
    	else if(keyType == KEY_TAG_DPK)
    	{
    		key = ADFDCCK.getKey();
    		//消费密钥
    		if (!apdu.unwrap(key))
				ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
		
		
			ALG.decrypt(key, apdu.buffer, (short)0, apdu.lc, out, (short)0);
		
			DPK = new KEY(keyId, out);
    	}
    	else if(keyType == KEY_TAG_DLK)
    	{
    		key = ADFDCCK.getKey();
    		
    		//充值密钥
    		if (!apdu.unwrap(key))
				ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
		
		
			ALG.decrypt(key, apdu.buffer, (short)0, apdu.lc, out, (short)0);
		
			DLK = new KEY(keyId, out);
    	}
    	else if(keyType == KEY_TAG_DTK)
    	{
    		key = ADFDCCK.getKey();
    		
    		//TAC密钥
    		if (!apdu.unwrap(key))
				ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
		
		
			ALG.decrypt(key, apdu.buffer, (short)0, apdu.lc, out, (short)0);
		
			DTK = new KEY(keyId, out);
    	}
    	else if(keyType == KEY_TAG_DABK)
    	{
    		key = ADFDCCK.getKey();
    		
    		//应用锁定密钥
    		if (!apdu.unwrap(key))
				ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
		
		
			ALG.decrypt(key, apdu.buffer, (short)0, apdu.lc, out, (short)0);
		
			DABK = new KEY(keyId, out);
    	}
    	else if(keyType == KEY_TAG_DAUK)
    	{
    		key = ADFDCCK.getKey();
    		
    		//应用解锁密钥
    		if (!apdu.unwrap(key))
				ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
		
		
			ALG.decrypt(key, apdu.buffer, (short)0, apdu.lc, out, (short)0);
		
			DAUK = new KEY(keyId, out);
    	}
    	
    	else if(keyType == KEY_TAG_DPUK)
    	{
    		key = ADFDCCK.getKey();
    		
    		//PIN解锁密钥
    		if (!apdu.unwrap(key))
				ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
		
		
			ALG.decrypt(key, apdu.buffer, (short)0, apdu.lc, out, (short)0);
		
			DPUK = new KEY(keyId, out);
    	}
    	
    	else if(keyType == KEY_TAG_DPRK)
    	{
    		key = ADFDCCK.getKey();
    		
    		//PIN重装密钥
    		if (!apdu.unwrap(key))
				ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
		
		
			ALG.decrypt(key, apdu.buffer, (short)0, apdu.lc, out, (short)0);
		
			DPRK = new KEY(keyId, out);
    	}
    	else
    	{
    		
    	}
    	
    	apdu.le = 0;
    }
    
    public void getKey(MyAPDU apdu) throws ISOException
    {
    	 byte[] buffer = apdu.getBuffer();
    	 
    	 byte[] key = null;
    	 
    	 if (apdu.p1 == (byte)0x00)
    	 {
    		 key = MFDCCK.getKey();
    	 }
    	 else if(apdu.p1 == (byte)0x01)
    	 {
    		 key = MFDCMK.getKey();
    	 }
    	 else if(apdu.p1 == (byte)0x02)
    	 {
    		 key = ADFDCCK.getKey();
    	 }
    	 else if(apdu.p1 == (byte)0x03)
    	 {
    		 key = ADFDCMK.getKey();
    	 }
    	 else if(apdu.p1 == (byte)0x04)
    	 {
    		 key = ADFDCMK01.getKey();
    	 }
    	 else if(apdu.p1 == (byte)0x05)
    	 {
    		 key = ADFDCMK02.getKey();
    	 }
    	 else if(apdu.p1 == (byte)0x06)
    	 {
    		 
    	 }
    	 else if(apdu.p1 == (byte)0x07)
    	 {
    		 key = DPK.getKey();
    		 
    	 }
    	 else if(apdu.p1 == (byte)0x08)
    	 {
    		 key = DLK.getKey();
    	 }
    	 else if(apdu.p1 == (byte)0x09)
    	 {
    		 key = DTK.getKey();
    	 }
    	 else if(apdu.p1 == (byte)0x0A)
    	 {
    		 key = DABK.getKey();
    	 }
    	 else if(apdu.p1 == (byte)0x0B)
    	 {
    		 key = DAUK.getKey();
    	 }
    	 else if(apdu.p1 == (byte)0x0C)
    	 {
    		 key = DPUK.getKey();
    	 }
    	 else if(apdu.p1 == (byte)0x0D)
    	 {
    		 key = DPRK.getKey();
    	 }
    	 else if(apdu.p1 == (byte)0x0E)
    	 {
    		 
    	 }
    	 else if(apdu.p1 == (byte)0x0F)
    	 {
    		 
    	 }
    	 else
    	 {
    		 
    	 }
    	 
    	 Util.arrayCopyNonAtomic(key, (short)0, buffer, (short)0, (short) key.length);
    	 apdu.le = (short) key.length;
    }
    
    
}//end class

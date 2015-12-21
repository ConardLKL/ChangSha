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
        
   
    
    byte[] cappPurchaseRecord = null;
    RecordFileTLV cappPurchaseRecordFile;// = new RecordFileTLV((short)0x17, (short)0x150, (byte)0xf0, (byte)0xf0);
    
   
    
    
    

    
    //byte[] sessionKey = JCSystem.makeTransientByteArray((short)16, JCSystem.CLEAR_ON_DESELECT);
    //byte[] sessionKey = JCSystem.makeTransientByteArray((short)8, JCSystem.CLEAR_ON_DESELECT);
    byte[] purchaseSessionKey = new byte[16];
    byte[] chargeSessionKey = new byte[8];
    
    //JCint balanace;
    EP balance = new EP((byte)0x00, (byte)0x00, (byte)0xf0, (byte)0xf0);
    
    
    
    
   
    
   
    
    COS cos;
    MyRandom myRandom;
    byte[] random;
    
    
    public Changsha(COS c, MyRandom rand)
    {
        cos = c;
        myRandom = rand;
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
        byte[] buffer = apdu.getBuffer();
        // buffer = CLA INS P1 P2 LC keyIndex1 money4 terminalId6

        // get key index
        byte keyId = buffer[0];
        KEY key = cos.loadKey(keyId);
        if (key == null)
        {
            ISOException.throwIt(ISO7816.SW_WRONG_DATA);
        }
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
        byte[] chargeKey = key.getKey();
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
        buffer[6] = key.getKeyVersion();
        buffer[7] = key.getAlgId();
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
        
        if (!cos.appendLog((short)0x1A, log))
            ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);
                
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
        
        
        KEY key = cos.loadKey((byte)0x60);
        if (key == null)
        {
            ISOException.throwIt(ISO7816.SW_WRONG_DATA);
        }
        byte[] TACKey = key.getKey();
        
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
        KEY key = cos.loadKey(keyId);
        if (key == null)
        {
            ISOException.throwIt(ISO7816.SW_WRONG_DATA);
        }
        purchaseKey = key.getKey();
        
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
        buffer[6] = key.getKeyVersion();
        buffer[7] = key.getAlgId();
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
        
        KEY key = cos.loadKey((byte)0x60);
        if (key == null)
        {
            ISOException.throwIt(ISO7816.SW_WRONG_DATA);
        }
        byte[] TACKey = key.getKey();
        
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
        
        if (!cos.appendLog((short)0x18, log))
            ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);

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
        KEY key = cos.loadKey(keyId);
        if (key == null)
        {
            ISOException.throwIt(ISO7816.SW_WRONG_DATA);
        }
        purchaseKey = key.getKey();
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
        buffer[6] = key.getKeyVersion();
        // set alg type
        buffer[7] = key.getAlgId();
        
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
             ISOException.throwIt(ISO7816.SW_WRONG_DATA);
            
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
        //temp = money4 tradeType1 ternimalId6 terminalDate4 ternimalTime3
        Util.arrayCopy(money, (short)0, temp, (short)0, (short)4);
        temp[4] = TRADE_TYPE_CAPP_PURCHASE;
        Util.arrayCopy(terminalId, (short)0, temp, (short)5, (short)6);
        Util.arrayCopy(terminalDatetime, (short)0, temp, (short)11, (short)7); // terminalDate terminlaTime
        
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
        KEY key = cos.loadKey((byte)0x60);
        if (key == null)
        {
            ISOException.throwIt(ISO7816.SW_WRONG_DATA);
        }
        byte[] TACKey = key.getKey();
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
            boolean ret = cos.updateCAPPPurchaseRecord((short)0x17, cappPurchaseRecord[0], cappPurchaseRecord);
            if (!ret)
                 ISOException.throwIt(ISO7816.SW_RECORD_NOT_FOUND);
            
            //step 4
            byte[] log = new byte[23];
            Util.setShort(log, (short)0, purchaseTradeId);
            Util.arrayCopy(money, (short)0, log, (short)5, (short)4);
            log[9] = TRADE_TYPE_CAPP_PURCHASE;
            Util.arrayCopy(terminalId, (short)0, log, (short)10, (short)6);
            Util.arrayCopy(terminalDatetime, (short)0, log, (short)16, (short)7);
            
           if (!cos.appendLog((short)0x18, log))
               ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);


            JCSystem.commitTransaction();
        }
        else
        {
            // status isn't 0x00 
            //save to file
            boolean ret = cos.updateCAPPPurchaseRecord((short)0x17, cappPurchaseRecord[0], cappPurchaseRecord);
            if (!ret)
                 ISOException.throwIt(ISO7816.SW_RECORD_NOT_FOUND);
            
        }
        
        
        
        
        // return buffer = TAC4 MAC(2)4
        Util.arrayCopy(TAC, (short)0, buffer, (short)0, (short)4);
        Util.arrayCopy(MAC2, (short)0, buffer, (short)4, (short)4);
        apdu.le = 8;
    }
    
    
    
    public void TranProof(MyAPDU apdu)
    {
         byte[] buffer = apdu.getBuffer();
        // buffer = 80 5A A1/A2 00 02 Data
        // response buffer = MAC/TAC
    }
    
   
}

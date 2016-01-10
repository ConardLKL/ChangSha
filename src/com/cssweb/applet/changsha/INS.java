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
public class INS {
  
    public static final byte SELECT = (byte)0xA4;
    

    
    
    public static final byte READ_BINARY = (byte)0xB0;
    public static final byte WRITE_BINARY = (byte)0xD6;
    
    public static final byte READ_RECORD = (byte)0xB2;
    //public static final byte WRITE_RECORD = (byte)0xDC; //CLA=00/04
    //public static final byte UPDATE_CAPP_PURCHASE = (byte)0xDC; //CLA=80
    public static final byte APPEND_RECORD = (byte)0xE2;
    
    
    public static final byte GET_MESSAGE= (byte)0xCA; //get security auth code
    
    public static final byte GET_CHALLENGE = (byte)0x84; //random
    public static final byte INTERNAL_AUTH = (byte)0x88;
    public static final byte EXTERNAL_AUTH = (byte)0x82;
    
    public static final byte GET_BALANCE = (byte)0x5C;
    public static final byte INIT_PURCHASE_CHARGE = (byte)0x50;
    public static final byte PURCHASE = (byte)0x54;
    public static final byte LOAD = (byte)0x52;
   
   
    

    
 
    public static final byte APP_BLOCK = (byte)0x1E;
    public static final byte APP_UNBLOCK = (byte)0x18;
    public static final byte CARD_BLOCK = (byte)0x16;
    
    
    
    public static final byte UNBLOCK_PIN = (byte)0x24;//84 24 00 00
    public static final byte VERIFY_PIN = (byte)0x20;//00 20 00 00
    public static final byte CHANGE_PIN = (byte)0x5E;//80 5E 01 00
    public static final byte RELOAD_PIN = (byte)0x5E;//80 5E 00 00
    
    
    
    public static final byte GET_TRANSACTION_PROVE = (byte)0x5A;//80 5A 00 交易类型标识  长度2 交易序号 
    
    
    public static final byte GET_RESPONSE = (byte)0xC0;
    
    
    // 华大智宝指令
    public static final byte WRITE_KEY = (byte)0xD4;
    public static final byte WRITE_UID = (byte)0x22; 
    public static final byte PERSONAL_END = (byte)0x08;
    
    
    
    //TEST INS
    public static final byte DES_TEST = (byte) 0x00;
    public static final byte MAC_TEST = (byte) 0x01;
    public static final byte TAC_TEST = (byte) 0x02;
}

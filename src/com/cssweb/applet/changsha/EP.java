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
public class EP extends File{
    
    //private byte[] balance = {0x00, 0x00, 0x00, 0x64};
	private byte[] balance = {0x00, 0x00, (byte)0xC3, (byte)0x50};//500,00
    
    
    public EP(short SFI, short len, byte read, byte write)
    {
        super(SFI, len, read, write);
        
        fileType = File.FILE_TYPE_RECORD_EP;
    }
    
    public byte[] toBytes()
    {
        return balance;
    }
    
    /*
    //4, 3, 2, 1
    public byte[] toBytes(int a) {
        return new byte[] { (byte) (0x000000ff & (a >>> 24)),
                (byte) (0x000000ff & (a >>> 16)),
                (byte) (0x000000ff & (a >>> 8)), (byte) (0x000000ff & (a)) };
    }

    public int toInt(byte[] b, int s, int n) {
        int ret = 0;

        final int e = s + n;
        for (int i = s; i < e; ++i) {
            ret <<= 8;
            ret |= b[i] & 0xFF;
        }
        return ret;
    }
    public int toInt(byte... b) {
        int ret = 0;
        for (final byte a : b) {
            ret <<= 8;
            ret |= a & 0xFF;
        }
        return ret;
    }
*/
    public short subtract(byte[] data, byte checkOverflow)
    {
        short  i,t1,t2,pos,ads;

        ads = 0;

        for(i=3; i>=0; i--)
        {
            t1 = (short)(balance[(short)(i)] & 0x0ff);
            t2 = (short)(data[i] & 0x0ff);

            if ( ads > (short)0 )
            {
                if ( t1 > (short)0 )
                {
                    t1--;
                    ads = (short)0;
                }
                else
                {
                    t1 = (short)255;
                    ads = (short)1;
                }
            }

            if ( t1 >= t2 )
            {
                t1 = (short)(t1-t2);
            }
            else
            {
                t1 = (short)(t1 + 256 - t2);
                ads = (short)1;
            }

            if (checkOverflow > (byte)0x0)
                balance[(short)(i)] = (byte)t1;
        }

        return ads;
    }

    public short add(byte[] data, byte checkOverflow)
    {
        short  i,t1,t2,ads;

        ads = (short)0;

        for(i=3; i>=0; i--) {
            t1 = (short)(balance[(short)(i)] & 0x0ff);
            t2 = (short)(data[i] & 0x0ff);

            t1 = (short)(t1 + t2 + ads);

            if (checkOverflow > (byte)0 )
                balance[(short)(i)] = (byte)(t1 % 256); 

            ads = (short)(t1 / 256); 
        }

        return ads;
    }
    
}

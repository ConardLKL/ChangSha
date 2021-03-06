package com.cssweb.applet.changsha;

import javacard.framework.JCSystem;
import javacard.framework.Util;
import javacard.security.RandomData;

public class MyRandom {
	
	byte[] random = JCSystem.makeTransientByteArray((short)4, JCSystem.CLEAR_ON_DESELECT);
	
	
	
	public byte[] genRandom()
    {
        RandomData ICC = RandomData.getInstance(RandomData.ALG_PSEUDO_RANDOM);
        ICC.setSeed(random, (short)0, (short)4);
        ICC.generateData(random, (short)0, (short)4);
        
        if (Config.mode == Config.MODE_DEBUG)
        {
            byte[] test = {0x41, 0x42, 0x43, 0x44};
            
            Util.arrayCopyNonAtomic(test, (short)0, random, (short)0, (short)4);
        }
        
       return random;
    }
	
	public byte[] getRandom()
	{
		 return random;
	}
	
	
	
	    

}

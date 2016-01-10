package com.cssweb.applet.changsha;

public class Config {
	
	public static final short MODE_DEBUG   = 0x00;
	public static final short MODE_RELEASE = 0X01;
    
	public static short mode = MODE_DEBUG;
	
	public static boolean personalEnd = false;
	
	public static boolean cardLock = false;//所有应用是否锁定
	public static boolean appLock = false;//应用是否锁定
    public static boolean appLockForEver = false;//应用是否永久锁定
    
	
	//重试次数
	public static short retry = 3;

}

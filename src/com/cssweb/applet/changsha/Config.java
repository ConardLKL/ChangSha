package com.cssweb.applet.changsha;

public class Config {
	
	public static final short MODE_DEBUG   = 0x00;
	public static final short MODE_RELEASE = 0X01;
    
	public static short mode = MODE_DEBUG;
	
	public static boolean personalEnd = false;
	
	public static boolean cardLock = false;//����Ӧ���Ƿ�����
	public static boolean appLock = false;//Ӧ���Ƿ�����
    public static boolean appLockForEver = false;//Ӧ���Ƿ���������
    
	
	//���Դ���
	public static short retry = 3;

}

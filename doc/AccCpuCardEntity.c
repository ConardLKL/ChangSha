#include "AccCpuCardEntity.h"
#include "DataTypeDefine.h"
#include "CSReaderCode.h"
#include "Globle.h"
#include "./inc/DataType.h"

int Read_AccCpuCard_All_Info(uint8 u8Antenna,ST_CARD_ACC_CPU * stAccCpuCardInfo,BOOL bReadHistory)
{
	unsigned char TmpBuff[400];
	//INT32U 	apdu_rlen=0;
	INT16U 	apdu_rlen=0;
	INT8U 	RevBuff[400];
	INT16U	sw;
	int i=0;

//	memset(stAccCpuCardInfo->Info_0005,0x00,sizeof(stAccCpuCardInfo->Info_0005));
//	memset(stAccCpuCardInfo->Info_0015,0x00,sizeof(stAccCpuCardInfo->Info_0015));
//	memset(stAccCpuCardInfo->Info_0016,0x00,sizeof(stAccCpuCardInfo->Info_0016));
//	memset(stAccCpuCardInfo->Info_0002,0x00,sizeof(stAccCpuCardInfo->Info_0002));
//	memset(stAccCpuCardInfo->Info_0018,0x00,sizeof(stAccCpuCardInfo->Info_0018));
//	memset(stAccCpuCardInfo->Info_0010,0x00,sizeof(stAccCpuCardInfo->Info_0010));
//	memset(stAccCpuCardInfo->Info_001A,0x00,sizeof(stAccCpuCardInfo->Info_001A));
//	memset(stAccCpuCardInfo->Info_0017,0x00,sizeof(stAccCpuCardInfo->Info_0017));
//	memset(stAccCpuCardInfo->Info_0011,0x00,sizeof(stAccCpuCardInfo->Info_0011));
//	memset(stAccCpuCardInfo->Info_0012,0x00,sizeof(stAccCpuCardInfo->Info_0012));

	//步骤1：复位Cpu卡片 前边有了
//	if(Rfa_RATS(RevBuff)){
//		PrintLog("AccCpu_Reset....RATS is FAILE\r\n");
//		return 1;
//	}
//	PrintLog("AccCpu_Reset.......ATS=%s\r\n",BCD2ASC(RevBuff,10));

	//步骤2：选MF  00A40000023F00
	TmpBuff[0] =0x00;
	TmpBuff[1] =0xA4;
	TmpBuff[2] =0x00;
	TmpBuff[3] =0x00;
	TmpBuff[4] =0x02;
	TmpBuff[5] =0x3F;
	TmpBuff[6] =0x00;
	if(Rfa_APDU(TmpBuff,7,RevBuff,&apdu_rlen)){
		PrintLog("APDU 00 A4 to MF is Error\r\n");
		return 1;
	}
	sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
	PrintLog("Select MF OK!Data=%s\r\n",BCD2ASC(TmpBuff,7));
	PrintLog("APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
	if(sw != 0x9000){
		return 1;
	}

	//步骤3：读0005发行基本信息文件  00B0850028
	TmpBuff[0] =0x00;
	TmpBuff[1] =0xB0;
	TmpBuff[2] =0x85;
	TmpBuff[3] =0x00;
	TmpBuff[4] =0x28;
	if(Rfa_APDU(TmpBuff,5,RevBuff,&apdu_rlen)){
		PrintLog("APDU 00 B0 read 0005 Error\r\n");
		return 1;
	}
	sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
	PrintLog("Read 0005 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
	PrintLog("APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
	if(sw != 0x9000){
		return 1;
	}
	memcpy(stAccCpuCardInfo->Info_0005,RevBuff,40);

	//步骤4：选应用目录  00A40000023F01
	TmpBuff[0] =0x00;
	TmpBuff[1] =0xA4;
	TmpBuff[2] =0x00;
	TmpBuff[3] =0x00;
	TmpBuff[4] =0x02;
	TmpBuff[5] =0x3F;
	TmpBuff[6] =0x01;
	if(Rfa_APDU(TmpBuff,7,RevBuff,&apdu_rlen)){
		PrintLog("APDU 00 A4 to ADF is Error\r\n");
		return 1;
	}
	sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
	PrintLog("Select ADF OK!Data=%s\r\n",BCD2ASC(TmpBuff,7));
	PrintLog("APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
	if(sw != 0x9000){
		return 1;
	}

	//步骤5：读0015公共应用基本数据文件  00B095001E
	TmpBuff[0] =0x00;
	TmpBuff[1] =0xB0;
	TmpBuff[2] =0x95;
	TmpBuff[3] =0x00;
	TmpBuff[4] =0x1E;
	if(Rfa_APDU(TmpBuff,5,RevBuff,&apdu_rlen)){
		PrintLog("APDU 00 B0 read 0015 Error\r\n");
		return 1;
	}
	sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
	PrintLog("Read 0015 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
	PrintLog("APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
	if(sw != 0x9000){
		return 1;
	}
	memcpy(stAccCpuCardInfo->Info_0015,RevBuff,0x1E);

	//步骤6：读0016持卡人基本数据文件  00B096005C
	TmpBuff[0] =0x00;
	TmpBuff[1] =0xB0;
	TmpBuff[2] =0x96;
	TmpBuff[3] =0x00;
	TmpBuff[4] =0x5C;
	if(Rfa_APDU(TmpBuff,5,RevBuff,&apdu_rlen)){
		PrintLog("APDU 00 B0 read 0016 Error\r\n");
		return 1;
	}
	sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
	PrintLog("Read 0016 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
	PrintLog("APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
	if(sw != 0x9000){
		return 1;
	}
	memcpy(stAccCpuCardInfo->Info_0016,RevBuff,0x5C);

	//步骤7：读0002电子钱包文件  805C000204
	TmpBuff[0] =0x80;
	TmpBuff[1] =0x5C;
	TmpBuff[2] =0x00;
	TmpBuff[3] =0x02;
	TmpBuff[4] =0x04;
	if(Rfa_APDU(TmpBuff,5,RevBuff,&apdu_rlen)){
		PrintLog("APDU 80 5C read 0002 Error\r\n");
		return 1;
	}
	sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
	PrintLog("Read 0002 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
	PrintLog("APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
	if(sw != 0x9000){
		return 1;
	}
	memcpy(stAccCpuCardInfo->Info_0002,RevBuff,0x04);

	//步骤8：读0018本地消费明细文件过程  00A40000020018


	if(bReadHistory)
	{
		for(i=0;i<10;i++){
			TmpBuff[0] =0x00;
			TmpBuff[1] =0xB2;
			TmpBuff[2] =0x01+i;
			TmpBuff[3] =0xC4;
			TmpBuff[4] =0x00;
			if(Rfa_APDU(TmpBuff,5,RevBuff,&apdu_rlen)){
				PrintLog("APDU 00 B2 Read 0018 Error\r\n");
				return 1;
			}
			sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
			PrintLog("Read 0018 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
			PrintLog("APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
			if(sw == 0x6A83){
				break;
			}
			else if(sw != 0x9000){
				return 1;
			}
			memcpy(&(stAccCpuCardInfo->Info_0018[i][0]),RevBuff,0x17);
		}

		//步骤9：读0010异地消费明细文件过程  00A40000020010
	/*	TmpBuff[0] =0x00;
		TmpBuff[1] =0xA4;
		TmpBuff[2] =0x00;
		TmpBuff[3] =0x00;
		TmpBuff[4] =0x02;
		TmpBuff[5] =0x00;
		TmpBuff[6] =0x10;
		if(Rfa_APDU(TmpBuff,7,RevBuff,&apdu_rlen)){
			PrintLog("APDU 00 A4 Select 0010 Error\r\n");
			return 1;
		}
		sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
		PrintLog("Select 0010 OK!Data=%s\r\n",BCD2ASC(TmpBuff,7));
		PrintLog("APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
		if(sw != 0x9000){
			return 1;
		}*/

		for(i=0;i<10;i++){
			TmpBuff[0] =0x00;
			TmpBuff[1] =0xB2;
			TmpBuff[2] =0x01+i;
	//		TmpBuff[3] =0x04;
			TmpBuff[3] =0x84;
	//		TmpBuff[4] =0x17;
			TmpBuff[4] =0x00;
			if(Rfa_APDU(TmpBuff,5,RevBuff,&apdu_rlen)){
				PrintLog("APDU 00 B2 Read 0010 Error\r\n");
				return 1;
			}
			sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
			PrintLog("Read 0010 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
			PrintLog("APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
			if(sw == 0x6A83){
				break;
			}
			else if(sw != 0x9000){
				return 1;
			}
			memcpy(&(stAccCpuCardInfo->Info_0010[i][0]),RevBuff,0x17);
		}


		//步骤10：读001A充值明细文件过程  00A4000002001A
	/*	TmpBuff[0] =0x00;
		TmpBuff[1] =0xA4;
		TmpBuff[2] =0x00;
		TmpBuff[3] =0x00;
		TmpBuff[4] =0x02;
		TmpBuff[5] =0x00;
		TmpBuff[6] =0x1A;
		if(Rfa_APDU(TmpBuff,7,RevBuff,&apdu_rlen)){
			PrintLog("APDU 00 A4 Select 001A Error\r\n");
			return 1;
		}
		sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
		PrintLog("Select 001A OK!Data=%s\r\n",BCD2ASC(TmpBuff,7));
		PrintLog("APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
		if(sw != 0x9000){
			return 1;
		}*/

		for(i=0;i<10;i++){
			TmpBuff[0] =0x00;
			TmpBuff[1] =0xB2;
			TmpBuff[2] =0x01+i;
			TmpBuff[3] =0xD4;
			TmpBuff[4] =0x00;
			if(Rfa_APDU(TmpBuff,5,RevBuff,&apdu_rlen)){
				PrintLog("APDU 00 B2 Read 001A Error\r\n");
				return 1;
			}
			sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
			PrintLog("Read 001A OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
			PrintLog("APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
			if(sw == 0x6A83){
				break;
			}
			else if(sw != 0x9000){
				return 1;
			}
			memcpy(&(stAccCpuCardInfo->Info_001A[i][0]),RevBuff,0x17);
		}
	}else
	{

		TmpBuff[0] =0x00;
		TmpBuff[1] =0xB2;
		TmpBuff[2] =0x01;
		TmpBuff[3] =0xC4;
		TmpBuff[4] =0x00;
		if(Rfa_APDU(TmpBuff,5,RevBuff,&apdu_rlen)){
			PrintLog("APDU 00 B2 Read 0018 Error\r\n");
			return 1;
		}
		sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
		PrintLog("Read 0018 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
		PrintLog("APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));

		if(sw == 0x9000)
		{
			memcpy(&(stAccCpuCardInfo->Info_0018[0][0]),RevBuff,0x17);
		}


		TmpBuff[0] =0x00;
		TmpBuff[1] =0xB2;
		TmpBuff[2] =0x01;
		TmpBuff[3] =0xD4;
		TmpBuff[4] =0x00;
		if(Rfa_APDU(TmpBuff,5,RevBuff,&apdu_rlen)){
			PrintLog("APDU 00 B2 Read 001A Error\r\n");
		}
		sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
		PrintLog("Read 001A OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
		PrintLog("APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));

		if(sw == 0x9000)
		{
			memcpy(&(stAccCpuCardInfo->Info_001A[0][0]),RevBuff,0x17);
		}
	}
	//步骤11：读0017复合交易记录文件  00A40000020017
/*	TmpBuff[0] =0x00;
	TmpBuff[1] =0xA4;
	TmpBuff[2] =0x00;
	TmpBuff[3] =0x00;
	TmpBuff[4] =0x02;
	TmpBuff[5] =0x00;
	TmpBuff[6] =0x17;
	if(Rfa_APDU(TmpBuff,7,RevBuff,&apdu_rlen)){
		PrintLog("APDU 00 A4 Select 0017 Error\r\n");
		return 1;
	}
	sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
	PrintLog("Select 0017 OK!Data=%s\r\n",BCD2ASC(TmpBuff,7));
	PrintLog("APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
	if(sw != 0x9000){
		return 1;
	}*/

	for(i=0;i<=6;i++){
		TmpBuff[0] =0x00;
		TmpBuff[1] =0xB2;
		TmpBuff[2] =0x01+i;
		TmpBuff[3] =0xBC;
		TmpBuff[4] =0x30;
		if(i==6){
			TmpBuff[4] =0x18;//测试卡最后一条记录长度与文档不符，所以修改连此处szp
		}
		if(Rfa_APDU(TmpBuff,5,RevBuff,&apdu_rlen)){
			PrintLog("APDU 00 B2 Read 001A Error\r\n");
			return 1;
		}
		sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
		PrintLog("Read 001A OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
		PrintLog("APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
		if(sw == 0x6A83){
			break;
		}
		else if(sw != 0x9000){
			return 1;
		}
		if(i==6){
			memcpy(&(stAccCpuCardInfo->Info_0017[i][0]),RevBuff,apdu_rlen-2);//测试卡最后一条记录长度与文档不符，所以修改连此处szp
		}
		else{
			memcpy(&(stAccCpuCardInfo->Info_0017[i][0]),RevBuff,0x30);
		}
	}

	//步骤12：读0011辅助信息文件  00B0910020
	TmpBuff[0] =0x00;
	TmpBuff[1] =0xB0;
	TmpBuff[2] =0x91;
	TmpBuff[3] =0x00;
	TmpBuff[4] =0x20;
	if(Rfa_APDU(TmpBuff,5,RevBuff,&apdu_rlen)){
		PrintLog("APDU 00 B0 read 0011 Error\r\n");
		return 1;
	}
	sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
	PrintLog("Read 0011 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
	PrintLog("APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
	if(sw != 0x9000){
		return 1;
	}
	memcpy(stAccCpuCardInfo->Info_0011,RevBuff,0x20);

	//步骤13：读0012保留数据文件  00B0920020
	TmpBuff[0] =0x00;
	TmpBuff[1] =0xB0;
	TmpBuff[2] =0x92;
	TmpBuff[3] =0x00;
	TmpBuff[4] =0x20;
	if(Rfa_APDU(TmpBuff,5,RevBuff,&apdu_rlen)){
		PrintLog("APDU 00 B0 read 0012 Error\r\n");
		return 1;
	}
	sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
	PrintLog("Read 0012 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
	PrintLog("APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
	if(sw != 0x9000){
		return 1;
	}
	memcpy(stAccCpuCardInfo->Info_0012,RevBuff,0x20);

	return 0;
}

//地铁AccCpu卡复合消费
int Write_AccCpuCard_Recombine_Info(uint32 u32ConsumeMoney,uint8* bcdCurrentTime,ST_CARD_ACC_CPU * stAccCpuCardInfo,uint8 ConsumeFlag,uint8 *TAC,uint32 *u8OutTerminalTradeNum)//ConsumeFlag 00:普通消费   01:复合消费
{
	int i =0;
	INT32U slot=SAM_SLOT_ACC;//默认地铁CPU卡的PSAM卡槽是1
	INT32U baud=38400;
	INT8U 	rlen=0;
	unsigned char TmpBuff[400];
	INT16U 	apdu_rlen=0;
	INT32U 	samApdu_rlen=0;
	INT8U 	RevBuff[400];
	INT16U	sw;
	uint8 u8ConsumeMoney[4];//消费金额
	uint8 u8Terminal[6];//终端机编号
	uint8 u8InitPurseBuff[15];//Cpu卡复合消费返回数据（4旧金额+2脱机交易序号+3透支限额+1秘钥版本+1算法标识+4伪随机数）
	uint8 u8TerminalTradeNum[4];//终端脱机交易序号
	uint8 u8MAC1[4];//MAC1
	uint8 u8TAC[4];//TAC
	uint8 u8MAC2[4];//MAC2


	memset(u8Terminal,0x00,sizeof(u8Terminal));
	memset(u8InitPurseBuff,0x00,sizeof(u8InitPurseBuff));
	memset(u8TerminalTradeNum,0x00,sizeof(u8TerminalTradeNum));
	memset(u8MAC1,0x00,sizeof(u8MAC1));
	memset(u8TAC,0x00,sizeof(u8TAC));
	memset(u8MAC2,0x00,sizeof(u8MAC2));
	u8ConsumeMoney[0] =u32ConsumeMoney/0x1000000;//由于不缺定系统存储数据大小端，所以这样写
	u8ConsumeMoney[1] =(u32ConsumeMoney/0x10000)%0x100;
	u8ConsumeMoney[2] =(u32ConsumeMoney/0x100)%0x100;
	u8ConsumeMoney[3] =u32ConsumeMoney%0x100;

//	PrintLog("IccSimReset OK !!!!\n");
//	if(IccSimReset(slot, baud, 3, &rlen,  RevBuff,1)){
//		PrintLog("Reset %d is ERROR\r\n",slot);
//		//CloseSimModule();
//		return 1;
//	}
//	PrintLog("Reset %d is OK\r\n",slot);
//	PrintLog("RESET SIM:Len=%d,Data=%s\r\n",rlen,BCD2ASC(RevBuff,rlen));
//
//	//步骤0：PSAM卡选MF  00A40000023F00
//	TmpBuff[0] =0x00;
//	TmpBuff[1] =0xA4;
//	TmpBuff[2] =0x00;
//	TmpBuff[3] =0x00;
//	TmpBuff[4] =0x02;
//	TmpBuff[5] =0x3F;
//	TmpBuff[6] =0x00;
//	if(Sim_Apdu(slot,TmpBuff,7,RevBuff,&samApdu_rlen,&sw)){
//		PrintLog("PSAM-Select MF %d is ERROR\r\n",slot);
//		return 1;
//	}
//	PrintLog("PSAM-Select MF OK!Data=%s\r\n",BCD2ASC(TmpBuff,7));
//	PrintLog("PPSAM-Select MF:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
//	if(sw != 0x9000){
//		return 1;
//	}
//
//	//步骤1：PSAM卡读0015卡公共信息文件 00B095000E
//	TmpBuff[0] =0x00;
//	TmpBuff[1] =0xB0;
//	TmpBuff[2] =0x95;
//	TmpBuff[3] =0x00;
//	TmpBuff[4] =0x0E;
//	if(Sim_Apdu(slot,TmpBuff,5,RevBuff,&samApdu_rlen,&sw)){
//		PrintLog("PSAM-Read0015 %d is ERROR\r\n",slot);
//		return 1;
//	}
//	PrintLog("PSAM-Read0015 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
//	PrintLog("PSAM-Read0015:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
//	if(sw != 0x9000){
//		return 1;
//	}
//
//	//步骤2：PSAM卡读0016终端信息文件 00B0960006
//	TmpBuff[0] =0x00;
//	TmpBuff[1] =0xB0;
//	TmpBuff[2] =0x96;
//	TmpBuff[3] =0x00;
//	TmpBuff[4] =0x06;
//	if(Sim_Apdu(slot,TmpBuff,5,RevBuff,&samApdu_rlen,&sw)){
//		PrintLog("PSAM-Read0016 %d is ERROR\r\n",slot);
//		return 1;
//	}
//	PrintLog("PSAM-Read0016 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
//	PrintLog("PSAM-Read0016:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
//	if(sw != 0x9000){
//		return 1;
//	}
//	memcpy(u8Terminal,RevBuff,6);//获取终端机编号
//
//	//步骤3：PSAM卡选应用目录 00A40000021001
//	TmpBuff[0] =0x00;
//	TmpBuff[1] =0xA4;
//	TmpBuff[2] =0x00;
//	TmpBuff[3] =0x00;
//	TmpBuff[4] =0x02;
//	TmpBuff[5] =0x10;
//	TmpBuff[6] =0x01;
//	if(Sim_Apdu(slot,TmpBuff,7,RevBuff,&samApdu_rlen,&sw)){
//		PrintLog("PSAM-SelectADF1 %d is ERROR\r\n",slot);
//		return 1;
//	}
//	PrintLog("PSAM-SelectADF1 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
//	PrintLog("PSAM-SelectADF1:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
//	if(sw != 0x9000){
//		return 1;
//	}

	memcpy(u8Terminal,g_BRContext.u8AccPsamTerminalID,6);//获取终端机编号

	//步骤4：CPU卡复合消费初始化  8050 01 02 0B 01 00000001 474AB3002800（秘钥标识符+交易金额+终端机编号）
	TmpBuff[0] =0x80;
	TmpBuff[1] =0x50;
	if(ConsumeFlag==0x00)//普通消费
	{TmpBuff[2] =0x01;}
	else//复合应用消费
	{TmpBuff[2] =0x03;}
	TmpBuff[3] =0x02;
	TmpBuff[4] =0x0B;
	TmpBuff[5] =0x01;//秘钥标识符
	memcpy(&TmpBuff[6],u8ConsumeMoney,4);//交易金额
	memcpy(&TmpBuff[10],u8Terminal,6);//终端机编号
	if(Rfa_APDU(TmpBuff,16,RevBuff,&apdu_rlen)){
		PrintLog("Cpu_InitPurse 80 50 Error\r\n");
		return RW_EC_WRITE_FAILED;
	}
	sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
	PrintLog("Cpu_InitPurse 80 50 OK!Data=%s\r\n",BCD2ASC(TmpBuff,16));
	PrintLog("Cpu_InitPurse_APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
	if(sw != 0x9000){
		return RW_EC_WRITE_FAILED;
	}
	memcpy(u8InitPurseBuff,RevBuff,15);//（4旧金额+2脱机交易序号+3透支限额+1秘钥版本+1算法标识+4伪随机数）

	//步骤5：PSAM卡计算MAC1 8070 0000 1C ECA6BDEB 0000 00000001 06 20150304141822 01 00  4100000100295919(4用户随机数+2用户交易序号+4交易金额+1交易类型标识+4交易日期+3交易时间+1消费密钥版本号+1消费密钥算法标识+8用户卡应用序号+8成员银行标识+8试点城市标识)
	TmpBuff[0] =0x80;
	TmpBuff[1] =0x70;
	TmpBuff[2] =0x00;
	TmpBuff[3] =0x00;											i =4;
	TmpBuff[i] =0x14+0x08;										i +=1;
	memcpy(&TmpBuff[i],&u8InitPurseBuff[11],4);					i +=4;//4用户随机数
	memcpy(&TmpBuff[i],&u8InitPurseBuff[4],2);					i +=2;//2用户交易序号
	memcpy(&TmpBuff[i],&u8ConsumeMoney[0],4);					i +=4;//4交易金额
	if(ConsumeFlag==0x00)//普通消费
	{TmpBuff[i] =0x06;}
	else
	{TmpBuff[i] =0x09;}											i +=1;//1交易类型标识
	memcpy(&TmpBuff[i],bcdCurrentTime,7);						i +=7;//4交易日期+3交易时间
	TmpBuff[i] =u8InitPurseBuff[9];								i +=1;//1消费密钥版本号
	TmpBuff[i] =u8InitPurseBuff[10];							i +=1;//1消费密钥算法标识
	memcpy(&TmpBuff[i],&(stAccCpuCardInfo->Info_0015[12]),8);	i +=8;//8用户卡应用序号
	if(Sim_Apdu(slot,TmpBuff,i,RevBuff,&samApdu_rlen,&sw)){
		PrintLog("PSAM-CalcMAC1 %d is ERROR\r\n",slot);
		return RW_ERR_SAM_RESPONSE;
	}
	PrintLog("PSAM-CalcMAC1 OK!Data=%s\r\n",BCD2ASC(TmpBuff,i));
	PrintLog("PSAM-CalcMAC1:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
	if(sw/0x100==0x61){
		TmpBuff[0] =0x00;
		TmpBuff[1] =0xC0;
		TmpBuff[2] =0x00;
		TmpBuff[3] =0x00;
		TmpBuff[4] =sw%0x100;
		if(Sim_Apdu(slot,TmpBuff,5,RevBuff,&samApdu_rlen,&sw)){
			PrintLog("PSAM-GetMAC1 %d is ERROR\r\n",slot);
			return RW_ERR_SAM_RESPONSE;
		}
		PrintLog("PSAM-GetMAC1 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
		PrintLog("PSAM-GetMAC1:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));

	}
	else if(sw != 0x9000){
		return RW_ERR_SAM_RESPONSE;
	}
	memcpy(u8TerminalTradeNum,RevBuff,4);
	memcpy(u8MAC1,RevBuff+4,4);

	//步骤6：CPU卡更新复合应用数据缓存  80DC 02 BC 30 复合文件数据
	if(ConsumeFlag!=0x00)//复合消费
	{
		TmpBuff[0] =0x80;
		TmpBuff[1] =0xDC;
		TmpBuff[2] =0x02;
		TmpBuff[3] =0xB8;  // BC  to  B8  licd 20150805
		TmpBuff[4] =0x30;
		memcpy(&TmpBuff[5],&(stAccCpuCardInfo->Info_0017[1][0]),0x30);
		if(Rfa_APDU(TmpBuff,0x35,RevBuff,&apdu_rlen)){
			PrintLog("Cpu_Updata 80 DC Error\r\n");
			return RW_EC_WRITE_FAILED;
		}
		sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
		PrintLog("Cpu_Updata 80 DC OK!Data=%s\r\n",BCD2ASC(TmpBuff,0x35));
		PrintLog("Cpu_Updata_APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
		if(sw != 0x9000){
			return RW_EC_WRITE_FAILED;
		}
	}

	//步骤7：CPU卡消费  8054 0100 0F （4终端交易序号+7交易日期时间+4MAC1）
	TmpBuff[0] =0x80;
	TmpBuff[1] =0x54;
	TmpBuff[2] =0x01;
	TmpBuff[3] =0x00;
	TmpBuff[4] =0x0F;i=5;
	memcpy(&TmpBuff[i],u8TerminalTradeNum,4);	i +=4;//4终端交易序号
	memcpy(&TmpBuff[i],bcdCurrentTime,7);		i +=7;//7交易日期时间
	memcpy(&TmpBuff[i],u8MAC1,4);				i +=4;//4MAC1
	if(Rfa_APDU(TmpBuff,20,RevBuff,&apdu_rlen)){
		PrintLog("Cpu_Consume 80 54 Error\r\n");
		return RW_EC_WRITE_FAILED;
	}
	sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
	PrintLog("Cpu_Consume 80 54 OK!Data=%s\r\n",BCD2ASC(TmpBuff,i));
	PrintLog("Cpu_Consume_APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
	if(sw != 0x9000){
		return RW_EC_WRITE_FAILED;
	}
	memcpy(TAC,RevBuff,4);
	memcpy(u8MAC2,RevBuff+4,4);
    memcpy(u8OutTerminalTradeNum,u8TerminalTradeNum,4);
	//步骤8：PSAM验证MAC2 8072 0000 04 MAC2
	TmpBuff[0] =0x80;
	TmpBuff[1] =0x72;
	TmpBuff[2] =0x00;
	TmpBuff[3] =0x00;
	TmpBuff[4] =0x04;
	memcpy(&TmpBuff[5],u8MAC2,4);
	if(Sim_Apdu(slot,TmpBuff,9,RevBuff,&samApdu_rlen,&sw)){
		PrintLog("PSAM-CheckMAC2 %d is ERROR\r\n",slot);
		return RW_ERR_SAM_RESPONSE;
	}
	PrintLog("PSAM-CheckMAC2 OK!Data=%s\r\n",BCD2ASC(TmpBuff,9));
	PrintLog("PSAM-CheckMAC2:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
	if(sw != 0x9000){
		return RW_ERR_SAM_RESPONSE;
	}

	return 0;
}


//地铁AccCpu卡发售
int Write_AccCpuCard_Sail_Info(ST_CARD_ACC_CPU * stAccCpuCardInfo,uint8 SailFlag)//SailFlag 00:11应用控制记录   01:辅助信息文件
{
	int i =0;
	INT32U slot=SAM_SLOT_ACC;//默认地铁CPU卡的PSAM卡槽是1
	INT32U baud=38400;
	INT8U 	rlen=0;
	unsigned char TmpBuff[400];
	INT16U 	apdu_rlen=0;
	INT32U 	samApdu_rlen=0;
	INT8U 	RevBuff[400];
	INT16U	sw;

	uint8 u8Terminal[6];//终端机编号
	uint8 Random[8];//Cpu卡随机数
	uint8 MAC[4];//MAC


	memset(u8Terminal,0x00,sizeof(u8Terminal));
	memset(Random,0x00,sizeof(Random));
	memset(MAC,0x00,sizeof(MAC));



//	//步骤0：PSAM卡选1001应用目录:00A40000021001
//	TmpBuff[0] =0x00;
//	TmpBuff[1] =0xA4;
//	TmpBuff[2] =0x00;
//	TmpBuff[3] =0x00;
//	TmpBuff[4] =0x02;
//	TmpBuff[5] =0x10;
//	TmpBuff[6] =0x01;
//	if(Sim_Apdu(slot,TmpBuff,7,RevBuff,&samApdu_rlen,&sw)){
//		PrintLog("PSAM-Select1001 %d is ERROR\r\n",slot);
//		return 1;
//	}
//	PrintLog("PSAM-Select1001 OK!Data=%s\r\n",BCD2ASC(TmpBuff,7));
//	PrintLog("PSAM-Select1001:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
//	if(sw != 0x9000){
//		return 1;
//	}

	//步骤1：PSAM卡指令:801A260308 4100000320000117
	TmpBuff[0] =0x80;
	TmpBuff[1] =0x1A;
	TmpBuff[2] =0x26;
	TmpBuff[3] =0x03;
	TmpBuff[4] =0x08;i=5;
	memcpy(&TmpBuff[i],stAccCpuCardInfo->IssueInfo_0005.AppSn,8);	i +=8;//8用户卡应用序号
	printf("801A Sim_Apdu:Len=%d,Data=%s\r\n",i,BCD2ASC(TmpBuff,40));
	if(Sim_Apdu(slot,TmpBuff,i,RevBuff,&samApdu_rlen,&sw)){
		printf("PSAM-GetKey:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
		return 1;
	}
	PrintLog("PSAM-GetKey OK!Data=%s\r\n",BCD2ASC(TmpBuff,i));
	PrintLog("PSAM-GetKey:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
	printf("PSAM-GetKey:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
	if(sw != 0x9000){
		return RW_ERR_SAM_RESPONSE;
	}

	//步骤2：CPU卡获取随机数  0084000008
	TmpBuff[0] =0x00;
	TmpBuff[1] =0x84;
	TmpBuff[2] =0x00;
	TmpBuff[3] =0x00;
	TmpBuff[4] =0x08;
	if(Rfa_APDU(TmpBuff,5,RevBuff,&apdu_rlen)){
		PrintLog("Cpu_GetRandom 00 84 Error\r\n");
		return RW_EC_WRITE_FAILED;
	}
	sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
	PrintLog("Cpu_GetRandom 00 84 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
	PrintLog("Cpu_GetRandom_APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
	if(sw != 0x9000){
		return RW_ERR_SAM_RESPONSE;
	}
	memcpy(Random,RevBuff,8);//（8随机数）

	//步骤5：PSAM卡计算MAC
	TmpBuff[0] =0x80;
	TmpBuff[1] =0xFA;
	TmpBuff[2] =0x05;
	TmpBuff[3] =0x00;											i =5;
	memcpy(&TmpBuff[i],Random,8);								i +=8;//8用户随机数
	//00:11应用控制记录   01:辅助信息文件
	if(SailFlag==0x00)//80FA050025 50385E661660FDF0 04DC07BC1C 111601002015050909541500000000000000000000000000
	{
		TmpBuff[4] =0x25;
		TmpBuff[i] =0x04;										i +=1;
		TmpBuff[i] =0xDC;										i +=1;
		TmpBuff[i] =0x07;										i +=1;
		TmpBuff[i] =0xBC;										i +=1;
		TmpBuff[i] =0x1C;										i +=1;
		memcpy(&TmpBuff[i],&(stAccCpuCardInfo->Info_0017[6][0]),48);i +=24;//卡实际24字节
	}
	else if(SailFlag==0x01)//80FA05002D 89B534C0F384A2B9 04D6910024 01002819A01100000000000001F4000000000000000000000000000000000000
	{
		TmpBuff[4] =0x2D;
		TmpBuff[i] =0x04;										i +=1;
		TmpBuff[i] =0xD6;										i +=1;
		TmpBuff[i] =0x91;										i +=1;
		TmpBuff[i] =0x00;										i +=1;
		TmpBuff[i] =0x24;										i +=1;
		memcpy(&TmpBuff[i],&(stAccCpuCardInfo->Info_0011[0]),32);i +=32;//卡实际32字节
	}
	else
	{
		return RW_EC_ILLEGAL_INPUT_PARAM;
	}
	PrintLog("PSAM-CalcMAC1:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
	if(Sim_Apdu(slot,TmpBuff,i,RevBuff,&samApdu_rlen,&sw)){
		PrintLog("PSAM-CalcMAC1 %d is ERROR\r\n",slot);
		return RW_ERR_SAM_RESPONSE;
	}
	PrintLog("PSAM-CalcMAC1 OK!Data=%s\r\n",BCD2ASC(TmpBuff,i));
	PrintLog("PSAM-CalcMAC1:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
	if(sw/0x100==0x61){
		TmpBuff[0] =0x00;
		TmpBuff[1] =0xC0;
		TmpBuff[2] =0x00;
		TmpBuff[3] =0x00;
		TmpBuff[4] =sw%0x100;
		if(Sim_Apdu(slot,TmpBuff,5,RevBuff,&samApdu_rlen,&sw)){
			PrintLog("PSAM-GetMAC1 %d is ERROR\r\n",slot);
			return RW_ERR_SAM_RESPONSE;
		}
		PrintLog("PSAM-GetMAC1 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
		PrintLog("PSAM-GetMAC1:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));

	}
	else if(sw != 0x9000){
		return RW_ERR_SAM_RESPONSE;
	}
	memcpy(MAC,RevBuff,4);

	//步骤6：CPU卡更新文件 //00:11应用控制记录   01:辅助信息文件
	//00:11应用控制记录   01:辅助信息文件
	i=0;
	if(SailFlag==0x00)//80FA050025 50385E661660FDF0 04DC07BC1C 111601002015050909541500000000000000000000000000
	{
		TmpBuff[i] =0x04;										i +=1;
		TmpBuff[i] =0xDC;										i +=1;
		TmpBuff[i] =0x07;										i +=1;
		TmpBuff[i] =0xBC;										i +=1;
		TmpBuff[i] =0x1C;										i +=1;
		memcpy(&TmpBuff[i],&(stAccCpuCardInfo->Info_0017[6][0]),48);i +=24;//卡实际24字节
	}
	else if(SailFlag==0x01)//80FA05002D 89B534C0F384A2B9 04D6910024 01002819A01100000000000001F4000000000000000000000000000000000000
	{
		TmpBuff[i] =0x04;										i +=1;
		TmpBuff[i] =0xD6;										i +=1;
		TmpBuff[i] =0x91;										i +=1;
		TmpBuff[i] =0x00;										i +=1;
		TmpBuff[i] =0x24;										i +=1;
		memcpy(&TmpBuff[i],&(stAccCpuCardInfo->Info_0011[0]),32);i +=32;//卡实际32字节
	}
	else
	{
		return RW_EC_ILLEGAL_INPUT_PARAM;
	}
	memcpy(&TmpBuff[i],MAC,4);									i +=4;

	if(Rfa_APDU(TmpBuff,i,RevBuff,&apdu_rlen)){
		PrintLog("Cpu_UpdataSailFile 04 DX Error\r\n");
		return RW_EC_WRITE_FAILED;
	}
	sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
	PrintLog("Cpu_UpdataSailFile 04 DX OK!Data=%s\r\n",BCD2ASC(TmpBuff,i));
	PrintLog("Cpu_UpdataSailFile_APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
	if(sw != 0x9000){
		return RW_EC_WRITE_FAILED;
	}

	return 0;
}

int Lock_AccCpuCard_Info(uint32 u32ConsumeMoney,uint8* bcdCurrentTime,ST_CARD_ACC_CPU * stAccCpuCardInfo,uint8 *TAC,uint32 *u8OutTerminalTradeNum)
{
	int i =0;
	INT32U slot=SAM_SLOT_ACC;//默认地铁CPU卡的PSAM卡槽是1
	INT32U baud=38400;
	INT8U 	rlen=0;
	unsigned char TmpBuff[400];
	INT16U 	apdu_rlen=0;
	INT32U 	samApdu_rlen=0;
	INT8U 	RevBuff[400];
	INT16U	sw;
	uint8 u8ConsumeMoney[4];//消费金额
	uint8 u8Terminal[6];//终端机编号
	uint8 u8InitPurseBuff[15];//Cpu卡复合消费返回数据（4旧金额+2脱机交易序号+3透支限额+1秘钥版本+1算法标识+4伪随机数）
	uint8 u8TerminalTradeNum[4];//终端脱机交易序号
	uint8 u8MAC1[4];//MAC1
	uint8 u8TAC[4];//TAC
	uint8 u8MAC2[4];//MAC2


	memset(u8Terminal,0x00,sizeof(u8Terminal));
	memset(u8InitPurseBuff,0x00,sizeof(u8InitPurseBuff));
	memset(u8TerminalTradeNum,0x00,sizeof(u8TerminalTradeNum));
	memset(u8MAC1,0x00,sizeof(u8MAC1));
	memset(u8TAC,0x00,sizeof(u8TAC));
	memset(u8MAC2,0x00,sizeof(u8MAC2));
	u8ConsumeMoney[0] =u32ConsumeMoney/0x1000000;//由于不缺定系统存储数据大小端，所以这样写
	u8ConsumeMoney[1] =(u32ConsumeMoney/0x10000)%0x100;
	u8ConsumeMoney[2] =(u32ConsumeMoney/0x100)%0x100;
	u8ConsumeMoney[3] =u32ConsumeMoney%0x100;


//	PrintLog("IccSimReset OK !!!!\n");
//	if(IccSimReset(slot, baud, 3, &rlen,  RevBuff,1)){
//		PrintLog("Reset %d is ERROR\r\n",slot);
//		//CloseSimModule();
//		return 1;
//	}
//	PrintLog("Reset %d is OK\r\n",slot);
//	PrintLog("RESET SIM:Len=%d,Data=%s\r\n",rlen,BCD2ASC(RevBuff,rlen));

//	//步骤0：PSAM卡选MF  00A40000023F00
//	TmpBuff[0] =0x00;
//	TmpBuff[1] =0xA4;
//	TmpBuff[2] =0x00;
//	TmpBuff[3] =0x00;
//	TmpBuff[4] =0x02;
//	TmpBuff[5] =0x3F;
//	TmpBuff[6] =0x00;
//	if(Sim_Apdu(slot,TmpBuff,7,RevBuff,&samApdu_rlen,&sw)){
//		PrintLog("PSAM-Select MF %d is ERROR\r\n",slot);
//		return 1;
//	}
//	PrintLog("PSAM-Select MF OK!Data=%s\r\n",BCD2ASC(TmpBuff,7));
//	PrintLog("PPSAM-Select MF:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
//	if(sw != 0x9000){
//		return 1;
//	}

//	//步骤1：PSAM卡读0015卡公共信息文件 00B095000E
//	TmpBuff[0] =0x00;
//	TmpBuff[1] =0xB0;
//	TmpBuff[2] =0x95;
//	TmpBuff[3] =0x00;
//	TmpBuff[4] =0x0E;
//	if(Sim_Apdu(slot,TmpBuff,5,RevBuff,&samApdu_rlen,&sw)){
//		PrintLog("PSAM-Read0015 %d is ERROR\r\n",slot);
//		return 1;
//	}
//	PrintLog("PSAM-Read0015 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
//	PrintLog("PSAM-Read0015:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
//	if(sw != 0x9000){
//		return 1;
//	}

//	//步骤2：PSAM卡读0016终端信息文件 00B0960006
//	TmpBuff[0] =0x00;
//	TmpBuff[1] =0xB0;
//	TmpBuff[2] =0x96;
//	TmpBuff[3] =0x00;
//	TmpBuff[4] =0x06;
//	if(Sim_Apdu(slot,TmpBuff,5,RevBuff,&samApdu_rlen,&sw)){
//		PrintLog("PSAM-Read0016 %d is ERROR\r\n",slot);
//		return 1;
//	}
//	PrintLog("PSAM-Read0016 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
//	PrintLog("PSAM-Read0016:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
//	if(sw != 0x9000){
//		return 1;
//	}
//	memcpy(u8Terminal,g_BRContext.u8AccPsamTerminalID,6);//获取终端机编号
//
//	//步骤3：PSAM卡选应用目录 00A40000021001
//	TmpBuff[0] =0x00;
//	TmpBuff[1] =0xA4;
//	TmpBuff[2] =0x00;
//	TmpBuff[3] =0x00;
//	TmpBuff[4] =0x02;
//	TmpBuff[5] =0x10;
//	TmpBuff[6] =0x01;
//	if(Sim_Apdu(slot,TmpBuff,7,RevBuff,&samApdu_rlen,&sw)){
//		PrintLog("PSAM-SelectADF1 %d is ERROR\r\n",slot);
//		return 1;
//	}
//	PrintLog("PSAM-SelectADF1 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
//	PrintLog("PSAM-SelectADF1:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
//	if(sw != 0x9000){
//		return 1;
//	}

	memcpy(u8Terminal,g_BRContext.u8AccPsamTerminalID,6);//获取终端机编号
	//步骤4：CPU卡复合消费初始化  8050 01 02 0B 01 00000001 474AB3002800（秘钥标识符+交易金额+终端机编号）
	TmpBuff[0] =0x80;
	TmpBuff[1] =0x50;
    TmpBuff[2] =0x03;//复合应用消费
	TmpBuff[3] =0x02;
	TmpBuff[4] =0x0B;
	TmpBuff[5] =0x01;//秘钥标识符
	memcpy(&TmpBuff[6],u8ConsumeMoney,4);//交易金额
	memcpy(&TmpBuff[10],u8Terminal,6);//终端机编号
	if(Rfa_APDU(TmpBuff,16,RevBuff,&apdu_rlen)){
		PrintLog("Cpu_InitPurse 80 50 Error\r\n");
		return RW_EC_WRITE_FAILED;
	}
	sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
	PrintLog("Cpu_InitPurse 80 50 OK!Data=%s\r\n",BCD2ASC(TmpBuff,16));
	PrintLog("Cpu_InitPurse_APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
	if(sw != 0x9000){
		return RW_EC_WRITE_FAILED;
	}
	memcpy(u8InitPurseBuff,RevBuff,15);//（4旧金额+2脱机交易序号+3透支限额+1秘钥版本+1算法标识+4伪随机数）

	//步骤5：PSAM卡计算MAC1 8070 0000 1C ECA6BDEB 0000 00000001 06 20150304141822 01 00  4100000100295919(4用户随机数+2用户交易序号+4交易金额+1交易类型标识+4交易日期+3交易时间+1消费密钥版本号+1消费密钥算法标识+8用户卡应用序号+8成员银行标识+8试点城市标识)
	TmpBuff[0] =0x80;
	TmpBuff[1] =0x70;
	TmpBuff[2] =0x00;
	TmpBuff[3] =0x00;											i =4;
	TmpBuff[i] =0x14+0x08;										i +=1;
	memcpy(&TmpBuff[i],&u8InitPurseBuff[11],4);					i +=4;//4用户随机数
	memcpy(&TmpBuff[i],&u8InitPurseBuff[4],2);					i +=2;//2用户交易序号
	memcpy(&TmpBuff[i],&u8ConsumeMoney[0],4);					i +=4;//4交易金额
    TmpBuff[i] =0x09;											i +=1;//1交易类型标识
	memcpy(&TmpBuff[i],bcdCurrentTime,7);						i +=7;//4交易日期+3交易时间
	TmpBuff[i] =u8InitPurseBuff[9];								i +=1;//1消费密钥版本号
	TmpBuff[i] =u8InitPurseBuff[10];							i +=1;//1消费密钥算法标识
	memcpy(&TmpBuff[i],&(stAccCpuCardInfo->Info_0015[12]),8);	i +=8;//8用户卡应用序号
	if(Sim_Apdu(slot,TmpBuff,i,RevBuff,&samApdu_rlen,&sw)){
		PrintLog("PSAM-CalcMAC1 %d is ERROR\r\n",slot);
		return RW_ERR_SAM_RESPONSE;
	}
	PrintLog("PSAM-CalcMAC1 OK!Data=%s\r\n",BCD2ASC(TmpBuff,i));
	PrintLog("PSAM-CalcMAC1:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
	if(sw/0x100==0x61){
		TmpBuff[0] =0x00;
		TmpBuff[1] =0xC0;
		TmpBuff[2] =0x00;
		TmpBuff[3] =0x00;
		TmpBuff[4] =sw%0x100;
		if(Sim_Apdu(slot,TmpBuff,5,RevBuff,&samApdu_rlen,&sw)){
			PrintLog("PSAM-GetMAC1 %d is ERROR\r\n",slot);
			return RW_ERR_SAM_RESPONSE;
		}
		PrintLog("PSAM-GetMAC1 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
		PrintLog("PSAM-GetMAC1:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));

	}
	else if(sw != 0x9000){
		return RW_ERR_SAM_RESPONSE;
	}
	memcpy(u8TerminalTradeNum,RevBuff,4);
	memcpy(u8MAC1,RevBuff+4,4);

	//步骤6：CPU卡更新复合应用数据缓存  80DC 02 BC 30 复合文件数据

	TmpBuff[0] =0x80;
	TmpBuff[1] =0xDC;
	TmpBuff[2] =0x01;
	TmpBuff[3] =0xB8;  // BC  to  B8  licd 20150805
	TmpBuff[4] =0x30;
	memcpy(&TmpBuff[5],&(stAccCpuCardInfo->Info_0017[0][0]),0x30);
	if(Rfa_APDU(TmpBuff,0x35,RevBuff,&apdu_rlen)){
		PrintLog("Cpu_Updata 80 DC Error\r\n");
		return RW_EC_WRITE_FAILED;
	}
	sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
	PrintLog("Cpu_Updata 80 DC OK!Data=%s\r\n",BCD2ASC(TmpBuff,0x35));
	PrintLog("Cpu_Updata_APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
	if(sw != 0x9000){
		return RW_EC_WRITE_FAILED;
	}


	//步骤7：CPU卡消费  8054 0100 0F （4终端交易序号+7交易日期时间+4MAC1）
	TmpBuff[0] =0x80;
	TmpBuff[1] =0x54;
	TmpBuff[2] =0x01;
	TmpBuff[3] =0x00;
	TmpBuff[4] =0x0F;i=5;
	memcpy(&TmpBuff[i],u8TerminalTradeNum,4);	i +=4;//4终端交易序号
	memcpy(&TmpBuff[i],bcdCurrentTime,7);		i +=7;//7交易日期时间
	memcpy(&TmpBuff[i],u8MAC1,4);				i +=4;//4MAC1
	if(Rfa_APDU(TmpBuff,20,RevBuff,&apdu_rlen)){
		PrintLog("Cpu_Consume 80 54 Error\r\n");
		return RW_EC_WRITE_FAILED;
	}
	sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
	PrintLog("Cpu_Consume 80 54 OK!Data=%s\r\n",BCD2ASC(TmpBuff,i));
	PrintLog("Cpu_Consume_APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
	if(sw != 0x9000){
		return RW_EC_WRITE_FAILED;
	}
	memcpy(TAC,RevBuff,4);
	memcpy(u8MAC2,RevBuff+4,4);
    memcpy(u8OutTerminalTradeNum,u8TerminalTradeNum,4);
	//步骤8：PSAM验证MAC2 8072 0000 04 MAC2
	TmpBuff[0] =0x80;
	TmpBuff[1] =0x72;
	TmpBuff[2] =0x00;
	TmpBuff[3] =0x00;
	TmpBuff[4] =0x04;
	memcpy(&TmpBuff[5],u8MAC2,4);
	if(Sim_Apdu(slot,TmpBuff,9,RevBuff,&samApdu_rlen,&sw)){
		PrintLog("PSAM-CheckMAC2 %d is ERROR\r\n",slot);
		return RW_ERR_SAM_RESPONSE;
	}
	PrintLog("PSAM-CheckMAC2 OK!Data=%s\r\n",BCD2ASC(TmpBuff,9));
	PrintLog("PSAM-CheckMAC2:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
	if(sw != 0x9000){
		return RW_ERR_SAM_RESPONSE;
	}

	return 0;
}

//圈存初始化获取信息
int PopupInit_AccCpuCard(uint32 u32PopupMoney,uint8 * u8InitPurseBuff)
{
	//int i =0;
	INT32U slot=SAM_SLOT_ACC;//默认地铁CPU卡的PSAM卡槽是1
	INT32U baud=38400;
	INT8U 	rlen=0;
	unsigned char TmpBuff[400];
	INT32U 	apdu_rlen=0;
	INT16U	cpuApdu_rlen=0;
	INT8U 	RevBuff[400];
	INT16U	sw;
	uint8 u8PopupMoney[4];//圈存金额
	uint8 u8Terminal[6];//终端机编号
//	uint8 u8InitPurseBuff[16];//Cpu卡圈存初始化返回数据（4旧金额+2联机交易序号+1秘钥版本+1算法标识+4伪随机数+4MAC1）
	uint8 u8TerminalTradeNum[4];//终端脱机交易序号
	uint8 u8MAC1[4];//MAC1
	uint8 u8TAC[4];//TAC
	uint8 u8MAC2[4];//MAC2
	memset(u8Terminal,0x00,sizeof(u8Terminal));
	memcpy(u8Terminal,g_BRContext.u8AccPsamTerminalID,6);


	memset(u8InitPurseBuff,0x00,sizeof(u8InitPurseBuff));
	memset(u8TerminalTradeNum,0x00,sizeof(u8TerminalTradeNum));
	memset(u8MAC1,0x00,sizeof(u8MAC1));
	memset(u8TAC,0x00,sizeof(u8TAC));
	memset(u8MAC2,0x00,sizeof(u8MAC2));
	u8PopupMoney[0] =u32PopupMoney/0x1000000;//由于不缺定系统存储数据大小端，所以这样写
	u8PopupMoney[1] =(u32PopupMoney/0x10000)%0x100;
	u8PopupMoney[2] =(u32PopupMoney/0x100)%0x100;
	u8PopupMoney[3] =u32PopupMoney%0x100;

//	if(OpenSimMoudle()){
//		PrintLog("OpenSimMoudle ERROR !!!!\n");
//		return 1;
//	}
//	PrintLog("OpenSimMoudle OK !!!!\n");
//	if(IccSimReset(slot, baud, 3, &rlen,  RevBuff,0)){
//		PrintLog("Reset %d is ERROR\r\n",slot);
//		//CloseSimModule();
//		return 1;
//	}
//	PrintLog("Reset %d is OK\r\n",slot);
//	PrintLog("RESET SIM:Len=%d,Data=%s\r\n",rlen,BCD2ASC(RevBuff,rlen));
//
//	//步骤1：PSAM卡读0015卡公共信息文件 00B095000E
//	TmpBuff[0] =0x00;
//	TmpBuff[1] =0xB0;
//	TmpBuff[2] =0x95;
//	TmpBuff[3] =0x00;
//	TmpBuff[4] =0x0E;
//	if(Sim_Apdu(slot,TmpBuff,5,RevBuff,&apdu_rlen,&sw)){
//		PrintLog("PSAM-Read0015 %d is ERROR\r\n",slot);
//		return 1;
//	}
//	PrintLog("PSAM-Read0015 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
//	PrintLog("PSAM-Read0015:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
//	if(sw != 0x9000){
//		return 1;
//	}
//
//	//步骤2：PSAM卡读0016终端信息文件 00B0960006
//	TmpBuff[0] =0x00;
//	TmpBuff[1] =0xB0;
//	TmpBuff[2] =0x96;
//	TmpBuff[3] =0x00;
//	TmpBuff[4] =0x06;
//	if(Sim_Apdu(slot,TmpBuff,5,RevBuff,&apdu_rlen,&sw)){
//		PrintLog("PSAM-Read0016 %d is ERROR\r\n",slot);
//		return 1;
//	}
//	PrintLog("PSAM-Read0016 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
//	PrintLog("PSAM-Read0016:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
//	if(sw != 0x9000){
//		return 1;
//	}
//	memcpy(u8Terminal,RevBuff,6);//获取终端机编号



//	//步骤1：读0018本地消费明细文件过程 00A40000020018
//	TmpBuff[0] =0x00;
//	TmpBuff[1] =0xA4;
//	TmpBuff[2] =0x00;
//	TmpBuff[3] =0x00;
//	TmpBuff[4] =0x02;
//	TmpBuff[5] =0x00;
//	TmpBuff[6] =0x18;
//	if(Rfa_APDU(TmpBuff,7,RevBuff,&cpuApdu_rlen)){
//		PrintLog("Cpu_0018 Error\r\n");//PrintLog
//		return 1;
//	}
//	sw =RevBuff[cpuApdu_rlen-2]*256+RevBuff[cpuApdu_rlen-1];
//	PrintLog("Cpu_0018 00 20 OK!Data=%s\r\n",BCD2ASC(TmpBuff,7));
//	PrintLog("Cpu_0018_APDU:Len=%d,SW=%04X,Data=%s\r\n",cpuApdu_rlen,sw,BCD2ASC(RevBuff,cpuApdu_rlen));
//	if(sw != 0x9000){
//		return 1;
//	}


	//步骤2：读最新脱机交易序号 00B2010417
	TmpBuff[0] =0x00;
	TmpBuff[1] =0xB2;
	TmpBuff[2] =0x01;
	TmpBuff[3] =0xC4;
	TmpBuff[4] =0x00;
	if(Rfa_APDU(TmpBuff,5,RevBuff,&cpuApdu_rlen)){
		PrintLog("Cpu_0018 00 20 Error\r\n");
		return 1;
	}
	sw =RevBuff[cpuApdu_rlen-2]*256+RevBuff[cpuApdu_rlen-1];
	PrintLog("Cpu_0018 00 20 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
	PrintLog("Cpu_0018_APDU:Len=%d,SW=%04X,Data=%s\r\n",cpuApdu_rlen,sw,BCD2ASC(RevBuff,cpuApdu_rlen));
	if(sw == 0x6A83)
	{memset(&u8InitPurseBuff[16],0x00,2);}
	else if(sw != 0x9000){
		return RW_EC_WRITE_FAILED;
	}
	memcpy(&u8InitPurseBuff[16],RevBuff,2);




	//步骤3：CPU卡PIN校验 0020000003123456
	TmpBuff[0] =0x00;
	TmpBuff[1] =0x20;
	TmpBuff[2] =0x00;
	TmpBuff[3] =0x00;
	TmpBuff[4] =0x03;
	TmpBuff[5] =0x12;
	TmpBuff[6] =0x34;
	TmpBuff[7] =0x56;
	if(Rfa_APDU(TmpBuff,8,RevBuff,&cpuApdu_rlen)){
		PrintLog("Cpu_PIN 00 20 Error\r\n");
		return RW_EC_WRITE_FAILED;
	}
	sw =RevBuff[cpuApdu_rlen-2]*256+RevBuff[cpuApdu_rlen-1];
	PrintLog("Cpu_PIN 00 20 OK!Data=%s\r\n",BCD2ASC(TmpBuff,8));
	PrintLog("Cpu_PIN_APDU:Len=%d,SW=%04X,Data=%s\r\n",cpuApdu_rlen,sw,BCD2ASC(RevBuff,cpuApdu_rlen));
	if(sw != 0x9000){
		return RW_EC_WRITE_FAILED;
	}

	//步骤4：CPU卡圈存初始化  8050 00 02 0B 01 00000001 474AB3002800（秘钥标识符+交易金额+终端机编号）
	TmpBuff[0] =0x80;
	TmpBuff[1] =0x50;
	TmpBuff[2] =0x00;
	TmpBuff[3] =0x02;
	TmpBuff[4] =0x0B;
	TmpBuff[5] =0x01;//秘钥标识符
	memcpy(&TmpBuff[6],u8PopupMoney,4);//交易金额
	memcpy(&TmpBuff[10],u8Terminal,6);//终端机编号
	if(Rfa_APDU(TmpBuff,16,RevBuff,&cpuApdu_rlen)){
		PrintLog("Cpu_PopupInit 80 50 Error\r\n");
		return RW_EC_WRITE_FAILED;
	}
	sw =RevBuff[cpuApdu_rlen-2]*256+RevBuff[cpuApdu_rlen-1];
	PrintLog("Cpu_PopupInit 80 50 OK!Data=%s\r\n",BCD2ASC(TmpBuff,16));
	PrintLog("Cpu_PopupInit_APDU:Len=%d,SW=%04X,Data=%s\r\n",cpuApdu_rlen,sw,BCD2ASC(RevBuff,cpuApdu_rlen));
	if(sw != 0x9000){
		return RW_EC_WRITE_FAILED;
	}
	memcpy(u8InitPurseBuff,RevBuff,16);//（4旧金额+2脱机交易序号+1秘钥版本+1算法标识+4伪随机数+4MAC1）

	return 0;
}


//圈存
int Popup_AccCpuCard(uint8* bcdCurrentTime,uint8* u8MAC2,uint8* u8TAC)//输入时间和MAC2  输出TAC
{
	int i =0;
	//INT8U 	rlen=0;
	unsigned char TmpBuff[400];
	INT16U 	cpuApdu_rlen=0;
	INT8U 	RevBuff[400];
	INT16U	sw;
	uint8 u8InitPurseBuff[16];//Cpu卡圈存初始化返回数据（4旧金额+2脱机交易序号+1秘钥版本+1算法标识+4伪随机数+4MAC1）

	memset(TmpBuff,0x00,sizeof(TmpBuff));
	memset(RevBuff,0x00,sizeof(RevBuff));
	memset(u8InitPurseBuff,0x00,sizeof(u8InitPurseBuff));


	//步骤5：CPU卡圈存  8052 0000 0B （7交易日期时间+4MAC2）
	TmpBuff[0] =0x80;
	TmpBuff[1] =0x52;
	TmpBuff[2] =0x00;
	TmpBuff[3] =0x00;
	TmpBuff[4] =0x0B;i=5;
	memcpy(&TmpBuff[i],bcdCurrentTime,7);		i +=7;//7交易日期时间
	memcpy(&TmpBuff[i],u8MAC2,4);				i +=4;//4MAC2
	if(Rfa_APDU(TmpBuff,16,RevBuff,&cpuApdu_rlen)){
		PrintLog("Cpu_Popup 80 52 Error\r\n");
		return RW_EC_WRITE_FAILED;
	}
	sw =RevBuff[cpuApdu_rlen-2]*256+RevBuff[cpuApdu_rlen-1];
	PrintLog("Cpu_Popup 80 54 OK!Data=%s\r\n",BCD2ASC(TmpBuff,i));
	PrintLog("Cpu_Popup_APDU:Len=%d,SW=%04X,Data=%s\r\n",cpuApdu_rlen,sw,BCD2ASC(RevBuff,cpuApdu_rlen));
	if(sw != 0x9000){
		return RW_EC_WRITE_FAILED;
	}
	memcpy(u8TAC,RevBuff,4);

	return 0;
}

int Unlock_AccCpuCard_Info(ST_CARD_ACC_CPU * stAccCpuCardInfo)
{
	int i =0;
	INT32U slot=SAM_SLOT_ACC;//默认地铁CPU卡的PSAM卡槽是1
	INT32U baud=38400;
	INT8U 	rlen=0;
	unsigned char TmpBuff[400];
	INT16U 	apdu_rlen=0;
	INT32U 	samApdu_rlen=0;
	INT8U 	RevBuff[400];
	INT16U	sw;

	uint8 u8Terminal[6];//终端机编号
	uint8 Random[8];//Cpu卡随机数
	uint8 MAC[4];//MAC


	memset(u8Terminal,0x00,sizeof(u8Terminal));
	memset(Random,0x00,sizeof(Random));
	memset(MAC,0x00,sizeof(MAC));



//	//步骤0：PSAM卡选1001应用目录:00A40000021001
//	TmpBuff[0] =0x00;
//	TmpBuff[1] =0xA4;
//	TmpBuff[2] =0x00;
//	TmpBuff[3] =0x00;
//	TmpBuff[4] =0x02;
//	TmpBuff[5] =0x10;
//	TmpBuff[6] =0x01;
//	if(Sim_Apdu(slot,TmpBuff,7,RevBuff,&samApdu_rlen,&sw)){
//		PrintLog("PSAM-Select1001 %d is ERROR\r\n",slot);
//		return 1;
//	}
//	PrintLog("PSAM-Select1001 OK!Data=%s\r\n",BCD2ASC(TmpBuff,7));
//	PrintLog("PSAM-Select1001:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
//	if(sw != 0x9000){
//		return 1;
//	}

	//步骤1：PSAM卡指令:801A260308 4100000320000117
	TmpBuff[0] =0x80;
	TmpBuff[1] =0x1A;
	TmpBuff[2] =0x26;
	TmpBuff[3] =0x03;
	TmpBuff[4] =0x08;i=5;
	memcpy(&TmpBuff[i],stAccCpuCardInfo->IssueInfo_0005.AppSn,8);	i +=8;//8用户卡应用序号
	printf("801A Sim_Apdu:Len=%d,Data=%s\r\n",i,BCD2ASC(TmpBuff,40));
	if(Sim_Apdu(slot,TmpBuff,i,RevBuff,&samApdu_rlen,&sw)){
		printf("PSAM-GetKey:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
		return RW_ERR_SAM_RESPONSE;
	}
	PrintLog("PSAM-GetKey OK!Data=%s\r\n",BCD2ASC(TmpBuff,i));
	PrintLog("PSAM-GetKey:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
	printf("PSAM-GetKey:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
	if(sw != 0x9000){
		return RW_ERR_SAM_RESPONSE;
	}

	//步骤2：CPU卡获取随机数  0084000008
	TmpBuff[0] =0x00;
	TmpBuff[1] =0x84;
	TmpBuff[2] =0x00;
	TmpBuff[3] =0x00;
	TmpBuff[4] =0x08;
	if(Rfa_APDU(TmpBuff,5,RevBuff,&apdu_rlen)){
		PrintLog("Cpu_GetRandom 00 84 Error\r\n");
		return RW_EC_WRITE_FAILED;
	}
	sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
	PrintLog("Cpu_GetRandom 00 84 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
	PrintLog("Cpu_GetRandom_APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
	if(sw != 0x9000){
		return RW_EC_WRITE_FAILED;
	}
	memcpy(Random,RevBuff,8);//（8随机数）

	//步骤5：PSAM卡计算MAC
	TmpBuff[0] =0x80;
	TmpBuff[1] =0xFA;
	TmpBuff[2] =0x05;
	TmpBuff[3] =0x00;											i =5;
	memcpy(&TmpBuff[i],Random,8);								i +=8;//8用户随机数
	//00:11应用控制记录   01:辅助信息文件
     //80FA050025 50385E661660FDF0 04DC07BC1C 111601002015050909541500000000000000000000000000
	TmpBuff[4] =0x3D;
	TmpBuff[i] =0x04;										i +=1;
	TmpBuff[i] =0xDC;										i +=1;
	TmpBuff[i] =0x01;										i +=1;
	TmpBuff[i] =0xBC;										i +=1;
	TmpBuff[i] =0x34;										i +=1;
	memcpy(&TmpBuff[i],&(stAccCpuCardInfo->Info_0017[0][0]),48);
	i +=48;//卡实际48字节

	PrintLog("PSAM-CalcMAC1:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
	if(Sim_Apdu(slot,TmpBuff,i,RevBuff,&samApdu_rlen,&sw)){
		PrintLog("PSAM-CalcMAC1 %d is ERROR\r\n",slot);
		return RW_ERR_SAM_RESPONSE;
	}
	PrintLog("PSAM-CalcMAC1 OK!Data=%s\r\n",BCD2ASC(TmpBuff,i));
	PrintLog("PSAM-CalcMAC1:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));
	if(sw/0x100==0x61){
		TmpBuff[0] =0x00;
		TmpBuff[1] =0xC0;
		TmpBuff[2] =0x00;
		TmpBuff[3] =0x00;
		TmpBuff[4] =sw%0x100;
		if(Sim_Apdu(slot,TmpBuff,5,RevBuff,&samApdu_rlen,&sw)){
			PrintLog("PSAM-GetMAC1 %d is ERROR\r\n",slot);
			return RW_ERR_SAM_RESPONSE;
		}
		PrintLog("PSAM-GetMAC1 OK!Data=%s\r\n",BCD2ASC(TmpBuff,5));
		PrintLog("PSAM-GetMAC1:Len=%d,SW=%04X,Data=%s\r\n",samApdu_rlen,sw,BCD2ASC(RevBuff,samApdu_rlen));

	}
	else if(sw != 0x9000){
		return RW_ERR_SAM_RESPONSE;
	}
	memcpy(MAC,RevBuff,4);

	//步骤6：CPU卡更新文件 //00:11应用控制记录   01:辅助信息文件
	//00:11应用控制记录   01:辅助信息文件
	i=0;
     //80FA050025 50385E661660FDF0 04DC07BC1C 111601002015050909541500000000000000000000000000
	TmpBuff[i] =0x04;										i +=1;
	TmpBuff[i] =0xDC;										i +=1;
	TmpBuff[i] =0x01;										i +=1;
	TmpBuff[i] =0xBC;										i +=1;
	TmpBuff[i] =0x34;										i +=1;
	memcpy(&TmpBuff[i],&(stAccCpuCardInfo->Info_0017[0][0]),48);i +=48;//卡实际24字节
	memcpy(&TmpBuff[i],MAC,4);									i +=4;

	if(Rfa_APDU(TmpBuff,i,RevBuff,&apdu_rlen)){
		PrintLog("Cpu_UpdataSailFile 04 DX Error\r\n");
		return RW_EC_WRITE_FAILED;
	}
	sw =RevBuff[apdu_rlen-2]*256+RevBuff[apdu_rlen-1];
	PrintLog("Cpu_UpdataSailFile 04 DX OK!Data=%s\r\n",BCD2ASC(TmpBuff,i));
	PrintLog("Cpu_UpdataSailFile_APDU:Len=%d,SW=%04X,Data=%s\r\n",apdu_rlen,sw,BCD2ASC(RevBuff,apdu_rlen));
	if(sw != 0x9000){
		return RW_EC_WRITE_FAILED;
	}

	return 0;
}




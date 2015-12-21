"""
# 本脚本功能：
#   长沙地铁应用圈存
"""
#两个16进制数相加 输出字符串
# a = '00200000' b='00010000'
# a +b = '00210000'
# a -b = '001F0000'
def str_add(a,b):
    tmp = int(a,16) + int(b,16)
    return '%08x'%tmp
def str_dec(a,b):
    tmp = int(a,16) - int(b,16)
    return '%08x'%tmp

#长沙地铁应用AID
applet_aid = 'A00000000386980700'
ep_df01 = 'A00000000386980701'
card_no = '4100000089999985'


#圈存子密钥1  -密钥接口获取 DLK 01-07
load1 = '374598B5C0B0EAE1E1D06609AC5BB369'
#TAC密钥 -密钥接口获取 DTK 02-04
tac = 'CAA6025290114BFCC3C9043F70F7A07A'


atr = scf_reset()
# select aid
if CONTACTTAG=='00':
    r = scf_gp_select_applet_by_aid(applet_aid)
    scf_assert_str(r[-4:],'9000','failed!')


r = scf_gp_select_applet_by_aid(ep_df01)
scf_assert_str(r[-4:],'9000','failed!')

#Verify pin
r = scf_send_str('0020 0000 03 123456')
scf_assert_str(r[-4:],'9000','failed!')

# get balance
r = scf_send_str('80 5C 00 02 04')
#交易前余额
JYQYE = r[:8]

# load
#-------------------------------------------------
#圈存初始化:80 50 00 02 0B 密钥索引号1 交易金额4 终端机编号6
#-------------------------------------------------
#交易金额
JYJE = '00001C37'
#终端机编号    
TPOS_NO = '000001000050'

r = scf_send_str('80 50 00 02 0B 01' +  JYJE + TPOS_NO)
if CONTACTTAG=='00':
    r = scf_send_str('00c00000'+r[-2:])

#卡片响应：余额4 联机交易序号2 密钥版本号1（DLK）算法标识1（DLK）伪随机数4 MAC14
#ASSERT 00000000 0000 02 00 4D5FED82 DBA6B929 9000

#伪随机数
Random = r[16:24]
print 'Random = ' + Random
#钱包余额
ETYE = r[:8]
print 'ETYE = '+ ETYE
#联机交易序号
ETLJJYXH = r[8:12]
print 'ETLJJYXH = '+ ETLJJYXH

#卡片返回的MAC1
CARDMAC1 = r[24:32]
print 'CARDMAC1 = '+ CARDMAC1
#计算会话密钥
DESInputData = Random + ETLJJYXH + '8000'
SESKEY = scf_tdesenc(load1 ,DESInputData ,0)

# 计算MAC1
MACInit = '0000000000000000'
MACInputData = ETYE + JYJE + '02' + TPOS_NO
MAC1Result = scf_desmac(SESKEY,MACInputData,MACInit)
MAC1Result = MAC1Result[:8]
print 'MAC1Result = '+ MAC1Result

if CARDMAC1 != MAC1Result:
    scf_script_error('failed')


#-------------------------------------------------
#正确的圈存命令 80 52 00 00 0B 主机交易日期4 主机交易时间3 MAC24
#-------------------------------------------------
#MAC2=DES（SESLK）[交易金额||交易类型标识||终端机编号||主机交易日期||主机交易时间]

MACInputData = JYJE +'02'+ TPOS_NO+ '20140825103100'
MAC2Result = scf_desmac(SESKEY,MACInputData,MACInit)
MAC2Result = MAC2Result[:8]
r = scf_send_str('80 52 00 00 0B' + '20140825103100' + MAC2Result)
if CONTACTTAG=='00':
    r = scf_send_str('00c00000'+r[-2:])
CARDTAC = r[:8]
print 'CARDTAC =' + CARDTAC	
#TAC：DES（DTK左8 XOR DTK右8）[余额||联机交易序号（+1前）||交易金额||交易类型标识||终端机编号||主机交易日期||主机交易时间]
SESKEY = scf_XOR(tac[:16],tac[16:32])
print "SESKEY:" + SESKEY
JYHYE = str_add(ETYE,JYJE)
JYHYE = JYHYE.upper()
print 'JYHYE =' + JYHYE
MACInit = '0000000000000000'
#MACInputData = JYHYE + ETLJJYXH + JYJE + '02' + TPOS_NO + '20140825103100' + '8000000000000000'
MACInputData = JYHYE + ETLJJYXH + JYJE + '02' + TPOS_NO + '20140825103100'
print "MACInit:" + MACInit
print "MACInputData:" + MACInputData
TACResult = scf_desmac(SESKEY,MACInputData,MACInit)
TACResult = TACResult[:8]
print 'TACResult = ' + TACResult

if CARDTAC != TACResult:
    scf_script_error('failed')

r = scf_send_str('80 5C 00 02 04')
if r[:8] != JYHYE:
    scf_script_error('failed')

scf_script_success()
"""
# ���ű����ܣ�
#   ��ɳ����Ӧ��Ȧ��
"""
#����16��������� ����ַ���
# a = '00200000' b='00010000'
# a +b = '00210000'
# a -b = '001F0000'
def str_add(a,b):
    tmp = int(a,16) + int(b,16)
    return '%08x'%tmp
def str_dec(a,b):
    tmp = int(a,16) - int(b,16)
    return '%08x'%tmp

#��ɳ����Ӧ��AID
applet_aid = 'A00000000386980700'
ep_df01 = 'A00000000386980701'
card_no = '4100000089999985'


#Ȧ������Կ1  -��Կ�ӿڻ�ȡ DLK 01-07
load1 = '374598B5C0B0EAE1E1D06609AC5BB369'
#TAC��Կ -��Կ�ӿڻ�ȡ DTK 02-04
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
#����ǰ���
JYQYE = r[:8]

# load
#-------------------------------------------------
#Ȧ���ʼ��:80 50 00 02 0B ��Կ������1 ���׽��4 �ն˻����6
#-------------------------------------------------
#���׽��
JYJE = '00001C37'
#�ն˻����    
TPOS_NO = '000001000050'

r = scf_send_str('80 50 00 02 0B 01' +  JYJE + TPOS_NO)
if CONTACTTAG=='00':
    r = scf_send_str('00c00000'+r[-2:])

#��Ƭ��Ӧ�����4 �����������2 ��Կ�汾��1��DLK���㷨��ʶ1��DLK��α�����4 MAC14
#ASSERT 00000000 0000 02 00 4D5FED82 DBA6B929 9000

#α�����
Random = r[16:24]
print 'Random = ' + Random
#Ǯ�����
ETYE = r[:8]
print 'ETYE = '+ ETYE
#�����������
ETLJJYXH = r[8:12]
print 'ETLJJYXH = '+ ETLJJYXH

#��Ƭ���ص�MAC1
CARDMAC1 = r[24:32]
print 'CARDMAC1 = '+ CARDMAC1
#����Ự��Կ
DESInputData = Random + ETLJJYXH + '8000'
SESKEY = scf_tdesenc(load1 ,DESInputData ,0)

# ����MAC1
MACInit = '0000000000000000'
MACInputData = ETYE + JYJE + '02' + TPOS_NO
MAC1Result = scf_desmac(SESKEY,MACInputData,MACInit)
MAC1Result = MAC1Result[:8]
print 'MAC1Result = '+ MAC1Result

if CARDMAC1 != MAC1Result:
    scf_script_error('failed')


#-------------------------------------------------
#��ȷ��Ȧ������ 80 52 00 00 0B ������������4 ��������ʱ��3 MAC24
#-------------------------------------------------
#MAC2=DES��SESLK��[���׽��||�������ͱ�ʶ||�ն˻����||������������||��������ʱ��]

MACInputData = JYJE +'02'+ TPOS_NO+ '20140825103100'
MAC2Result = scf_desmac(SESKEY,MACInputData,MACInit)
MAC2Result = MAC2Result[:8]
r = scf_send_str('80 52 00 00 0B' + '20140825103100' + MAC2Result)
if CONTACTTAG=='00':
    r = scf_send_str('00c00000'+r[-2:])
CARDTAC = r[:8]
print 'CARDTAC =' + CARDTAC	
#TAC��DES��DTK��8 XOR DTK��8��[���||����������ţ�+1ǰ��||���׽��||�������ͱ�ʶ||�ն˻����||������������||��������ʱ��]
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
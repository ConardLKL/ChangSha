"""
# ���ű����ܣ�
#   ��ɳ����Ӧ�ø��˻��ͷ���
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

#��Ƭ������Կ   -�̶�����
transKey='FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF'
#��Ƭ��������Կ -��Կ�ӿڻ�ȡ DCCK 03-01
cardControl ='D03A01624D2E0255E5CE47218BA45DC8' 
#��Ƭά������Կ -��Կ�ӿڻ�ȡ DCCK 03-02
cardManage  ='F28E92292DCB63F11EA7F5776A7122B8' 

#Ӧ����������Կ -��Կ�ӿڻ�ȡ DCCK  03-03
appControl = '6CA1901BE8F968A23A6BD5F62A20BDC8'
#Ӧ��ά������Կ1 -��Կ�ӿڻ�ȡ DCMK 03-04
appManage = 'F54C39CABBB8B3C46661640123322920'
#�ļ�������Կ1 -��Կ�ӿڻ�ȡ DCMK01 02-01  ���ڸ���sfi=15\16
FileManage1 = 'FE43601E47B93FC500099AA0A17D1554'
#�ļ�������Կ2 -��Կ�ӿڻ�ȡ DCMK02 03-06  ���ڸ���sfi=17\11\12
FileManage2 = '78BBDFD3CECA48FD7BD5F6AE69882C60'
#��������ά����Կ -��Կ�ӿڻ�ȡ DPK
cappManage = '4987876B59A1A5A5981F0B228F13D4EA' 
#PIN������Կ -��Կ�ӿڻ�ȡ 01-09
pinUnblock = '8D866109C6C9C26216FF859B0C1E20B2' 
#PIN��װ��Կ -��Կ�ӿڻ�ȡ DPRK 01-10
pinReload = 'C3BBF8CEF78F40F0E8A1E5ED2E913743' 
#������Կ1 -��Կ�ӿڻ�ȡ DPK 02-02
purchase1 = 'A338D92A10C8285411E761445C831C22' 
#Ȧ������Կ1 -��Կ�ӿڻ�ȡ DLK 01-07
load1 = '374598B5C0B0EAE1E1D06609AC5BB369' 
#Ȧ������Կ2 -��Կ�ӿڻ�ȡ ���ṩʱ��Ĭ��ֵ����
load2 = 'FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF' 
#TAC��Կ -��Կ�ӿڻ�ȡ DTK 02-04
tac = 'CAA6025290114BFCC3C9043F70F7A07A'
#Ȧ������Կ -��Կ�ӿڻ�ȡ ���ṩʱ��Ĭ��ֵ����
upload = 'FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF' 
#�޸�͸֧�޶� -��Կ�ӿڻ�ȡ ���ṩʱ��Ĭ��ֵ����
update = 'FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF' 
#Ӧ������ -��Կ�ӿڻ�ȡ DABK 02-03
applock = '6474215EF549707831BB8DB9895FD48A' 
#Ӧ�ý��� -��Կ�ӿڻ�ȡ DAUK 03-05
appprelock = 'A2F237922142D1605DDD33113E617823' 

atr = scf_reset()
if CONTACTTAG=='00':
    r = scf_gp_select_applet_by_aid(applet_aid)
    scf_assert_str(r[-4:],'9000','failed!')

r = scf_gp_select_applet_by_aid('315041592E5359532E4444463031')
scf_assert_str(r[-4:],'9000','Testfailed!')

#�ⲿ��֤ 
# external authenticate
r = scf_send_str('0084000004')
random = r[:8]+'00000000'
tdesResult = scf_tdesenc(transKey ,random ,0)
r = scf_send_str('0082000008' + tdesResult)
scf_assert_str(r[-4:],'9000','failed!')

# д��UID -�ӵ����ICCID��UID�Ķ�Ӧ��ϵ���л�ȡ
UID = '88D32B76'
r = scf_send_str('0022000009 85'+UID+ '88D32B76')
scf_assert_str(r[-4:],'9000','failed!')

#��Ƭ��������Կ 
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ cardControl + '800000'
keyCipher = scf_tdesenc(transKey ,keyData ,0)
macData = '84D439001C'+ keyCipher
macResult = scf_tdesmac(transKey , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D439001C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

#��Ƭά������Կ
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ cardManage + '800000'
keyCipher = scf_tdesenc(cardControl ,keyData ,0)
macData = '84D436001C'+ keyCipher
macResult = scf_tdesmac(cardControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D436001C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')


r = scf_gp_select_applet_by_aid('315041592E5359532E4444463031')
scf_assert_str(r[-4:],'9000','Testfailed!')

# update card manage key
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ cardManage + '800000'
keyCipher = scf_tdesenc(cardControl ,keyData ,0)
macData = '84D436001C'+ keyCipher
macResult = scf_tdesmac(cardControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D436001C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# update 0005
# �ļ���ʽ��
# 2 ���������� -������ Ĭ��ֵ0731
# 2 ���д��� -������ Ĭ��ֵ4100
# 2 ��ҵ���� -������ Ĭ��ֵ0000
# 1 ���Ա�� -������ Ĭ��ֵ00 ������
# 1 �������� - 00 �̶�ֵ 
# 8 Ӧ�����кţ��߼����ţ� - �߼�����
# 2 ������ -������ Ĭ��ֵ 0200 ������ͨ��ֵƱ 
# 4 �������� - ����ϵͳ�������ڣ�ȡʵ������
# 6 �����豸��Ϣ - ������ 
# 2 ���汾�� -������ ��Ӧ���߼����ݽṹ�汾
# 4 ���������� -������ ����ϵͳ�п�Ӧ��ʹ�õ���ʼ����
# 4 ����Ч���� -������ �ڴ�����ǰ��Ƭ��Ч
# 2 ���� -0000 �̶�ֵ
print " mf:0005 ���л�����Ϣ�ļ�"
print "�ļ�������Ҫ�滻"
print " �ļ���ʽ��"
print " 2 ���������� -������ Ĭ��ֵ0731 "
print " 2 ���д��� -������ Ĭ��ֵ4100 ͨ��ACC-33�ӿڻ�ȡ"
print " 2 ��ҵ���� -������ Ĭ��ֵ0000 ͨ��ACC-33�ӿڻ�ȡ "
print " 1 ���Ա�� -������ Ĭ��ֵ00 ������"
print " 1 �������� - 00 �̶�ֵ "
print " 8 Ӧ�����кţ��߼����ţ� - �߼�����  ͨ��ACC-35�ӿڻ�ȡ"
print " 2 ������ -������ Ĭ��ֵ 0200 ������ͨ��ֵƱ  ͨ��ACC-35�ӿڻ�ȡ"
print " 4 �������� - ����ϵͳ�������ڣ�ȡʵ�ʷ������� "
print " 6 �����豸��Ϣ - ������ "
print " 2 ���汾�� -������ ��Ӧ���߼����ݽṹ�汾 Ĭ��ֵ 0001"
print " 4 ���������� -������ ����ϵͳ�п�Ӧ��ʹ�õ���ʼ���� ȡʵ�ʷ�����������"
print " 4 ����Ч���� -������ �ڴ�����ǰ��Ƭ��Ч  ͨ��ACC-35�ӿڻ�ȡ"
print " 2 ���� -0000 �̶�ֵ" 
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
macData = '04D6 8500 2C 0731  4100  0000  00  00' + card_no +'0200  20150605  000F00000001  0001  20150605  20380808  0000'
macResult = scf_tdesmac(cardManage,macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('04D6 8500 2C 0731  4100  0000  00  00' + card_no +'0200  20150605  000F00000001  0001  20150605  20380808  0000' + macResult)
scf_assert_str(r[-4:],'9000','failed!')


#select purse
r = scf_gp_select_applet_by_aid(ep_df01)
scf_assert_str(r[-4:],'9000','failed!')

# update ep app key
# appControl
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ appControl + '800000'
keyCipher = scf_tdesenc(cardControl ,keyData ,0)
macData = '84D439001C'+ keyCipher
macResult = scf_tdesmac(cardControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D439001C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# appManage Ӧ��ά������Կ1
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ appManage + '800000'
keyCipher = scf_tdesenc(appControl ,keyData ,0)
macData = '84D450001C'+ keyCipher
macResult = scf_tdesmac(appControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D450001C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# FileManage1 �ļ�������Կ1 ���ڸ���sfi=15\16
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ FileManage1 + '800000'
keyCipher = scf_tdesenc(appControl ,keyData ,0)
macData = '84D436001C'+ keyCipher
macResult = scf_tdesmac(appControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D436001C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# FileManage2 �ļ�������Կ2  ���ڸ���sfi=17\11\12
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ FileManage2 + '800000'
keyCipher = scf_tdesenc(appControl ,keyData ,0)
macData = '84D436011C'+ keyCipher
macResult = scf_tdesmac(appControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D436011C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# appManage ��������ά����Կ
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ cappManage + '800000'
keyCipher = scf_tdesenc(appControl ,keyData ,0)
macData = '84D403011C'+ keyCipher
macResult = scf_tdesmac(appControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D403011C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# pinUnblock
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ pinUnblock + '800000'
keyCipher = scf_tdesenc(appControl ,keyData ,0)
macData = '84D404001C'+ keyCipher
macResult = scf_tdesmac(appControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D404001C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# pinReload
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ pinReload + '800000'
keyCipher = scf_tdesenc(appControl ,keyData ,0)
macData = '84D405001C'+ keyCipher
macResult = scf_tdesmac(appControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D405001C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# purchase1
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ purchase1 + '800000'
keyCipher = scf_tdesenc(appControl ,keyData ,0)
macData = '84D406011C'+ keyCipher
macResult = scf_tdesmac(appControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D406011C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# load1
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ load1 + '800000'
keyCipher = scf_tdesenc(appControl ,keyData ,0)
macData = '84D407011C'+ keyCipher
macResult = scf_tdesmac(appControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D407011C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# load2
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ load2 + '800000'
keyCipher = scf_tdesenc(appControl ,keyData ,0)
macData = '84D407021C'+ keyCipher
macResult = scf_tdesmac(appControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D407021C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# tac
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ tac + '800000'
keyCipher = scf_tdesenc(appControl ,keyData ,0)
macData = '84D408001C'+ keyCipher
macResult = scf_tdesmac(appControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D408001C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# upload
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ upload + '800000'
keyCipher = scf_tdesenc(appControl ,keyData ,0)
macData = '84D409001C'+ keyCipher
macResult = scf_tdesmac(appControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D409001C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# update �޸�͸֧�޶�
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ update + '800000'
keyCipher = scf_tdesenc(appControl ,keyData ,0)
macData = '84D40A001C'+ keyCipher
macResult = scf_tdesmac(appControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D40A001C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

#Ӧ������
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ applock + '800000'
keyCipher = scf_tdesenc(appControl ,keyData ,0)
macData = '84D433001C'+ keyCipher
macResult = scf_tdesmac(appControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D433001C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

#Ӧ�ý���
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ appprelock + '800000'
keyCipher = scf_tdesenc(appControl ,keyData ,0)
macData = '84D434001C'+ keyCipher
macResult = scf_tdesmac(appControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D434001C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')


# update file 0015
print "df:0015 ����Ӧ�û��������ļ�"
print "�ļ�������Ҫ�滻"
print "�ļ���ʽ��"
print "2 ���������� -������ Ĭ��ֵ0731"
print "2 ���д��� -������ Ĭ��ֵ4100  ͨ��ACC-33�ӿڻ�ȡ"
print "2 ��ҵ���� -������ Ĭ��ֵ0000  ͨ��ACC-33�ӿڻ�ȡ"
print "2 �������� - 0000 �̶�ֵ"
print "1 Ӧ�����ͱ�ʶ�����ñ�־��  00 δ���ã�01���ã�Ĭ��ֵ01"
print "1 Ӧ�ð汾 -������  Ĭ��ֵ 01"
print "2 �������� - 0000 �̶�ֵ "
print "8 Ӧ�����кţ��߼����ţ� - �߼�����  ͨ��ACC-35�ӿڻ�ȡ"
print "4 Ӧ���������� -������ ����ϵͳ�п�Ӧ��ʹ�õ���ʼ���� ȡʵ�ʷ�����������"
print "4 Ӧ����Ч���� -������ �ڴ�����ǰ��Ƭ��Ч ͨ��ACC-35�ӿڻ�ȡ"
print "2 ���� -0000 �̶�ֵ"
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
macData = '04D6950022 0731  4100  0000  0000  01  01  0000'+card_no +'20150605  20380808  0000'
macResult = scf_tdesmac(FileManage1,macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('04D6950022 0731  4100  0000  0000  01  01  0000'+card_no +'20150605  20380808  0000' + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# update file 0011
print "df:0011 ������Ϣ�ļ�"
print "�ļ�������Ҫ�滻"
print "�ļ���ʽ��"
print "1 ���ۼ����־  01  -��Ƭ����ʱ��д���ֵ �̶�ֵ"
print "4 ���ۼ�����Чʱ�䣨���ӣ�002819A0 2�� -��Ƭ����ʱ��д���ֵ �̶�ֵ"
print "1 ����Ѻ��Ԫ��  0x11 17  - ������ ����ʱд��00"
print "6 �����豸��Ϣ  -������ Ĭ��ֵ000F00000001"
print "2 ��ֵ���ޣ�Ԫ��01F4 500  -������ Ĭ��ֵ01F4"
print "1 ����ģʽ�ж� Ĭ��ֵ��00  -�̶�ֵ"
print "1 ������· Ĭ��ֵ��00 -�̶�ֵ"
print "1 ����վ�� Ĭ��ֵ��00 -�̶�ֵ"
print "1 �ɳ���· Ĭ��ֵ��00 -�̶�ֵ"
print "1 �ɳ�վ�� Ĭ��ֵ��00 -�̶�ֵ"
print "13 �������� Ĭ��ֵ��00000000000000000000000000 -�̶�ֵ"
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
macData = '04D6910024 01 002819A0 00 000F00000001 01F4 00 00 00 00 00 00000000000000000000000000'
macResult = scf_tdesmac(FileManage2,macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('04D6910024 01 002819A0 00 000F00000001 01F4 00 00 00 00 00 00000000000000000000000000' + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# ����
#����ʱ�� -��ȡ��ǰ����ϵͳʱ��
JYTIME = '20150827172423'
#���׽�� -�̶�ֵ
JYJE = '00000000'
#�ն˻���ţ������ն˱��룩 -������   
TPOS_NO = '410002E90101'
#�ն˽������ -������
POS_SN = '00000001'
#�豸���ͼ�����  -������
DEVICETYPE_CODE = '9802'

#-------------------------------------------------
#���ѳ�ʼ��:80 50 03 02 0B ��Կ������1 ���׽��4 �ն˻����6
#-------------------------------------------------
r = scf_send_str('80 50 03 02 0B 01' + JYJE + TPOS_NO)
if CONTACTTAG=='00':
    r = scf_send_str('00c00000'+r[-2:])
#��Ƭ��Ӧ�����4 �ѻ��������2 ͸֧�޶�3 ��Կ�汾��1��DPK���㷨��ʶ1��DPK��α�����4
#α�����
Random = r[22:30]
print 'Random = ' + Random
#Ǯ�����
ETYE = r[:8]
print 'ETYE = '+ ETYE
#ET�ϻ��������
ETTJJYXH = r[8:12]
print 'ETTJJYXH = '+ ETTJJYXH

#����Ự��Կ
DESInputData = Random + ETTJJYXH + POS_SN[-4:]
SESKEY = scf_tdesenc(purchase1 ,DESInputData ,0)

#get ס������֤�� UID 
r = scf_send_str('80ca 0000 09')
UID = r[:18]

#����MAC
MACInit = '0000000000000000'
MACInputData = JYJE + '09' + TPOS_NO + JYTIME + UID
MAC1Result = scf_desmac(SESKEY,MACInputData,MACInit)
MAC1Result = MAC1Result[:8]
print 'MAC1Result = '+ MAC1Result

# ���¸������Ѽ�¼
# ��Ҫ����CRC 
CRC_VALUE = '1122' 
r = scf_send_str('80DC 02B8 30 022E0000180000000000000000000000 02 01' + DEVICETYPE_CODE +JYTIME +'00000000000000000000000000000000000000' + CRC_VALUE)
scf_assert_str(r[-4:],'9000','failed!')

#-------------------------------------------------
#��ȷ����80 54 01 00 0F �ն˽������ �ն˽�������4 �ն˽���ʱ��3 MAC14
#-------------------------------------------------
r = scf_send_str('80 54 01 00 0F' + POS_SN + JYTIME + MAC1Result)
if CONTACTTAG=='00':
    r = scf_send_str('00c00000'+r[-2:])
CARDMAC2 = r[8:16]
CARDTAC = r[:8]

#��֤MAC2��TAC��ȷ
#MAC2=DES��SESPK��[���׽��]
MACInit = '0000000000000000'
MACInputData = JYJE
MAC2Result = scf_desmac(SESKEY,MACInputData,MACInit)
MAC2Result = MAC2Result[:8]

SESKEY = scf_XOR(tac[:16],tac[16:32])
print 'SESKEY =' + SESKEY

#TACResult
MACInit = '0000000000000000'
MACInputData = JYJE + '09' +  TPOS_NO + POS_SN +  JYTIME
print 'MACInputData:' +MACInputData
TACResult = scf_desmac(SESKEY,MACInputData,MACInit)
TACResult = TACResult[:8]
print 'CARDMAC2:'+CARDMAC2
print 'MAC2Result:'+MAC2Result
print 'CARDTAC:'+CARDTAC
print 'TACResult:'+TACResult
if CARDMAC2 != MAC2Result:
    scf_script_error('failed')
if CARDTAC != TACResult:
    scf_script_error('failed')


print "df:0017 ��7����¼ Ӧ�ÿ��Ƽ�¼�ļ�"
print "�ļ�������Ҫ�滻"
print "�ļ���ʽ��"
print "1 �������ѱ�־ 0x11 �̶�ֵ"
print "1 ��¼����0x16 �̶�ֵ"
print "1 Ӧ��������־ 0x01 �̶�ֵ"
print "1 ��¼�汾0x00 �̶�ֵ"
print "7 Ӧ�ü���ʱ�� -ȡ����ʵ��ʱ��"
print "11 �������� 00000000000000000000000000 -�̶�ֵ"
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
macData = '04DC07BC 1C 1116 01 00  20150827172423  00000000000000000000000000'
macResult = scf_tdesmac(FileManage2,macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('04DC07BC 1C 1116 01 00  20150827172423 00000000000000000000000000' + macResult)
scf_assert_str(r[-4:],'9000','failed!')


# ���˻�����
r = scf_send_str('00 08 00 00 00')
scf_assert_str(r[-4:],'9000','failed!')

scf_script_success()
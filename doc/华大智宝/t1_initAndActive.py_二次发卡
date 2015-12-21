"""
# 本脚本功能：
#   长沙地铁应用个人化和激活
"""
#长沙地铁应用AID
applet_aid = 'A00000000386980700'
ep_df01 = 'A00000000386980701'
card_no = '4100000089999985'

#卡片传输密钥   -固定不变
transKey='FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF'
#卡片主控子密钥 -密钥接口获取 DCCK 03-01
cardControl ='D03A01624D2E0255E5CE47218BA45DC8' 
#卡片维护子密钥 -密钥接口获取 DCCK 03-02
cardManage  ='F28E92292DCB63F11EA7F5776A7122B8' 

#应用主控子密钥 -密钥接口获取 DCCK  03-03
appControl = '6CA1901BE8F968A23A6BD5F62A20BDC8'
#应用维护子密钥1 -密钥接口获取 DCMK 03-04
appManage = 'F54C39CABBB8B3C46661640123322920'
#文件更新密钥1 -密钥接口获取 DCMK01 02-01  用于更新sfi=15\16
FileManage1 = 'FE43601E47B93FC500099AA0A17D1554'
#文件更新密钥2 -密钥接口获取 DCMK02 03-06  用于更新sfi=17\11\12
FileManage2 = '78BBDFD3CECA48FD7BD5F6AE69882C60'
#复合消费维护密钥 -密钥接口获取 DPK
cappManage = '4987876B59A1A5A5981F0B228F13D4EA' 
#PIN解锁密钥 -密钥接口获取 01-09
pinUnblock = '8D866109C6C9C26216FF859B0C1E20B2' 
#PIN重装密钥 -密钥接口获取 DPRK 01-10
pinReload = 'C3BBF8CEF78F40F0E8A1E5ED2E913743' 
#消费密钥1 -密钥接口获取 DPK 02-02
purchase1 = 'A338D92A10C8285411E761445C831C22' 
#圈存子密钥1 -密钥接口获取 DLK 01-07
load1 = '374598B5C0B0EAE1E1D06609AC5BB369' 
#圈存子密钥2 -密钥接口获取 不提供时按默认值处理
load2 = 'FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF' 
#TAC密钥 -密钥接口获取 DTK 02-04
tac = 'CAA6025290114BFCC3C9043F70F7A07A'
#圈提子密钥 -密钥接口获取 不提供时按默认值处理
upload = 'FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF' 
#修改透支限额 -密钥接口获取 不提供时按默认值处理
update = 'FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF' 
#应用锁定 -密钥接口获取 DABK 02-03
applock = '6474215EF549707831BB8DB9895FD48A' 
#应用解锁 -密钥接口获取 DAUK 03-05
appprelock = 'A2F237922142D1605DDD33113E617823' 

atr = scf_reset()
if CONTACTTAG=='00':
    r = scf_gp_select_applet_by_aid(applet_aid)
    scf_assert_str(r[-4:],'9000','failed!')

r = scf_gp_select_applet_by_aid('315041592E5359532E4444463031')
scf_assert_str(r[-4:],'9000','Testfailed!')

#外部认证 
# external authenticate
r = scf_send_str('0084000004')
random = r[:8]+'00000000'
tdesResult = scf_tdesenc(transKey ,random ,0)
r = scf_send_str('0082000008' + tdesResult)
scf_assert_str(r[-4:],'9000','failed!')

# 写入UID -从导入的ICCID与UID的对应关系表中获取
UID = '88D32B76'
r = scf_send_str('0022000009 85'+UID+ '88D32B76')
scf_assert_str(r[-4:],'9000','failed!')

#卡片主控子密钥 
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ cardControl + '800000'
keyCipher = scf_tdesenc(transKey ,keyData ,0)
macData = '84D439001C'+ keyCipher
macResult = scf_tdesmac(transKey , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D439001C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

#卡片维护子密钥
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
# 文件格式：
# 2 发卡方代码 -可配置 默认值0731
# 2 城市代码 -可配置 默认值4100
# 2 行业代码 -可配置 默认值0000
# 1 测试标记 -可配置 默认值00 正常卡
# 1 保留数据 - 00 固定值 
# 8 应用序列号（逻辑卡号） - 逻辑卡号
# 2 卡类型 -可配置 默认值 0200 成人普通储值票 
# 4 发行日期 - 卡的系统发行日期，取实际日期
# 6 发行设备信息 - 可配置 
# 2 卡版本号 -可配置 卡应用逻辑数据结构版本
# 4 卡启动日期 -可配置 卡在系统中可应用使用的起始日期
# 4 卡有效日期 -可配置 在此日期前卡片有效
# 2 保留 -0000 固定值
print " mf:0005 发行基本信息文件"
print "文件内容需要替换"
print " 文件格式："
print " 2 发卡方代码 -可配置 默认值0731 "
print " 2 城市代码 -可配置 默认值4100 通过ACC-33接口获取"
print " 2 行业代码 -可配置 默认值0000 通过ACC-33接口获取 "
print " 1 测试标记 -可配置 默认值00 正常卡"
print " 1 保留数据 - 00 固定值 "
print " 8 应用序列号（逻辑卡号） - 逻辑卡号  通过ACC-35接口获取"
print " 2 卡类型 -可配置 默认值 0200 成人普通储值票  通过ACC-35接口获取"
print " 4 发行日期 - 卡的系统发行日期，取实际发卡日期 "
print " 6 发行设备信息 - 可配置 "
print " 2 卡版本号 -可配置 卡应用逻辑数据结构版本 默认值 0001"
print " 4 卡启动日期 -可配置 卡在系统中可应用使用的起始日期 取实际发卡发卡日期"
print " 4 卡有效日期 -可配置 在此日期前卡片有效  通过ACC-35接口获取"
print " 2 保留 -0000 固定值" 
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

# appManage 应用维护子密钥1
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ appManage + '800000'
keyCipher = scf_tdesenc(appControl ,keyData ,0)
macData = '84D450001C'+ keyCipher
macResult = scf_tdesmac(appControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D450001C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# FileManage1 文件更新密钥1 用于更新sfi=15\16
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ FileManage1 + '800000'
keyCipher = scf_tdesenc(appControl ,keyData ,0)
macData = '84D436001C'+ keyCipher
macResult = scf_tdesmac(appControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D436001C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# FileManage2 文件更新密钥2  用于更新sfi=17\11\12
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ FileManage2 + '800000'
keyCipher = scf_tdesenc(appControl ,keyData ,0)
macData = '84D436011C'+ keyCipher
macResult = scf_tdesmac(appControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D436011C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# appManage 复合消费维护密钥
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

# update 修改透支限额
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ update + '800000'
keyCipher = scf_tdesenc(appControl ,keyData ,0)
macData = '84D40A001C'+ keyCipher
macResult = scf_tdesmac(appControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D40A001C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

#应用锁定
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
keyData = '14'+'01003300'+ applock + '800000'
keyCipher = scf_tdesenc(appControl ,keyData ,0)
macData = '84D433001C'+ keyCipher
macResult = scf_tdesmac(appControl , macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('84D433001C' + keyCipher + macResult)
scf_assert_str(r[-4:],'9000','failed!')

#应用解锁
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
print "df:0015 公共应用基本数据文件"
print "文件内容需要替换"
print "文件格式："
print "2 发卡方代码 -可配置 默认值0731"
print "2 城市代码 -可配置 默认值4100  通过ACC-33接口获取"
print "2 行业代码 -可配置 默认值0000  通过ACC-33接口获取"
print "2 保留数据 - 0000 固定值"
print "1 应用类型标识（启用标志）  00 未启用；01启用，默认值01"
print "1 应用版本 -可配置  默认值 01"
print "2 保留数据 - 0000 固定值 "
print "8 应用序列号（逻辑卡号） - 逻辑卡号  通过ACC-35接口获取"
print "4 应用启动日期 -可配置 卡在系统中可应用使用的起始日期 取实际发卡发卡日期"
print "4 应用有效日期 -可配置 在此日期前卡片有效 通过ACC-35接口获取"
print "2 保留 -0000 固定值"
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
macData = '04D6950022 0731  4100  0000  0000  01  01  0000'+card_no +'20150605  20380808  0000'
macResult = scf_tdesmac(FileManage1,macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('04D6950022 0731  4100  0000  0000  01  01  0000'+card_no +'20150605  20380808  0000' + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# update file 0011
print "df:0011 辅助信息文件"
print "文件内容需要替换"
print "文件格式："
print "1 发售激活标志  01  -卡片激活时，写入该值 固定值"
print "4 发售激活有效时间（分钟）002819A0 2年 -卡片激活时，写入该值 固定值"
print "1 发售押金（元）  0x11 17  - 可配置 发卡时写入00"
print "6 发售设备信息  -可配置 默认值000F00000001"
print "2 充值上限（元）01F4 500  -可配置 默认值01F4"
print "1 出入模式判断 默认值：00  -固定值"
print "1 可入线路 默认值：00 -固定值"
print "1 可入站点 默认值：00 -固定值"
print "1 可出线路 默认值：00 -固定值"
print "1 可出站点 默认值：00 -固定值"
print "13 保留数据 默认值：00000000000000000000000000 -固定值"
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
macData = '04D6910024 01 002819A0 11 000F00000001 01F4 00 00 00 00 00 00000000000000000000000000'
macResult = scf_tdesmac(FileManage2,macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('04D6910024 01 002819A0 11 000F00000001 01F4 00 00 00 00 00 00000000000000000000000000' + macResult)
scf_assert_str(r[-4:],'9000','failed!')


print "df:0017 第2条记录 轨道交通文件"
# 注意更新内容需要计算CRC
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
macData = '04DC02BC 34 022E00000900000000000000000000000000900000000000000000000000000000000000000000000000000000004F95'
macResult = scf_tdesmac(FileManage2,macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('04DC02BC 34 022E00000900000000000000000000000000900000000000000000000000000000000000000000000000000000004F95' + macResult)
scf_assert_str(r[-4:],'9000','failed!')


print "df:0017 第7条记录 应用控制记录文件"
print "文件内容需要替换"
print "文件格式："
print "1 复合消费标志 0x11 固定值"
print "1 记录长度0x16 固定值"
print "1 应用锁定标志 0x01 固定值"
print "1 记录版本0x00 固定值"
print "7 应用激活时间 -取发卡实际时间"
print "11 保留数据 00000000000000000000000000 -固定值"
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
macData = '04DC07BC 1C 1116 01 00  20150827172423  00000000000000000000000000'
macResult = scf_tdesmac(FileManage2,macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('04DC07BC 1C 1116 01 00  20150827172423 00000000000000000000000000' + macResult)
scf_assert_str(r[-4:],'9000','failed!')


# 个人化结束
r = scf_send_str('00 08 00 00 00')
scf_assert_str(r[-4:],'9000','failed!')

scf_script_success()
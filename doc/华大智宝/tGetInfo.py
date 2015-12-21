"""
# 本脚本功能：
#   获取长沙地铁应用各文件信息
"""
#非接标志
CONTACT_TAG = 1

#长沙地铁应用AID
applet_aid = 'A00000000386980700'
ep_df01 = 'A00000000386980701'

atr = scf_reset()
#select 长沙地铁应用
#接触读卡时需要执行该命令
if CONTACTTAG=='00':
    r = scf_gp_select_applet_by_aid(applet_aid)
    scf_assert_str(r[-4:],'9000','failed!')


#非接读卡时，复位后从该条开始
r = scf_gp_select_applet_by_aid('315041592E5359532E4444463031')
scf_assert_str(r[-4:],'9000','Testfailed!')

# mf:0005 发行基本信息文件
print " mf:0005 发行基本信息文件"
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
r = scf_send_str('00B0850028')
print " 2 发卡方代码: "+ r[:4]
print " 2 城市代码:" + r[4:8]
print " 2 行业代码:" + r[8:12]
print " 1 测试标记:" + r[12:14]
print " 1 保留数据: "+ r[14:16]
print " 8 应用序列号(逻辑卡号):" + r[16:32]
print " 2 卡类型:" + r[32:36]
print " 4 发行日期:" +r[36:44]
print " 6 发行设备信息:" + r[44:56]
print " 2 卡版本号:" + r[56:60]
print " 4 卡启动日期:" + r[60:68]
print " 4 卡有效日期:" + r[68:76]
print " 2 保留:" + r[76:80]


#select purse
r = scf_gp_select_applet_by_aid(ep_df01)
scf_assert_str(r[-4:],'9000','failed!')

# get balance
r = scf_send_str('80 5C 00 02 04')
#scf_assert_str(r,'000000009000','failed!')
print "Balance:" + r[:8]

#get UID
r = scf_send_str('80CA 0000 09')
print "UID: " + r[2:10]

# df:0015 公共应用基本数据文件
print "df:0015 公共应用基本数据文件"
print "文件格式："
print "2 发卡方代码 -可配置 默认值0731"
print "2 城市代码 -可配置 默认值4100  通过ACC-33接口获取"
print "2 行业代码 -可配置 默认值0000  通过ACC-33接口获取"
print "2 保留数据 - 0000 固定值"
print "1 应用类型标识（启用标志）  00 未启用；01启用，默认值00"
print "1 应用版本 -可配置  默认值 01"
print "2 保留数据 - 0000 固定值 "
print "8 应用序列号（逻辑卡号） - 逻辑卡号  通过ACC-35接口获取"
print "4 应用启动日期 -可配置 卡在系统中可应用使用的起始日期 取实际发卡发卡日期"
print "4 应用有效日期 -可配置 在此日期前卡片有效 通过ACC-35接口获取"
print "2 保留 -0000 固定值"
r = scf_send_str('00B095001E')
print "2 发卡方代码:" + r[:4]
print "2 城市代码:" + r[4:8]
print "2 行业代码:" + r[8:12]
print "2 保留数据:" + r[12:16]
print "1 应用类型标识:" + r[16:18]
print "1 应用版本:" + r[18:20]
print "2 保留数据:" + r[20:24]
print "8 应用序列号（逻辑卡号）:" + r[24:40]
print "4 应用启动日期:" + r[40:48]
print "4 应用有效日期:" + r[48:56]
print "2 保留:" + r[56:60]


# df:0016 持卡人基本数据文件
print "df:0016 持卡人基本数据文件"
r = scf_send_str('00B0960038')

# df:0011 辅助信息文件
print "df:0011 辅助信息文件"
print "文件格式："
print "1 发售激活标志  01  -卡片激活时，写入该值 固定值"
print "4 发售激活有效时间（分钟）002819A0 2年 -卡片激活时，写入该值 固定值"
print "1 发售押金（元）  0x11 17  - 可配置 发卡时写入00"
print "6 发售设备信息  -可配置 默认值112233445566"
print "2 充值上限（元）01F4 500  -可配置 默认值01F4"
print "1 出入模式判断 默认值：00  -固定值"
print "1 可入线路 默认值：00 -固定值"
print "1 可入站点 默认值：00 -固定值"
print "1 可出线路 默认值：00 -固定值"
print "1 可出站点 默认值：00 -固定值"
print "13 保留数据 默认值：00000000000000000000000000 -固定值"
r = scf_send_str('00B0910020')
print "1 发售激活标志:" + r[:2]
print "4 发售激活有效时间（分钟）:" + r[2:10]
print "1 发售押金（元）:" + r[10:12]
print "6 发售设备信息:" + r[12:24]
print "2 充值上限（元）:" + r[24:28]
print "1 出入模式判断:" + r[28:30]
print "1 可入线路:" + r[30:32]
print "1 可入站点:" + r[32:34]
print "1 可出线路:" + r[34:36]
print "1 可出站点:" + r[36:38]
print "13 保留数据:" + r[38:64]


# df:0012 保留数据文件
print "df:0012 保留数据文件"
r = scf_send_str('00B0920020')

# df:0018 本地消费明细文件
print "df:0018 本地消费明细文件"
r = scf_send_str('00B201C417')
r = scf_send_str('00B202C417')
r = scf_send_str('00B203C417')
r = scf_send_str('00B204C417')
r = scf_send_str('00B205C417')
r = scf_send_str('00B206C417')
r = scf_send_str('00B207C417')
r = scf_send_str('00B208C417')
r = scf_send_str('00B209C417')
r = scf_send_str('00B20AC417')

# df:0010 异地消费明细文件
print "df:0010 异地消费明细文件"
r = scf_send_str('00B2018417')
r = scf_send_str('00B2028417')
r = scf_send_str('00B2038417')
r = scf_send_str('00B2048417')
r = scf_send_str('00B2058417')
r = scf_send_str('00B2068417')
r = scf_send_str('00B2078417')
r = scf_send_str('00B2088417')
r = scf_send_str('00B2098417')
r = scf_send_str('00B20A8417')

# df:001A 充值明细文件
print "df:001A 充值明细文件"
r = scf_send_str('00B201D417')
r = scf_send_str('00B202D417')
r = scf_send_str('00B203D417')
r = scf_send_str('00B204D417')
r = scf_send_str('00B205D417')
r = scf_send_str('00B206D417')
r = scf_send_str('00B207D417')
r = scf_send_str('00B208D417')
r = scf_send_str('00B209D417')
r = scf_send_str('00B20AD417')

# df:0017 复合交易记录文件
print "df:0017 复合交易记录文件"
r = scf_send_str('00B201BC30')
r = scf_send_str('00B202BC30')
r = scf_send_str('00B203BC30')
r = scf_send_str('00B204BC30')
r = scf_send_str('00B205BC30')
r = scf_send_str('00B206BC30')
r = scf_send_str('00B207BC16')
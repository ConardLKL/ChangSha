"""
# 本脚本功能：
#   长沙地铁应用读取交易明细
"""
#长沙地铁应用AID
applet_aid = 'A00000000386980700'
ep_df01 = 'A00000000386980701'

atr = scf_reset()
# select aid
if CONTACTTAG=='00':
    r = scf_gp_select_applet_by_aid(applet_aid)
    scf_assert_str(r[-4:],'9000','failed!')

r = scf_gp_select_applet_by_aid(ep_df01)
scf_assert_str(r[-4:],'9000','failed!')

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
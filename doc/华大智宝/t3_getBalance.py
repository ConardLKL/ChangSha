"""
# 本脚本功能：
#   长沙地铁应用获取余额
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

# get balance
r = scf_send_str('80 5C 00 02 04')
print 'balance:' + r[:8]
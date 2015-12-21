"""
# 本脚本功能：
#   长沙地铁应用解锁
"""
#长沙地铁应用AID
applet_aid = 'A00000000386980700'
ep_df01 = 'A00000000386980701'

#应用解锁 -密钥接口获取 DAUK 03-05
appprelock = 'A2F237922142D1605DDD33113E617823' 

atr = scf_reset()
#select 长沙地铁应用
if CONTACTTAG=='00':
    r = scf_gp_select_applet_by_aid(applet_aid)
    scf_assert_str(r[-4:],'9000','failed!')

r = scf_send_str('00A4040009'+ep_df01)
if CONTACTTAG == '00':
    r = scf_send_str('00C00000'+r[-2:])
scf_assert_str(r[-4:],'6A81','failed!')

#应用解锁
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
macData = '8418 0000 04'
macResult = scf_tdesmac(appprelock,macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('8418 0000 04' + macResult)
scf_assert_str(r[-4:],'9000','failed!')

#确定解锁状态
r = scf_gp_select_applet_by_aid(ep_df01)
scf_assert_str(r[-4:],'9000','failed!')

scf_script_success()
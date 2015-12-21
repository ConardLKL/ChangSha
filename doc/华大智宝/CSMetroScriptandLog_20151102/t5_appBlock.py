"""
# 本脚本功能：
#   长沙地铁应用锁定
"""
#长沙地铁应用AID
applet_aid = 'A00000000386980700'
ep_df01 = 'A00000000386980701'

#应用锁定 -密钥接口获取 DABK 02-03
applock = '6474215EF549707831BB8DB9895FD48A' 


atr = scf_reset()
#select 长沙地铁应用
if CONTACTTAG=='00':
    r = scf_gp_select_applet_by_aid(applet_aid)
    scf_assert_str(r[-4:],'9000','failed!')

r = scf_gp_select_applet_by_aid(ep_df01)
scf_assert_str(r[-4:],'9000','failed!')

#应用锁定
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
macData = '841E 0000 04'
macResult = scf_tdesmac(applock,macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('841E 0000 04' + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# 确定锁定状态
r = scf_send_str('00A4040009A00000000386980701')
if CONTACTTAG=='00':
    scf_assert_str(r[-4:],'6134','Testfailed!')
    r = scf_send_str('00C0000034')
scf_assert_str(r[-4:],'6A81','Testfailed!')

scf_script_success()
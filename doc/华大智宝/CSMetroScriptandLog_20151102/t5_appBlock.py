"""
# ���ű����ܣ�
#   ��ɳ����Ӧ������
"""
#��ɳ����Ӧ��AID
applet_aid = 'A00000000386980700'
ep_df01 = 'A00000000386980701'

#Ӧ������ -��Կ�ӿڻ�ȡ DABK 02-03
applock = '6474215EF549707831BB8DB9895FD48A' 


atr = scf_reset()
#select ��ɳ����Ӧ��
if CONTACTTAG=='00':
    r = scf_gp_select_applet_by_aid(applet_aid)
    scf_assert_str(r[-4:],'9000','failed!')

r = scf_gp_select_applet_by_aid(ep_df01)
scf_assert_str(r[-4:],'9000','failed!')

#Ӧ������
r = scf_send_str('0084000004')
MACinit = r[:8]+'00000000'
macData = '841E 0000 04'
macResult = scf_tdesmac(applock,macData,MACinit)
macResult = macResult[:8]
r = scf_send_str('841E 0000 04' + macResult)
scf_assert_str(r[-4:],'9000','failed!')

# ȷ������״̬
r = scf_send_str('00A4040009A00000000386980701')
if CONTACTTAG=='00':
    scf_assert_str(r[-4:],'6134','Testfailed!')
    r = scf_send_str('00C0000034')
scf_assert_str(r[-4:],'6A81','Testfailed!')

scf_script_success()
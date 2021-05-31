package com.easou.androidsdk.login.para;

import com.easou.androidsdk.login.service.EucAPIException;
import com.easou.androidsdk.login.service.EucService;
import com.easou.androidsdk.login.service.JBody;
import com.easou.androidsdk.login.service.JHead;
import com.easou.androidsdk.login.service.Md5SignUtil;
import com.easou.androidsdk.login.service.RequestInfo;

import java.util.Map;

/**
 * 外部加密鉴权接口
 *
 * @author jay
 * @version 1.0
 * @since 2013.01.22
 */
public class OAuthParametric implements AuthParametric<RequestInfo> {

    public JHead getVeriHeader(JBody jBody, EucService service, RequestInfo info) {
        // TODO Auto-generated method stub
        JHead head = service.buildDefaultRequestHeader();
        try {
            String sign = getSign(jBody, service.getKey());
            head.setSign(sign);
            head.setFcm(info.isFcm());
            head.setFlowCode(System.currentTimeMillis() + "");    // 流水号
            if (null != info) {
                if (info.getQn() != null)
                    head.setQn(info.getQn());    //渠道
                if (info.getAppId() != null)
                    head.setAppId(info.getAppId());    //应用/游戏id
                if (info.getAgent() != null)
                    head.setAgent(info.getAgent());    //客户端agent
                if (info.getSource() != null)
                    head.setSource(info.getSource());
            }
        } catch (EucAPIException e) {
            // TODO error vericode
//			LOG.error("produce vericode is error...", e);
        }
        return head;
    }

    @SuppressWarnings("unchecked")
    public String getSign(JBody jBody, String key) throws EucAPIException {
        return Md5SignUtil.sign((Map) jBody, key);

    }
}

package cn.gotoil.unipay.web.services;

import cn.gotoil.unipay.web.services.impl.AlipayServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 支付宝支付接口
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、17:47
 */
public interface AlipayService extends BasePayService {

    Logger logger = LoggerFactory.getLogger(AlipayServiceImpl.class);

    // 正式
    String GATEWAYURL = "https://openapi.alipay.com/gateway.do";
    // 沙箱
//    String GATEWAYURL = "https://openapi.alipaydev.com/gateway.do";
    String FORMAT = "json";
    String SIGNTYPE = "RSA2";
}

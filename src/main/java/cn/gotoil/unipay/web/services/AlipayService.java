package cn.gotoil.unipay.web.services;

import cn.gotoil.unipay.model.ChargeAccount;
import cn.gotoil.unipay.model.ChargeAlipayModel;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.entity.Refund;
import cn.gotoil.unipay.web.message.request.ContinuePayRequest;
import cn.gotoil.unipay.web.message.response.OrderQueryResponse;
import cn.gotoil.unipay.web.message.response.OrderRefundResponse;
import cn.gotoil.unipay.web.message.response.RefundQueryResponse;
import cn.gotoil.unipay.web.services.impl.AlipayServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 支付宝支付接口
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、17:47
 */
public interface AlipayService extends BasePayService<ChargeAlipayModel> {

    Logger logger = LoggerFactory.getLogger(AlipayServiceImpl.class);

    // 正式
    String GATEWAYURL = "https://openapi.alipay.com/gateway.do";
    // 沙箱
//    String GATEWAYURL = "https://openapi.alipaydev.com/gateway.do";
    String FORMAT = "json";
    String SIGNTYPE = "RSA2";

}

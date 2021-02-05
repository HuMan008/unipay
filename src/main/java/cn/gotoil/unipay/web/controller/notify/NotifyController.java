package cn.gotoil.unipay.web.controller.notify;

import cn.gotoil.bill.tools.encoder.Hash;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.model.OrderNotifyBean;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.enums.EnumOrderMessageType;
import cn.gotoil.unipay.utils.UtilBase64;
import cn.gotoil.unipay.utils.UtilPageRedirect;
import cn.gotoil.unipay.web.services.AppService;
import cn.gotoil.unipay.web.services.OrderService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;

/**
 * 通知控制器
 *
 * @author think <syj247@qq.com>、
 * @date 2021/2/4、17:13
 */
@Controller
public class NotifyController {
    @Autowired
    OrderService orderService;
    @Autowired
    AppService appService;

    @GetMapping("payment/payR")
    public ModelAndView cc(@RequestParam String s, HttpServletRequest request,
                           HttpServletResponse httpServletResponse) throws Exception {
        String jsonStr = new String(Base64.decodeBase64(s.getBytes()));
        JSONObject json = JSON.parseObject(jsonStr);
        String orderId = json.getString("orderId");
        long timeStamp = json.getLong("timeStamp");
        String sign = json.getString("sign");
        Order order = orderService.loadByOrderID(orderId);
        if (order == null) {
            return new ModelAndView(UtilPageRedirect.makeErrorPage(UnipayError.OrderNotExists, ""));
        }

        String signStr = Hash.md5(orderId + timeStamp + appService.key(order.getAppId()));
        if (!signStr.equalsIgnoreCase(sign)) {
            return new ModelAndView(UtilPageRedirect.makeErrorPage(UnipayError.IllegalRequest, ""));
        }
        if (Instant.now().getEpochSecond() - timeStamp > 1800) {
            return new ModelAndView(UtilPageRedirect.makeErrorPage(UnipayError.PageTimeOut, ""));
        }

        OrderNotifyBean orderNotifyBean =
                OrderNotifyBean.builder().appId(order.getAppId()).unionOrderID(order.getId()).method(EnumOrderMessageType.PAY.name()).appOrderNO(order.getAppOrderNo()).paymentId(order.getPaymentId()).status(order.getStatus()).orderFee(order.getFee()).payFee(order.getPayFee()).arrFee(order.getArrFee()).asyncUrl(order.getAsyncUrl()).extraParam(order.getExtraParam()).payDate(order.getOrderPayDatetime()).timeStamp(Instant.now().getEpochSecond()).build();
        String param = UtilBase64.encode(JSONObject.toJSONString(orderNotifyBean).getBytes()).replaceAll("\\+",
                "GT680");
        String sign1 = Hash.md5(param + appService.key(order.getAppId()));
        httpServletResponse.sendRedirect(order.getSyncUrl() + "?param=" + param + "&sign=" + sign1);
        return null;
    }


}

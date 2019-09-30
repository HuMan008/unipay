package cn.gotoil.unipay.web.controller.notify;

import cn.gotoil.bill.web.annotation.NeedLogin;
import com.alipay.api.AlipayApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * 支付宝通知
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-27、11:48
 */
@Slf4j
@RequestMapping("payment/alipay")
@Controller
public class AlipayNotifyController {
    @RequestMapping(value = {"{orderId:^\\d{21}$}"})
    @NeedLogin(value = false)
    public void asyncNotify(Model model, HttpServletRequest request, HttpServletResponse httpServletResponse,
                            @PathVariable String orderId) throws UnsupportedEncodingException, AlipayApiException {
        log.info("支付宝异步通知");
    }

    @NeedLogin(value = false)
    @RequestMapping("return/{orderId:^\\d{21}$}}")
    public void syncNotify(@PathVariable String orderId) {
        log.info("支付宝同步通知");

    }
}

import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.tools.ObjectHelper;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.model.OrderNotifyBean;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.enums.EnumStatus;
import cn.gotoil.unipay.utils.UtilHttpClient;
import cn.gotoil.unipay.utils.UtilString;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.net.UrlEscapers;
import com.sun.jndi.toolkit.url.UrlUtil;
import io.netty.util.internal.MathUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.util.DomainNameUtil;
import org.junit.Test;
import sun.net.util.URLUtil;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * 苏亚江测试1
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-30、17:26
 */
public class SuyjUnitTes {

    @Test
    public void t1() {
        String wechat_open_id_grant_url = "http://thirdparty.guotongshiyou" + ".cn/third_party/oauth/wechat/%s" +
                "?redirect=%s";
        String redirectUrlP = String.format(wechat_open_id_grant_url, "wx9f2ffdd3e6afc476", "https://www.baidu.com");
        System.out.println(redirectUrlP);
    }

    @Test
    public void t2() {
        UtilHttpClient.doPost("http://www.gio2yfwe.com", null);
        UtilHttpClient.doPost("www.gio2yfwe.com", null);
    }

    @Test
    @SuppressWarnings("all")
    public void t3() {
        String str = "{\"appId\":\"AQhPkDihNxljLFpsBUdelgIP\"," + "\"appOrderNO\":\"10752467826376704\"," +
                "\"asyncUrl\":\"http://bole.guotongshiyou" + ".cn/payment/notify/bal/MaxAlipay\",\"doCount\":1," +
                "\"extraParam\":\"\",\"method\":\"PAY\"," + "\"orderFee\":1,\"payDate\":1571904454,\"payFee\":1," +
                "\"paymentId\":\"2019102422001491801403471107\"," + "\"refundFee\":0,\"sendType\":0," +
                "\"sign\":\"471811eb49016921bc48cb172785c7b0\",\"status\":0," + "\"timeStamp\":1571904467," +
                "\"totalRefundFee\":0,\"unionOrderID\":\"201910241607249411769\"}";
        OrderNotifyBean oo = JSON.parseObject(str, OrderNotifyBean.class);
        try {
            String yyy = UtilHttpClient.notifyPost("http://bole.guotongshiyou.cn/payment/notify/bal/MaxAlipay",
                    ObjectHelper.introspect(oo));
            System.out.println(yyy);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ;
    }

    @Test
    public  void t4(){
        String roleStr =  "ADMIN";
        Set<String> roles = new HashSet(Splitter.on(",").omitEmptyStrings().splitToList(roleStr));
        System.out.println(roles);
    }

    @Test
    public void t5(){
        String x ="http://www.baiudu.com?q=ABC&e=苏亚江好帅";
        System.out.println(UtilString.toUtf8(x));
        try {
            System.out.println(URLEncoder.encode(x, Charsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println(Charsets.UTF_8.encode(x));
        System.out.println( UrlEscapers.urlFormParameterEscaper().escape(x));
        System.out.println( UrlEscapers.urlFragmentEscaper().escape(x));
        System.out.println( UrlEscapers.urlPathSegmentEscaper().escape(x));
    }


    @Test
    public void t6(){
        String x =  "http://thirdparty.guotongshiyou.cn/third_party/oauth/wechat/%s?redirect=%s&sign=%s&time=%s";
//        DomainNameUtil.isValidDomainAddress();
        URL url = null;
        try {
            url = new URL(x);
            System.out.println(URLUtil.urlNoFragString(url));
            System.out.println(url.getPath());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void t7() throws Exception{
        xx();
    }

    @Test
    public void t8(){

        ChargeConfig chargeConfig  = new ChargeConfig();
        chargeConfig.setStatus(EnumStatus.Disable.getCode());
        chargeConfig.setId(1);
        Optional.ofNullable(chargeConfig).orElseThrow(() -> new BillException(UnipayError.AppNotSupportThisPay));
        Optional.of(chargeConfig).filter(c -> EnumStatus.Enable.getCode() == c.getStatus()).orElseThrow(() -> new BillException(UnipayError.ChargeConfigIsDisabled));
        System.out.println(chargeConfig.getId());
    }

    public  void xx()  throws Exception{ int x =RandomUtils.nextInt(1,10);
        if(x>5){
            System.out.println(x);
        }else{
            throw new Exception("aaaa");

        }
        System.out.println("----");

    }
}

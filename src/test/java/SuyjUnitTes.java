import cn.gotoil.bill.tools.ObjectHelper;
import cn.gotoil.unipay.model.OrderNotifyBean;
import cn.gotoil.unipay.utils.UtilHttpClient;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
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
}

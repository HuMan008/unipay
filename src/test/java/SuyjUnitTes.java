import cn.gotoil.unipay.utils.UtilHttpClient;
import org.junit.Test;

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
        String redirectUrlP = String.format(wechat_open_id_grant_url, "wxcwefwefewefw", "https://www.baidu.com");
        System.out.println(redirectUrlP);
    }

    @Test
    public void t2() {
        UtilHttpClient.doPost("http://www.gio2yfwe.com", null);
        UtilHttpClient.doPost("www.gio2yfwe.com", null);
    }
}

package cn.gotoil.unipay.web.helper;

import cn.gotoil.unipay.model.ChargeAlipayModel;
import com.alipay.easysdk.factory.MultipleFactory;
import com.alipay.easysdk.kernel.Config;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author think <syj247@qq.com>、
 * @date 2021/1/28、10:28
 */
public class AlipayConfigHelper {

    private static volatile AlipayConfigHelper INSTANCE = new AlipayConfigHelper();

    private ConcurrentHashMap<String, MultipleFactory> map = new ConcurrentHashMap<>();

    private AlipayConfigHelper() {
    }

    public static AlipayConfigHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (INSTANCE) {
                if (INSTANCE == null) {
                    return new AlipayConfigHelper();
                }
            }
        }
        return INSTANCE;
    }

    public void init(ChargeAlipayModel model) {
        if (map.contains(model.getAppID())) {
            return;
        }
        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost = "openapi.alipay.com";
        config.signType = "RSA2";

        config.appId = model.getAppID();

        // 为避免私钥随源码泄露，推荐从文件中读取私钥字符串而不是写入源码中
        config.merchantPrivateKey = model.getPriKey();

        //注：证书文件路径支持设置为文件系统中的路径或CLASS_PATH中的路径，优先从文件系统中加载，加载失败后会继续尝试从CLASS_PATH中加载
        //        config.merchantCertPath = "<-- 请填写您的应用公钥证书文件路径，例如：/foo/appCertPublicKey_2019051064521003.crt -->";
        //        config.alipayCertPath = "<-- 请填写您的支付宝公钥证书文件路径，例如：/foo/alipayCertPublicKey_RSA2.crt -->";
        //        config.alipayRootCertPath = "<-- 请填写您的支付宝根证书文件路径，例如：/foo/alipayRootCert.crt -->";

        //注：如果采用非证书模式，则无需赋值上面的三个证书路径，改为赋值如下的支付宝公钥字符串即可
        config.alipayPublicKey = model.getPubKey();

        //可设置异步通知接收服务地址（可选）
        //        config.notifyUrl = "<-- 请填写您的支付类接口异步通知接收服务地址，例如：https://www.test.com/callback -->";

        //可设置AES密钥，调用AES加解密相关接口时需要（可选）
        //        config.encryptKey = "<-- 请填写您的AES密钥，例如：aa4BtZ4tspm2wnXLb1ThQA== -->";
        MultipleFactory multipleFactory = new MultipleFactory();

        multipleFactory.setOptions(config);
        map.put(model.getAppID(), multipleFactory);
    }

    public MultipleFactory getFactory(String appId) {
        return map.get(appId);
    }
}

package cn.gotoil.unipay.web.helper;


import cn.gotoil.unipay.model.ChargeWechatV2Model;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 微信客户端
 *
 * @author think <syj247@qq.com>、
 * @date 2021/1/8、11:54
 */
@Slf4j
public class WechatClientHelper {

    private static volatile WechatClientHelper INSTANCE = new WechatClientHelper();


    private WechatClientHelper() {
    }

    /**
     * weichatClient
     * key merchantId 商户Id
     * value HTTP客户端
     */

    static ConcurrentHashMap<String, CloseableHttpClient> clientMap = new ConcurrentHashMap<>();
    //商户私钥
    static ConcurrentHashMap<String, PrivateKey> merchantPrivateKeyMap = new ConcurrentHashMap<>();
    //微信平台证书
    static ConcurrentHashMap<String, X509Certificate> wechatPlatCertMap = new ConcurrentHashMap<>();


    public PrivateKey privateKey(String merchantId) {
        return merchantPrivateKeyMap.get(merchantId);
    }

    public X509Certificate certificate(String merchantId) {
        return wechatPlatCertMap.get(merchantId);
    }

    /**
     * 放进去
     *
     * @param merchantId
     * @param closeableHttpClient
     */
    public void add(String merchantId, CloseableHttpClient closeableHttpClient) {

        clientMap.put(merchantId, closeableHttpClient);
    }

    /**
     * 取
     *
     * @param chargeWechatModel
     * @return
     */
    public CloseableHttpClient client(ChargeWechatV2Model chargeWechatModel) {
        CloseableHttpClient closeableHttpClient = clientMap.get(chargeWechatModel.getMerchId());
        if (closeableHttpClient == null) {
            closeableHttpClient = init(chargeWechatModel);
            if (closeableHttpClient != null) {
                add(chargeWechatModel.getMerchId(), closeableHttpClient);
            } else {
                return null;
            }
        }
        return closeableHttpClient;
    }


    public void initClientMap(ChargeWechatV2Model chargeWechatModel) {
        //        if (StringUtils.isEmpty(chargeWechatModel.getPrivateKeyPath())) {
        //            log.info("未设置商户【{}】私钥文件路径 ----> skip", chargeWechatModel.getMerchId());
        //            return;
        //        }
        if (StringUtils.isEmpty(chargeWechatModel.getCertPath())) {
            chargeWechatModel.setCertPath("/application/certs/" + chargeWechatModel.getMerchId() + "_apiclient_cert" +
                    ".p12");
        }
        if (StringUtils.isEmpty(chargeWechatModel.getPrivateKeyPath())) {
            chargeWechatModel.setPrivateKeyPath("/application/certs/" + chargeWechatModel.getMerchId() +
                    "_apiclient_key.pem");
        }
        if (StringUtils.isEmpty(chargeWechatModel.getWxPublicPemPath())) {
            chargeWechatModel.setWxPublicPemPath("/application/certs/" + chargeWechatModel.getMerchId() +
                    "_wxPublicPem.pem");
        }

        CloseableHttpClient closeableHttpClient = init(chargeWechatModel);
        if (closeableHttpClient != null) {
            add(chargeWechatModel.getMerchId(), closeableHttpClient);
            log.info("==========》\t加载微信商户【{}】client 完成 \t", chargeWechatModel.getMerchId());
        } else {
            log.info("初始化微信客户端【{}】失败了", chargeWechatModel.getMerchId());
        }

    }

    private CloseableHttpClient init(ChargeWechatV2Model chargeWechatModel) {
        assert StringUtils.isNotEmpty(chargeWechatModel.getPrivateKeyPath());
        assert StringUtils.isNotEmpty(chargeWechatModel.getSerialNo());
        try {
            PrivateKey merchantPrivateKey =
                    PemUtil.loadPrivateKey(new FileInputStream(new File(chargeWechatModel.getPrivateKeyPath())));

            merchantPrivateKeyMap.put(chargeWechatModel.getMerchId(), merchantPrivateKey);

            if (StringUtils.isNotEmpty(chargeWechatModel.getWxPublicPemPath())) {
                X509Certificate certificate =
                        PemUtil.loadCertificate(new FileInputStream(new File(chargeWechatModel.getWxPublicPemPath())));

                wechatPlatCertMap.put(chargeWechatModel.getMerchId(), certificate);
            }


            AutoUpdateCertificatesVerifier verifier =
                    new AutoUpdateCertificatesVerifier(new WechatPay2Credentials(chargeWechatModel.getMerchId(),
                            new PrivateKeySigner(chargeWechatModel.getSerialNo(), merchantPrivateKey)), chargeWechatModel.getApiKeyV3().getBytes("utf-8"));
            CloseableHttpClient httpClient =
                    WechatPayHttpClientBuilder.create().withMerchant(chargeWechatModel.getMerchId(),
                            chargeWechatModel.getSerialNo(), merchantPrivateKey).withValidator(new WechatPay2Validator(verifier)).build();


            return httpClient;
        } catch (FileNotFoundException e) {
            log.info("证书文件不存在---client创建失败 {}", e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            log.info("UTF8编码错误---client创建失败 {}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    public static WechatClientHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (INSTANCE) {
                if (INSTANCE == null) {
                    return new WechatClientHelper();
                }
            }
        }
        return INSTANCE;
    }


}



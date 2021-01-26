package cn.gotoil.unipay.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 微信收款账号模型  APIV3
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-19、14:59
 */
@Getter
@Setter
public class ChargeWechatV2Model extends ChargeAccount {

    /**
     * 微信应用ID wx开头那个
     */
    String appId;


    /**
     * 商户ID
     */
    String merchId;

    /**
     * APIKEY V3的值
     */
    String apiKeyV3;

    /**
     * APIkEY V2的值
     */
    String apiKeyV2;

    /**
     * 商户私钥文件路径
     * zip文件里的
     * apiclient_key.pem
     * 内容 -----BEGIN PRIVATE KEY----- 开头 -----END PRIVATE KEY-----结束
     */
    String privateKeyPath;

    /**
     * 微信证书序列号
     * zip文件里的
     * apiclient_cert.pem 的序列号
     * 查看方法 ：https://myssl.com/cert_decode.html
     */
    String serialNo;

    /**
     * 微信证书路径
     * zip文件里的
     * apiclient_cert.p12
     */
    String certPath;


    /**
     * 导出来的微信证书
     * 这个证书包含了微信公钥
     * 导处方法： https://github.com/wechatpay-apiv3/CertificateDownloader
     * C:\Users\think\Desktop\支付\微信支付>java -jar CertificateDownloader-1.1.jar -k 6739f96bc74ed06e033d69642d2754bb -m
     * 1570277731 -f C:\Users\think\Desktop\支付\微信支付\1570277731_20191223_cert\apiclient_key.pem -s
     * 2A3011489464431BDBEAFDD2D9B1C8AFD27602F2 -o 1570277731\out
     */
    String wxPublicPemPath;


}

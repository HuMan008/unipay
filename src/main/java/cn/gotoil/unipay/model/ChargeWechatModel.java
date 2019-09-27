package cn.gotoil.unipay.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 微信收款账号模型
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-19、14:59
 */
@Getter
@Setter
public class ChargeWechatModel extends ChargeAccount {

    /** 微信应用ID wx开头那个 */
    String appID;
    /** 应用秘钥 */
    String appSecret;

    /** 商户ID */
    String merchID;

    /** 商户Key */
    String merchKey;

}

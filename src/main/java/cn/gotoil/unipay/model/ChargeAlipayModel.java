package cn.gotoil.unipay.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 支付宝收款账号模型
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-19、14:57
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ChargeAlipayModel extends ChargeAccount {

    /**
     * 支付宝应用ID
     */
    String appID;
    /**
     * 公钥
     */
    String pubKey;
    /**
     * 私钥
     */
    String priKey;
}

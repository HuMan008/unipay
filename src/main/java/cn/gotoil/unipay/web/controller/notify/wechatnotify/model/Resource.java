package cn.gotoil.unipay.web.controller.notify.wechatnotify.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author think <syj247@qq.com>、
 * @date 2021/1/11、14:05
 */
@Data
@ToString
public class Resource implements Serializable {

    /**
     * 加密算法类型
     * 对开启结果数据进行加密的加密算法，目前只支持AEAD_AES_256_GCM
     */
    String algorithm;

    /**
     * 数据密文
     */
    String ciphertext;


    /** 附加数据 */
    String associated_data;

    /**
     * 原始类型
     * 原始回调类型，为transaction
     */

    String original_type;

    /**
     * 随机串
     */
    String nonce;
}

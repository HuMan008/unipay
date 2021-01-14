package cn.gotoil.unipay.web.controller.notify.wechatnotify.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 支付结果通知模型
 *
 * @author think <syj247@qq.com>、
 * @date 2021/1/11、14:02
 */
@Data
@ToString
public class PayNotifyModel implements Serializable {

    /**
     * 通知id
     */
    String id;

    /**
     * 通知创建时间
     */
    String create_time;

    /**
     * 通知的类型，支付成功通知的类型为TRANSACTION.SUCCESS
     */
    String event_type;

    /**
     * 通知的资源数据类型，支付成功通知为encrypt-resource
     */
    String resource_type;

    /**
     * 通知数据
     */
    Resource resource;

    /**
     * 回调摘要
     */
    String summary;
}



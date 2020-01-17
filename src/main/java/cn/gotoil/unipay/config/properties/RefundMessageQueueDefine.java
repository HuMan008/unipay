package cn.gotoil.unipay.config.properties;

import cn.gotoil.unipay.config.consts.ConstsRabbitMQ;
import lombok.Getter;
import lombok.Setter;

/**
 * 退款消息配置
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-19、8:45
 */

@Setter
@Getter
public class RefundMessageQueueDefine {

    String queueName;
    String exchangeName;
    String routeKey = ConstsRabbitMQ.ORDERROUTINGKEY;
    long ttl = 0;

}

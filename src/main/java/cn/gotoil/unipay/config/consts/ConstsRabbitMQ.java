package cn.gotoil.unipay.config.consts;

import java.util.HashMap;
import java.util.Map;

/**
 * rabbitmq常量
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-19、10:53
 */
public class ConstsRabbitMQ {

    public static final String orderRoutingKey = "dead";
    public static String orderFirstExchangeName = "";
    public static Map<String, Integer> orderQueueIndex = new HashMap();
}

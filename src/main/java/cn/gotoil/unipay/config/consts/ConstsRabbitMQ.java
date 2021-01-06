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

    public static final String ORDERROUTINGKEY = "dead";

    public static String ORDERFIRSTEXCHANGENAME = "";
    public static Map<String, Integer> ORDERQUEUEINDEX = new HashMap();

    public static String REFUNDORDERFIRSTEXCHANGENAME = "";
    public static Map<String, Integer> REFUNDORDERQUEUEINDEX = new HashMap();


    public static String EXCHANGE_ORDER_STATUS = "unipay.orderquery.exchange";
    public static String QUEUE_ORDER_STATUS = "unipay.orderquery.queue";

}

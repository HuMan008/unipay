package cn.gotoil.unipay.config;

import cn.gotoil.unipay.config.consts.ConstsRabbitMQ;
import cn.gotoil.unipay.config.properties.MessageQueueDefine;
import cn.gotoil.unipay.config.properties.OrderMessageConfig;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * rabbitmq动态配置
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-19、9:01
 */
@Configuration
@ConditionalOnClass(RabbitAdmin.class)
public class RabbitMQConfigure {


    public static final String DEAEXCHANGENAME4ORDER = "unipay.order.exchange.dead";
    public static final String DEADQUEUENAME4ORDER = "unipay.order.queue.dead";
    public static Map<String, Object> args = new HashMap<>();

    static {
        //以下参数配置具体查看官方文档或者到rabbit管理后台添加queue中查看

        //配置死信队列的路由routingKey
        args.put("x-dead-letter-routing-key", ConstsRabbitMQ.ORDERROUTINGKEY);
    }

    @Autowired
    OrderMessageConfig payNotifyConfig;
    @Autowired
    ConnectionFactory connectionFactory;

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @PostConstruct
    public void initoOrderConfig() {
        //配置死信队列的交换机
        args.put("x-dead-letter-exchange", DEAEXCHANGENAME4ORDER);

        DirectExchange deadExchange = new DirectExchange(DEAEXCHANGENAME4ORDER, true, false);
        rabbitAdmin(connectionFactory).declareExchange(deadExchange);
        Queue deadQueue = new Queue(DEADQUEUENAME4ORDER);
        rabbitAdmin(connectionFactory).declareQueue(deadQueue);
        rabbitAdmin(connectionFactory).declareBinding(BindingBuilder.bind(deadQueue).to(deadExchange).with(ConstsRabbitMQ.ORDERROUTINGKEY));
        for (int i = 0; i < payNotifyConfig.getMessageQueues().size(); i++) {
            MessageQueueDefine define = payNotifyConfig.getMessageQueues().get(i);
            if (i == 0) {
                //设置第一个发送队列的名称
                ConstsRabbitMQ.ORDERFIRSTEXCHANGENAME = define.getExchangeName();
            }
            //保存名称及对应序号
            ConstsRabbitMQ.ORDERQUEUEINDEX.put(define.getExchangeName(), i);
            DirectExchange e1 = new DirectExchange(define.getExchangeName(), true, false);
            rabbitAdmin(connectionFactory).declareExchange(e1);
            //设置过期时间
            args.put("x-message-ttl", define.getTtl());
            Queue n1 = new Queue(define.getQueueName(), true, false, false, args);
            rabbitAdmin(connectionFactory).declareQueue(n1);
            rabbitAdmin(connectionFactory).declareBinding(BindingBuilder.bind(n1).to(e1).with(define.getRouteKey()));
        }

    }

}

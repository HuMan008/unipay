package cn.gotoil.unipay.classes.listener;

import cn.gotoil.unipay.config.RabbitMQConfigure;
import cn.gotoil.unipay.config.consts.ConstsRabbitMQ;
import cn.gotoil.unipay.config.properties.OrderMessageConfig;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 死信监听 --订单支付、退款通知监听
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-18、10:31
 */
@Slf4j
@Service
@RabbitListener(queues = RabbitMQConfigure.DeadQueueName4Order)
public class OrderDeadMessageListener {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrderMessageConfig orderMessageConfig;

    @RabbitHandler
    public void receive(String msg, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag, Message message) {
        //         这里的new String(message.getBody()) =msg;
        log.info("接收到死信消息：" + new String(msg));
        //业务处理 todo
        //处理成功 手动ack
        //        channel.basicAck(tag,true);
        //处理失败， 继续进入下一轮队列
        message.getMessageProperties().getExpiration();
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        List<Object> list = (ArrayList) headers.get("x-death");
        Map<String, Object> pp = (HashMap) list.get(0);
        log.debug("本次消息是通过这个{}发送的", pp.get("exchange"));
        int index = ConstsRabbitMQ.orderQueueIndex.get(pp.get("exchange")) == null ? -1 :
                ConstsRabbitMQ.orderQueueIndex.get(pp.get("exchange"));
        if (index != -1 && index <= orderMessageConfig.getMessageQueues().size() - 2) {
            //用下一个队列发送消息
            rabbitTemplate.convertAndSend(orderMessageConfig.getMessageQueues().get(index + 1).getExchangeName(),
                    ConstsRabbitMQ.orderRoutingKey, msg);
        }
    }
}
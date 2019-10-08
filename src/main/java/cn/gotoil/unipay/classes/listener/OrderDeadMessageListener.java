package cn.gotoil.unipay.classes.listener;

import cn.gotoil.bill.tools.ObjectHelper;
import cn.gotoil.unipay.config.RabbitMQConfigure;
import cn.gotoil.unipay.config.consts.ConstsRabbitMQ;
import cn.gotoil.unipay.config.properties.OrderMessageConfig;
import cn.gotoil.unipay.model.OrderNotifyBean;
import cn.gotoil.unipay.utils.UtilHttpClient;
import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    public void receive(String msg, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag, Message message) throws Exception {
        //         这里的new String(message.getBody()) =msg;
        log.debug("接收消息：" + new String(msg));
        String jsonStr = new String(message.getBody());
        //通知内容为空，或者不是json格式 直接消费掉
        if (StringUtils.isEmpty(jsonStr) || !jsonStr.startsWith("{") || !jsonStr.endsWith("}")) {
            channel.basicAck(tag, true);
            return;
        }
        OrderNotifyBean notifyBean = JSON.parseObject(new String(msg), OrderNotifyBean.class);
        //把参数post提交到异步通知地址里去
        String responStr = UtilHttpClient.doPost(notifyBean.getAsyncUrl(), ObjectHelper.introspect(notifyBean));

        if ("success".equalsIgnoreCase(responStr)) {
            channel.basicAck(tag, true);
            return;
        } else {
            //对方未响应success
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
}
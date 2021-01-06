package cn.gotoil.unipay.classes.listener;

import cn.gotoil.unipay.config.RabbitMQConfigure;
import cn.gotoil.unipay.config.consts.ConstsRabbitMQ;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.enums.EnumOrderStatus;
import cn.gotoil.unipay.web.services.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

/**
 * @author think <syj247@qq.com>、
 * @date 2021/1/5、13:45
 */
@Slf4j
@Service
@RabbitListener(queues = RabbitMQConfigure.DEAD_QUEUE_ORDER_STATUS)
public class OrderQueryDeadMessageListener {

    @Autowired
    OrderService orderService;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @RabbitHandler
    @SuppressWarnings("all")
    public void receive(String orderId, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag, Message message) {


        try {
            Order order = orderService.loadByOrderID(orderId);

            if (order == null) {
                log.error("订单状态确认-->{}订单不存在", orderId);
                return;
            }
            //订单状态判断
            if (EnumOrderStatus.PaySuccess.getCode() == order.getStatus() || EnumOrderStatus.PayFailed.getCode() == order.getStatus()) {
                log.info("订单状态确认-->{}订单已完成", orderId);
                return;
            }
            // 判断订单是否过期 及是否需要状态查询

            Date expirseDate = DateUtils.addMinutes(order.getCreatedAt(), order.getExpiredTimeMinute());
            //订单创建2分钟后
            Date orderCreateAfterTwoMinute = DateUtils.addMinutes(order.getCreatedAt(), 2);
            long now = new Date().getTime();


            if (now < orderCreateAfterTwoMinute.getTime()) {
                // 2分钟以内的订单 重新入列
                rabbitTemplate.convertAndSend(ConstsRabbitMQ.EXCHANGE_ORDER_STATUS, ConstsRabbitMQ.ORDERROUTINGKEY,
                        new String(order.getId()));
                return;
            } else if (now >= orderCreateAfterTwoMinute.getTime() && now <= expirseDate.getTime()) {
                // 有效期内 2分钟外 状态查询
                orderService.syncOrderWithReomte(order);
                rabbitTemplate.convertAndSend(ConstsRabbitMQ.EXCHANGE_ORDER_STATUS, ConstsRabbitMQ.ORDERROUTINGKEY,
                        new String(order.getId()));
            } else {
                Order newOrder = new Order();
                newOrder.setId(order.getId());
                newOrder.setStatus(EnumOrderStatus.PayFailed.getCode());
                int x = orderService.updateOrder(order, newOrder);
                if (x != 1) {
                    log.error("标记订单【{}】过期失败状态失败", order.getId());
                    //重新入列
                    rabbitTemplate.convertAndSend(ConstsRabbitMQ.EXCHANGE_ORDER_STATUS,
                            ConstsRabbitMQ.ORDERROUTINGKEY, new String(order.getId()));
                }
                return;
            }
        } catch (Exception io) {
            log.error("消息处理出错了->{}", orderId);
        } finally {
            try {
                channel.basicAck(tag, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}

package cn.gotoil.unipay.web.controller;

import cn.gotoil.unipay.config.consts.ConstsRabbitMQ;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-12、17:07
 */
@RestController
@RequestMapping("g1")
public class TestController {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @RequestMapping("t2")
    public Object t3() {
        rabbitTemplate.convertAndSend(ConstsRabbitMQ.orderFirstExchangeName, ConstsRabbitMQ.orderRoutingKey,
                "我是一条消息哈哈哈哈哈");
        return "ssss";
    }

}

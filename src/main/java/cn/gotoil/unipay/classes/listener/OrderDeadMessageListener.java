package cn.gotoil.unipay.classes.listener;

import cn.gotoil.bill.tools.ObjectHelper;
import cn.gotoil.unipay.config.RabbitMQConfigure;
import cn.gotoil.unipay.config.consts.ConstsRabbitMQ;
import cn.gotoil.unipay.config.properties.OrderMessageConfig;
import cn.gotoil.unipay.model.OrderNotifyBean;
import cn.gotoil.unipay.model.entity.NoticeLog;
import cn.gotoil.unipay.model.enums.EnumOrderMessageType;
import cn.gotoil.unipay.utils.UtilHttpClient;
import cn.gotoil.unipay.utils.UtilMySign;
import cn.gotoil.unipay.web.services.AppService;
import cn.gotoil.unipay.web.services.NoticeLogService;
import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.*;

/**
 * 死信监听 --订单支付、退款通知监听
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-18、10:31
 */
@Slf4j
@Service
@RabbitListener(queues = RabbitMQConfigure.DEADQUEUENAME4ORDER)
public class OrderDeadMessageListener {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrderMessageConfig orderMessageConfig;

    @Autowired
    NoticeLogService noticeLogService;

    @Autowired
    AppService appService;


    @RabbitHandler
    public void receive(String msg, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag, Message message) {
        //         这里的new String(message.getBody()) =msg;
        NoticeLog noticeLog = new NoticeLog();

        try {
            log.debug("接收消息：{}", msg);
            String jsonStr = new String(message.getBody());
            //通知内容为空，或者不是json格式 直接消费掉
            if (StringUtils.isEmpty(jsonStr) || !jsonStr.startsWith("{") || !jsonStr.endsWith("}")) {
                channel.basicAck(tag, true);
                return;
            }
            OrderNotifyBean notifyBean = JSON.parseObject(msg, OrderNotifyBean.class);

            noticeLog.setOrderId(notifyBean.getUnionOrderID());
            noticeLog.setAppId(notifyBean.getAppId());
            noticeLog.setAppOrderNo(notifyBean.getAppOrderNO());
            noticeLog.setMethod(EnumOrderMessageType.PAY.name().equals(notifyBean.getMethod()) ? (byte) 0 : (byte) 1);
            noticeLog.setNotifyUrl(notifyBean.getAsyncUrl());
            noticeLog.setParams(JSON.toJSONString(notifyBean));

            /**
             * 创建订单的时候无通知地址，并且应用未设置。
             */
            if (StringUtils.isEmpty(notifyBean.getAsyncUrl())) {
                log.error("订单【{}】异步通知地址为空...请通知应用【{}】补充", notifyBean.getUnionOrderID(), notifyBean.getAppId());
                noticeLog.setResponseContent("通知地址为空");
                channel.basicAck(tag, true);
                return;
            }


            noticeLog.setNoticeDatetime(new Date());
            noticeLog.setRepeatCount(notifyBean.getDoCount());
            noticeLog.setSendType(notifyBean.getSendType());
            noticeLog.setCreatedAt(new Date(notifyBean.getTimeStamp() * 1000));

            //把参数post提交到异步通知地址里去
            String responStr = UtilHttpClient.notifyPost(notifyBean.getAsyncUrl(), ObjectHelper.introspect(notifyBean));
            noticeLog.setResponseContent(responStr);
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
                int index = ConstsRabbitMQ.ORDERQUEUEINDEX.get(pp.get("exchange")) == null ? -1 :
                        ConstsRabbitMQ.ORDERQUEUEINDEX.get(pp.get("exchange"));
                if (index != -1 && index <= orderMessageConfig.getMessageQueues().size() - 2) {
                    notifyBean.setTimeStamp(Instant.now().getEpochSecond());
                    notifyBean.setDoCount(notifyBean.getDoCount() + 1);
                    String appSecret = appService.key(notifyBean.getAppId());
                    String signStr = UtilMySign.sign(notifyBean, appSecret);
                    notifyBean.setSign(signStr);
                    //用下一个队列发送消息
                    rabbitTemplate.convertAndSend(orderMessageConfig.getMessageQueues().get(index + 1).getExchangeName(),
                            ConstsRabbitMQ.ORDERROUTINGKEY, JSON.toJSONString(notifyBean));
                }
            }
        } catch (UnknownHostException uhe) {
            noticeLog.setResponseContent("通知地址不可到达");
            log.error("通知地址不可到达【】..{},\t异常：{}", message.getBody(), uhe);
        } catch (ClientProtocolException cpe) {
            noticeLog.setResponseContent("通知地址协议错误");
            log.error("通知地址协议错误【】..{},\t异常：{}", message.getBody(), cpe);
        } catch (IOException io) {
            noticeLog.setResponseContent(io.getMessage());
            log.error("{}", io);
        } catch (Exception e) {
            noticeLog.setResponseContent(e.getMessage());
            log.error("{}", e);
        } finally {
            noticeLogService.addNoticeLog(noticeLog);
        }
    }
}
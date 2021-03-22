package cn.gotoil.unipay.classes.listener;

import cn.gotoil.bill.tools.ObjectHelper;
import cn.gotoil.unipay.config.RabbitMQConfigure;
import cn.gotoil.unipay.config.consts.ConstsRabbitMQ;
import cn.gotoil.unipay.config.properties.OrderMessageConfig;
import cn.gotoil.unipay.model.OrderRefundNotifyBean;
import cn.gotoil.unipay.model.entity.NoticeLog;
import cn.gotoil.unipay.model.enums.EnumOrderMessageType;
import cn.gotoil.unipay.utils.UtilHttpClient;
import cn.gotoil.unipay.utils.UtilMySign;
import cn.gotoil.unipay.utils.UtilString;
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
 * 死信监听 --订单退款通知监听
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-18、10:31
 */
@Slf4j
@Service
@RabbitListener(queues = RabbitMQConfigure.DEAD_QUEUE_NAME_FOR_REFUND)
public class RefundDeadMessageListener {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrderMessageConfig orderMessageConfig;

    @Autowired
    NoticeLogService noticeLogService;

    @Autowired
    AppService appService;


    @RabbitHandler
    @SuppressWarnings("all")
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
            OrderRefundNotifyBean refundNotifyBean = JSON.parseObject(msg, OrderRefundNotifyBean.class);


            noticeLog.setOrderId(refundNotifyBean.getUnionOrderID());
            noticeLog.setAppId(refundNotifyBean.getAppId());
            noticeLog.setAppOrderNo(refundNotifyBean.getAppOrderNO());
            noticeLog.setMethod(EnumOrderMessageType.REFUND.name().equals(refundNotifyBean.getMethod()) ? (byte) 1 :
                    (byte) 0);
            noticeLog.setNotifyUrl(refundNotifyBean.getAsyncUrl());
            noticeLog.setParams(UtilString.getLongString(msg,4000));
            noticeLog.setNoticeDatetime(new Date());
            noticeLog.setRepeatCount(refundNotifyBean.getDoCount());
            noticeLog.setSendType(refundNotifyBean.getSendType());
            noticeLog.setCreatedAt(new Date(refundNotifyBean.getTimeStamp() * 1000));

            /**
             * 创建订单的时候无通知地址，并且应用未设置。
             */
            if (StringUtils.isEmpty(refundNotifyBean.getAsyncUrl()) || !refundNotifyBean.getAsyncUrl().toUpperCase().startsWith(
                    "HTTP")) {
                log.error("订单【{}】异步通知地址为空，或者没带协议...请通知应用【{}】补充", refundNotifyBean.getUnionOrderID(), refundNotifyBean.getAppId());
                noticeLog.setResponseContent("通知地址格式不正确");
                channel.basicAck(tag, true);
                return;
            }


            //把参数post提交到异步通知地址里去
            String responStr = "";
            try {
                responStr = UtilHttpClient.notifyPost(refundNotifyBean.getAsyncUrl(), ObjectHelper.introspect(refundNotifyBean));
                noticeLog.setResponseContent(responStr);
            } catch (UnknownHostException uhe) {
                noticeLog.setResponseContent("通知地址不可到达");
                log.error("通知地址不可到达【】..{},\t异常：{}", message.getBody(), uhe);
            } catch (ClientProtocolException cpe1) {
                noticeLog.setResponseContent("通知地址协议错误");
                log.error("通知地址协议错误【】..{},\t异常：{}", message.getBody(), cpe1);
            } catch (IOException io1) {
                log.error("{}", io1);
                noticeLog.setResponseContent(io1.getMessage());
            }
            log.info("退款通知\t订单号【{}】地址【{}】,参数【{}】\t响应【{}】",refundNotifyBean.getUnionOrderID(),
                    refundNotifyBean.getAsyncUrl(),
                    jsonStr,responStr);
            if (!"success".equalsIgnoreCase(responStr)) {
                //对方未响应success
                message.getMessageProperties().getExpiration();
                Map<String, Object> headers = message.getMessageProperties().getHeaders();
                List<Object> list = (ArrayList) headers.get("x-death");
                Map<String, Object> pp = (HashMap) list.get(0);
                log.debug("本次消息是通过这个{}发送的", pp.get("exchange"));
                int index = ConstsRabbitMQ.REFUNDORDERQUEUEINDEX.get(pp.get("exchange")) == null ? -1 :
                        ConstsRabbitMQ.REFUNDORDERQUEUEINDEX.get(pp.get("exchange"));
                if (index != -1 && index <= orderMessageConfig.getRefundMessageQueues().size() - 2) {
                    refundNotifyBean.setTimeStamp(Instant.now().getEpochSecond());
                    refundNotifyBean.setDoCount(refundNotifyBean.getDoCount() + 1);
                    String appSecret = appService.key(refundNotifyBean.getAppId());
                    String signStr = UtilMySign.sign(refundNotifyBean, appSecret);
                    refundNotifyBean.setSign(signStr);
                    //用下一个队列发送消息
                    rabbitTemplate.convertAndSend(orderMessageConfig.getRefundMessageQueues().get(index + 1).getExchangeName(),
                            ConstsRabbitMQ.ORDERROUTINGKEY, JSON.toJSONString(refundNotifyBean));
                }
            }
            // 不管什么情况 ，我都要消费掉他。
            channel.basicAck(tag, true);
            return;
        } catch (Exception e) {
            noticeLog.setResponseContent(e.getMessage());
            log.error("{}", e);
        } finally {
            noticeLogService.addNoticeLog(noticeLog);
        }
    }
}
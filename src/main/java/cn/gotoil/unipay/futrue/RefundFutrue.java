package cn.gotoil.unipay.futrue;

import cn.gotoil.unipay.config.consts.ConstsRabbitMQ;
import cn.gotoil.unipay.model.OrderRefundNotifyBean;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.entity.Refund;
import cn.gotoil.unipay.model.enums.EnumOrderMessageType;
import cn.gotoil.unipay.utils.UtilMySign;
import cn.gotoil.unipay.web.services.AppService;
import cn.gotoil.unipay.web.services.OrderService;
import cn.gotoil.unipay.web.services.RefundService;
import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Instant;
import java.util.concurrent.*;

/**
 * 退款异步事件
 *
 * @author think <syj247@qq.com>、
 * @date 2020-1-9、9:24
 */
public class RefundFutrue {

    Logger log = LoggerFactory.getLogger(RefundFutrue.class);

    ListeningExecutorService guavaExecutor = MoreExecutors
            .listeningDecorator(new ThreadPoolExecutor(1, 1,
                    3L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>()));

    /**
     * 退款表ID;
     */
    String refundOrderId;
    RefundService refundService;
    OrderService orderService;
    AppService appService;
    RabbitTemplate rabbitTemplate;

    public RefundFutrue(String refundOrderId, RefundService refundService, OrderService orderService,
                        AppService appService,RabbitTemplate rabbitTemplate) {
        this.refundOrderId = refundOrderId;
        this.refundService = refundService;
        this.orderService = orderService;
        this.appService = appService;
        this.rabbitTemplate  =rabbitTemplate;
    }

    public void  afterFetchRefundResult(){
        ListenableFuture<Void> listenableFuture =guavaExecutor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
//                根据refundOrderId找refund对象;
                Refund refund = refundService.loadById(refundOrderId);
                if(refund==null){
                    log.error("根据{}找Refund对象为空",refundOrderId);
                    return null;
                }
//                根据refund.orderId 找Order对象;
                Order order  =orderService.loadByOrderID(refund.getOrderId());

//                创建通知内容；
                OrderRefundNotifyBean orderRefundNotifyBean = new OrderRefundNotifyBean();
                orderRefundNotifyBean.setAppId(order.getAppId());
                orderRefundNotifyBean.setUnionOrderID(refund.getOrderId());
                orderRefundNotifyBean.setMethod(EnumOrderMessageType.REFUND.name());
                orderRefundNotifyBean.setAppOrderNO(refund.getAppOrderNo());
                orderRefundNotifyBean.setAppRefundOrderNo(refund.getAppOrderRefundNo());
                orderRefundNotifyBean.setUnionRefundOrderID(refund.getRefundOrderId());
                orderRefundNotifyBean.setApplyFee(refund.getApplyFee());
                orderRefundNotifyBean.setApplyDateTime(refund.getCreatedAt().getTime()/1000);
                orderRefundNotifyBean.setRemake(refund.getDescp());
                orderRefundNotifyBean.setStatus(refund.getProcessResult());
                orderRefundNotifyBean.setPassFee(refund.getFee());
                orderRefundNotifyBean.setFinishDate(Instant.now().getEpochSecond());
                orderRefundNotifyBean.setFailReason(refund.getFailMsg());
                orderRefundNotifyBean.setAsyncUrl(order.getAsyncUrl());
                orderRefundNotifyBean.setTimeStamp(Instant.now().getEpochSecond());
                orderRefundNotifyBean.setDoCount(0);
                String sign = UtilMySign.sign(orderRefundNotifyBean,appService.key(order.getAppId()));
                orderRefundNotifyBean.setSendType((byte)0);
                orderRefundNotifyBean.setSign(sign);

                rabbitTemplate.convertAndSend(ConstsRabbitMQ.REFUNDORDERFIRSTEXCHANGENAME,
                        ConstsRabbitMQ.ORDERROUTINGKEY, JSON.toJSONString(orderRefundNotifyBean));
                return null;
            }
        });

    }
}

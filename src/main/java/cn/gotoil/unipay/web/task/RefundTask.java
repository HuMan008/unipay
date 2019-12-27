package cn.gotoil.unipay.web.task;

import cn.gotoil.unipay.model.entity.Refund;
import cn.gotoil.unipay.model.enums.EnumRefundStatus;
import cn.gotoil.unipay.web.helper.RedisLockHelper;
import cn.gotoil.unipay.web.message.response.RefundQueryResponse;
import cn.gotoil.unipay.web.services.RefundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static cn.gotoil.unipay.web.helper.RedisLockHelper.Key.RefundStatusSync;

/**
 * 订单定时任务
 *
 * @author think <syj247@qq.com>、
 * @date 2019-10-11、10:47
 */
@Component
@Slf4j
public class RefundTask {

    @Autowired
    RedisLockHelper redisLockHelper;
    @Autowired
    RefundService refundService;


    @Autowired
    RabbitTemplate rabbitTemplate;
    private ExecutorService executorService;


    @Scheduled(initialDelay = 1200, fixedDelay = 1000 * 60 * 5)
    public void expiredOrder() {
        log.info("退款状态同步任务");
        if (redisLockHelper.hasLock(RefundStatusSync)) {
            return;
        }
        redisLockHelper.addLock(RefundStatusSync, true, 30, TimeUnit.MINUTES);
        try {
            //已经过期了10分钟，但是本地状态还是待支付的订单 单批次取200条
            List<Refund> refundList = refundService.getWaitSureResultList();
            for (Refund refund : refundList) {
                RefundQueryResponse refundQueryResponse = refundService.refundQueryFromRemote(refund.getRefundOrderId());
                if (refundQueryResponse != null && (EnumRefundStatus.Success.getCode() == refundQueryResponse.getRefundStatus()  || EnumRefundStatus.Failed.getCode() == refundQueryResponse.getRefundStatus() )  ) {
                    Refund newRefund = new Refund();
                    newRefund.setRefundOrderId(refund.getRefundOrderId());
                    newRefund.setProcessResult(refundQueryResponse.getRefundStatus());
                    newRefund.setFee(refundQueryResponse.getPassFee());
                    int x = refundService.updateRefund(refund, newRefund);
                    if (x != 1) {
                        log.error("【{}】退款状态更新出错", refund.getRefundOrderId());
                    }
                    log.info("订单退款状态更新完成【{}】", refund.getRefundOrderId());
                } else{
                    log.error("获取退款订单【{}】,远程响应{}",refund.getRefundOrderId(),refundQueryResponse.toString());
                }
            }
        } catch (Exception e) {
            log.error("退款状态同步任务{}", e);
        } finally {
            redisLockHelper.releaseLock(RefundStatusSync);
        }
    }



}

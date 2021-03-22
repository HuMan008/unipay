package cn.gotoil.unipay.web.task;

import cn.gotoil.bill.tools.date.DateUtils;
import cn.gotoil.unipay.futrue.RefundFutrue;
import cn.gotoil.unipay.model.entity.Refund;
import cn.gotoil.unipay.model.enums.EnumRefundStatus;
import cn.gotoil.unipay.web.helper.RedissonLockHelper;
import cn.gotoil.unipay.web.message.response.RefundQueryResponse;
import cn.gotoil.unipay.web.services.AppService;
import cn.gotoil.unipay.web.services.OrderService;
import cn.gotoil.unipay.web.services.RefundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

//import static cn.gotoil.unipay.web.helper.RedissonLockHelper.Key.RefundStatusSync;

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
    RefundService refundService;


    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    OrderService orderService;
    @Autowired
    AppService appService;

    @Scheduled(initialDelay = 1200, fixedDelay = 1000 * 60 * 3)
    public void fetchRefundOrder() {

        if (RedissonLockHelper.isLocked(RedissonLockHelper.Key.RefundStatusSync)) {
            return;
        }
        if (RedissonLockHelper.tryLock(RedissonLockHelper.Key.RefundStatusSync)) {

            //
            log.info("退款状态同步任务");
            //        redisLockHelper.addLock(RefundStatusSync, true, 30, TimeUnit.MINUTES);
            try {
                //已经提交了2个小时，状态还是处理中的
                List<Refund> refundList = refundService.getWaitSureResultList();

                for (Refund refund : refundList) {
                    if(DateUtils.dateAdd(refund.getCreatedAt(),0,0,0,1,0,0).after(new Date())){
                        //1小时以内提交的跳过
                        continue;
                    }
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
                        RefundFutrue refundFutrue = new RefundFutrue(refund.getRefundOrderId(),refundService,orderService,appService,rabbitTemplate);
                        refundFutrue.afterFetchRefundResult(false);
                        log.info("订单退款状态更新完成【{}】", refund.getRefundOrderId());
                    } else{
                        log.error("获取退款订单【{}】,远程响应{}",refund.getRefundOrderId(),refundQueryResponse.toString());
                    }
                }
            } catch (Exception e) {
                log.error("退款状态同步任务{}", e);
            } finally {
                RedissonLockHelper.unlock(RedissonLockHelper.Key.RefundStatusSync);
                //            redisLockHelper.releaseLock(RefundStatusSync);
            }

        } else {
            return;
        }
    }



}

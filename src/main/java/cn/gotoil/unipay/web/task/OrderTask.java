package cn.gotoil.unipay.web.task;

import cn.gotoil.unipay.web.helper.RedisLockHelper;
import cn.gotoil.unipay.web.services.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 订单定时任务
 *
 * @author think <syj247@qq.com>、
 * @date 2019-10-11、10:47
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    RedisLockHelper redisLockHelper;

    @Autowired
    OrderService orderService;

    @Scheduled(initialDelay = 90000, fixedDelay = 1000 * 60 * 3)
    @Async
    public void getOrderStatus() {
        if (redisLockHelper.hasLock(RedisLockHelper.Key.OrderStatusSync)) {
            return;
        }
        redisLockHelper.addLock(RedisLockHelper.Key.OrderStatusSync, true, 30, TimeUnit.MINUTES);
        try {
            //            List<Order> unionOrderList = orderService.loadWaitPayOrder();
            log.info("订单状态同步定时任务");
        } catch (Exception e) {
            log.error("同步订单状态任务出错{}", e);
        } finally {
            redisLockHelper.releaseLock(RedisLockHelper.Key.OrderStatusSync);
        }
    }
}

package cn.gotoil.unipay.web.task;

import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.enums.EnumPayCategory;
import cn.gotoil.unipay.web.helper.RedisLockHelper;
import cn.gotoil.unipay.web.services.AppService;
import cn.gotoil.unipay.web.services.OrderQueryService;
import cn.gotoil.unipay.web.services.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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

    @Autowired
    OrderQueryService orderQueryService;
    @Autowired
    AppService appService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    private ExecutorService executorService;

    @Scheduled(initialDelay = 90000, fixedDelay = 1000 * 60 * 3)
    @Async
    public void getOrderStatus() {
        if (redisLockHelper.hasLock(RedisLockHelper.Key.OrderStatusSync)) {
            return;
        }
        redisLockHelper.addLock(RedisLockHelper.Key.OrderStatusSync, true, 30, TimeUnit.MINUTES);
        try {
            List<Order> orderList = orderQueryService.queryOrderByIn10();
            if (orderList != null && orderList.size() == 0) {
                //无数据
                return;
            }
            log.info("订单状态同步定时任务");

            List<Order> aliPayOrders = Collections.synchronizedList(new ArrayList<>());
            List<Order> wechatPayOrders = Collections.synchronizedList(new ArrayList<>());
            List<Order> otherPayOrders = Collections.synchronizedList(new ArrayList<>());

            orderList.parallelStream().forEach(new Consumer<Order>() {
                @Override
                public void accept(Order order) {
                    if (EnumPayCategory.Wechat.getCodeValue() == order.getPayTypeCategory().byteValue()) {
                        wechatPayOrders.add(order);
                    } else if (EnumPayCategory.Alipay.getCodeValue() == order.getPayTypeCategory().byteValue()) {
                        aliPayOrders.add(order);
                    } else {
                        otherPayOrders.add(order);
                    }
                }
            });

            try {
                initExecutoreServe();

                executorService.execute(new OrderStatusSyncWork(aliPayOrders, EnumPayCategory.Alipay.getDescp()));
                executorService.execute(new OrderStatusSyncWork(wechatPayOrders, EnumPayCategory.Wechat.getDescp()));
                executorService.execute(new OrderStatusSyncWork(otherPayOrders, "其他未分组支付方式"));

                executorService.shutdown();//不接受新任务

//            while (!executorService.awaitTermination(1,TimeUnit.SECONDS)){
                while (!executorService.isTerminated()) {
                    //pass 等待线程全部搞完
                }
                //任务做完了。释放锁
                redisLockHelper.releaseLock(RedisLockHelper.Key.OrderStatusSync);

                //释放数组
                aliPayOrders.clear();
                wechatPayOrders.clear();
                otherPayOrders.clear();

            } catch (Exception e) {
                log.error("定时取订单状态失败了{}", e);
            }
        } catch (Exception e) {
            log.error("同步订单状态任务出错{}", e);
        } finally {
            redisLockHelper.releaseLock(RedisLockHelper.Key.OrderStatusSync);
        }
    }

    private void initExecutoreServe() {
        executorService = new ThreadPoolExecutor(12, 12,
                15L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }


    private class OrderStatusSyncWork implements Runnable {

        List<Order> waitProcessList = new ArrayList<>();
        String payTypeName;

        public OrderStatusSyncWork(List<Order> waitProcessList, String taskName) {
            this.waitProcessList = waitProcessList;
            this.payTypeName = taskName;
        }


        @Override
        public void run() {
            if (waitProcessList == null || waitProcessList.isEmpty()) {
                return;
            }
            log.debug("组【{}】线程|主动获取订单状态,本轮待处理的数据有{}条。", payTypeName, waitProcessList.size());
            for (int i = 0; i < waitProcessList.size(); i++) {
                Order order = waitProcessList.get(i);
                orderService.syncOrderWithReomte(order);
            }

        }
    }

}

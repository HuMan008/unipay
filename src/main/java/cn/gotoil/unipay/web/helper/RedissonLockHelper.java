package cn.gotoil.unipay.web.helper;

import cn.gotoil.unipay.utils.Locker;

import java.util.concurrent.TimeUnit;

/**
 * @author think <syj247@qq.com>、
 * @date 2021/3/3、16:14
 */
public class RedissonLockHelper {
    public static final String LOCKPREFIX = "Unipay:Lock:";

    public static class Key {
        public static final String Notify = "Notify:";
        public static final String RefundNotify = "RefundNotify:";
        public static final String OrderStatusSync = "OrderStatusSync";
        public static final String OrderExpiredSync = "OrderExpiredSync";
        public static final String Refund = "Refund";
        public static final String RefundStatusSync = "RefundStatusSync";
    }

    private static Locker locker;

    /**
     * 设置工具类使用的locker
     *
     * @param locker
     */
    public static void setLocker(Locker locker) {
        RedissonLockHelper.locker = locker;
    }

    /**
     * 获取锁
     *
     * @param lockKey
     */
    public static void lock(String lockKey) {
        locker.lock(getKey(lockKey));
    }

    /**
     * 释放锁
     *
     * @param lockKey
     */
    public static void unlock(String lockKey) {
        locker.unlock(getKey(lockKey));
    }

    /**
     * 获取锁，超时释放
     *
     * @param lockKey
     * @param timeout
     */
    public static void lock(String lockKey, int timeout) {
        locker.lock(getKey(lockKey), timeout);
    }

    /**
     * 获取锁，超时释放，指定时间单位
     *
     * @param lockKey
     * @param unit
     * @param timeout
     */
    public static void lock(String lockKey, TimeUnit unit, int timeout) {
        locker.lock(getKey(lockKey), unit, timeout);
    }

    /**
     * 尝试获取锁，获取到立即返回true,获取失败立即返回false
     *
     * @param lockKey
     * @return
     */
    public static boolean tryLock(String lockKey) {
        return locker.tryLock(getKey(lockKey));
    }

    /**
     * 尝试获取锁，在给定的waitTime时间内尝试，获取到返回true,获取失败返回false,获取到后再给定的leaseTime时间超时释放
     *
     * @param lockKey
     * @param waitTime
     * @param leaseTime
     * @param unit
     * @return
     * @throws InterruptedException
     */
    public static boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        return locker.tryLock(getKey(lockKey), waitTime, leaseTime, unit);
    }

    /**
     * 锁释放被任意一个线程持有
     *
     * @param lockKey
     * @return
     */
    public static boolean isLocked(String lockKey) {
        return locker.isLocked(getKey(lockKey));
    }

    private static String getKey(String key) {
        if (key.startsWith(LOCKPREFIX)) {
            return key;
        } else {
            return LOCKPREFIX + key;
        }
    }
}

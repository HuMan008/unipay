package cn.gotoil.unipay.web.helper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 *
 * 这个锁有问题。改用
 * <p>
 * {@link cn.gotoil.unipay.utils.RedissonLocker}
 * </p>
 *
 * @Modify 2021年3月3日16点20分 苏亚江 标记过期
 *
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-27、17:25
 */
//@Component
@Deprecated
public class RedisLockHelper {
    public static final String LOCKPREFIX = "Unipay:Lock:";


    public static class Key {
        public static final String Notify = "Notify:";
        public static final String RefundNotify = "RefundNotify:";
        public static final String OrderStatusSync = "OrderStatusSync";
        public static final String OrderExpiredSync = "OrderExpiredSync";
        public static final String Refund = "Refund";
        public static final String RefundStatusSync = "RefundStatusSync";
    }

    public static final String LockedFlag = "LOCKED";
    public static final String UnLockedFlag = "UnLockedFlag";

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 加一个锁
     *
     * @param key
     * @param autoRelease 是否自动释放
     * @param timeout     锁时长
     * @param timeUnit    时长单位
     */
    public void addLock(String key, boolean autoRelease, long timeout, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(getKey(key), LockedFlag);
        if (autoRelease) {
            stringRedisTemplate.opsForValue().getOperations().expire(getKey(key), timeout, timeUnit);
        }
    }

    /**
     * 判断是否有锁
     *
     * @param key
     * @return true 有锁，false 没得锁
     */
    public boolean hasLock(String key) {
        String v = stringRedisTemplate.opsForValue().get(getKey(key));
        if (StringUtils.isEmpty(v) || UnLockedFlag.equals(v)) {
            return false;
        }
        return true;
    }

    /**
     * 开锁
     *
     * @param key
     * @return
     */
    public boolean releaseLock(String key) {
        return stringRedisTemplate.opsForValue().getOperations().expire(getKey(key), 0, TimeUnit.SECONDS);

    }


    private String getKey(String key) {
        if (key.startsWith(LOCKPREFIX)) {
            return key;
        } else {
            return LOCKPREFIX + key;
        }
    }
}

package cn.gotoil.unipay.web.helper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-27、17:25
 */
@Component
public class RedisLockHelper {
    public static final String LockPrefix = "Unipay:Lock:";


    public static class Key {
        public static final String Notify = "Notify:";
        public static final String OrderStatusSync = "OrderStatusSync";
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
        if (key.startsWith(LockPrefix)) {
            return key;
        } else {
            return LockPrefix + key;
        }
    }
}

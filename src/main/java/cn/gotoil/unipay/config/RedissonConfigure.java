package cn.gotoil.unipay.config;

import cn.gotoil.unipay.utils.Locker;
import cn.gotoil.unipay.utils.RedissonLocker;
import cn.gotoil.unipay.web.helper.RedissonLockHelper;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author think <syj247@qq.com>、
 * @date 2021/3/3、14:29
 */
@Configuration

public class RedissonConfigure {

    @Value("${spring.redis.database}")
    private int database;
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.password}")
    private String password = "";


    /**
     * RedissonClient,单机模式
     *
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress("redis://" + host + ":" + port);
        singleServerConfig.setTimeout(2000);
        singleServerConfig.setDatabase(database);
        if (password != null && !"".equals(password)) { //有密码
            singleServerConfig.setPassword(password);
        }
        return Redisson.create(config);
    }

    @Bean
    public Locker redissonLocker(RedissonClient redissonClient) {
        Locker locker = new RedissonLocker(redissonClient);
        //设置LockUtil的锁处理对象
        RedissonLockHelper.setLocker(locker);
        return locker;
    }

}

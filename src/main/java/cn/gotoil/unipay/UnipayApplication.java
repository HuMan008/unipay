package cn.gotoil.unipay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * 主方法入口
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-25、15:30
 */
@SpringBootApplication
@EnableWebMvc
@ComponentScan({"cn.gotoil.unipay", "cn.gotoil.bill"})
@EnableScheduling
public class UnipayApplication {

    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(UnipayApplication.class, args);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}

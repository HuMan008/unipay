package cn.gotoil.unipay.config.properties;

import cn.gotoil.unipay.config.YamlPropertySourceFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户配置
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-26、10:27
 */
@Component
@ConfigurationProperties(prefix = "userconfig")
@PropertySource(value = "classpath:user.yml", factory = YamlPropertySourceFactory.class)
@Setter
@Getter
public class UserConfig {
    Map<String, UserDefine> users = new HashMap<>();
}

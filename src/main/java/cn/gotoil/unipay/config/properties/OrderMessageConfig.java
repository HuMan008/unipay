package cn.gotoil.unipay.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单通知配置（支付 ，退款）
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-18、9:40
 */
@Component
@ConfigurationProperties("ordermessageconfig")
@Setter
@Getter
public class OrderMessageConfig {

    List<MessageQueueDefine> messageQueues = new ArrayList<>();
}

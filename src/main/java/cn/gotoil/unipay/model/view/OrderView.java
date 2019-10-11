package cn.gotoil.unipay.model.view;

import cn.gotoil.unipay.model.entity.Order;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderView extends Order {
    private String appName;
    private String appkey;
}

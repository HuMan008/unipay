package cn.gotoil.unipay.web.message.response.admin;

import cn.gotoil.unipay.model.entity.ChargeConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class PaytypeResponse {
    private String payName;
    private String payType;
    private List<ChargeConfig> chargeConfig;

    public PaytypeResponse() {
    }
}

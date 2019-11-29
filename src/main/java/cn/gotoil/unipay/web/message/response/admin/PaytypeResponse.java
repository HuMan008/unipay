package cn.gotoil.unipay.web.message.response.admin;

import cn.gotoil.unipay.model.entity.ChargeConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaytypeResponse {
    private String payName;
    private String payType;
    private List<ChargeConfig> chargeConfig;
    private Integer selected;
    private String payCategoryType;
    private String payCategoryTypeName;

}

package cn.gotoil.unipay.web.message.response.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 授权页面专业模型
 *
 * @author think <syj247@qq.com>、
 * @date 2019-11-6、17:04
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayTypeWithCatResponse {
    String cateName;
    List<PaytypeResponse> paytypeResponse ;
}

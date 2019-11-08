package cn.gotoil.unipay.model.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@ToString
@Data
@EqualsAndHashCode(callSuper = false)
public class AppAccountIds {

     @ApiModelProperty(example = "", value = "微信H5", position = 1)
     private Integer WechatH5;

     @ApiModelProperty(example = "", value = "微信JSAPI", position = 3)
     private Integer WechatJSAPI;

     @ApiModelProperty(example = "", value = "微信SDK", position = 5)
     private Integer WechatSDK;

     @ApiModelProperty(example = "", value = "微信NATIVE", position = 7)
     private Integer WechatNative;

     @ApiModelProperty(example = "", value = "支付宝H5", position = 9)
     private Integer AlipayH5;

     @ApiModelProperty(required = true, example = "", value = "支付宝SDK", position = 11)
     private Integer AlipaySDK;

     private String appKey;
}

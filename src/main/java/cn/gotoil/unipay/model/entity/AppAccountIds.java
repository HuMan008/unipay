package cn.gotoil.unipay.model.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@ToString
@Data
@EqualsAndHashCode(callSuper = false)
public class AppAccountIds {

     @ApiModelProperty(required = true, example = "", value = "微信H5", position = 1)
     private Integer wechatH5Id;

     @ApiModelProperty(required = true, example = "", value = "微信JSAPI", position = 3)
     private Integer wechatJSAPIId;

     @ApiModelProperty(required = true, example = "", value = "微信SDK", position = 5)
     private Integer wechatSDKId;

     @ApiModelProperty(required = true, example = "", value = "微信NATIVE", position = 7)
     private Integer wechatNAtiveId;

     @ApiModelProperty(required = true, example = "", value = "支付宝H5", position = 9)
     private Integer alipayH5Id;

     @ApiModelProperty(required = true, example = "", value = "支付宝SDK", position = 11)
     private Integer alipaySDKId;
}

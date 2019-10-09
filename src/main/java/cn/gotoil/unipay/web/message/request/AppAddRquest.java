package cn.gotoil.unipay.web.message.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AppAddRquest {

    @NotNull
    @Size(max = 50)
    @ApiModelProperty(required = true, example = "", value = "名称", position = 1)
    String appName;

    @ApiModelProperty(required = true, example = "", value = "appkey", position = 2)
    String appKey;

    @NotNull
    @Size(max = 1)
    @ApiModelProperty(required = true, example = "", value = "状态", position = 3,
            dataType="cn.gotoil.unipay.model.enums.EnumStatus")
    String status;

    @Size(max = 100)
    @ApiModelProperty(required = true, example = "", value = "备注", position = 5)
    String remark;

    @Size(max = 200)
    @ApiModelProperty(required = true, example = "", value = "同步通知地址", position = 7)
    String syncUrl;

    @Size(max = 200)
    @ApiModelProperty(required = true, example = "", value = "异步通知地址", position = 9)
    String asyncUrl;

    @Size(max = 40)
    @ApiModelProperty(required = true, example = "", value = "默认订单标题", position = 11)
    String orderHeader;

    @Size(max = 100)
    @ApiModelProperty(required = true, example = "", value = "默认订单描述", position = 13)
    String orderDescp;

    @NotNull
    @Size(max = 11)
    @ApiModelProperty(required = true, example = "", value = "默认订单有效时间(单位分钟)", position = 15)
    Integer defaultOrderExpiredTime;
}

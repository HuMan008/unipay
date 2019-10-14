package cn.gotoil.unipay.web.message.request.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = false)
public class AccountAddRequest {


    @NotNull
    @Size(max = 20)
    @ApiModelProperty(required = true, example = "", value = "名称", position = 1)
    String name;

    @Size(max = 11)
    @ApiModelProperty(required = true, example = "0", value = "id", position = 2)
    Integer id;

    @NotNull
    @Size(max = 20)
    @ApiModelProperty(required = true, example = "", value = "支付类型", position = 3,
            dataType = "cn.gotoil.unipay.model.enums.EnumPayType")
    String payType;

    @NotNull
    @ApiModelProperty(required = true, example = "", value = "配置", position = 5)
    String configJson;

    @NotNull
    @Size(max = 1)
    @ApiModelProperty(required = true, example = "0", value = "状态", position = 7,
            dataType = "cn.gotoil.unipay.model.enums.EnumStatus")
    Integer status;
}

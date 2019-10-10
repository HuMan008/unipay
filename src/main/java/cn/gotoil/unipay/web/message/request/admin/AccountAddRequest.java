package cn.gotoil.unipay.web.message.request.admin;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AccountAddRequest {



    @NotNull
    @Size(max = 20)
    @ApiModelProperty(required = true, example = "", value = "名称", position = 1)
    String name;

    @Size(max = 11)
    @ApiModelProperty(required = true, example = "", value = "id", position = 2)
    Integer id;

    @NotNull
    @Size(max = 20)
    @ApiModelProperty(required = true, example = "", value = "支付类型", position = 3)
    String payType;

    @NotNull
    @ApiModelProperty(required = true, example = "", value = "配置", position = 5)
    String configJson;

    @NotNull
    @Size(max = 1)
    @ApiModelProperty(required = true, example = "", value = "状态", position = 7,
            dataType="cn.gotoil.unipay.model.enums.EnumStatus")
    String status;
}

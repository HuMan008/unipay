package cn.gotoil.unipay.web.message.request.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 修改密码请求
 *
 * @author think <syj247@qq.com>、
 * @date 2019-11-28、14:50
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ModifyPwdRequest {
    @NotNull
    @Size(max = 30)
    @ApiModelProperty(required = true, example = "", value = "旧密码", position = 1)
    String oldPwd;   @NotNull
    @Size(max = 30)
    @ApiModelProperty(required = true, example = "", value = "新密码", position = 1)
    String newPwd;
}

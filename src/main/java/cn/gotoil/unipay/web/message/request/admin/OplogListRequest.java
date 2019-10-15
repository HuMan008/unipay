package cn.gotoil.unipay.web.message.request.admin;

import cn.gotoil.unipay.web.message.BasePageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
public class OplogListRequest extends BasePageRequest {
    // 查询参数
    @ApiModelProperty(required = false, example = "{\"name\":\"超级\"}",
            value = "查询条件Key为[name:操作人,descp:描述]", position = 1)
    private Map params = new HashMap();
}

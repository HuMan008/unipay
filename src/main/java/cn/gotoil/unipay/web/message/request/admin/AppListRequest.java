package cn.gotoil.unipay.web.message.request.admin;

import cn.gotoil.unipay.web.message.BasePageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
public class AppListRequest extends BasePageRequest {
    // 查询参数
    @ApiModelProperty(required = false, example = "{\"status\":\"0\"}", value = "查询条件Key为[appName:应用名称," +
            "status:状态", position = 1)
    private Map params = new HashMap();
}

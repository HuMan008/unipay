package cn.gotoil.unipay.web.message.request.admin;

import cn.gotoil.unipay.web.message.BasePageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("all")
public class RefundQueryListRequest extends BasePageRequest {
    // 查询参数
    @ApiModelProperty(required = false, example = "{\"queryId\":\"201910110950394386590\",\"status\":\"0\"}", value =
            "查询条件Key为[appid:应用id," + "status:状态,queryId:退款查询ID,应用订单号、应用退款订单号、订单ID,beginTime(yyyy-mm-dd):开始时间,endTime" +
                    "(yyyy-mm-dd)" + ":结束时间]", position = 1)
    private Map params = new HashMap();
}

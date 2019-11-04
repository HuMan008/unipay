package cn.gotoil.unipay.web.message.request.admin;

import cn.gotoil.unipay.web.message.BasePageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
public class OrderQueryListRequest extends BasePageRequest {
    // 查询参数
    @ApiModelProperty(required = false, example = "{\"appkey\":\"AQhPkDihNxljLFpsBUdelgIP\",\"queryId\":\"201910110950394386590\",\"status\":\"0\",\"payType\":\"AlipayH5\"}", value = "查询条件Key为[appkey:应用key," +
            "status:状态,queryId:订单ID/应用订单号/交易订单编号,payType:支付类型,beginTime(yyyy-mm-dd):开始时间,endTime(yyyy-mm-dd):结束时间]", position = 1)
    private Map params = new HashMap();
}

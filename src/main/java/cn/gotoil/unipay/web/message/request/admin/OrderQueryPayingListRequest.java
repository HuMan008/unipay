package cn.gotoil.unipay.web.message.request.admin;

import cn.gotoil.unipay.web.message.BasePageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
public class OrderQueryPayingListRequest extends BasePageRequest {
    // 查询参数
    @ApiModelProperty(required = false, example = "{\"payType\":\"AlipayH5\",\"beginTime\":\"2019-10-11 00:00:00\",\"endTime\":\"2019-10-11 23:59:59\"}",
            value = "查询条件Key为[payType:支付类型,beginTime:开始时间,endTime:结束时间]", position = 1)
    private Map params = new HashMap();
}

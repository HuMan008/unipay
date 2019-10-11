package cn.gotoil.unipay.web.controller.admin;

import cn.gotoil.bill.web.annotation.NeedLogin;
import cn.gotoil.unipay.web.message.request.admin.OrderQueryListRequest;
import cn.gotoil.unipay.web.message.request.admin.OrderQueryPayingListRequest;
import cn.gotoil.unipay.web.services.OrderQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.SortParameters;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("web/order")
@Api(description = "订单管理api")
public class OrderController {

    @Autowired
    OrderQueryService orderQueryService;

    @ApiOperation(value = "订单查询", position = 1, tags = "订单管理")
    @RequestMapping(value = "queryOrder", method = RequestMethod.GET)
    @NeedLogin
    public Object queryOrder(@RequestBody OrderQueryListRequest orderQueryListRequest){
        return orderQueryService.queryOrder(orderQueryListRequest);
    }

    @ApiOperation(value = "支付中订单查询", position = 3, tags = "订单管理")
    @RequestMapping(value = "queryPayingOrder", method = RequestMethod.GET)
    @NeedLogin
    public Object queryPayingOrder(@RequestBody OrderQueryPayingListRequest orderQueryPayingListRequest){
        return orderQueryService.queryPayingOrder(orderQueryPayingListRequest);
    }
}

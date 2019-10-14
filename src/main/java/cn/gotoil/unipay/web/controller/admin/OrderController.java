package cn.gotoil.unipay.web.controller.admin;

import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.web.annotation.NeedLogin;
import cn.gotoil.bill.web.message.BillApiResponse;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.web.message.request.admin.OrderQueryListRequest;
import cn.gotoil.unipay.web.message.request.admin.OrderQueryPayingListRequest;
import cn.gotoil.unipay.web.services.OrderQueryService;
import cn.gotoil.unipay.web.services.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.SortParameters;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("web/order")
@Api(description = "订单管理api")
public class OrderController {

    @Autowired
    OrderQueryService orderQueryService;

    @Autowired
    OrderService orderService;

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

    @ApiOperation(value = "查询订单状态，退款状态", position = 5, tags = "订单管理")
    @RequestMapping(value = "queryOrderStatus", method = RequestMethod.GET)
    @NeedLogin
    public Object queryOrderStatus(@ApiParam(value = "appkey") @PathVariable String appkey,@ApiParam(value = "订单号") @PathVariable String orderId,
                                   @ApiParam(value = " localStatus/remoteStatus 本地/远程查询订单状态, " + "localRefund/remoteRefund 本地/远程查询退款状态",
                                           allowableValues = "localStatus,remoteStatus,localRefund,remoteRefund", example = "localStatus") @PathVariable String type){
        Order order = orderQueryService.getOrderByAppKeyAndId(appkey,orderId);
        if(order == null){
            throw new BillException(5100,"没有对应订单");
        }
        if("localStatus".equals(type)){
            return new BillApiResponse(order);
        }else if("remoteStatus".equals(type)){
            return new BillApiResponse(orderService.queryOrderStatusFromRemote(order));
        }else{
            throw new BillException(5100,"查询类型出错");
        }
    }
}

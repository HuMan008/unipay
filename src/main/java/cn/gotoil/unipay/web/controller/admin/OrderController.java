package cn.gotoil.unipay.web.controller.admin;

import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.web.annotation.HasRole;
import cn.gotoil.bill.web.annotation.NeedLogin;
import cn.gotoil.bill.web.message.BillApiResponse;
import cn.gotoil.unipay.config.consts.ConstsRole;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.entity.Refund;
import cn.gotoil.unipay.model.enums.EnumOrderMessageType;
import cn.gotoil.unipay.model.enums.EnumOrderStatus;
import cn.gotoil.unipay.model.mapper.ext.ExtOrderQueryMapper;
import cn.gotoil.unipay.web.message.request.admin.OrderQueryListRequest;
import cn.gotoil.unipay.web.message.request.admin.OrderQueryPayingListRequest;
import cn.gotoil.unipay.web.message.response.admin.BaseComboResponse;
import cn.gotoil.unipay.web.services.NoticeLogService;
import cn.gotoil.unipay.web.services.OrderQueryService;
import cn.gotoil.unipay.web.services.OrderService;
import cn.gotoil.unipay.web.services.RefundService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("web/order")
@Api(description = "订单管理")
public class OrderController {

    @Autowired
    OrderQueryService orderQueryService;

    @Autowired
    OrderService orderService;
    @Autowired
    NoticeLogService noticeLogService;
@Resource
ExtOrderQueryMapper extOrderQueryMapper;

@Autowired
    RefundService refundService;

    @ApiOperation(value = "订单查询", position = 1, tags = "订单管理")
    @RequestMapping(value = "queryOrder", method = RequestMethod.POST)
    @NeedLogin  @HasRole(values = {ConstsRole.ADMIN,ConstsRole.ORDER})
    public Object queryOrderAction(@RequestBody OrderQueryListRequest orderQueryListRequest) {
        return orderQueryService.queryOrder(orderQueryListRequest);
    }

//   /* @ApiOperation(value = "支付中订单查询", position = 3, tags = "订单管理")
    @RequestMapping(value = "queryPayingOrder", method = RequestMethod.POST)
    @NeedLogin  @HasRole(values = {ConstsRole.ADMIN,ConstsRole.ORDER})
    public Object queryPayingOrderAction(@RequestBody OrderQueryPayingListRequest orderQueryPayingListRequest){
        return orderQueryService.queryPayingOrder(orderQueryPayingListRequest);
    }

    @RequestMapping(value = "doSyncOrder", method = RequestMethod.POST)
    @NeedLogin  @HasRole(values = {ConstsRole.ADMIN,ConstsRole.ORDER})
    public Object doSyncOrderrAction(@RequestBody OrderQueryPayingListRequest orderQueryPayingListRequest){
        Map<String,Object> params =  new HashMap<>();
        params.put("status", EnumOrderStatus.Created.getCode());
        params.put("beginTime",(String)orderQueryPayingListRequest.getParams().getOrDefault("beginTime",""));
        params.put("endTime",(String)orderQueryPayingListRequest.getParams().getOrDefault("endTime",""));
        params.put("payType",(String)orderQueryPayingListRequest.getParams().getOrDefault("payType",""));
        params.put("offset", orderQueryPayingListRequest.getPageNo()-1);
        params.put("pageSize", orderQueryPayingListRequest.getPageSize());
        List<Order> orderList = extOrderQueryMapper.queryOrder(params);
        for(Order order:orderList){
            orderService.syncOrderWithReomte(order);
        }
        return new BillApiResponse(0,"同步成功，请刷新页面",null);

    }

    @ApiOperation(value = "查询订单状态，退款状态", position = 5, tags = "订单管理")
    @RequestMapping(value = "queryOrderStatus", method = RequestMethod.GET)
    @NeedLogin  @HasRole(values = {ConstsRole.ADMIN,ConstsRole.ORDER})
    public Object queryOrderStatusAction(@ApiParam(value = "appkey") @RequestParam String appkey, @ApiParam(value = "订单号") @RequestParam String orderId,
                                         @ApiParam(value = " localStatus/remoteStatus 本地/远程查询订单状态, " + "localRefund/remoteRefund 本地/远程查询退款状态", allowableValues = "localStatus,remoteStatus,localRefund,remoteRefund", example = "localStatus") @RequestParam(defaultValue = "localStatus") String type, HttpServletRequest httpServletRequest) {

        if("localStatus".equals(type) ||  "remoteStatus".equals(type)){
            Order order = orderQueryService.getOrderByAppKeyAndId(appkey,orderId);
            if(order == null){
                throw new BillException(5100,"没有对应订单");
            }
            if("localStatus".equals(type)){
                return orderService.orderQueryLocal(order);
            }else{
                return new BillApiResponse(orderService.queryOrderStatusFromRemote(order));
            }
        }else if("localRefund".equalsIgnoreCase(type)){
            return  refundService.refundQuery(orderId,0);
        }else if("remoteRefund".equalsIgnoreCase(type)){
            return  refundService.refundQuery(orderId,1);
        }else{
            throw new BillException(5100,"查询类型出错");
        }
    }


    @ApiOperation(value = "支付状态下拉框", position = 2, tags = "应用管理")
    @RequestMapping(value = "getOrderStatus", method = RequestMethod.GET)
    @NeedLogin
    public Object getStatusAction() {
        return BaseComboResponse.getPayStatusCombo();
    }

    @RequestMapping(value = "getNotifyLog/{orderId:^\\d{21}$}", method = RequestMethod.GET)
    @NeedLogin     @HasRole(values = {ConstsRole.ADMIN,ConstsRole.ORDER})

    public Object getNotifyLogListAction(@PathVariable String orderId,@RequestParam  String type){
        return noticeLogService.listByOrderId(orderId,type);
    }

    @RequestMapping(value = {"doNotify/{method}/{orderId:^\\d{21}$}","doNotify/{method}/{orderId:r_\\d{21}_\\d+}"},
            method =
            RequestMethod.GET)
    @NeedLogin  @HasRole(values = {ConstsRole.ADMIN,ConstsRole.ORDER})
    public Object sendNotifyAction(@PathVariable String orderId ,@PathVariable(required = true) String method){
        if(EnumOrderMessageType.PAY.name().equalsIgnoreCase(method)){
            Order order = orderService.loadByOrderID(orderId);
            if(order==null){
                throw new BillException(UnipayError.OrderNotExists);
            }
            return  orderService.manualSendNotify(order);
        }else{
            Refund refund = refundService.loadById(orderId);
            if(refund ==null ){
                throw new BillException(UnipayError.RefundQueryIdError);
            }
            return  orderService.manualSendNotify(refund);
        }

    }
}

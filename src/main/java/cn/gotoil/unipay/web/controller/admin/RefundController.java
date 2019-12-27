package cn.gotoil.unipay.web.controller.admin;

import cn.gotoil.bill.web.annotation.HasRole;
import cn.gotoil.bill.web.annotation.NeedLogin;
import cn.gotoil.unipay.config.consts.ConstsRole;
import cn.gotoil.unipay.web.message.request.admin.RefundQueryListRequest;
import cn.gotoil.unipay.web.message.response.admin.BaseComboResponse;
import cn.gotoil.unipay.web.services.RefundService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 退款管理
 *
 * @author think <syj247@qq.com>、
 * @date 2019-12-26、17:31
 */
@RestController
@RequestMapping("web/refund")
@Api(description = "退款管理")
public class RefundController {

    @Autowired
    RefundService refundService;

    /**
     * 退款申请记录
     */
    @ApiOperation(value = "退款查询", position = 1, tags = "退款管理")
    @RequestMapping(value = "list", method = RequestMethod.POST)
    @NeedLogin
    @HasRole(values = {ConstsRole.ADMIN, ConstsRole.ORDER, ConstsRole.FINANCE})
    public Object listRefundAction(@RequestBody RefundQueryListRequest request) {
        return refundService.queryRefundList(request);
    }


    @ApiOperation(value = "退款状态", position = 3, tags = "退款管理")
    @RequestMapping(value = "status", method = RequestMethod.GET)
    @NeedLogin
    public Object getAccountTypesAction() {
        return BaseComboResponse.getRefundStatus();
    }

    @ApiOperation(value = "退款状态远程查询", position = 1, tags = "退款管理")
    @RequestMapping(value = "queryStatus", method = RequestMethod.GET)
    @NeedLogin
    @HasRole(values = {ConstsRole.ADMIN, ConstsRole.ORDER, ConstsRole.FINANCE})
    public Object queryRefundRemoteStatusAction(@ApiParam(value = "queryId") @RequestParam String queryId) {
        return refundService.refundQueryFromRemote(queryId);
    }



}

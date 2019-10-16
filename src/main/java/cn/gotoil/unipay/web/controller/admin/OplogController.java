package cn.gotoil.unipay.web.controller.admin;

import cn.gotoil.bill.web.annotation.NeedLogin;
import cn.gotoil.unipay.web.message.request.admin.OplogListRequest;
import cn.gotoil.unipay.web.services.OpLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("web/log")
@Api(description = "日志管理")
public class OplogController {

    @Autowired
    OpLogService opLogService;

    @ApiOperation(value = "查询日志", position = 1, tags = "日志管理")
    @RequestMapping(value = "/queryOpLogList", method = {RequestMethod.GET})
    @NeedLogin
    public Object queryOpLogList(@RequestBody OplogListRequest oplogListRequest){
        return opLogService.queryOplog(oplogListRequest);
    }

}

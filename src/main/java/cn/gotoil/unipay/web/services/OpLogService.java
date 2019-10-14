package cn.gotoil.unipay.web.services;

import cn.gotoil.unipay.model.entity.OpLog;
import cn.gotoil.unipay.web.message.BasePageResponse;
import cn.gotoil.unipay.web.message.request.admin.OplogListRequest;

public interface OpLogService {
    int insert(OpLog opLog);

    /**
     * 查询日志
     * @param oplogListRequest
     * @return
     */
    BasePageResponse queryOplog(OplogListRequest oplogListRequest);
}

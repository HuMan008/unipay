package cn.gotoil.unipay.web.services;

import cn.gotoil.unipay.model.entity.NotifyAccept;
import cn.gotoil.unipay.model.enums.EnumOrderMessageType;
import cn.gotoil.unipay.utils.UtilRequest;
import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author think <syj247@qq.com>、
 * @date 2019-10-14、16:21
 */
public interface NotifyAcceptService {
    static NotifyAccept createDefault(HttpServletRequest request, EnumOrderMessageType messageType, String orderId) {
        NotifyAccept notifyAccept = new NotifyAccept();
        notifyAccept.setOrderId(orderId);
        notifyAccept.setMethod(messageType == EnumOrderMessageType.PAY ? (byte) 0 : (byte) 1);
        notifyAccept.setIp(UtilRequest.getIpAddress(request));
        notifyAccept.setParams(JSON.toJSONString(UtilRequest.request2Map(request)));
        notifyAccept.setCreatedAt(new Date());
        return notifyAccept;
    }

    /**
     * 添加
     *
     * @param notifyAccept
     * @return
     */
    int add(NotifyAccept notifyAccept);
}

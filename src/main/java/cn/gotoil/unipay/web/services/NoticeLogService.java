package cn.gotoil.unipay.web.services;

import cn.gotoil.unipay.model.entity.NoticeLog;

import java.util.List;

/**
 * @author think <syj247@qq.com>、
 * @date 2019-10-12、9:57
 */
public interface NoticeLogService {
    /**
     * 添加一条通知记录
     *
     * @param noticeLog
     * @return
     */
    int addNoticeLog(NoticeLog noticeLog);

    /**
     * 根据订单号获取订单通知记录
     * @param orderId
     * @return
     */
    List<NoticeLog> listByOrderId(String orderId);
}

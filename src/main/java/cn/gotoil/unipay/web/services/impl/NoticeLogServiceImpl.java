package cn.gotoil.unipay.web.services.impl;

import cn.gotoil.unipay.model.entity.NoticeLog;
import cn.gotoil.unipay.model.entity.NoticeLogExample;
import cn.gotoil.unipay.model.mapper.NoticeLogMapper;
import cn.gotoil.unipay.utils.UtilString;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 通知应用的日志
 *
 * @author think <syj247@qq.com>、
 * @date 2019-10-12、9:15
 */
@Service
public class NoticeLogServiceImpl implements cn.gotoil.unipay.web.services.NoticeLogService {

    @Resource
    NoticeLogMapper noticeLogMapper;

    /**
     * 添加一条通知记录
     *
     * @param noticeLog
     * @return
     */
    @Override
    public int addNoticeLog(NoticeLog noticeLog) {

        noticeLog.setParams(UtilString.getLongString(noticeLog.getParams(), 3999));
        noticeLog.setResponseContent(UtilString.getLongString(noticeLog.getResponseContent(), 50));
        return noticeLogMapper.insert(noticeLog);
    }

    /**
     * 根据订单号获取订单通知记录
     * @param orderId
     * @return
     */
    @Override
    public List<NoticeLog> listByOrderId(String orderId){
        NoticeLogExample noticeLogExample = new NoticeLogExample();
        noticeLogExample.createCriteria().andOrderIdEqualTo(orderId);
        noticeLogExample.setOrderByClause("notice_datetime");
        return  noticeLogMapper.selectByExample(noticeLogExample);
    }
}

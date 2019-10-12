package cn.gotoil.unipay.web.services.impl;

import cn.gotoil.unipay.model.entity.NoticeLog;
import cn.gotoil.unipay.model.mapper.NoticeLogMapper;
import cn.gotoil.unipay.utils.UtilString;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
        if (noticeLog.getParams().length() > 3999) {
            noticeLog.setParams(UtilString.getLongString(noticeLog.getParams(), 3999));
        }
        if (noticeLog.getResponseContent().length() > 50) {
            noticeLog.setResponseContent(UtilString.getLongString(noticeLog.getResponseContent(), 50));
        }
        return noticeLogMapper.insert(noticeLog);
    }
}

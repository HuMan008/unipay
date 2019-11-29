package cn.gotoil.unipay.web.services.impl;

import cn.gotoil.unipay.model.entity.NotifyAccept;
import cn.gotoil.unipay.model.mapper.NotifyAcceptMapper;
import cn.gotoil.unipay.web.services.NotifyAcceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 收到的异步通知服务实现类
 *
 * @author think <syj247@qq.com>、
 * @date 2019-10-14、16:09
 */
@Service
public class NotifyAcceptServiceImpl implements NotifyAcceptService {


    @Autowired
    NotifyAcceptMapper notifyAcceptMapper;

    /**
     * 添加
     *
     * @param notifyAccept
     * @return
     */
    @Override
    public int add(NotifyAccept notifyAccept) {
        return notifyAcceptMapper.insert(notifyAccept);
    }
}

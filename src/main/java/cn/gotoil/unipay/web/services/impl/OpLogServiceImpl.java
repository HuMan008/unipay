package cn.gotoil.unipay.web.services.impl;

import cn.gotoil.unipay.model.entity.OpLog;
import cn.gotoil.unipay.model.mapper.OpLogMapper;
import cn.gotoil.unipay.web.services.OpLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OpLogServiceImpl implements OpLogService {

    @Resource
    OpLogMapper opLogMapper;

    @Override
    public int insert(OpLog opLog){
        return opLogMapper.insert(opLog);
    }
}

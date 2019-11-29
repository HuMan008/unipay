package cn.gotoil.unipay.web.services.impl;

import cn.gotoil.unipay.model.entity.OpLog;
import cn.gotoil.unipay.model.entity.OpLogExample;
import cn.gotoil.unipay.model.mapper.OpLogMapper;
import cn.gotoil.unipay.web.message.BasePageResponse;
import cn.gotoil.unipay.web.message.request.admin.OplogListRequest;
import cn.gotoil.unipay.web.services.OpLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class OpLogServiceImpl implements OpLogService {

    @Resource
    OpLogMapper opLogMapper;

    @Override
    public int insert(OpLog opLog){
        return opLogMapper.insert(opLog);
    }


    /**
     * 查询日志
     * @param oplogListRequest
     * @return
     */
    @Override
    public BasePageResponse queryOplog(OplogListRequest oplogListRequest){
        BasePageResponse pageResponse = new BasePageResponse();
        BeanUtils.copyProperties(oplogListRequest, pageResponse);
        OpLogExample example = new OpLogExample();
        OpLogExample.Criteria criteria = example.createCriteria();
        Map<String, Object> params = oplogListRequest.getParams();
        if (params.containsKey("name") && StringUtils.isNotEmpty((String) params.get("name"))) {
            criteria.andOpUserNameLike(String.valueOf(params.get("name")) + "%");
        }

        if (params.containsKey("descp") && StringUtils.isNotEmpty((String) params.get("descp"))) {
            criteria.andDescpLike(String.valueOf(params.get("descp")) + "%");
        }
        List<OpLog> lists = opLogMapper.selectByExample(example);
        pageResponse.setTotal(lists.isEmpty() ? 0 : lists.size());
        example.setLimit(pageResponse.getPageSize());
        example.setOffset(pageResponse.getOffset());
        example.setOrderByClause("created_at desc");
        pageResponse.setRows(opLogMapper.selectByExample(example));
        return pageResponse;
    }
}

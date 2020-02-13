package cn.gotoil.unipay.web.services.impl;

import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.exception.CommonError;
import cn.gotoil.bill.tools.date.DateUtils;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.model.ChargeAlipayModel;
import cn.gotoil.unipay.model.ChargeWechatModel;
import cn.gotoil.unipay.model.OrderRefundQueryModel;
import cn.gotoil.unipay.model.RefundDetail;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.entity.Refund;
import cn.gotoil.unipay.model.entity.RefundExample;
import cn.gotoil.unipay.model.enums.EnumPayCategory;
import cn.gotoil.unipay.model.enums.EnumPayType;
import cn.gotoil.unipay.model.enums.EnumRefundStatus;
import cn.gotoil.unipay.model.mapper.RefundMapper;
import cn.gotoil.unipay.model.mapper.ext.ExtRefundQueryMapper;
import cn.gotoil.unipay.utils.DateUtil;
import cn.gotoil.unipay.web.message.BasePageResponse;
import cn.gotoil.unipay.web.message.request.RefundRequest;
import cn.gotoil.unipay.web.message.request.admin.RefundQueryListRequest;
import cn.gotoil.unipay.web.message.response.RefundQueryResponse;
import cn.gotoil.unipay.web.services.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 退款服务
 *
 * @author think <syj247@qq.com>、
 * @date 2019-12-18、16:39
 */
@Service
public class RefundServiceImpl implements RefundService {

    @Resource
    RefundMapper refundMapper;

    @Autowired
    ChargeConfigService chargeConfigService;
    @Autowired
    AlipayService alipayService;

    @Autowired
    WechatService wechatService;

    @Autowired
    OrderService orderService;

    @Resource
    ExtRefundQueryMapper extRefundQueryMapper;

    /**
     * 发起退款申请
     *
     * @param order
     * @param refundRequest
     * @return
     */
    @Override
    public Object refund(Order order, RefundRequest refundRequest) {
        assert order != null;
        List<Refund> refundList = getRefundList(order.getId());
        // A 存在历史退款请求
        if (refundList != null && !refundList.isEmpty()) {
            //检查是否存在退款正在处理的退款记录
            boolean hasRefnding =
                    refundList.stream().anyMatch(r -> r.getProcessResult() == EnumRefundStatus.Refunding.getCode() || r.getProcessResult() == EnumRefundStatus.WaitSure.getCode());
            if (hasRefnding) {
                throw new BillException(UnipayError.ExistRefundApplay);
            }
            //检查是否再退款期以内
            if(!canRefund(order)){
                throw new BillException(UnipayError.RefundError_OutTime);
            }
            int refundedFee = 0;
            refundedFee =
                    refundList.stream().filter(r -> r.getProcessResult() == EnumRefundStatus.Success.getCode()).mapToInt(r -> r.getFee()).sum();
            if (refundedFee + refundRequest.getFee() > order.getPayFee()) {
                throw new BillException(UnipayError.RefundFeeOutOfOrderFee);
            }
        }
        //B 不存在历史退款记录
        Refund refund = warpBean(order, refundRequest,refundList==null?0:refundList.size());
        int x = refundMapper.insert(refund);
        if (x != 1) {
            throw new BillException(CommonError.SystemError);
        }
        // B2开始申请
        // 这里的退款申请需要用原下单账号
        ChargeConfig chargeConfig = chargeConfigService.loadByChargeId(order.getChargeAccountId());
        if (EnumPayType.WechatSDK.getCode().equals(chargeConfig.getPayType()) || EnumPayType.WechatNAtive.getCode().equals(chargeConfig.getPayType()) || EnumPayType.WechatJSAPI.getCode().equals(chargeConfig.getPayType()) || EnumPayType.WechatH5.getCode().equals(chargeConfig.getPayType())) {
            ChargeWechatModel chargeWechatModel =
                    JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()), ChargeWechatModel.class);
            return wechatService.orderRefund(chargeWechatModel, refund);
        } else if (EnumPayType.AlipayH5.getCode().equals(chargeConfig.getPayType()) || EnumPayType.AlipaySDK.getCode().equals(chargeConfig.getPayType())) {
            ChargeAlipayModel chargeAlipayModel =
                    JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()), ChargeAlipayModel.class);
            return alipayService.orderRefund(chargeAlipayModel, refund);
        } else {
            throw new BillException((UnipayError.UnSupportRefund));
        }

    }


    /**
     * 查订单的所有退款记录
     *
     * @param orderId 统一订单号
     * @return
     */
    @Override
    public List<Refund> getRefundList(String orderId) {
        RefundExample refundExample = new RefundExample();
        refundExample.createCriteria().andOrderIdEqualTo(orderId);
        return refundMapper.selectByExample(refundExample);
    }


    public Refund warpBean(Order order, RefundRequest refundRequest,int index) {
        Refund refund = new Refund();
        refund.setRefundOrderId("r_" + order.getId() + "_" + index);
        refund.setOrderId(order.getId());
        refund.setOrderFee(order.getPayFee());
        refund.setAppOrderRefundNo(refundRequest.getAppOrderRefundNo().isEmpty() ? refund.getAppOrderNo() :
                refundRequest.getAppOrderRefundNo());
        refund.setAppOrderNo(refundRequest.getAppOrderNo());
        refund.setApplyFee(refundRequest.getFee());
        refund.setApplyDatetime(new Date());
        refund.setDescp(refundRequest.getRemark());
        refund.setFee(0);
        refund.setProcessResult(EnumRefundStatus.Refunding.getCode());
        refund.setStatusUpdateDatetime(refund.getApplyDatetime());
        refund.setCreatedAt(refund.getApplyDatetime());
        refund.setUpdateAt(refund.getApplyDatetime());
        return refund;
    }


    /**
     * 订单查询
     *
     * @param refundQueryId
     * @param type          0本地查询 1远程查
     * @return 远程查询服务器自身调用
     */
    @Override
    public Object refundQuery(String refundQueryId, int type) {
        //本地查询
        if (type == 0) {
            Refund refund = refundMapper.selectByPrimaryKey(refundQueryId);
            if (refund == null) {
                throw new BillException(UnipayError.RefundQueryIdError);
            } else {
                return RefundDetail.refund2DetailBean(refund);
            }
        } else {
            return refundQueryFromRemote(refundQueryId);
        }
    }


    /**
     * 查询订单的退款记录
     *
     * @param orderId 统一订单ID
     */
    @Override
    public OrderRefundQueryModel orderRefundQuery(String orderId) {
        Order order = orderService.loadByOrderID(orderId);
        Optional.ofNullable(order).orElseThrow(() -> new BillException(UnipayError.OrderNotExists));

        List<Refund> list = getRefundList(orderId);
        OrderRefundQueryModel model = new OrderRefundQueryModel();
        model.setUnionOrderID(order.getId());
        model.setOrderArrFee(order.getArrFee());
        model.setOrderFee(order.getFee());
        model.setPayFee(order.getPayFee());
        int totalRefundFee =
                list.stream().filter(e -> e.getProcessResult() == EnumRefundStatus.Success.getCode()).mapToInt(Refund::getFee).sum();
        model.setTotolRefundFee(totalRefundFee);
        List<RefundDetail> detailList = list.stream().map(e -> {
            return RefundDetail.refund2DetailBean(e);
        }).collect(Collectors.toList());
        model.setRefundDetail(detailList);
        return model;
    }


    /**
     * 退款状态查询
     *
     * @param refundId
     * @return
     */
    @Override
    public RefundQueryResponse refundQueryFromRemote(String refundId) {
        Refund refund = refundMapper.selectByPrimaryKey(refundId);
        if(refund==null){
            throw new BillException(UnipayError.RefundQueryIdError);
        }
        Order order =orderService.loadByOrderID(refund.getOrderId());
        if(order==null){
            throw new BillException(UnipayError.OrderNotExists);
        }
        ChargeConfig chargeConfig = chargeConfigService.loadByChargeId(order.getChargeAccountId());
        if (EnumPayType.WechatSDK.getCode().equals(chargeConfig.getPayType()) || EnumPayType.WechatNAtive.getCode().equals(chargeConfig.getPayType()) || EnumPayType.WechatJSAPI.getCode().equals(chargeConfig.getPayType()) || EnumPayType.WechatH5.getCode().equals(chargeConfig.getPayType())) {
            ChargeWechatModel chargeWechatModel =
                    JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()), ChargeWechatModel.class);
            return wechatService.orderRefundQuery(chargeWechatModel, refund);
        } else if (EnumPayType.AlipayH5.getCode().equals(chargeConfig.getPayType()) || EnumPayType.AlipaySDK.getCode().equals(chargeConfig.getPayType())) {
            ChargeAlipayModel chargeAlipayModel =
                    JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()), ChargeAlipayModel.class);
            return alipayService.orderRefundQuery(chargeAlipayModel, refund);
        } else {
            throw new BillException((UnipayError.UnSupportRefund));
        }

    }

    /**
     * 退款列表
     *
     * @param request
     * @return
     */
    @Override
    public Object queryRefundList(RefundQueryListRequest request) {
        BasePageResponse pageResponse = new BasePageResponse();
        BeanUtils.copyProperties(request, pageResponse);
        Map<String, Object> params = request.getParams();
        int total = extRefundQueryMapper.queryRefundCounts(params);
        pageResponse.setTotal(total);

        params.put("offset", pageResponse.getOffset());
        params.put("pageSize", pageResponse.getPageSize());
        pageResponse.setRows(extRefundQueryMapper.refundList(params));
        return pageResponse;
    }


    /**
     * 获取退款提交成功但是结果未知的 退款列表
     * @return
     */
    @Override
    public List<Refund> getWaitSureResultList(){
        RefundExample refundExample = new RefundExample();
        List<Byte> statusList =  new ArrayList<>();
        statusList.add(EnumRefundStatus.WaitSure.getCode());
        statusList.add(EnumRefundStatus.Refunding.getCode());
        refundExample.createCriteria().andProcessResultIn(statusList);
        refundExample.setLimit(200);
        refundExample.setOrderByClause("apply_datetime");
        return refundMapper.selectByExample(refundExample);
    }

    /**
     * 更新退款记录
     */
    @Override
    public int updateRefund(Refund dbRefund, Refund newRefund) {
        //这里要重写
        assert newRefund != null && newRefund.getRefundOrderId() != null && newRefund.getRefundOrderId().equals(newRefund.getRefundOrderId());
        newRefund.setUpdateAt(new Date());
        newRefund.setStatusUpdateDatetime(newRefund.getUpdateAt());
        RefundExample refundExample = new RefundExample();
        refundExample.createCriteria().andUpdateAtEqualTo(dbRefund.getUpdateAt());
        return refundMapper.updateByExampleSelective(newRefund, refundExample);
    }


    /**
     * 根据退款订单id找退款对象
     */
    @Override
    public Refund loadById(String refundId){
        return refundMapper.selectByPrimaryKey(refundId);
    }


    /**
     * 判断是否再退款期以内，如果是 返回ture,如果不是返回false
     * 支付宝3个月内的订单
     * 微信12个月内的
     * @param o
     * @return
     */
    private boolean canRefund(Order o ){
        Date payDate = new Date(o.getOrderPayDatetime()*1000);
        if(EnumPayCategory.Alipay.getCodeValue() == o.getPayTypeCategory().byteValue()){
            return Math.abs(DateUtils.monthsBetween(new Date(),payDate))<=3;
        }else if(EnumPayCategory.Wechat.getCodeValue() == o.getPayTypeCategory().byteValue()){
            return Math.abs(DateUtils.monthsBetween(new Date(),payDate))<=12;
        }else {
            return false;
        }
    }
}

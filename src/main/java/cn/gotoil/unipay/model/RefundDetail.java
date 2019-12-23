package cn.gotoil.unipay.model;

import cn.gotoil.unipay.model.entity.Refund;
import cn.gotoil.unipay.model.enums.EnumRefundStatus;
import lombok.Data;

import java.util.Date;

/**
 * @author think <syj247@qq.com>、
 * @date 2019-12-19、15:55
 */
@Data
public class RefundDetail {
    String refundOrderId;
    String appOrderNo;
    String appOrderRefundNo;
    int applyFee;
    int passFee;
    Date applyTime;
    String descp;
    String fialMsg;
    byte processResult;

    public static RefundDetail refund2DetailBean (Refund refund){
        RefundDetail detail = new RefundDetail();
        detail.setRefundOrderId(refund.getRefundOrderId());
        detail.setAppOrderNo(refund.getAppOrderNo());
        detail.setAppOrderRefundNo(refund.getAppOrderRefundNo());
        detail.setApplyFee(refund.getApplyFee());
        detail.setPassFee(refund.getProcessResult() == EnumRefundStatus.Success.getCode() ? refund.getFee() : 0);
        detail.setApplyTime(refund.getCreatedAt());
        detail.setFialMsg(refund.getFailMsg());
        detail.setProcessResult(refund.getProcessResult());
        return detail;
    }

}

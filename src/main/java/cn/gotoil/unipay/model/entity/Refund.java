package cn.gotoil.unipay.model.entity;

import java.util.Date;

public class Refund {
    private String refundOrderId;

    private String orderId;

    private Integer orderFee;

    private String appOrderRefundNo;

    private String appOrderNo;

    private Integer applyFee;

    private Date applyDatetime;

    private String descp;

    private Byte processResult;

    private String failMsg;

    private Integer fee;

    private Date statusUpdateDatetime;

    private Date createdAt;

    private Date updateAt;

    public String getRefundOrderId() {
        return refundOrderId;
    }

    public void setRefundOrderId(String refundOrderId) {
        this.refundOrderId = refundOrderId == null ? null : refundOrderId.trim();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public Integer getOrderFee() {
        return orderFee;
    }

    public void setOrderFee(Integer orderFee) {
        this.orderFee = orderFee;
    }

    public String getAppOrderRefundNo() {
        return appOrderRefundNo;
    }

    public void setAppOrderRefundNo(String appOrderRefundNo) {
        this.appOrderRefundNo = appOrderRefundNo == null ? null : appOrderRefundNo.trim();
    }

    public String getAppOrderNo() {
        return appOrderNo;
    }

    public void setAppOrderNo(String appOrderNo) {
        this.appOrderNo = appOrderNo == null ? null : appOrderNo.trim();
    }

    public Integer getApplyFee() {
        return applyFee;
    }

    public void setApplyFee(Integer applyFee) {
        this.applyFee = applyFee;
    }

    public Date getApplyDatetime() {
        return applyDatetime;
    }

    public void setApplyDatetime(Date applyDatetime) {
        this.applyDatetime = applyDatetime;
    }

    public String getDescp() {
        return descp;
    }

    public void setDescp(String descp) {
        this.descp = descp == null ? null : descp.trim();
    }

    public Byte getProcessResult() {
        return processResult;
    }

    public void setProcessResult(Byte processResult) {
        this.processResult = processResult;
    }

    public String getFailMsg() {
        return failMsg;
    }

    public void setFailMsg(String failMsg) {
        this.failMsg = failMsg == null ? null : failMsg.trim();
    }

    public Integer getFee() {
        return fee;
    }

    public void setFee(Integer fee) {
        this.fee = fee;
    }

    public Date getStatusUpdateDatetime() {
        return statusUpdateDatetime;
    }

    public void setStatusUpdateDatetime(Date statusUpdateDatetime) {
        this.statusUpdateDatetime = statusUpdateDatetime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", refundOrderId=").append(refundOrderId);
        sb.append(", orderId=").append(orderId);
        sb.append(", orderFee=").append(orderFee);
        sb.append(", appOrderRefundNo=").append(appOrderRefundNo);
        sb.append(", appOrderNo=").append(appOrderNo);
        sb.append(", applyFee=").append(applyFee);
        sb.append(", applyDatetime=").append(applyDatetime);
        sb.append(", descp=").append(descp);
        sb.append(", processResult=").append(processResult);
        sb.append(", failMsg=").append(failMsg);
        sb.append(", fee=").append(fee);
        sb.append(", statusUpdateDatetime=").append(statusUpdateDatetime);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updateAt=").append(updateAt);
        sb.append("]");
        return sb.toString();
    }
}
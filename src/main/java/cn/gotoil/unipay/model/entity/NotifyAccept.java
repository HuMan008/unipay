package cn.gotoil.unipay.model.entity;

import java.util.Date;

public class NotifyAccept {
    private Integer id;

    private Byte method;

    private String orderId;

    private String ip;

    private String appOrderNo;

    private String paymentId;

    private String responstr;

    private Date createdAt;

    private String params;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Byte getMethod() {
        return method;
    }

    public void setMethod(Byte method) {
        this.method = method;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    public String getAppOrderNo() {
        return appOrderNo;
    }

    public void setAppOrderNo(String appOrderNo) {
        this.appOrderNo = appOrderNo == null ? null : appOrderNo.trim();
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId == null ? null : paymentId.trim();
    }

    public String getResponstr() {
        return responstr;
    }

    public void setResponstr(String responstr) {
        this.responstr = responstr == null ? null : responstr.trim();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params == null ? null : params.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", method=").append(method);
        sb.append(", orderId=").append(orderId);
        sb.append(", ip=").append(ip);
        sb.append(", appOrderNo=").append(appOrderNo);
        sb.append(", paymentId=").append(paymentId);
        sb.append(", responstr=").append(responstr);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", params=").append(params);
        sb.append("]");
        return sb.toString();
    }
}
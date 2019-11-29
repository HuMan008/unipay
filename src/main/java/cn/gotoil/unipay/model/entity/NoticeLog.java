package cn.gotoil.unipay.model.entity;

import java.util.Date;

public class NoticeLog {
    private Integer id;

    private String orderId;

    private String appId;

    private String appOrderNo;

    private Byte method;

    private String notifyUrl;

    private String params;

    private String responseContent;

    private Date noticeDatetime;

    private Integer repeatCount;

    private Byte sendType;

    private Date createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId == null ? null : appId.trim();
    }

    public String getAppOrderNo() {
        return appOrderNo;
    }

    public void setAppOrderNo(String appOrderNo) {
        this.appOrderNo = appOrderNo == null ? null : appOrderNo.trim();
    }

    public Byte getMethod() {
        return method;
    }

    public void setMethod(Byte method) {
        this.method = method;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl == null ? null : notifyUrl.trim();
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params == null ? null : params.trim();
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent == null ? null : responseContent.trim();
    }

    public Date getNoticeDatetime() {
        return noticeDatetime;
    }

    public void setNoticeDatetime(Date noticeDatetime) {
        this.noticeDatetime = noticeDatetime;
    }

    public Integer getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(Integer repeatCount) {
        this.repeatCount = repeatCount;
    }

    public Byte getSendType() {
        return sendType;
    }

    public void setSendType(Byte sendType) {
        this.sendType = sendType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", orderId=").append(orderId);
        sb.append(", appId=").append(appId);
        sb.append(", appOrderNo=").append(appOrderNo);
        sb.append(", method=").append(method);
        sb.append(", asyncUrl=").append(notifyUrl);
        sb.append(", params=").append(params);
        sb.append(", responseContent=").append(responseContent);
        sb.append(", noticeDatetime=").append(noticeDatetime);
        sb.append(", repeatCount=").append(repeatCount);
        sb.append(", sendType=").append(sendType);
        sb.append(", createdAt=").append(createdAt);
        sb.append("]");
        return sb.toString();
    }
}
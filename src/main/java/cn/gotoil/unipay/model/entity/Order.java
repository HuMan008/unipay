package cn.gotoil.unipay.model.entity;

import java.util.Date;

public class Order {
    private String id;

    private String appId;

    private String appOrderNo;

    private String appUserId;

    private String paymentId;

    private String paymentUid;

    private Integer expiredTimeMinute;

    private Integer fee;

    private String subjects;

    private String descp;

    private String extraParam;

    private String syncUrl;

    private String asyncUrl;

    private Byte status;

    private Long orderPayDatetime;

    private Integer payFee;

    private Integer arrFee;

    private String payType;

    private Byte payTypeCategory;

    private Integer chargeAccountId;

    private String apiVersion;

    private Integer dataVersion;

    private Date createdAt;

    private Date updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
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

    public String getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(String appUserId) {
        this.appUserId = appUserId == null ? null : appUserId.trim();
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId == null ? null : paymentId.trim();
    }

    public String getPaymentUid() {
        return paymentUid;
    }

    public void setPaymentUid(String paymentUid) {
        this.paymentUid = paymentUid == null ? null : paymentUid.trim();
    }

    public Integer getExpiredTimeMinute() {
        return expiredTimeMinute;
    }

    public void setExpiredTimeMinute(Integer expiredTimeMinute) {
        this.expiredTimeMinute = expiredTimeMinute;
    }

    public Integer getFee() {
        return fee;
    }

    public void setFee(Integer fee) {
        this.fee = fee;
    }

    public String getSubjects() {
        return subjects;
    }

    public void setSubjects(String subjects) {
        this.subjects = subjects == null ? null : subjects.trim();
    }

    public String getDescp() {
        return descp;
    }

    public void setDescp(String descp) {
        this.descp = descp == null ? null : descp.trim();
    }

    public String getExtraParam() {
        return extraParam;
    }

    public void setExtraParam(String extraParam) {
        this.extraParam = extraParam == null ? null : extraParam.trim();
    }

    public String getSyncUrl() {
        return syncUrl;
    }

    public void setSyncUrl(String syncUrl) {
        this.syncUrl = syncUrl == null ? null : syncUrl.trim();
    }

    public String getAsyncUrl() {
        return asyncUrl;
    }

    public void setAsyncUrl(String asyncUrl) {
        this.asyncUrl = asyncUrl == null ? null : asyncUrl.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Long getOrderPayDatetime() {
        return orderPayDatetime;
    }

    public void setOrderPayDatetime(Long orderPayDatetime) {
        this.orderPayDatetime = orderPayDatetime;
    }

    public Integer getPayFee() {
        return payFee;
    }

    public void setPayFee(Integer payFee) {
        this.payFee = payFee;
    }

    public Integer getArrFee() {
        return arrFee;
    }

    public void setArrFee(Integer arrFee) {
        this.arrFee = arrFee;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType == null ? null : payType.trim();
    }

    public Byte getPayTypeCategory() {
        return payTypeCategory;
    }

    public void setPayTypeCategory(Byte payTypeCategory) {
        this.payTypeCategory = payTypeCategory;
    }

    public Integer getChargeAccountId() {
        return chargeAccountId;
    }

    public void setChargeAccountId(Integer chargeAccountId) {
        this.chargeAccountId = chargeAccountId;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion == null ? null : apiVersion.trim();
    }

    public Integer getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(Integer dataVersion) {
        this.dataVersion = dataVersion;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", appId=").append(appId);
        sb.append(", appOrderNo=").append(appOrderNo);
        sb.append(", appUserId=").append(appUserId);
        sb.append(", paymentId=").append(paymentId);
        sb.append(", paymentUid=").append(paymentUid);
        sb.append(", expiredTimeMinute=").append(expiredTimeMinute);
        sb.append(", fee=").append(fee);
        sb.append(", subjects=").append(subjects);
        sb.append(", descp=").append(descp);
        sb.append(", extraParam=").append(extraParam);
        sb.append(", syncUrl=").append(syncUrl);
        sb.append(", asyncUrl=").append(asyncUrl);
        sb.append(", status=").append(status);
        sb.append(", orderPayDatetime=").append(orderPayDatetime);
        sb.append(", payFee=").append(payFee);
        sb.append(", arrFee=").append(arrFee);
        sb.append(", payType=").append(payType);
        sb.append(", payTypeCategory=").append(payTypeCategory);
        sb.append(", chargeAccountId=").append(chargeAccountId);
        sb.append(", apiVersion=").append(apiVersion);
        sb.append(", dataVersion=").append(dataVersion);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append("]");
        return sb.toString();
    }
}
package cn.gotoil.unipay.model.entity;

import java.util.Date;

public class App {
    private String appKey;

    private String appName;

    private String appSecret;

    private Byte status;

    private String remark;

    private String syncUrl;

    private String asyncUrl;

    private String orderHeader;

    private String orderDescp;

    private Integer defaultOrderExpiredTime;

    private Date createdAt;

    private Date updatedAt;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey == null ? null : appKey.trim();
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName == null ? null : appName.trim();
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret == null ? null : appSecret.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
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

    public String getOrderHeader() {
        return orderHeader;
    }

    public void setOrderHeader(String orderHeader) {
        this.orderHeader = orderHeader == null ? null : orderHeader.trim();
    }

    public String getOrderDescp() {
        return orderDescp;
    }

    public void setOrderDescp(String orderDescp) {
        this.orderDescp = orderDescp == null ? null : orderDescp.trim();
    }

    public Integer getDefaultOrderExpiredTime() {
        return defaultOrderExpiredTime;
    }

    public void setDefaultOrderExpiredTime(Integer defaultOrderExpiredTime) {
        this.defaultOrderExpiredTime = defaultOrderExpiredTime;
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
        sb.append(", appKey=").append(appKey);
        sb.append(", appName=").append(appName);
        sb.append(", appSecret=").append(appSecret);
        sb.append(", status=").append(status);
        sb.append(", remark=").append(remark);
        sb.append(", syncUrl=").append(syncUrl);
        sb.append(", asyncUrl=").append(asyncUrl);
        sb.append(", orderHeader=").append(orderHeader);
        sb.append(", orderDescp=").append(orderDescp);
        sb.append(", defaultOrderExpiredTime=").append(defaultOrderExpiredTime);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append("]");
        return sb.toString();
    }
}
package cn.gotoil.unipay.model.entity;

import lombok.ToString;

@ToString
public class AppAccountIds {
    private Integer alipayId;
    private Integer wechatId;

    public Integer getAlipayId() {
        return alipayId;
    }

    public void setAlipayId(Integer alipayId) {
        this.alipayId = alipayId;
    }

    public Integer getWechatId() {
        return wechatId;
    }

    public void setWechatId(Integer wechatId) {
        this.wechatId = wechatId;
    }
}

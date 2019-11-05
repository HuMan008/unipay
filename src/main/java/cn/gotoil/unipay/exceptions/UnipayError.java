package cn.gotoil.unipay.exceptions;

import cn.gotoil.bill.exception.BillError;

@SuppressWarnings("all")
public enum UnipayError implements BillError {
    AppNotExists(4001, "应用不存在"),

    AppStatusError(4002, "应用状态不可用"),

    AppNotSupportThisPay(4000, "应用不支持的支付方式"),

    AppOrderNoHasExists(4005, "应用订单号重复"),

    ChargeConfigIsDisabled(4008, "收款账号被停用"),

    CreatOrderError(4010, "不允许创建其他应用的订单"),

    PayTypeNotImpl(4012, "支付方式未实现"),

    OrderAppMatchError(4021, "订单所属匹配错误"),

    OrderNotExists(4020, "订单不存在"),

    IllegalRequest(5000, "非法请求"),

    WebUserError_UserPwdError(11009, "用户名密码错误"),

    OrderStatusIsNotPaySuccess(4021,"订单状态不是支付成功，不允许发送通知");

    ;

    private int code;
    private String message;

    UnipayError(int code, String message) {
        this.code = code;
        this.message = message;
    }


    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

package cn.gotoil.unipay.exceptions;

import cn.gotoil.bill.exception.BillError;

public enum UnipayError implements BillError {
    AppNotExists(4001, "应用不存在"), AppStatusError(4002, "应用状态不可用"), AppNotSupportThisPay(4000, "应用不支持的支付方式"),
    AppOrderNoHasExists(4005, "应用订单号重复"), ChargeConfigIsDisabled(4008, "收款账号被停用"), CreatOrderError(4010,
            "不允许创建其他应用的订单"), PayTypeNotImpl(4012, "支付方式未实现"),


    OrderNotExists(4020, "订单不存在"),

    IllegalRequest(5000, "非法请求"),
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

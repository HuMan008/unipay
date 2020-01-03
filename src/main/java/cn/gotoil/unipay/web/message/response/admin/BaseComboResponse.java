package cn.gotoil.unipay.web.message.response.admin;

import cn.gotoil.unipay.model.enums.EnumOrderStatus;
import cn.gotoil.unipay.model.enums.EnumPayType;
import cn.gotoil.unipay.model.enums.EnumRefundStatus;
import cn.gotoil.unipay.model.enums.EnumStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基本的下拉框枚举
 *
 * @author think <syj247@qq.com>、
 * @date 2019-10-30、17:24
 */
@Getter
@Setter
@AllArgsConstructor
public class BaseComboResponse {
    private String label;
    private String value;

    /**
     * 返回状态下拉框
     *
     * @return
     */
    public static List<BaseComboResponse> getEnumsStatusCombo() {
        return Arrays.stream(EnumStatus.values()).map(es -> new BaseComboResponse(es.getDescp(),
                String.valueOf(es.getCode()))).collect(Collectors.toList());
    }

    /**
     * 返回支付类型下拉框
     *
     * @return
     */
    public static List<BaseComboResponse> getPayTypeCombo() {
        return Arrays.stream(EnumPayType.values()).map(es -> new BaseComboResponse(es.getDescp(),
                String.valueOf(es.getCode()))).collect(Collectors.toList());
    }

    /**
     * 返回订单状态下拉框
     * @return
     */
    public static List<BaseComboResponse> getPayStatusCombo() {
        return Arrays.stream(EnumOrderStatus.values()).map(es -> new BaseComboResponse(es.getDescp(),
                String.valueOf(es.getCode()))).collect(Collectors.toList());
    }


    /**
     * 返回退款状态下拉框
     */
    public static List<BaseComboResponse> getRefundStatus() {
        return Arrays.stream(EnumRefundStatus.values()).map(es -> new BaseComboResponse(es.getDescp(),
                String.valueOf(es.getCode()))).collect(Collectors.toList());
    }


}

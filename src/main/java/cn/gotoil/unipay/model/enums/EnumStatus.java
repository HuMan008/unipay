package cn.gotoil.unipay.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnumStatus {
    Enable((byte) 0, "有效"), Disable((byte) 1, "无效"),
    ;
    byte code;
    String descp;
}

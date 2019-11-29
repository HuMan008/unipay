package cn.gotoil.unipay.model.view;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class AccountView {

    private Integer id;
    private String name;
    private String payDesc;
    private String payType;
    private Byte state;
    private Date createdAt;
    private Date updatedAt;
}

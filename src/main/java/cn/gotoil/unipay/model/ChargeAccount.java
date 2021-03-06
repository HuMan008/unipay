package cn.gotoil.unipay.model;

import java.io.Serializable;

/**
 * 收款账号配置标识接口
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、15:52
 */
public class ChargeAccount implements Serializable {

    // 特殊ID
    String mySpaceId;

    public String getMySpaceId() {
        return mySpaceId;
    }

    public void setMySpaceId(String mySpaceId) {
        this.mySpaceId = mySpaceId;
    }
}

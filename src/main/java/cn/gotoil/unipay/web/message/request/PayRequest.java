package cn.gotoil.unipay.web.message.request;

import cn.gotoil.bill.web.annotation.ThirdValidation;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 支付请求
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-19、15:29
 */
@Data
public class PayRequest {


    /***
     * 应用类别
     **/
    @NotNull(message = "应用Id不得为空!")
    private String appId;

    /***
     * 支付方式
     **/
    @NotNull(message = "支付方式不得为空!")
    @ThirdValidation(message = "无效的支付类型,注意区分大小写", className = "cn.gotoil.unipay.utils.UtilValid", methodName =
            "validEnumPayTypeByCode", staticMethod = true)
    private String payType;

    /***
     * 订单用户id[发起者]
     **/
    @Size(max = 50)
    private String appUserId;

    @Size(max = 24, message = "应用订单号最大长度为24")
    @NotNull
    private String appOrderNo;

    @Size(max = 500, message = "额外参数长度不得大于500！")
    private String extraParam;

    @Size(max = 300)
    private String syncUrl;

    @Size(max = 300)
    private String asyncUrl;

    //H5点返回按钮跳转的地址
    @Size(max = 300)
    private String backUrl;

    /** 取消支付 返回的路径  支付宝页面支付必填 */
    private String cancelUrl;

    /**
     * 订单 有效 时间(分钟)
     **/
    @Min(value = 0, message = "有效时间不得为负！")
    private int expireOutTime;


    //交易标题
    @Size(max = 50, message = "交易标题的长度不得大于50！")
    private String subject;

    @Size(max = 200, message = "交易备注最大长度为200个字符！")
    private String reMark;

    //请求支付的金额  分
    @NotNull(message = "支付金额不得为空！")
    @Min(value = 1, message = "支付金额不得少于1分！")
    private int fee;

    //通联微信支付的时候传的openID 如果日后要做通联支付宝支付可以传支付宝ID
    //    private String accId="o7N3k1cbqngyo6oWyfDhp-bpf6Vg";
    private String paymentUserID;

    /** 签名 */
    private String sign;
}


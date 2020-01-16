package cn.gotoil.unipay.web.message.request;

import cn.gotoil.bill.web.annotation.ThirdValidation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
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
@ApiModel(value = "PayRequest", description = "支付请求")
public class PayRequest {


    /***
     * 应用类别
     **/
    @NotNull(message = "应用Id不得为空!")
    @ApiModelProperty(required = true, example = "", value = "应用ID", notes = "统一分配", position = 1)
    private String appId;

    /***
     * 支付方式
     **/
    @NotNull(message = "支付方式不得为空!")
    @ThirdValidation(message = "无效的支付类型,注意区分大小写", className = "cn.gotoil.unipay.utils.UtilValid", methodName =
            "validEnumPayTypeByCode", staticMethod = true)
    @ApiModelProperty(required = true, example = "", value = "支付方式", allowEmptyValue = false, dataType =
            "cn.gotoil" + ".unipay.model.enums.EnumPayType", position = 5)
    private String payType;

    /***
     * 订单用户id[发起者]
     **/
    @Size(max = 50)
    @ApiModelProperty(required = false, example = "", value = "发起方用户唯一标识", position = 10)
    private String appUserId;

    @Size(max = 24, message = "应用订单号最大长度为24")
    @NotNull
    @ApiModelProperty(required = true, example = "", value = "发起方订单唯一标识", position = 15)
    private String appOrderNo;

    @Size(max = 500, message = "额外参数长度不得大于500！")
    @ApiModelProperty(required = false, example = "", value = "额外参数，请求后原样返回", position = 20)
    private String extraParam;

    @Size(max = 300)
    @ApiModelProperty(required = false, example = "", value = "同步返回地址", position = 25)
    private String syncUrl;

    @Size(max = 300)
    @ApiModelProperty(required = false, example = "", value = "异步返回地址", position = 30)
    private String asyncUrl;

    //H5点返回按钮跳转的地址
    @Size(max = 300)
    @ApiModelProperty(required = false, example = "", value = "H5支付中点返回按钮跳转的地址，不传按照浏览器处理", position = 35)
    private String backUrl;

    /**
     * 取消支付 返回的路径  支付宝页面支付必填
     */
    @Size(max = 300)
    @ApiModelProperty(required = false, example = "", value = "H5支付中点取消返回的路径,支付宝H5的时候必传", position = 40)
    private String cancelUrl;

    /**
     * 订单 有效 时间(分钟)
     **/
    @Min(value = 3, message = "有效时间不得为负！")
    @Max(value = 60, message = "订单有效时间不超过1小时")
    @ApiModelProperty(required = false, example = "5", value = "订单有效时间", position = 45)
    private int expireOutTime = 10;


    /**
     * 交易标题
     */
    @Size(max = 50, message = "交易标题的长度不得大于50！")
    @ApiModelProperty(required = false, example = "", value = "交易标题", position = 50)
    private String subject;

    @Size(max = 200, message = "交易备注最大长度为200个字符！")
    @ApiModelProperty(required = false, example = "", value = "交易备注", position = 55)
    private String reMark;

    /**
     * 请求支付的金额  分
     */
    @NotNull(message = "支付金额不得为空！")
    @Min(value = 1, message = "支付金额不得少于1分！")
    @ApiModelProperty(required = true, example = "1", value = "支付金额", position = 60)
    private int fee;


    //通联微信支付的时候传的openID 如果日后要做通联支付宝支付可以传支付宝ID
    //    private String accId="o7N3k1cbqngyo6oWyfDhp-bpf6Vg";
    /**
     * 支付方式对应用户ID
     */
    @Size(max = 50)
    @ApiModelProperty(required = false, example = "", value = "支付方式用户ID", position = 65)
    private String paymentUserID;

    /*
     *H5支付的时候 是否自动提交
     */
    @Min(value = 0, message = "自动提交参数只支持0(自动)或者1(手动)！")
    @Max(value = 1, message = "自动提交参数只支持0(自动)或者1(手动)！")
    @ApiModelProperty(required = false, example = "", value = "进入支付页面 是否自定提交 1不自动 0 自动", position = 41)
    private int autoCommit = 0;

    /**
     * 签名
     */
    @ApiModelProperty(required = false, example = "", value = "签名，WEB支付必传,API支付不用传,加密为 " + "MD5(appId+appOrderNo" +
            "+payType+fee+appKey) 再转大写 ", position = 70)
    private String sign;
}


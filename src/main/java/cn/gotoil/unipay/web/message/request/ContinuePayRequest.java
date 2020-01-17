package cn.gotoil.unipay.web.message.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 支付请求 -->订单创建后，继续支付
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-19、15:29
 */
@Data
@ApiModel(value = "ContinuePayRequest", description = "支付请求")
public class ContinuePayRequest {

    /***
     * 应用类别
     **/
    @NotNull(message = "应用Id不得为空!")
    @ApiModelProperty(required = true, example = "", value = "应用ID", notes = "统一分配", position = 1)
    private String appId;

    @Size(max = 24, message = "应用订单号最大长度为24")
    @NotNull
    @ApiModelProperty(required = true, example = "", value = "发起方订单唯一标识", position = 15)
    private String appOrderNo;

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


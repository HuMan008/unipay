

# 创建支付请求

## 请求路径
API模`/api/v1/dopay`

------

Web模式 `/web/dopay`

## 请求方法
API模`POST`

------

Web模式 ``GET``

## 请求参数

| 参数名  | 说明         | 选项                   | 类型 | 备注            |
| ------- | ------------ | ---------------------- | ---- | --------------- |
| appId   | 应用         | String(32)             | N    | 统一分配,必须32 |
| payType | 支付方式代码 | String(1,20)) | N    | <a href="#payType">支付方式说明</a> |
| appUserId | 应用用户唯一标识 | String(0,50) | O |  |
| appOrderNo | 应用订单号 | String(1,24) | N |  |
| extraParam | 订单扩展参数 | String(0,500) | O | 响应及通知中原样返回 |
| syncUrl | 同步通知地址 | String(0,300) | O | 如果为空，则采用应用申请的预留同步通知地址 |
| asyncUrl | 异步通知地址 | String(0,300) | O | 如果为空，则采用应用申请的预留异步通知地址 |
| backUrl | 返回跳转地址 | String(0,300) | O | H5支付情景用户点击返回按钮跳转的地址。如果不传则使用浏览器自带的返回.API模式无效 |
| cancelUrl | 取消支付跳转地址 | String(0,300) | O | 取消支付返回页面。API模式无效。``支付宝H5支付必传。`` |
| expireOutTime | 订单有效时间(分钟) | int | O | 默认10分钟 |
| subject | 订单标题 | String(0,50) | O | 订单头,不传将使用应用申请预留订单标题。 |
| reMark | 订单备注 | String(0,200) | O | 订单备注信息。不传将使用应用申请预留订单备注。 |
| fee | 订单金额 | int min=1 | N | 订单金额。 |
| paymentUserID | 支付方用户ID | String(0,50) | O | 支付方用户ID.如微信的open_id |
| sign | 签名 |  | O | 签名，``WEB支付必传``,API支付不用传,加密方法MD5(appId+appOrderNo+payType+fee+appKey) 再转大写。 |
|  |  |  |  |  |

## 响应
API模式
```json
{
    data:{
        payData:{
           //--具体的支付发起参数
        },
        extraParam:"", 支付请求里的extraParam原样返回
        sign:"" // md5(appSecert+ payData里map的对象排序后key1=value&key2=value... )
    },
    message	“”， //具体错误信息
    status	int  //响应代码 0成功其他失败
}
```

------

Web模式 页面跳转


# 异步通知说明
## 说明
```POST提交参数到异步通知地址。异步通知必须在三秒内响应。响应内容为“SUCCESS”表示不需要再发，否则按照通知定义机制多次发送。```

```同样的通知可能会多次发送给应用系统。应用必须能够正确处理重复的通知。```

````
异步通知内容为订单创建时的syncUrl=?param=xxxx&sign=xx
其中xxxx为base64加密后字符串，将xxx里内容中的所有"GT680"字符串替换成“+”并逆向解码后，得到通知参数。
sign的内容为md5(xxxx+appSecret(统一分配))
````

## 参数说明

<a name="asyncParam"></a>

| 参数名  | 说明         |
| ------- | ------------ |
| appId | 统一分配的应用ID |
| unionOrderID | 统一订单号 |
| method | 通知类型 PAY:支付通知;REFUND退款通知 |
| appOrderNO | 创建订单提交的订单编号 |
| paymentId | 支付方订单号(如：支付宝微信订单号) |
| status | <a href="#payStatus">支付状态</a> |
| orderFee | 订单金额，单位分 |
| payFee | 支付金额，单位分。实际用户支付金额，不包含优惠券等。 |
| arrFee | 实际到账金额 |
| refundFee | 退款金额。退款通知才有 |
| totalRefundFee | 累计退款金额。退款通知才有 |
| asyncUrl | 通知地址 |
| extraParam | 创建订单的时候的extraParam原样 |
| payDate | 支付时间 10位时间戳 |
| sign | 签名 通知对象转map去sign后转key1=value1&key2=value2....+key=appSecret得到字符串再MD5 |
| timeStamp | 通知创建时间 10位时间戳|
| doCount | 通知次数0开始 |
| sendType | 通知方式 0状态机触发 1手动触发 |
|  |  |



# 同步通知

## 说明

```GET提交参数到通步通知地址。同步通知内容不允许做业务逻辑处理，仅供页面展示```.

## 参数说明

参见<a href ="#asyncParam">异步通知说明</a>

# 退款申请
### 说明
```
	1、同一笔订单允许多次退款。但是只允许一笔处理中的订单。
	2、退款累计金额不超过用户实际支付金额（除优惠券等...）。
	3、需要退款的订单状态必须为已经支付的订单。
```

## 请求路径
`/api/v1/dopay `
## 请求参数

| 参数名           | 说明       | 选项       | 类型 | 备注                     |
| ---------------- | ---------- | ---------- | ---- | ------------------------ |
| appOrderNo       | 应用订单号 | String(24) | Y    | 创建订单时候提交的订单号 |
| appOrderRefundNo | 退款订单号 | String(30) | N    | 本地退款订单编号需唯一   |
| fee              | 退款金额   | int        | Y    | 单位分                   |
| remark           | 退款备注   | String(50) | N    |                          |

## 响应
```json
{
    "status": 0,
    "message": null,
    "data": {
        "reslutQueryId": "r_201912231459358781672_0",
        "refundStatus": 1, # <a href="#refundStatus"> 退款状态码 </a> 
        "msg": "退款申请成功，请调用查询退款获取退款结果"
    }
}
```
# 退款查询
## 退款状态码
 详见 <a href="#refundStatus"> 退款状态码 </a> 

## 请求路径 
`/api/v1/refundQuery/resultQueryId`
`resultQueryId 为退款申请时返回的`

## 请求参数
无
## 响应
```json
{
    "status": 0,
    "message": null,
    "data": {
        "orderRefundId": "r_201912231459358781672_0",
        "orderId": "201912231459358781672",
        "appOrderNo": "32478896825827328",
        "appOrderRefundNo": "32478896825827328",
        "applyFee": 1,
        "passFee": 1,
        "thirdMsg": "OKnull",
        "thirdCode": null,
        "refundStatus": 0
    }
}

```





# 全局定义

## 支付方式说明

<a name="payType"></a>

| 支付方式    | 说明                   | API  | Web  |
| ----------- | ---------------------- | ---- | ---- |
| WechatH5    | 微信H5（非微信浏览器） | ✘    | √    |
| WechatJSAPI | 微信H5(微信浏览器)     | ✘    | √    |
| WechatSDK    | 微信SDK |  √   |   ✘  |
| AlipayH5 | 支付宝H5支付 | ✘    | √    |
| AlipaySDK | 支付宝SDK支付 | √   | ✘   |
|             |                        |      |      |

## 支付状态

<a name="payStatus"></a>

| 状态码 | 说明     |
| ------ | -------- |
| 0      | 支付成功   |
| 1      | 待支付   |
| 2      | 支付失败 |

##  退款状态
<a name="refundStatus"></a>

| 状态码  | 说明 |
| ------ | -------- |
| 0 | 成功 |
| 1 | 处理中 |
| 2 | 失败 |
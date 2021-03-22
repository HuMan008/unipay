### 变更日志
1、微信支付升级到微信支付V3;
2、由于微信验签加签的方式变更，需要重新设置收款账号。



复制certs 下的文件到 certs 下去


-- 请注意-- 
```
暂不支持设置证书密码 默认密码为商户证书号。
```


// 
application.yml  增加redis密码 适配 redisson配置
```yaml
spring:
    redis:
      password: 

```
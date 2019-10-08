package cn.gotoil.unipay.utils;

import cn.gotoil.bill.tools.ObjectHelper;
import cn.gotoil.bill.tools.encoder.Hash;
import cn.gotoil.unipay.model.OrderNotifyBean;
import com.google.common.base.Charsets;

import java.util.Map;
import java.util.TreeMap;

/**
 * 我的加签工具
 *
 * @author think <syj247@qq.com>、
 * @date 2019-10-8、15:18
 */
public class UtilMySign {

    public static String sign(TreeMap<String, String> treeMap, String appKey) {
        //签名明文组装不包含sign字段
        if (treeMap.containsKey("sign")) {
            treeMap.remove("sign");
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : treeMap.entrySet()) {
            if (entry.getValue() != null && entry.getValue().length() > 0) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        String sign = Hash.md5(new String(sb.append("key=").append(appKey).toString().getBytes(Charsets.UTF_8)));
        //记得是md5编码的加签
        return sign;
    }


    @SuppressWarnings("unchecked")
    public static String sign(OrderNotifyBean orderNotifyBean, String appKey) {
        try {
            TreeMap<String, String> treeMap = new TreeMap(ObjectHelper.introspectStringValueMap(orderNotifyBean));
            //签名明文组装不包含sign字段
            if (treeMap.containsKey("sign")) {
                treeMap.remove("sign");
            }
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : treeMap.entrySet()) {
                if (entry.getValue() != null && entry.getValue().length() > 0) {
                    sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                }
            }
            String sign = Hash.md5(new String(sb.append("key=").append(appKey).toString().getBytes(Charsets.UTF_8)));
            //记得是md5编码的加签
            return sign;
        } catch (Exception e) {
            return "";
        }
    }

}

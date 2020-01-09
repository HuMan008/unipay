package cn.gotoil.unipay.utils;

import cn.gotoil.bill.tools.encoder.Hash;
import cn.gotoil.unipay.model.OrderNotifyBean;
import cn.gotoil.unipay.model.OrderRefundNotifyBean;
import com.google.common.base.Charsets;
import org.apache.commons.lang3.StringUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
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

    public static String makeSignStr(TreeMap<String, String> treeMap) {
        //签名明文组装不包含sign字段
        if (treeMap.containsKey("sign")) {
            treeMap.remove("sign");
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : treeMap.entrySet()) {
            if (entry.getValue() != null) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        if(sb.toString().endsWith("&")){
            return sb.substring(0,sb.length()-1);
        }
        //记得是md5编码的加签
        return sb.toString();
    }



    @SuppressWarnings("unchecked")
    public static String sign(OrderNotifyBean orderNotifyBean, String appKey) {
        try {
            TreeMap<String, String> treeMap = new TreeMap(introspectStringValueMapValueNotEmpty(orderNotifyBean));
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

    @SuppressWarnings("unchecked")
    public static String sign(OrderRefundNotifyBean orderNotifyBean, String appKey) {
        try {
            TreeMap<String, String> treeMap = new TreeMap(introspectStringValueMapValueNotEmpty(orderNotifyBean));
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


    public static Map<String, String> introspectStringValueMapValueNotEmpty(Object obj) throws Exception {
        Map<String, String> result = new HashMap<>();
        BeanInfo info = Introspector.getBeanInfo(obj.getClass());
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            Method reader = pd.getReadMethod();
            if (reader != null && !"class".equals(pd.getName())) {
                if (reader.invoke(obj) != null && StringUtils.isNotEmpty(reader.invoke(obj).toString())) {
                    result.put(pd.getName(), reader.invoke(obj).toString());
                }

            }
        }
        return result;
    }


}

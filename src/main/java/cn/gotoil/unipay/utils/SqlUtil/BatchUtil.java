package cn.gotoil.unipay.utils.SqlUtil;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BatchUtil {
    public synchronized static BatchEntity buildBatchEntity(String tbName, Class insertClazz, List values,
                                                            String primaryKey) {
        BatchEntity batch = new BatchEntity();
        batch.setTable(tbName);
        batch.setValues(values);

        Field[] fs = insertClazz.getDeclaredFields();
        StringBuffer cols = new StringBuffer();
        StringBuffer vals = new StringBuffer();
        for (Field field : fs) {
            //得到成员变量的名称
            String fieldName = field.getName();
            if (primaryKey != null) {
                if (fieldName.equals(primaryKey)) {
                    continue;
                }
            }
            if (cols.length() == 0) {
                cols.append(humpToLine(fieldName));
            } else {
                cols.append(",");
                cols.append(humpToLine(fieldName));
            }

            if (vals.length() == 0) {
                vals.append("#{item.");
                vals.append(fieldName);
                vals.append("}");
            } else {
                vals.append(",");
                vals.append("#{item.");
                vals.append(fieldName);
                vals.append("}");
            }

        }
        batch.setColName(cols.toString());
        batch.setValuesName(vals.toString());
        return batch;
    }

    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    public static String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}

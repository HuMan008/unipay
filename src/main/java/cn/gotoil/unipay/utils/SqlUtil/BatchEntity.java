package cn.gotoil.unipay.utils.SqlUtil;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BatchEntity implements Serializable {
    private String table;//表名
    private String colName;//列名称
    private String valuesName;//值名称
    private java.util.List values;//值
}

package cn.gotoil.unipay.web.message;

import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基础翻页控件
 *
 * @author think <syj247@qq.com>、
 * @date 2019-4-15、14:47
 */
public class BasePageResponse {


    @ApiModelProperty(required = true, example = "1", value = "当前页码", position = 10)
    private int pageNo = 0;


    @ApiModelProperty(required = true, example = "20", value = "每页显示N条", position = 15, readOnly = true)
    private int pageSize = 0;

    @ApiModelProperty(hidden = false, value = "总页数", position = 20)
    private int totalPage = 0;

    @ApiModelProperty(hidden = true, value = "记录总行数", position = 22)
    private int totalCount = 0;

    @ApiModelProperty(required = false, example = "{\"name\":\"admin\"}", value = "查询参数，name=admin", position = 25)
    private Map<String, Object> params;

    @ApiModelProperty(value = "返回结果", position = 25)
    private List<?> rows = new ArrayList<>();

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }

    public void setTotal(int total) {
        this.totalCount = total;
        setTotalPage(this.getTotalCount() % this.getPageSize() == 0 ?
                this.getTotalCount() / this.getPageSize() :
                this.getTotalCount() / this.getPageSize() + 1);
    }

    public Integer getOffset() {
        return (this.getPageNo() - 1) * this.getPageSize();
    }
}

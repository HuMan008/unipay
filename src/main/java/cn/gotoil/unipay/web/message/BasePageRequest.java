package cn.gotoil.unipay.web.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础翻页控件
 *
 * @author think <syj247@qq.com>、
 * @date 2019-4-15、14:47
 */
@ApiModel("分页请求")
public class BasePageRequest {

    // 默认分页条数
    public final static int DEFAULT_PAGE_SIZE = 20;
    // 默认当前页
    public final static int DEFAULT_PAGE = 1;

    @NotNull
    @Min(value = 1, message = "页码必须为一个正整数，最小值为1，最大值为总页数值")
    @Max(value = Integer.MAX_VALUE, message = "页码必须为一个正整数，最小值为1，最大值为总页数值")
    @ApiModelProperty(required = true, example = "1", value = "当前页码", position = 10)
    private int pageNo = DEFAULT_PAGE;


    // 每页条数

    @Min(value = 3, message = "页码必须为一个正整数，最小值为3，最大值为100")
    @Max(value = 100, message = "页码必须为一个正整数，最小值为1，最大值为100")
    @ApiModelProperty(required = true, example = "20", value = "每页显示N条", position = 15)
    private int pageSize = DEFAULT_PAGE_SIZE;

    // 总页数
    @ApiModelProperty(hidden = true)
    private int totalPage = 0;

    // 总记录数
    @ApiModelProperty(hidden = true)
    private int totalCount = 0;

    // 查询参数
    @ApiModelProperty(required = false, example = "{\"name\":\"admin\"}", value = "查询参数，name=admin", position = 25)
    private Map<String, Object> params = new HashMap<>();


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
}

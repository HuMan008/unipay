package cn.gotoil.unipay.model.entity;

import cn.gotoil.bill.model.BaseAdminUser;

public class AdminUser  extends BaseAdminUser {
    private String code;

    private String pwd;

    private String roleStr;

    private String permissionStr;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd == null ? null : pwd.trim();
    }

    @Override
    public String getRoleStr() {
        return roleStr;
    }
    @Override
    public void setRoleStr(String roleStr) {
        this.roleStr = roleStr == null ? null : roleStr.trim();
    }
    @Override
    public String getPermissionStr() {
        return permissionStr;
    }
    @Override
    public void setPermissionStr(String permissionStr) {
        this.permissionStr = permissionStr == null ? null : permissionStr.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", code=").append(code);
        sb.append(", pwd=").append(pwd);
        sb.append(", roleStr=").append(roleStr);
        sb.append(", permissionStr=").append(permissionStr);
        sb.append("]");
        return sb.toString();
    }
}
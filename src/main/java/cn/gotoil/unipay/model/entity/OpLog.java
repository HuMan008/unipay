package cn.gotoil.unipay.model.entity;

import java.util.Date;

public class OpLog {
    private Integer id;

    private String opUserName;

    private Byte opType;

    private String descp;

    private String callMethod;

    private String methodArgs;

    private String methodReturn;

    private Date createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOpUserName() {
        return opUserName;
    }

    public void setOpUserName(String opUserName) {
        this.opUserName = opUserName == null ? null : opUserName.trim();
    }

    public Byte getOpType() {
        return opType;
    }

    public void setOpType(Byte opType) {
        this.opType = opType;
    }

    public String getDescp() {
        return descp;
    }

    public void setDescp(String descp) {
        this.descp = descp == null ? null : descp.trim();
    }

    public String getCallMethod() {
        return callMethod;
    }

    public void setCallMethod(String callMethod) {
        this.callMethod = callMethod == null ? null : callMethod.trim();
    }

    public String getMethodArgs() {
        return methodArgs;
    }

    public void setMethodArgs(String methodArgs) {
        this.methodArgs = methodArgs == null ? null : methodArgs.trim();
    }

    public String getMethodReturn() {
        return methodReturn;
    }

    public void setMethodReturn(String methodReturn) {
        this.methodReturn = methodReturn == null ? null : methodReturn.trim();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", opUserName=").append(opUserName);
        sb.append(", opType=").append(opType);
        sb.append(", descp=").append(descp);
        sb.append(", callMethod=").append(callMethod);
        sb.append(", methodArgs=").append(methodArgs);
        sb.append(", methodReturn=").append(methodReturn);
        sb.append(", createdAt=").append(createdAt);
        sb.append("]");
        return sb.toString();
    }
}
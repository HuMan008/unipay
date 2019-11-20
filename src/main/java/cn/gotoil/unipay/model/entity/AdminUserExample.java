package cn.gotoil.unipay.model.entity;

import java.util.ArrayList;
import java.util.List;

public class AdminUserExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset;

    public AdminUserExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getOffset() {
        return offset;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andCodeIsNull() {
            addCriterion("code is null");
            return (Criteria) this;
        }

        public Criteria andCodeIsNotNull() {
            addCriterion("code is not null");
            return (Criteria) this;
        }

        public Criteria andCodeEqualTo(String value) {
            addCriterion("code =", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeNotEqualTo(String value) {
            addCriterion("code <>", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeGreaterThan(String value) {
            addCriterion("code >", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeGreaterThanOrEqualTo(String value) {
            addCriterion("code >=", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeLessThan(String value) {
            addCriterion("code <", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeLessThanOrEqualTo(String value) {
            addCriterion("code <=", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeLike(String value) {
            addCriterion("code like", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeNotLike(String value) {
            addCriterion("code not like", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeIn(List<String> values) {
            addCriterion("code in", values, "code");
            return (Criteria) this;
        }

        public Criteria andCodeNotIn(List<String> values) {
            addCriterion("code not in", values, "code");
            return (Criteria) this;
        }

        public Criteria andCodeBetween(String value1, String value2) {
            addCriterion("code between", value1, value2, "code");
            return (Criteria) this;
        }

        public Criteria andCodeNotBetween(String value1, String value2) {
            addCriterion("code not between", value1, value2, "code");
            return (Criteria) this;
        }

        public Criteria andPwdIsNull() {
            addCriterion("pwd is null");
            return (Criteria) this;
        }

        public Criteria andPwdIsNotNull() {
            addCriterion("pwd is not null");
            return (Criteria) this;
        }

        public Criteria andPwdEqualTo(String value) {
            addCriterion("pwd =", value, "pwd");
            return (Criteria) this;
        }

        public Criteria andPwdNotEqualTo(String value) {
            addCriterion("pwd <>", value, "pwd");
            return (Criteria) this;
        }

        public Criteria andPwdGreaterThan(String value) {
            addCriterion("pwd >", value, "pwd");
            return (Criteria) this;
        }

        public Criteria andPwdGreaterThanOrEqualTo(String value) {
            addCriterion("pwd >=", value, "pwd");
            return (Criteria) this;
        }

        public Criteria andPwdLessThan(String value) {
            addCriterion("pwd <", value, "pwd");
            return (Criteria) this;
        }

        public Criteria andPwdLessThanOrEqualTo(String value) {
            addCriterion("pwd <=", value, "pwd");
            return (Criteria) this;
        }

        public Criteria andPwdLike(String value) {
            addCriterion("pwd like", value, "pwd");
            return (Criteria) this;
        }

        public Criteria andPwdNotLike(String value) {
            addCriterion("pwd not like", value, "pwd");
            return (Criteria) this;
        }

        public Criteria andPwdIn(List<String> values) {
            addCriterion("pwd in", values, "pwd");
            return (Criteria) this;
        }

        public Criteria andPwdNotIn(List<String> values) {
            addCriterion("pwd not in", values, "pwd");
            return (Criteria) this;
        }

        public Criteria andPwdBetween(String value1, String value2) {
            addCriterion("pwd between", value1, value2, "pwd");
            return (Criteria) this;
        }

        public Criteria andPwdNotBetween(String value1, String value2) {
            addCriterion("pwd not between", value1, value2, "pwd");
            return (Criteria) this;
        }

        public Criteria andRoleStrIsNull() {
            addCriterion("role_str is null");
            return (Criteria) this;
        }

        public Criteria andRoleStrIsNotNull() {
            addCriterion("role_str is not null");
            return (Criteria) this;
        }

        public Criteria andRoleStrEqualTo(String value) {
            addCriterion("role_str =", value, "roleStr");
            return (Criteria) this;
        }

        public Criteria andRoleStrNotEqualTo(String value) {
            addCriterion("role_str <>", value, "roleStr");
            return (Criteria) this;
        }

        public Criteria andRoleStrGreaterThan(String value) {
            addCriterion("role_str >", value, "roleStr");
            return (Criteria) this;
        }

        public Criteria andRoleStrGreaterThanOrEqualTo(String value) {
            addCriterion("role_str >=", value, "roleStr");
            return (Criteria) this;
        }

        public Criteria andRoleStrLessThan(String value) {
            addCriterion("role_str <", value, "roleStr");
            return (Criteria) this;
        }

        public Criteria andRoleStrLessThanOrEqualTo(String value) {
            addCriterion("role_str <=", value, "roleStr");
            return (Criteria) this;
        }

        public Criteria andRoleStrLike(String value) {
            addCriterion("role_str like", value, "roleStr");
            return (Criteria) this;
        }

        public Criteria andRoleStrNotLike(String value) {
            addCriterion("role_str not like", value, "roleStr");
            return (Criteria) this;
        }

        public Criteria andRoleStrIn(List<String> values) {
            addCriterion("role_str in", values, "roleStr");
            return (Criteria) this;
        }

        public Criteria andRoleStrNotIn(List<String> values) {
            addCriterion("role_str not in", values, "roleStr");
            return (Criteria) this;
        }

        public Criteria andRoleStrBetween(String value1, String value2) {
            addCriterion("role_str between", value1, value2, "roleStr");
            return (Criteria) this;
        }

        public Criteria andRoleStrNotBetween(String value1, String value2) {
            addCriterion("role_str not between", value1, value2, "roleStr");
            return (Criteria) this;
        }

        public Criteria andPermissionStrIsNull() {
            addCriterion("permission_str is null");
            return (Criteria) this;
        }

        public Criteria andPermissionStrIsNotNull() {
            addCriterion("permission_str is not null");
            return (Criteria) this;
        }

        public Criteria andPermissionStrEqualTo(String value) {
            addCriterion("permission_str =", value, "permissionStr");
            return (Criteria) this;
        }

        public Criteria andPermissionStrNotEqualTo(String value) {
            addCriterion("permission_str <>", value, "permissionStr");
            return (Criteria) this;
        }

        public Criteria andPermissionStrGreaterThan(String value) {
            addCriterion("permission_str >", value, "permissionStr");
            return (Criteria) this;
        }

        public Criteria andPermissionStrGreaterThanOrEqualTo(String value) {
            addCriterion("permission_str >=", value, "permissionStr");
            return (Criteria) this;
        }

        public Criteria andPermissionStrLessThan(String value) {
            addCriterion("permission_str <", value, "permissionStr");
            return (Criteria) this;
        }

        public Criteria andPermissionStrLessThanOrEqualTo(String value) {
            addCriterion("permission_str <=", value, "permissionStr");
            return (Criteria) this;
        }

        public Criteria andPermissionStrLike(String value) {
            addCriterion("permission_str like", value, "permissionStr");
            return (Criteria) this;
        }

        public Criteria andPermissionStrNotLike(String value) {
            addCriterion("permission_str not like", value, "permissionStr");
            return (Criteria) this;
        }

        public Criteria andPermissionStrIn(List<String> values) {
            addCriterion("permission_str in", values, "permissionStr");
            return (Criteria) this;
        }

        public Criteria andPermissionStrNotIn(List<String> values) {
            addCriterion("permission_str not in", values, "permissionStr");
            return (Criteria) this;
        }

        public Criteria andPermissionStrBetween(String value1, String value2) {
            addCriterion("permission_str between", value1, value2, "permissionStr");
            return (Criteria) this;
        }

        public Criteria andPermissionStrNotBetween(String value1, String value2) {
            addCriterion("permission_str not between", value1, value2, "permissionStr");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}
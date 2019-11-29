package cn.gotoil.unipay.model.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset;

    public AppExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
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

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
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

        public Criteria andAppKeyIsNull() {
            addCriterion("app_key is null");
            return (Criteria) this;
        }

        public Criteria andAppKeyIsNotNull() {
            addCriterion("app_key is not null");
            return (Criteria) this;
        }

        public Criteria andAppKeyEqualTo(String value) {
            addCriterion("app_key =", value, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyNotEqualTo(String value) {
            addCriterion("app_key <>", value, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyGreaterThan(String value) {
            addCriterion("app_key >", value, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyGreaterThanOrEqualTo(String value) {
            addCriterion("app_key >=", value, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyLessThan(String value) {
            addCriterion("app_key <", value, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyLessThanOrEqualTo(String value) {
            addCriterion("app_key <=", value, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyLike(String value) {
            addCriterion("app_key like", value, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyNotLike(String value) {
            addCriterion("app_key not like", value, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyIn(List<String> values) {
            addCriterion("app_key in", values, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyNotIn(List<String> values) {
            addCriterion("app_key not in", values, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyBetween(String value1, String value2) {
            addCriterion("app_key between", value1, value2, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppKeyNotBetween(String value1, String value2) {
            addCriterion("app_key not between", value1, value2, "appKey");
            return (Criteria) this;
        }

        public Criteria andAppNameIsNull() {
            addCriterion("app_name is null");
            return (Criteria) this;
        }

        public Criteria andAppNameIsNotNull() {
            addCriterion("app_name is not null");
            return (Criteria) this;
        }

        public Criteria andAppNameEqualTo(String value) {
            addCriterion("app_name =", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameNotEqualTo(String value) {
            addCriterion("app_name <>", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameGreaterThan(String value) {
            addCriterion("app_name >", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameGreaterThanOrEqualTo(String value) {
            addCriterion("app_name >=", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameLessThan(String value) {
            addCriterion("app_name <", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameLessThanOrEqualTo(String value) {
            addCriterion("app_name <=", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameLike(String value) {
            addCriterion("app_name like", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameNotLike(String value) {
            addCriterion("app_name not like", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameIn(List<String> values) {
            addCriterion("app_name in", values, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameNotIn(List<String> values) {
            addCriterion("app_name not in", values, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameBetween(String value1, String value2) {
            addCriterion("app_name between", value1, value2, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameNotBetween(String value1, String value2) {
            addCriterion("app_name not between", value1, value2, "appName");
            return (Criteria) this;
        }

        public Criteria andAppSecretIsNull() {
            addCriterion("app_secret is null");
            return (Criteria) this;
        }

        public Criteria andAppSecretIsNotNull() {
            addCriterion("app_secret is not null");
            return (Criteria) this;
        }

        public Criteria andAppSecretEqualTo(String value) {
            addCriterion("app_secret =", value, "appSecret");
            return (Criteria) this;
        }

        public Criteria andAppSecretNotEqualTo(String value) {
            addCriterion("app_secret <>", value, "appSecret");
            return (Criteria) this;
        }

        public Criteria andAppSecretGreaterThan(String value) {
            addCriterion("app_secret >", value, "appSecret");
            return (Criteria) this;
        }

        public Criteria andAppSecretGreaterThanOrEqualTo(String value) {
            addCriterion("app_secret >=", value, "appSecret");
            return (Criteria) this;
        }

        public Criteria andAppSecretLessThan(String value) {
            addCriterion("app_secret <", value, "appSecret");
            return (Criteria) this;
        }

        public Criteria andAppSecretLessThanOrEqualTo(String value) {
            addCriterion("app_secret <=", value, "appSecret");
            return (Criteria) this;
        }

        public Criteria andAppSecretLike(String value) {
            addCriterion("app_secret like", value, "appSecret");
            return (Criteria) this;
        }

        public Criteria andAppSecretNotLike(String value) {
            addCriterion("app_secret not like", value, "appSecret");
            return (Criteria) this;
        }

        public Criteria andAppSecretIn(List<String> values) {
            addCriterion("app_secret in", values, "appSecret");
            return (Criteria) this;
        }

        public Criteria andAppSecretNotIn(List<String> values) {
            addCriterion("app_secret not in", values, "appSecret");
            return (Criteria) this;
        }

        public Criteria andAppSecretBetween(String value1, String value2) {
            addCriterion("app_secret between", value1, value2, "appSecret");
            return (Criteria) this;
        }

        public Criteria andAppSecretNotBetween(String value1, String value2) {
            addCriterion("app_secret not between", value1, value2, "appSecret");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Byte value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Byte value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Byte value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Byte value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Byte value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Byte> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Byte> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Byte value1, Byte value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNull() {
            addCriterion("remark is null");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNotNull() {
            addCriterion("remark is not null");
            return (Criteria) this;
        }

        public Criteria andRemarkEqualTo(String value) {
            addCriterion("remark =", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotEqualTo(String value) {
            addCriterion("remark <>", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThan(String value) {
            addCriterion("remark >", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("remark >=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThan(String value) {
            addCriterion("remark <", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThanOrEqualTo(String value) {
            addCriterion("remark <=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLike(String value) {
            addCriterion("remark like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotLike(String value) {
            addCriterion("remark not like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkIn(List<String> values) {
            addCriterion("remark in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotIn(List<String> values) {
            addCriterion("remark not in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkBetween(String value1, String value2) {
            addCriterion("remark between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotBetween(String value1, String value2) {
            addCriterion("remark not between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andSyncUrlIsNull() {
            addCriterion("sync_url is null");
            return (Criteria) this;
        }

        public Criteria andSyncUrlIsNotNull() {
            addCriterion("sync_url is not null");
            return (Criteria) this;
        }

        public Criteria andSyncUrlEqualTo(String value) {
            addCriterion("sync_url =", value, "syncUrl");
            return (Criteria) this;
        }

        public Criteria andSyncUrlNotEqualTo(String value) {
            addCriterion("sync_url <>", value, "syncUrl");
            return (Criteria) this;
        }

        public Criteria andSyncUrlGreaterThan(String value) {
            addCriterion("sync_url >", value, "syncUrl");
            return (Criteria) this;
        }

        public Criteria andSyncUrlGreaterThanOrEqualTo(String value) {
            addCriterion("sync_url >=", value, "syncUrl");
            return (Criteria) this;
        }

        public Criteria andSyncUrlLessThan(String value) {
            addCriterion("sync_url <", value, "syncUrl");
            return (Criteria) this;
        }

        public Criteria andSyncUrlLessThanOrEqualTo(String value) {
            addCriterion("sync_url <=", value, "syncUrl");
            return (Criteria) this;
        }

        public Criteria andSyncUrlLike(String value) {
            addCriterion("sync_url like", value, "syncUrl");
            return (Criteria) this;
        }

        public Criteria andSyncUrlNotLike(String value) {
            addCriterion("sync_url not like", value, "syncUrl");
            return (Criteria) this;
        }

        public Criteria andSyncUrlIn(List<String> values) {
            addCriterion("sync_url in", values, "syncUrl");
            return (Criteria) this;
        }

        public Criteria andSyncUrlNotIn(List<String> values) {
            addCriterion("sync_url not in", values, "syncUrl");
            return (Criteria) this;
        }

        public Criteria andSyncUrlBetween(String value1, String value2) {
            addCriterion("sync_url between", value1, value2, "syncUrl");
            return (Criteria) this;
        }

        public Criteria andSyncUrlNotBetween(String value1, String value2) {
            addCriterion("sync_url not between", value1, value2, "syncUrl");
            return (Criteria) this;
        }

        public Criteria andAsyncUrlIsNull() {
            addCriterion("async_url is null");
            return (Criteria) this;
        }

        public Criteria andAsyncUrlIsNotNull() {
            addCriterion("async_url is not null");
            return (Criteria) this;
        }

        public Criteria andAsyncUrlEqualTo(String value) {
            addCriterion("async_url =", value, "asyncUrl");
            return (Criteria) this;
        }

        public Criteria andAsyncUrlNotEqualTo(String value) {
            addCriterion("async_url <>", value, "asyncUrl");
            return (Criteria) this;
        }

        public Criteria andAsyncUrlGreaterThan(String value) {
            addCriterion("async_url >", value, "asyncUrl");
            return (Criteria) this;
        }

        public Criteria andAsyncUrlGreaterThanOrEqualTo(String value) {
            addCriterion("async_url >=", value, "asyncUrl");
            return (Criteria) this;
        }

        public Criteria andAsyncUrlLessThan(String value) {
            addCriterion("async_url <", value, "asyncUrl");
            return (Criteria) this;
        }

        public Criteria andAsyncUrlLessThanOrEqualTo(String value) {
            addCriterion("async_url <=", value, "asyncUrl");
            return (Criteria) this;
        }

        public Criteria andAsyncUrlLike(String value) {
            addCriterion("async_url like", value, "asyncUrl");
            return (Criteria) this;
        }

        public Criteria andAsyncUrlNotLike(String value) {
            addCriterion("async_url not like", value, "asyncUrl");
            return (Criteria) this;
        }

        public Criteria andAsyncUrlIn(List<String> values) {
            addCriterion("async_url in", values, "asyncUrl");
            return (Criteria) this;
        }

        public Criteria andAsyncUrlNotIn(List<String> values) {
            addCriterion("async_url not in", values, "asyncUrl");
            return (Criteria) this;
        }

        public Criteria andAsyncUrlBetween(String value1, String value2) {
            addCriterion("async_url between", value1, value2, "asyncUrl");
            return (Criteria) this;
        }

        public Criteria andAsyncUrlNotBetween(String value1, String value2) {
            addCriterion("async_url not between", value1, value2, "asyncUrl");
            return (Criteria) this;
        }

        public Criteria andOrderHeaderIsNull() {
            addCriterion("order_header is null");
            return (Criteria) this;
        }

        public Criteria andOrderHeaderIsNotNull() {
            addCriterion("order_header is not null");
            return (Criteria) this;
        }

        public Criteria andOrderHeaderEqualTo(String value) {
            addCriterion("order_header =", value, "orderHeader");
            return (Criteria) this;
        }

        public Criteria andOrderHeaderNotEqualTo(String value) {
            addCriterion("order_header <>", value, "orderHeader");
            return (Criteria) this;
        }

        public Criteria andOrderHeaderGreaterThan(String value) {
            addCriterion("order_header >", value, "orderHeader");
            return (Criteria) this;
        }

        public Criteria andOrderHeaderGreaterThanOrEqualTo(String value) {
            addCriterion("order_header >=", value, "orderHeader");
            return (Criteria) this;
        }

        public Criteria andOrderHeaderLessThan(String value) {
            addCriterion("order_header <", value, "orderHeader");
            return (Criteria) this;
        }

        public Criteria andOrderHeaderLessThanOrEqualTo(String value) {
            addCriterion("order_header <=", value, "orderHeader");
            return (Criteria) this;
        }

        public Criteria andOrderHeaderLike(String value) {
            addCriterion("order_header like", value, "orderHeader");
            return (Criteria) this;
        }

        public Criteria andOrderHeaderNotLike(String value) {
            addCriterion("order_header not like", value, "orderHeader");
            return (Criteria) this;
        }

        public Criteria andOrderHeaderIn(List<String> values) {
            addCriterion("order_header in", values, "orderHeader");
            return (Criteria) this;
        }

        public Criteria andOrderHeaderNotIn(List<String> values) {
            addCriterion("order_header not in", values, "orderHeader");
            return (Criteria) this;
        }

        public Criteria andOrderHeaderBetween(String value1, String value2) {
            addCriterion("order_header between", value1, value2, "orderHeader");
            return (Criteria) this;
        }

        public Criteria andOrderHeaderNotBetween(String value1, String value2) {
            addCriterion("order_header not between", value1, value2, "orderHeader");
            return (Criteria) this;
        }

        public Criteria andOrderDescpIsNull() {
            addCriterion("order_descp is null");
            return (Criteria) this;
        }

        public Criteria andOrderDescpIsNotNull() {
            addCriterion("order_descp is not null");
            return (Criteria) this;
        }

        public Criteria andOrderDescpEqualTo(String value) {
            addCriterion("order_descp =", value, "orderDescp");
            return (Criteria) this;
        }

        public Criteria andOrderDescpNotEqualTo(String value) {
            addCriterion("order_descp <>", value, "orderDescp");
            return (Criteria) this;
        }

        public Criteria andOrderDescpGreaterThan(String value) {
            addCriterion("order_descp >", value, "orderDescp");
            return (Criteria) this;
        }

        public Criteria andOrderDescpGreaterThanOrEqualTo(String value) {
            addCriterion("order_descp >=", value, "orderDescp");
            return (Criteria) this;
        }

        public Criteria andOrderDescpLessThan(String value) {
            addCriterion("order_descp <", value, "orderDescp");
            return (Criteria) this;
        }

        public Criteria andOrderDescpLessThanOrEqualTo(String value) {
            addCriterion("order_descp <=", value, "orderDescp");
            return (Criteria) this;
        }

        public Criteria andOrderDescpLike(String value) {
            addCriterion("order_descp like", value, "orderDescp");
            return (Criteria) this;
        }

        public Criteria andOrderDescpNotLike(String value) {
            addCriterion("order_descp not like", value, "orderDescp");
            return (Criteria) this;
        }

        public Criteria andOrderDescpIn(List<String> values) {
            addCriterion("order_descp in", values, "orderDescp");
            return (Criteria) this;
        }

        public Criteria andOrderDescpNotIn(List<String> values) {
            addCriterion("order_descp not in", values, "orderDescp");
            return (Criteria) this;
        }

        public Criteria andOrderDescpBetween(String value1, String value2) {
            addCriterion("order_descp between", value1, value2, "orderDescp");
            return (Criteria) this;
        }

        public Criteria andOrderDescpNotBetween(String value1, String value2) {
            addCriterion("order_descp not between", value1, value2, "orderDescp");
            return (Criteria) this;
        }

        public Criteria andDefaultOrderExpiredTimeIsNull() {
            addCriterion("default_order_expired_time is null");
            return (Criteria) this;
        }

        public Criteria andDefaultOrderExpiredTimeIsNotNull() {
            addCriterion("default_order_expired_time is not null");
            return (Criteria) this;
        }

        public Criteria andDefaultOrderExpiredTimeEqualTo(Integer value) {
            addCriterion("default_order_expired_time =", value, "defaultOrderExpiredTime");
            return (Criteria) this;
        }

        public Criteria andDefaultOrderExpiredTimeNotEqualTo(Integer value) {
            addCriterion("default_order_expired_time <>", value, "defaultOrderExpiredTime");
            return (Criteria) this;
        }

        public Criteria andDefaultOrderExpiredTimeGreaterThan(Integer value) {
            addCriterion("default_order_expired_time >", value, "defaultOrderExpiredTime");
            return (Criteria) this;
        }

        public Criteria andDefaultOrderExpiredTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("default_order_expired_time >=", value, "defaultOrderExpiredTime");
            return (Criteria) this;
        }

        public Criteria andDefaultOrderExpiredTimeLessThan(Integer value) {
            addCriterion("default_order_expired_time <", value, "defaultOrderExpiredTime");
            return (Criteria) this;
        }

        public Criteria andDefaultOrderExpiredTimeLessThanOrEqualTo(Integer value) {
            addCriterion("default_order_expired_time <=", value, "defaultOrderExpiredTime");
            return (Criteria) this;
        }

        public Criteria andDefaultOrderExpiredTimeIn(List<Integer> values) {
            addCriterion("default_order_expired_time in", values, "defaultOrderExpiredTime");
            return (Criteria) this;
        }

        public Criteria andDefaultOrderExpiredTimeNotIn(List<Integer> values) {
            addCriterion("default_order_expired_time not in", values, "defaultOrderExpiredTime");
            return (Criteria) this;
        }

        public Criteria andDefaultOrderExpiredTimeBetween(Integer value1, Integer value2) {
            addCriterion("default_order_expired_time between", value1, value2, "defaultOrderExpiredTime");
            return (Criteria) this;
        }

        public Criteria andDefaultOrderExpiredTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("default_order_expired_time not between", value1, value2, "defaultOrderExpiredTime");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIsNull() {
            addCriterion("created_at is null");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIsNotNull() {
            addCriterion("created_at is not null");
            return (Criteria) this;
        }

        public Criteria andCreatedAtEqualTo(Date value) {
            addCriterion("created_at =", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotEqualTo(Date value) {
            addCriterion("created_at <>", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtGreaterThan(Date value) {
            addCriterion("created_at >", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtGreaterThanOrEqualTo(Date value) {
            addCriterion("created_at >=", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtLessThan(Date value) {
            addCriterion("created_at <", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtLessThanOrEqualTo(Date value) {
            addCriterion("created_at <=", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIn(List<Date> values) {
            addCriterion("created_at in", values, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotIn(List<Date> values) {
            addCriterion("created_at not in", values, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtBetween(Date value1, Date value2) {
            addCriterion("created_at between", value1, value2, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotBetween(Date value1, Date value2) {
            addCriterion("created_at not between", value1, value2, "createdAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIsNull() {
            addCriterion("updated_at is null");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIsNotNull() {
            addCriterion("updated_at is not null");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtEqualTo(Date value) {
            addCriterion("updated_at =", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotEqualTo(Date value) {
            addCriterion("updated_at <>", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtGreaterThan(Date value) {
            addCriterion("updated_at >", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtGreaterThanOrEqualTo(Date value) {
            addCriterion("updated_at >=", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtLessThan(Date value) {
            addCriterion("updated_at <", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtLessThanOrEqualTo(Date value) {
            addCriterion("updated_at <=", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIn(List<Date> values) {
            addCriterion("updated_at in", values, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotIn(List<Date> values) {
            addCriterion("updated_at not in", values, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtBetween(Date value1, Date value2) {
            addCriterion("updated_at between", value1, value2, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotBetween(Date value1, Date value2) {
            addCriterion("updated_at not between", value1, value2, "updatedAt");
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
    }
}
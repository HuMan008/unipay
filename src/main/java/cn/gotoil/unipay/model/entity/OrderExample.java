package cn.gotoil.unipay.model.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset;

    public OrderExample() {
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

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(String value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(String value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(String value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(String value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(String value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(String value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLike(String value) {
            addCriterion("id like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotLike(String value) {
            addCriterion("id not like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<String> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<String> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(String value1, String value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(String value1, String value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andAppIdIsNull() {
            addCriterion("app_id is null");
            return (Criteria) this;
        }

        public Criteria andAppIdIsNotNull() {
            addCriterion("app_id is not null");
            return (Criteria) this;
        }

        public Criteria andAppIdEqualTo(String value) {
            addCriterion("app_id =", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotEqualTo(String value) {
            addCriterion("app_id <>", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdGreaterThan(String value) {
            addCriterion("app_id >", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdGreaterThanOrEqualTo(String value) {
            addCriterion("app_id >=", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLessThan(String value) {
            addCriterion("app_id <", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLessThanOrEqualTo(String value) {
            addCriterion("app_id <=", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLike(String value) {
            addCriterion("app_id like", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotLike(String value) {
            addCriterion("app_id not like", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdIn(List<String> values) {
            addCriterion("app_id in", values, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotIn(List<String> values) {
            addCriterion("app_id not in", values, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdBetween(String value1, String value2) {
            addCriterion("app_id between", value1, value2, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotBetween(String value1, String value2) {
            addCriterion("app_id not between", value1, value2, "appId");
            return (Criteria) this;
        }

        public Criteria andAppOrderNoIsNull() {
            addCriterion("app_order_no is null");
            return (Criteria) this;
        }

        public Criteria andAppOrderNoIsNotNull() {
            addCriterion("app_order_no is not null");
            return (Criteria) this;
        }

        public Criteria andAppOrderNoEqualTo(String value) {
            addCriterion("app_order_no =", value, "appOrderNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderNoNotEqualTo(String value) {
            addCriterion("app_order_no <>", value, "appOrderNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderNoGreaterThan(String value) {
            addCriterion("app_order_no >", value, "appOrderNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderNoGreaterThanOrEqualTo(String value) {
            addCriterion("app_order_no >=", value, "appOrderNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderNoLessThan(String value) {
            addCriterion("app_order_no <", value, "appOrderNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderNoLessThanOrEqualTo(String value) {
            addCriterion("app_order_no <=", value, "appOrderNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderNoLike(String value) {
            addCriterion("app_order_no like", value, "appOrderNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderNoNotLike(String value) {
            addCriterion("app_order_no not like", value, "appOrderNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderNoIn(List<String> values) {
            addCriterion("app_order_no in", values, "appOrderNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderNoNotIn(List<String> values) {
            addCriterion("app_order_no not in", values, "appOrderNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderNoBetween(String value1, String value2) {
            addCriterion("app_order_no between", value1, value2, "appOrderNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderNoNotBetween(String value1, String value2) {
            addCriterion("app_order_no not between", value1, value2, "appOrderNo");
            return (Criteria) this;
        }

        public Criteria andAppUserIdIsNull() {
            addCriterion("app_user_id is null");
            return (Criteria) this;
        }

        public Criteria andAppUserIdIsNotNull() {
            addCriterion("app_user_id is not null");
            return (Criteria) this;
        }

        public Criteria andAppUserIdEqualTo(String value) {
            addCriterion("app_user_id =", value, "appUserId");
            return (Criteria) this;
        }

        public Criteria andAppUserIdNotEqualTo(String value) {
            addCriterion("app_user_id <>", value, "appUserId");
            return (Criteria) this;
        }

        public Criteria andAppUserIdGreaterThan(String value) {
            addCriterion("app_user_id >", value, "appUserId");
            return (Criteria) this;
        }

        public Criteria andAppUserIdGreaterThanOrEqualTo(String value) {
            addCriterion("app_user_id >=", value, "appUserId");
            return (Criteria) this;
        }

        public Criteria andAppUserIdLessThan(String value) {
            addCriterion("app_user_id <", value, "appUserId");
            return (Criteria) this;
        }

        public Criteria andAppUserIdLessThanOrEqualTo(String value) {
            addCriterion("app_user_id <=", value, "appUserId");
            return (Criteria) this;
        }

        public Criteria andAppUserIdLike(String value) {
            addCriterion("app_user_id like", value, "appUserId");
            return (Criteria) this;
        }

        public Criteria andAppUserIdNotLike(String value) {
            addCriterion("app_user_id not like", value, "appUserId");
            return (Criteria) this;
        }

        public Criteria andAppUserIdIn(List<String> values) {
            addCriterion("app_user_id in", values, "appUserId");
            return (Criteria) this;
        }

        public Criteria andAppUserIdNotIn(List<String> values) {
            addCriterion("app_user_id not in", values, "appUserId");
            return (Criteria) this;
        }

        public Criteria andAppUserIdBetween(String value1, String value2) {
            addCriterion("app_user_id between", value1, value2, "appUserId");
            return (Criteria) this;
        }

        public Criteria andAppUserIdNotBetween(String value1, String value2) {
            addCriterion("app_user_id not between", value1, value2, "appUserId");
            return (Criteria) this;
        }

        public Criteria andPaymentIdIsNull() {
            addCriterion("payment_id is null");
            return (Criteria) this;
        }

        public Criteria andPaymentIdIsNotNull() {
            addCriterion("payment_id is not null");
            return (Criteria) this;
        }

        public Criteria andPaymentIdEqualTo(String value) {
            addCriterion("payment_id =", value, "paymentId");
            return (Criteria) this;
        }

        public Criteria andPaymentIdNotEqualTo(String value) {
            addCriterion("payment_id <>", value, "paymentId");
            return (Criteria) this;
        }

        public Criteria andPaymentIdGreaterThan(String value) {
            addCriterion("payment_id >", value, "paymentId");
            return (Criteria) this;
        }

        public Criteria andPaymentIdGreaterThanOrEqualTo(String value) {
            addCriterion("payment_id >=", value, "paymentId");
            return (Criteria) this;
        }

        public Criteria andPaymentIdLessThan(String value) {
            addCriterion("payment_id <", value, "paymentId");
            return (Criteria) this;
        }

        public Criteria andPaymentIdLessThanOrEqualTo(String value) {
            addCriterion("payment_id <=", value, "paymentId");
            return (Criteria) this;
        }

        public Criteria andPaymentIdLike(String value) {
            addCriterion("payment_id like", value, "paymentId");
            return (Criteria) this;
        }

        public Criteria andPaymentIdNotLike(String value) {
            addCriterion("payment_id not like", value, "paymentId");
            return (Criteria) this;
        }

        public Criteria andPaymentIdIn(List<String> values) {
            addCriterion("payment_id in", values, "paymentId");
            return (Criteria) this;
        }

        public Criteria andPaymentIdNotIn(List<String> values) {
            addCriterion("payment_id not in", values, "paymentId");
            return (Criteria) this;
        }

        public Criteria andPaymentIdBetween(String value1, String value2) {
            addCriterion("payment_id between", value1, value2, "paymentId");
            return (Criteria) this;
        }

        public Criteria andPaymentIdNotBetween(String value1, String value2) {
            addCriterion("payment_id not between", value1, value2, "paymentId");
            return (Criteria) this;
        }

        public Criteria andPaymentUidIsNull() {
            addCriterion("payment_uid is null");
            return (Criteria) this;
        }

        public Criteria andPaymentUidIsNotNull() {
            addCriterion("payment_uid is not null");
            return (Criteria) this;
        }

        public Criteria andPaymentUidEqualTo(String value) {
            addCriterion("payment_uid =", value, "paymentUid");
            return (Criteria) this;
        }

        public Criteria andPaymentUidNotEqualTo(String value) {
            addCriterion("payment_uid <>", value, "paymentUid");
            return (Criteria) this;
        }

        public Criteria andPaymentUidGreaterThan(String value) {
            addCriterion("payment_uid >", value, "paymentUid");
            return (Criteria) this;
        }

        public Criteria andPaymentUidGreaterThanOrEqualTo(String value) {
            addCriterion("payment_uid >=", value, "paymentUid");
            return (Criteria) this;
        }

        public Criteria andPaymentUidLessThan(String value) {
            addCriterion("payment_uid <", value, "paymentUid");
            return (Criteria) this;
        }

        public Criteria andPaymentUidLessThanOrEqualTo(String value) {
            addCriterion("payment_uid <=", value, "paymentUid");
            return (Criteria) this;
        }

        public Criteria andPaymentUidLike(String value) {
            addCriterion("payment_uid like", value, "paymentUid");
            return (Criteria) this;
        }

        public Criteria andPaymentUidNotLike(String value) {
            addCriterion("payment_uid not like", value, "paymentUid");
            return (Criteria) this;
        }

        public Criteria andPaymentUidIn(List<String> values) {
            addCriterion("payment_uid in", values, "paymentUid");
            return (Criteria) this;
        }

        public Criteria andPaymentUidNotIn(List<String> values) {
            addCriterion("payment_uid not in", values, "paymentUid");
            return (Criteria) this;
        }

        public Criteria andPaymentUidBetween(String value1, String value2) {
            addCriterion("payment_uid between", value1, value2, "paymentUid");
            return (Criteria) this;
        }

        public Criteria andPaymentUidNotBetween(String value1, String value2) {
            addCriterion("payment_uid not between", value1, value2, "paymentUid");
            return (Criteria) this;
        }

        public Criteria andExpiredTimeMinuteIsNull() {
            addCriterion("expired_time_minute is null");
            return (Criteria) this;
        }

        public Criteria andExpiredTimeMinuteIsNotNull() {
            addCriterion("expired_time_minute is not null");
            return (Criteria) this;
        }

        public Criteria andExpiredTimeMinuteEqualTo(Integer value) {
            addCriterion("expired_time_minute =", value, "expiredTimeMinute");
            return (Criteria) this;
        }

        public Criteria andExpiredTimeMinuteNotEqualTo(Integer value) {
            addCriterion("expired_time_minute <>", value, "expiredTimeMinute");
            return (Criteria) this;
        }

        public Criteria andExpiredTimeMinuteGreaterThan(Integer value) {
            addCriterion("expired_time_minute >", value, "expiredTimeMinute");
            return (Criteria) this;
        }

        public Criteria andExpiredTimeMinuteGreaterThanOrEqualTo(Integer value) {
            addCriterion("expired_time_minute >=", value, "expiredTimeMinute");
            return (Criteria) this;
        }

        public Criteria andExpiredTimeMinuteLessThan(Integer value) {
            addCriterion("expired_time_minute <", value, "expiredTimeMinute");
            return (Criteria) this;
        }

        public Criteria andExpiredTimeMinuteLessThanOrEqualTo(Integer value) {
            addCriterion("expired_time_minute <=", value, "expiredTimeMinute");
            return (Criteria) this;
        }

        public Criteria andExpiredTimeMinuteIn(List<Integer> values) {
            addCriterion("expired_time_minute in", values, "expiredTimeMinute");
            return (Criteria) this;
        }

        public Criteria andExpiredTimeMinuteNotIn(List<Integer> values) {
            addCriterion("expired_time_minute not in", values, "expiredTimeMinute");
            return (Criteria) this;
        }

        public Criteria andExpiredTimeMinuteBetween(Integer value1, Integer value2) {
            addCriterion("expired_time_minute between", value1, value2, "expiredTimeMinute");
            return (Criteria) this;
        }

        public Criteria andExpiredTimeMinuteNotBetween(Integer value1, Integer value2) {
            addCriterion("expired_time_minute not between", value1, value2, "expiredTimeMinute");
            return (Criteria) this;
        }

        public Criteria andFeeIsNull() {
            addCriterion("fee is null");
            return (Criteria) this;
        }

        public Criteria andFeeIsNotNull() {
            addCriterion("fee is not null");
            return (Criteria) this;
        }

        public Criteria andFeeEqualTo(Integer value) {
            addCriterion("fee =", value, "fee");
            return (Criteria) this;
        }

        public Criteria andFeeNotEqualTo(Integer value) {
            addCriterion("fee <>", value, "fee");
            return (Criteria) this;
        }

        public Criteria andFeeGreaterThan(Integer value) {
            addCriterion("fee >", value, "fee");
            return (Criteria) this;
        }

        public Criteria andFeeGreaterThanOrEqualTo(Integer value) {
            addCriterion("fee >=", value, "fee");
            return (Criteria) this;
        }

        public Criteria andFeeLessThan(Integer value) {
            addCriterion("fee <", value, "fee");
            return (Criteria) this;
        }

        public Criteria andFeeLessThanOrEqualTo(Integer value) {
            addCriterion("fee <=", value, "fee");
            return (Criteria) this;
        }

        public Criteria andFeeIn(List<Integer> values) {
            addCriterion("fee in", values, "fee");
            return (Criteria) this;
        }

        public Criteria andFeeNotIn(List<Integer> values) {
            addCriterion("fee not in", values, "fee");
            return (Criteria) this;
        }

        public Criteria andFeeBetween(Integer value1, Integer value2) {
            addCriterion("fee between", value1, value2, "fee");
            return (Criteria) this;
        }

        public Criteria andFeeNotBetween(Integer value1, Integer value2) {
            addCriterion("fee not between", value1, value2, "fee");
            return (Criteria) this;
        }

        public Criteria andSubjectsIsNull() {
            addCriterion("subjects is null");
            return (Criteria) this;
        }

        public Criteria andSubjectsIsNotNull() {
            addCriterion("subjects is not null");
            return (Criteria) this;
        }

        public Criteria andSubjectsEqualTo(String value) {
            addCriterion("subjects =", value, "subjects");
            return (Criteria) this;
        }

        public Criteria andSubjectsNotEqualTo(String value) {
            addCriterion("subjects <>", value, "subjects");
            return (Criteria) this;
        }

        public Criteria andSubjectsGreaterThan(String value) {
            addCriterion("subjects >", value, "subjects");
            return (Criteria) this;
        }

        public Criteria andSubjectsGreaterThanOrEqualTo(String value) {
            addCriterion("subjects >=", value, "subjects");
            return (Criteria) this;
        }

        public Criteria andSubjectsLessThan(String value) {
            addCriterion("subjects <", value, "subjects");
            return (Criteria) this;
        }

        public Criteria andSubjectsLessThanOrEqualTo(String value) {
            addCriterion("subjects <=", value, "subjects");
            return (Criteria) this;
        }

        public Criteria andSubjectsLike(String value) {
            addCriterion("subjects like", value, "subjects");
            return (Criteria) this;
        }

        public Criteria andSubjectsNotLike(String value) {
            addCriterion("subjects not like", value, "subjects");
            return (Criteria) this;
        }

        public Criteria andSubjectsIn(List<String> values) {
            addCriterion("subjects in", values, "subjects");
            return (Criteria) this;
        }

        public Criteria andSubjectsNotIn(List<String> values) {
            addCriterion("subjects not in", values, "subjects");
            return (Criteria) this;
        }

        public Criteria andSubjectsBetween(String value1, String value2) {
            addCriterion("subjects between", value1, value2, "subjects");
            return (Criteria) this;
        }

        public Criteria andSubjectsNotBetween(String value1, String value2) {
            addCriterion("subjects not between", value1, value2, "subjects");
            return (Criteria) this;
        }

        public Criteria andDescpIsNull() {
            addCriterion("descp is null");
            return (Criteria) this;
        }

        public Criteria andDescpIsNotNull() {
            addCriterion("descp is not null");
            return (Criteria) this;
        }

        public Criteria andDescpEqualTo(String value) {
            addCriterion("descp =", value, "descp");
            return (Criteria) this;
        }

        public Criteria andDescpNotEqualTo(String value) {
            addCriterion("descp <>", value, "descp");
            return (Criteria) this;
        }

        public Criteria andDescpGreaterThan(String value) {
            addCriterion("descp >", value, "descp");
            return (Criteria) this;
        }

        public Criteria andDescpGreaterThanOrEqualTo(String value) {
            addCriterion("descp >=", value, "descp");
            return (Criteria) this;
        }

        public Criteria andDescpLessThan(String value) {
            addCriterion("descp <", value, "descp");
            return (Criteria) this;
        }

        public Criteria andDescpLessThanOrEqualTo(String value) {
            addCriterion("descp <=", value, "descp");
            return (Criteria) this;
        }

        public Criteria andDescpLike(String value) {
            addCriterion("descp like", value, "descp");
            return (Criteria) this;
        }

        public Criteria andDescpNotLike(String value) {
            addCriterion("descp not like", value, "descp");
            return (Criteria) this;
        }

        public Criteria andDescpIn(List<String> values) {
            addCriterion("descp in", values, "descp");
            return (Criteria) this;
        }

        public Criteria andDescpNotIn(List<String> values) {
            addCriterion("descp not in", values, "descp");
            return (Criteria) this;
        }

        public Criteria andDescpBetween(String value1, String value2) {
            addCriterion("descp between", value1, value2, "descp");
            return (Criteria) this;
        }

        public Criteria andDescpNotBetween(String value1, String value2) {
            addCriterion("descp not between", value1, value2, "descp");
            return (Criteria) this;
        }

        public Criteria andExtraParamIsNull() {
            addCriterion("extra_param is null");
            return (Criteria) this;
        }

        public Criteria andExtraParamIsNotNull() {
            addCriterion("extra_param is not null");
            return (Criteria) this;
        }

        public Criteria andExtraParamEqualTo(String value) {
            addCriterion("extra_param =", value, "extraParam");
            return (Criteria) this;
        }

        public Criteria andExtraParamNotEqualTo(String value) {
            addCriterion("extra_param <>", value, "extraParam");
            return (Criteria) this;
        }

        public Criteria andExtraParamGreaterThan(String value) {
            addCriterion("extra_param >", value, "extraParam");
            return (Criteria) this;
        }

        public Criteria andExtraParamGreaterThanOrEqualTo(String value) {
            addCriterion("extra_param >=", value, "extraParam");
            return (Criteria) this;
        }

        public Criteria andExtraParamLessThan(String value) {
            addCriterion("extra_param <", value, "extraParam");
            return (Criteria) this;
        }

        public Criteria andExtraParamLessThanOrEqualTo(String value) {
            addCriterion("extra_param <=", value, "extraParam");
            return (Criteria) this;
        }

        public Criteria andExtraParamLike(String value) {
            addCriterion("extra_param like", value, "extraParam");
            return (Criteria) this;
        }

        public Criteria andExtraParamNotLike(String value) {
            addCriterion("extra_param not like", value, "extraParam");
            return (Criteria) this;
        }

        public Criteria andExtraParamIn(List<String> values) {
            addCriterion("extra_param in", values, "extraParam");
            return (Criteria) this;
        }

        public Criteria andExtraParamNotIn(List<String> values) {
            addCriterion("extra_param not in", values, "extraParam");
            return (Criteria) this;
        }

        public Criteria andExtraParamBetween(String value1, String value2) {
            addCriterion("extra_param between", value1, value2, "extraParam");
            return (Criteria) this;
        }

        public Criteria andExtraParamNotBetween(String value1, String value2) {
            addCriterion("extra_param not between", value1, value2, "extraParam");
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

        public Criteria andOrderPayDatetimeIsNull() {
            addCriterion("order_pay_datetime is null");
            return (Criteria) this;
        }

        public Criteria andOrderPayDatetimeIsNotNull() {
            addCriterion("order_pay_datetime is not null");
            return (Criteria) this;
        }

        public Criteria andOrderPayDatetimeEqualTo(Long value) {
            addCriterion("order_pay_datetime =", value, "orderPayDatetime");
            return (Criteria) this;
        }

        public Criteria andOrderPayDatetimeNotEqualTo(Long value) {
            addCriterion("order_pay_datetime <>", value, "orderPayDatetime");
            return (Criteria) this;
        }

        public Criteria andOrderPayDatetimeGreaterThan(Long value) {
            addCriterion("order_pay_datetime >", value, "orderPayDatetime");
            return (Criteria) this;
        }

        public Criteria andOrderPayDatetimeGreaterThanOrEqualTo(Long value) {
            addCriterion("order_pay_datetime >=", value, "orderPayDatetime");
            return (Criteria) this;
        }

        public Criteria andOrderPayDatetimeLessThan(Long value) {
            addCriterion("order_pay_datetime <", value, "orderPayDatetime");
            return (Criteria) this;
        }

        public Criteria andOrderPayDatetimeLessThanOrEqualTo(Long value) {
            addCriterion("order_pay_datetime <=", value, "orderPayDatetime");
            return (Criteria) this;
        }

        public Criteria andOrderPayDatetimeIn(List<Long> values) {
            addCriterion("order_pay_datetime in", values, "orderPayDatetime");
            return (Criteria) this;
        }

        public Criteria andOrderPayDatetimeNotIn(List<Long> values) {
            addCriterion("order_pay_datetime not in", values, "orderPayDatetime");
            return (Criteria) this;
        }

        public Criteria andOrderPayDatetimeBetween(Long value1, Long value2) {
            addCriterion("order_pay_datetime between", value1, value2, "orderPayDatetime");
            return (Criteria) this;
        }

        public Criteria andOrderPayDatetimeNotBetween(Long value1, Long value2) {
            addCriterion("order_pay_datetime not between", value1, value2, "orderPayDatetime");
            return (Criteria) this;
        }

        public Criteria andPayFeeIsNull() {
            addCriterion("pay_fee is null");
            return (Criteria) this;
        }

        public Criteria andPayFeeIsNotNull() {
            addCriterion("pay_fee is not null");
            return (Criteria) this;
        }

        public Criteria andPayFeeEqualTo(Integer value) {
            addCriterion("pay_fee =", value, "payFee");
            return (Criteria) this;
        }

        public Criteria andPayFeeNotEqualTo(Integer value) {
            addCriterion("pay_fee <>", value, "payFee");
            return (Criteria) this;
        }

        public Criteria andPayFeeGreaterThan(Integer value) {
            addCriterion("pay_fee >", value, "payFee");
            return (Criteria) this;
        }

        public Criteria andPayFeeGreaterThanOrEqualTo(Integer value) {
            addCriterion("pay_fee >=", value, "payFee");
            return (Criteria) this;
        }

        public Criteria andPayFeeLessThan(Integer value) {
            addCriterion("pay_fee <", value, "payFee");
            return (Criteria) this;
        }

        public Criteria andPayFeeLessThanOrEqualTo(Integer value) {
            addCriterion("pay_fee <=", value, "payFee");
            return (Criteria) this;
        }

        public Criteria andPayFeeIn(List<Integer> values) {
            addCriterion("pay_fee in", values, "payFee");
            return (Criteria) this;
        }

        public Criteria andPayFeeNotIn(List<Integer> values) {
            addCriterion("pay_fee not in", values, "payFee");
            return (Criteria) this;
        }

        public Criteria andPayFeeBetween(Integer value1, Integer value2) {
            addCriterion("pay_fee between", value1, value2, "payFee");
            return (Criteria) this;
        }

        public Criteria andPayFeeNotBetween(Integer value1, Integer value2) {
            addCriterion("pay_fee not between", value1, value2, "payFee");
            return (Criteria) this;
        }

        public Criteria andPayTypeIsNull() {
            addCriterion("pay_type is null");
            return (Criteria) this;
        }

        public Criteria andPayTypeIsNotNull() {
            addCriterion("pay_type is not null");
            return (Criteria) this;
        }

        public Criteria andPayTypeEqualTo(String value) {
            addCriterion("pay_type =", value, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeNotEqualTo(String value) {
            addCriterion("pay_type <>", value, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeGreaterThan(String value) {
            addCriterion("pay_type >", value, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeGreaterThanOrEqualTo(String value) {
            addCriterion("pay_type >=", value, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeLessThan(String value) {
            addCriterion("pay_type <", value, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeLessThanOrEqualTo(String value) {
            addCriterion("pay_type <=", value, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeLike(String value) {
            addCriterion("pay_type like", value, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeNotLike(String value) {
            addCriterion("pay_type not like", value, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeIn(List<String> values) {
            addCriterion("pay_type in", values, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeNotIn(List<String> values) {
            addCriterion("pay_type not in", values, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeBetween(String value1, String value2) {
            addCriterion("pay_type between", value1, value2, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeNotBetween(String value1, String value2) {
            addCriterion("pay_type not between", value1, value2, "payType");
            return (Criteria) this;
        }

        public Criteria andPayTypeCategoryIsNull() {
            addCriterion("pay_type_category is null");
            return (Criteria) this;
        }

        public Criteria andPayTypeCategoryIsNotNull() {
            addCriterion("pay_type_category is not null");
            return (Criteria) this;
        }

        public Criteria andPayTypeCategoryEqualTo(Byte value) {
            addCriterion("pay_type_category =", value, "payTypeCategory");
            return (Criteria) this;
        }

        public Criteria andPayTypeCategoryNotEqualTo(Byte value) {
            addCriterion("pay_type_category <>", value, "payTypeCategory");
            return (Criteria) this;
        }

        public Criteria andPayTypeCategoryGreaterThan(Byte value) {
            addCriterion("pay_type_category >", value, "payTypeCategory");
            return (Criteria) this;
        }

        public Criteria andPayTypeCategoryGreaterThanOrEqualTo(Byte value) {
            addCriterion("pay_type_category >=", value, "payTypeCategory");
            return (Criteria) this;
        }

        public Criteria andPayTypeCategoryLessThan(Byte value) {
            addCriterion("pay_type_category <", value, "payTypeCategory");
            return (Criteria) this;
        }

        public Criteria andPayTypeCategoryLessThanOrEqualTo(Byte value) {
            addCriterion("pay_type_category <=", value, "payTypeCategory");
            return (Criteria) this;
        }

        public Criteria andPayTypeCategoryIn(List<Byte> values) {
            addCriterion("pay_type_category in", values, "payTypeCategory");
            return (Criteria) this;
        }

        public Criteria andPayTypeCategoryNotIn(List<Byte> values) {
            addCriterion("pay_type_category not in", values, "payTypeCategory");
            return (Criteria) this;
        }

        public Criteria andPayTypeCategoryBetween(Byte value1, Byte value2) {
            addCriterion("pay_type_category between", value1, value2, "payTypeCategory");
            return (Criteria) this;
        }

        public Criteria andPayTypeCategoryNotBetween(Byte value1, Byte value2) {
            addCriterion("pay_type_category not between", value1, value2, "payTypeCategory");
            return (Criteria) this;
        }

        public Criteria andChargeAccountIdIsNull() {
            addCriterion("charge_account_id is null");
            return (Criteria) this;
        }

        public Criteria andChargeAccountIdIsNotNull() {
            addCriterion("charge_account_id is not null");
            return (Criteria) this;
        }

        public Criteria andChargeAccountIdEqualTo(Integer value) {
            addCriterion("charge_account_id =", value, "chargeAccountId");
            return (Criteria) this;
        }

        public Criteria andChargeAccountIdNotEqualTo(Integer value) {
            addCriterion("charge_account_id <>", value, "chargeAccountId");
            return (Criteria) this;
        }

        public Criteria andChargeAccountIdGreaterThan(Integer value) {
            addCriterion("charge_account_id >", value, "chargeAccountId");
            return (Criteria) this;
        }

        public Criteria andChargeAccountIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("charge_account_id >=", value, "chargeAccountId");
            return (Criteria) this;
        }

        public Criteria andChargeAccountIdLessThan(Integer value) {
            addCriterion("charge_account_id <", value, "chargeAccountId");
            return (Criteria) this;
        }

        public Criteria andChargeAccountIdLessThanOrEqualTo(Integer value) {
            addCriterion("charge_account_id <=", value, "chargeAccountId");
            return (Criteria) this;
        }

        public Criteria andChargeAccountIdIn(List<Integer> values) {
            addCriterion("charge_account_id in", values, "chargeAccountId");
            return (Criteria) this;
        }

        public Criteria andChargeAccountIdNotIn(List<Integer> values) {
            addCriterion("charge_account_id not in", values, "chargeAccountId");
            return (Criteria) this;
        }

        public Criteria andChargeAccountIdBetween(Integer value1, Integer value2) {
            addCriterion("charge_account_id between", value1, value2, "chargeAccountId");
            return (Criteria) this;
        }

        public Criteria andChargeAccountIdNotBetween(Integer value1, Integer value2) {
            addCriterion("charge_account_id not between", value1, value2, "chargeAccountId");
            return (Criteria) this;
        }

        public Criteria andApiVersionIsNull() {
            addCriterion("api_version is null");
            return (Criteria) this;
        }

        public Criteria andApiVersionIsNotNull() {
            addCriterion("api_version is not null");
            return (Criteria) this;
        }

        public Criteria andApiVersionEqualTo(String value) {
            addCriterion("api_version =", value, "apiVersion");
            return (Criteria) this;
        }

        public Criteria andApiVersionNotEqualTo(String value) {
            addCriterion("api_version <>", value, "apiVersion");
            return (Criteria) this;
        }

        public Criteria andApiVersionGreaterThan(String value) {
            addCriterion("api_version >", value, "apiVersion");
            return (Criteria) this;
        }

        public Criteria andApiVersionGreaterThanOrEqualTo(String value) {
            addCriterion("api_version >=", value, "apiVersion");
            return (Criteria) this;
        }

        public Criteria andApiVersionLessThan(String value) {
            addCriterion("api_version <", value, "apiVersion");
            return (Criteria) this;
        }

        public Criteria andApiVersionLessThanOrEqualTo(String value) {
            addCriterion("api_version <=", value, "apiVersion");
            return (Criteria) this;
        }

        public Criteria andApiVersionLike(String value) {
            addCriterion("api_version like", value, "apiVersion");
            return (Criteria) this;
        }

        public Criteria andApiVersionNotLike(String value) {
            addCriterion("api_version not like", value, "apiVersion");
            return (Criteria) this;
        }

        public Criteria andApiVersionIn(List<String> values) {
            addCriterion("api_version in", values, "apiVersion");
            return (Criteria) this;
        }

        public Criteria andApiVersionNotIn(List<String> values) {
            addCriterion("api_version not in", values, "apiVersion");
            return (Criteria) this;
        }

        public Criteria andApiVersionBetween(String value1, String value2) {
            addCriterion("api_version between", value1, value2, "apiVersion");
            return (Criteria) this;
        }

        public Criteria andApiVersionNotBetween(String value1, String value2) {
            addCriterion("api_version not between", value1, value2, "apiVersion");
            return (Criteria) this;
        }

        public Criteria andDataVersionIsNull() {
            addCriterion("data_version is null");
            return (Criteria) this;
        }

        public Criteria andDataVersionIsNotNull() {
            addCriterion("data_version is not null");
            return (Criteria) this;
        }

        public Criteria andDataVersionEqualTo(Integer value) {
            addCriterion("data_version =", value, "dataVersion");
            return (Criteria) this;
        }

        public Criteria andDataVersionNotEqualTo(Integer value) {
            addCriterion("data_version <>", value, "dataVersion");
            return (Criteria) this;
        }

        public Criteria andDataVersionGreaterThan(Integer value) {
            addCriterion("data_version >", value, "dataVersion");
            return (Criteria) this;
        }

        public Criteria andDataVersionGreaterThanOrEqualTo(Integer value) {
            addCriterion("data_version >=", value, "dataVersion");
            return (Criteria) this;
        }

        public Criteria andDataVersionLessThan(Integer value) {
            addCriterion("data_version <", value, "dataVersion");
            return (Criteria) this;
        }

        public Criteria andDataVersionLessThanOrEqualTo(Integer value) {
            addCriterion("data_version <=", value, "dataVersion");
            return (Criteria) this;
        }

        public Criteria andDataVersionIn(List<Integer> values) {
            addCriterion("data_version in", values, "dataVersion");
            return (Criteria) this;
        }

        public Criteria andDataVersionNotIn(List<Integer> values) {
            addCriterion("data_version not in", values, "dataVersion");
            return (Criteria) this;
        }

        public Criteria andDataVersionBetween(Integer value1, Integer value2) {
            addCriterion("data_version between", value1, value2, "dataVersion");
            return (Criteria) this;
        }

        public Criteria andDataVersionNotBetween(Integer value1, Integer value2) {
            addCriterion("data_version not between", value1, value2, "dataVersion");
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
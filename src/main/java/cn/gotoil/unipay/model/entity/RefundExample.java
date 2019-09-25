package cn.gotoil.unipay.model.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RefundExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset;

    public RefundExample() {
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

        public Criteria andRefundOrderIdIsNull() {
            addCriterion("refund_order_id is null");
            return (Criteria) this;
        }

        public Criteria andRefundOrderIdIsNotNull() {
            addCriterion("refund_order_id is not null");
            return (Criteria) this;
        }

        public Criteria andRefundOrderIdEqualTo(String value) {
            addCriterion("refund_order_id =", value, "refundOrderId");
            return (Criteria) this;
        }

        public Criteria andRefundOrderIdNotEqualTo(String value) {
            addCriterion("refund_order_id <>", value, "refundOrderId");
            return (Criteria) this;
        }

        public Criteria andRefundOrderIdGreaterThan(String value) {
            addCriterion("refund_order_id >", value, "refundOrderId");
            return (Criteria) this;
        }

        public Criteria andRefundOrderIdGreaterThanOrEqualTo(String value) {
            addCriterion("refund_order_id >=", value, "refundOrderId");
            return (Criteria) this;
        }

        public Criteria andRefundOrderIdLessThan(String value) {
            addCriterion("refund_order_id <", value, "refundOrderId");
            return (Criteria) this;
        }

        public Criteria andRefundOrderIdLessThanOrEqualTo(String value) {
            addCriterion("refund_order_id <=", value, "refundOrderId");
            return (Criteria) this;
        }

        public Criteria andRefundOrderIdLike(String value) {
            addCriterion("refund_order_id like", value, "refundOrderId");
            return (Criteria) this;
        }

        public Criteria andRefundOrderIdNotLike(String value) {
            addCriterion("refund_order_id not like", value, "refundOrderId");
            return (Criteria) this;
        }

        public Criteria andRefundOrderIdIn(List<String> values) {
            addCriterion("refund_order_id in", values, "refundOrderId");
            return (Criteria) this;
        }

        public Criteria andRefundOrderIdNotIn(List<String> values) {
            addCriterion("refund_order_id not in", values, "refundOrderId");
            return (Criteria) this;
        }

        public Criteria andRefundOrderIdBetween(String value1, String value2) {
            addCriterion("refund_order_id between", value1, value2, "refundOrderId");
            return (Criteria) this;
        }

        public Criteria andRefundOrderIdNotBetween(String value1, String value2) {
            addCriterion("refund_order_id not between", value1, value2, "refundOrderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdIsNull() {
            addCriterion("order_id is null");
            return (Criteria) this;
        }

        public Criteria andOrderIdIsNotNull() {
            addCriterion("order_id is not null");
            return (Criteria) this;
        }

        public Criteria andOrderIdEqualTo(String value) {
            addCriterion("order_id =", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdNotEqualTo(String value) {
            addCriterion("order_id <>", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdGreaterThan(String value) {
            addCriterion("order_id >", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdGreaterThanOrEqualTo(String value) {
            addCriterion("order_id >=", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdLessThan(String value) {
            addCriterion("order_id <", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdLessThanOrEqualTo(String value) {
            addCriterion("order_id <=", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdLike(String value) {
            addCriterion("order_id like", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdNotLike(String value) {
            addCriterion("order_id not like", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdIn(List<String> values) {
            addCriterion("order_id in", values, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdNotIn(List<String> values) {
            addCriterion("order_id not in", values, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdBetween(String value1, String value2) {
            addCriterion("order_id between", value1, value2, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdNotBetween(String value1, String value2) {
            addCriterion("order_id not between", value1, value2, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderFeeIsNull() {
            addCriterion("order_fee is null");
            return (Criteria) this;
        }

        public Criteria andOrderFeeIsNotNull() {
            addCriterion("order_fee is not null");
            return (Criteria) this;
        }

        public Criteria andOrderFeeEqualTo(Integer value) {
            addCriterion("order_fee =", value, "orderFee");
            return (Criteria) this;
        }

        public Criteria andOrderFeeNotEqualTo(Integer value) {
            addCriterion("order_fee <>", value, "orderFee");
            return (Criteria) this;
        }

        public Criteria andOrderFeeGreaterThan(Integer value) {
            addCriterion("order_fee >", value, "orderFee");
            return (Criteria) this;
        }

        public Criteria andOrderFeeGreaterThanOrEqualTo(Integer value) {
            addCriterion("order_fee >=", value, "orderFee");
            return (Criteria) this;
        }

        public Criteria andOrderFeeLessThan(Integer value) {
            addCriterion("order_fee <", value, "orderFee");
            return (Criteria) this;
        }

        public Criteria andOrderFeeLessThanOrEqualTo(Integer value) {
            addCriterion("order_fee <=", value, "orderFee");
            return (Criteria) this;
        }

        public Criteria andOrderFeeIn(List<Integer> values) {
            addCriterion("order_fee in", values, "orderFee");
            return (Criteria) this;
        }

        public Criteria andOrderFeeNotIn(List<Integer> values) {
            addCriterion("order_fee not in", values, "orderFee");
            return (Criteria) this;
        }

        public Criteria andOrderFeeBetween(Integer value1, Integer value2) {
            addCriterion("order_fee between", value1, value2, "orderFee");
            return (Criteria) this;
        }

        public Criteria andOrderFeeNotBetween(Integer value1, Integer value2) {
            addCriterion("order_fee not between", value1, value2, "orderFee");
            return (Criteria) this;
        }

        public Criteria andAppOrderRefundNoIsNull() {
            addCriterion("app_order_refund_no is null");
            return (Criteria) this;
        }

        public Criteria andAppOrderRefundNoIsNotNull() {
            addCriterion("app_order_refund_no is not null");
            return (Criteria) this;
        }

        public Criteria andAppOrderRefundNoEqualTo(String value) {
            addCriterion("app_order_refund_no =", value, "appOrderRefundNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderRefundNoNotEqualTo(String value) {
            addCriterion("app_order_refund_no <>", value, "appOrderRefundNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderRefundNoGreaterThan(String value) {
            addCriterion("app_order_refund_no >", value, "appOrderRefundNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderRefundNoGreaterThanOrEqualTo(String value) {
            addCriterion("app_order_refund_no >=", value, "appOrderRefundNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderRefundNoLessThan(String value) {
            addCriterion("app_order_refund_no <", value, "appOrderRefundNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderRefundNoLessThanOrEqualTo(String value) {
            addCriterion("app_order_refund_no <=", value, "appOrderRefundNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderRefundNoLike(String value) {
            addCriterion("app_order_refund_no like", value, "appOrderRefundNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderRefundNoNotLike(String value) {
            addCriterion("app_order_refund_no not like", value, "appOrderRefundNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderRefundNoIn(List<String> values) {
            addCriterion("app_order_refund_no in", values, "appOrderRefundNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderRefundNoNotIn(List<String> values) {
            addCriterion("app_order_refund_no not in", values, "appOrderRefundNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderRefundNoBetween(String value1, String value2) {
            addCriterion("app_order_refund_no between", value1, value2, "appOrderRefundNo");
            return (Criteria) this;
        }

        public Criteria andAppOrderRefundNoNotBetween(String value1, String value2) {
            addCriterion("app_order_refund_no not between", value1, value2, "appOrderRefundNo");
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

        public Criteria andApplyFeeIsNull() {
            addCriterion("apply_fee is null");
            return (Criteria) this;
        }

        public Criteria andApplyFeeIsNotNull() {
            addCriterion("apply_fee is not null");
            return (Criteria) this;
        }

        public Criteria andApplyFeeEqualTo(Integer value) {
            addCriterion("apply_fee =", value, "applyFee");
            return (Criteria) this;
        }

        public Criteria andApplyFeeNotEqualTo(Integer value) {
            addCriterion("apply_fee <>", value, "applyFee");
            return (Criteria) this;
        }

        public Criteria andApplyFeeGreaterThan(Integer value) {
            addCriterion("apply_fee >", value, "applyFee");
            return (Criteria) this;
        }

        public Criteria andApplyFeeGreaterThanOrEqualTo(Integer value) {
            addCriterion("apply_fee >=", value, "applyFee");
            return (Criteria) this;
        }

        public Criteria andApplyFeeLessThan(Integer value) {
            addCriterion("apply_fee <", value, "applyFee");
            return (Criteria) this;
        }

        public Criteria andApplyFeeLessThanOrEqualTo(Integer value) {
            addCriterion("apply_fee <=", value, "applyFee");
            return (Criteria) this;
        }

        public Criteria andApplyFeeIn(List<Integer> values) {
            addCriterion("apply_fee in", values, "applyFee");
            return (Criteria) this;
        }

        public Criteria andApplyFeeNotIn(List<Integer> values) {
            addCriterion("apply_fee not in", values, "applyFee");
            return (Criteria) this;
        }

        public Criteria andApplyFeeBetween(Integer value1, Integer value2) {
            addCriterion("apply_fee between", value1, value2, "applyFee");
            return (Criteria) this;
        }

        public Criteria andApplyFeeNotBetween(Integer value1, Integer value2) {
            addCriterion("apply_fee not between", value1, value2, "applyFee");
            return (Criteria) this;
        }

        public Criteria andApplyDatetimeIsNull() {
            addCriterion("apply_datetime is null");
            return (Criteria) this;
        }

        public Criteria andApplyDatetimeIsNotNull() {
            addCriterion("apply_datetime is not null");
            return (Criteria) this;
        }

        public Criteria andApplyDatetimeEqualTo(Date value) {
            addCriterion("apply_datetime =", value, "applyDatetime");
            return (Criteria) this;
        }

        public Criteria andApplyDatetimeNotEqualTo(Date value) {
            addCriterion("apply_datetime <>", value, "applyDatetime");
            return (Criteria) this;
        }

        public Criteria andApplyDatetimeGreaterThan(Date value) {
            addCriterion("apply_datetime >", value, "applyDatetime");
            return (Criteria) this;
        }

        public Criteria andApplyDatetimeGreaterThanOrEqualTo(Date value) {
            addCriterion("apply_datetime >=", value, "applyDatetime");
            return (Criteria) this;
        }

        public Criteria andApplyDatetimeLessThan(Date value) {
            addCriterion("apply_datetime <", value, "applyDatetime");
            return (Criteria) this;
        }

        public Criteria andApplyDatetimeLessThanOrEqualTo(Date value) {
            addCriterion("apply_datetime <=", value, "applyDatetime");
            return (Criteria) this;
        }

        public Criteria andApplyDatetimeIn(List<Date> values) {
            addCriterion("apply_datetime in", values, "applyDatetime");
            return (Criteria) this;
        }

        public Criteria andApplyDatetimeNotIn(List<Date> values) {
            addCriterion("apply_datetime not in", values, "applyDatetime");
            return (Criteria) this;
        }

        public Criteria andApplyDatetimeBetween(Date value1, Date value2) {
            addCriterion("apply_datetime between", value1, value2, "applyDatetime");
            return (Criteria) this;
        }

        public Criteria andApplyDatetimeNotBetween(Date value1, Date value2) {
            addCriterion("apply_datetime not between", value1, value2, "applyDatetime");
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

        public Criteria andProcessResultIsNull() {
            addCriterion("process_result is null");
            return (Criteria) this;
        }

        public Criteria andProcessResultIsNotNull() {
            addCriterion("process_result is not null");
            return (Criteria) this;
        }

        public Criteria andProcessResultEqualTo(Byte value) {
            addCriterion("process_result =", value, "processResult");
            return (Criteria) this;
        }

        public Criteria andProcessResultNotEqualTo(Byte value) {
            addCriterion("process_result <>", value, "processResult");
            return (Criteria) this;
        }

        public Criteria andProcessResultGreaterThan(Byte value) {
            addCriterion("process_result >", value, "processResult");
            return (Criteria) this;
        }

        public Criteria andProcessResultGreaterThanOrEqualTo(Byte value) {
            addCriterion("process_result >=", value, "processResult");
            return (Criteria) this;
        }

        public Criteria andProcessResultLessThan(Byte value) {
            addCriterion("process_result <", value, "processResult");
            return (Criteria) this;
        }

        public Criteria andProcessResultLessThanOrEqualTo(Byte value) {
            addCriterion("process_result <=", value, "processResult");
            return (Criteria) this;
        }

        public Criteria andProcessResultIn(List<Byte> values) {
            addCriterion("process_result in", values, "processResult");
            return (Criteria) this;
        }

        public Criteria andProcessResultNotIn(List<Byte> values) {
            addCriterion("process_result not in", values, "processResult");
            return (Criteria) this;
        }

        public Criteria andProcessResultBetween(Byte value1, Byte value2) {
            addCriterion("process_result between", value1, value2, "processResult");
            return (Criteria) this;
        }

        public Criteria andProcessResultNotBetween(Byte value1, Byte value2) {
            addCriterion("process_result not between", value1, value2, "processResult");
            return (Criteria) this;
        }

        public Criteria andFailMsgIsNull() {
            addCriterion("fail_msg is null");
            return (Criteria) this;
        }

        public Criteria andFailMsgIsNotNull() {
            addCriterion("fail_msg is not null");
            return (Criteria) this;
        }

        public Criteria andFailMsgEqualTo(String value) {
            addCriterion("fail_msg =", value, "failMsg");
            return (Criteria) this;
        }

        public Criteria andFailMsgNotEqualTo(String value) {
            addCriterion("fail_msg <>", value, "failMsg");
            return (Criteria) this;
        }

        public Criteria andFailMsgGreaterThan(String value) {
            addCriterion("fail_msg >", value, "failMsg");
            return (Criteria) this;
        }

        public Criteria andFailMsgGreaterThanOrEqualTo(String value) {
            addCriterion("fail_msg >=", value, "failMsg");
            return (Criteria) this;
        }

        public Criteria andFailMsgLessThan(String value) {
            addCriterion("fail_msg <", value, "failMsg");
            return (Criteria) this;
        }

        public Criteria andFailMsgLessThanOrEqualTo(String value) {
            addCriterion("fail_msg <=", value, "failMsg");
            return (Criteria) this;
        }

        public Criteria andFailMsgLike(String value) {
            addCriterion("fail_msg like", value, "failMsg");
            return (Criteria) this;
        }

        public Criteria andFailMsgNotLike(String value) {
            addCriterion("fail_msg not like", value, "failMsg");
            return (Criteria) this;
        }

        public Criteria andFailMsgIn(List<String> values) {
            addCriterion("fail_msg in", values, "failMsg");
            return (Criteria) this;
        }

        public Criteria andFailMsgNotIn(List<String> values) {
            addCriterion("fail_msg not in", values, "failMsg");
            return (Criteria) this;
        }

        public Criteria andFailMsgBetween(String value1, String value2) {
            addCriterion("fail_msg between", value1, value2, "failMsg");
            return (Criteria) this;
        }

        public Criteria andFailMsgNotBetween(String value1, String value2) {
            addCriterion("fail_msg not between", value1, value2, "failMsg");
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

        public Criteria andStatusUpdateDatetimeIsNull() {
            addCriterion("status_update_datetime is null");
            return (Criteria) this;
        }

        public Criteria andStatusUpdateDatetimeIsNotNull() {
            addCriterion("status_update_datetime is not null");
            return (Criteria) this;
        }

        public Criteria andStatusUpdateDatetimeEqualTo(Date value) {
            addCriterion("status_update_datetime =", value, "statusUpdateDatetime");
            return (Criteria) this;
        }

        public Criteria andStatusUpdateDatetimeNotEqualTo(Date value) {
            addCriterion("status_update_datetime <>", value, "statusUpdateDatetime");
            return (Criteria) this;
        }

        public Criteria andStatusUpdateDatetimeGreaterThan(Date value) {
            addCriterion("status_update_datetime >", value, "statusUpdateDatetime");
            return (Criteria) this;
        }

        public Criteria andStatusUpdateDatetimeGreaterThanOrEqualTo(Date value) {
            addCriterion("status_update_datetime >=", value, "statusUpdateDatetime");
            return (Criteria) this;
        }

        public Criteria andStatusUpdateDatetimeLessThan(Date value) {
            addCriterion("status_update_datetime <", value, "statusUpdateDatetime");
            return (Criteria) this;
        }

        public Criteria andStatusUpdateDatetimeLessThanOrEqualTo(Date value) {
            addCriterion("status_update_datetime <=", value, "statusUpdateDatetime");
            return (Criteria) this;
        }

        public Criteria andStatusUpdateDatetimeIn(List<Date> values) {
            addCriterion("status_update_datetime in", values, "statusUpdateDatetime");
            return (Criteria) this;
        }

        public Criteria andStatusUpdateDatetimeNotIn(List<Date> values) {
            addCriterion("status_update_datetime not in", values, "statusUpdateDatetime");
            return (Criteria) this;
        }

        public Criteria andStatusUpdateDatetimeBetween(Date value1, Date value2) {
            addCriterion("status_update_datetime between", value1, value2, "statusUpdateDatetime");
            return (Criteria) this;
        }

        public Criteria andStatusUpdateDatetimeNotBetween(Date value1, Date value2) {
            addCriterion("status_update_datetime not between", value1, value2, "statusUpdateDatetime");
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

        public Criteria andUpdateAtIsNull() {
            addCriterion("update_at is null");
            return (Criteria) this;
        }

        public Criteria andUpdateAtIsNotNull() {
            addCriterion("update_at is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateAtEqualTo(Date value) {
            addCriterion("update_at =", value, "updateAt");
            return (Criteria) this;
        }

        public Criteria andUpdateAtNotEqualTo(Date value) {
            addCriterion("update_at <>", value, "updateAt");
            return (Criteria) this;
        }

        public Criteria andUpdateAtGreaterThan(Date value) {
            addCriterion("update_at >", value, "updateAt");
            return (Criteria) this;
        }

        public Criteria andUpdateAtGreaterThanOrEqualTo(Date value) {
            addCriterion("update_at >=", value, "updateAt");
            return (Criteria) this;
        }

        public Criteria andUpdateAtLessThan(Date value) {
            addCriterion("update_at <", value, "updateAt");
            return (Criteria) this;
        }

        public Criteria andUpdateAtLessThanOrEqualTo(Date value) {
            addCriterion("update_at <=", value, "updateAt");
            return (Criteria) this;
        }

        public Criteria andUpdateAtIn(List<Date> values) {
            addCriterion("update_at in", values, "updateAt");
            return (Criteria) this;
        }

        public Criteria andUpdateAtNotIn(List<Date> values) {
            addCriterion("update_at not in", values, "updateAt");
            return (Criteria) this;
        }

        public Criteria andUpdateAtBetween(Date value1, Date value2) {
            addCriterion("update_at between", value1, value2, "updateAt");
            return (Criteria) this;
        }

        public Criteria andUpdateAtNotBetween(Date value1, Date value2) {
            addCriterion("update_at not between", value1, value2, "updateAt");
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
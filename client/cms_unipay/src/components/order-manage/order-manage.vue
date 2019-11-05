<template>
  <div>
    <a-breadcrumb style="margin: 16px 0">
      <a-breadcrumb-item>应用管理</a-breadcrumb-item>
      <a-breadcrumb-item>应用列表</a-breadcrumb-item>
    </a-breadcrumb>
    <a-layout-content
      :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '300px' }"
    >
      <!-- 搜索框开始 -->
      <div class="row">
        <a-form-item label="订单号" class="serchItem row">
          <a-input
            title="应用订单号、支付订单号、统一订单号"
            placeholder="订单号"
            v-model="searchForm.queryId"
            style="width:180px"
          >{{searchForm.queryId}}</a-input>
        </a-form-item>
        <a-form-item label="应用" class="serchItem row">
          <a-select v-model="searchForm.appId" style="width:150px">
            <a-select-option value>全部</a-select-option>
            <a-select-option
              v-for="item in appListOfData"
              :key="item.value"
              :value="item.value"
            >{{item.label}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="支付状态" class="serchItem row">
          <a-select v-model="searchForm.status" style="width:80px">
            <a-select-option value>全部</a-select-option>
            <a-select-option
              v-for="item in payStatusListOfData"
              :key="item.value"
              :value="item.value"
            >{{item.label}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="支付方式" class="serchItem row">
          <a-select v-model="searchForm.payType" style="width:150px">
            <a-select-option value>全部</a-select-option>
            <a-select-option
              v-for="item in payTypeListOfData"
              :key="item.value"
              :value="item.value"
            >{{item.label}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="getList(1,20)">查询</a-button>
        </a-form-item>
      </div>
      <!-- 搜索框结束 -->
      <a-table
        :columns="tHead"
        :dataSource="ListOfData"
        :pagination="pagination"
        @change="pageList"
      >
        <div slot="statusDiv" slot-scope="item">{{formatStatus(item)}}</div>
        <div slot="appDiv" slot-scope="item">{{formatApp(item)}}</div>
        <div slot="payTypeDiv" slot-scope="item">{{formatPayType(item)}}</div>
        <a
          href="javaScript:void(0)"
          slot="notifyLog"
          slot-scope="item"
          @click="showNotifyLog(item)"
        >查看</a>
        <a href="#" slot="sendMsg" slot-scope="item" @click="sendMsg(item)">发送</a>

        <a-table
          :bordered="true"
          slot="expandedRowRender"
          slot-scope="record"
          :columns="innerColumns"
          :dataSource="record.innerData"
          :pagination="false"
        ></a-table>
      </a-table>
      <a-modal
        width="860"
        :bordered="true"
        title="通知记录"
        :visible="modal_notifyLogVisible"
        @ok="modal_notify_close"
        @cancel="modal_notify_close"
        :confirmLoading="false"
      >
        <a-table :columns="notifyLogHd" :dataSource="notifyLogList" :pagination="false">
          <div
            :title="record.params"
            :style="{maxWidth: '300px',whiteSpace: 'nowrap',textOverflow: 'ellipsis',overflow: 'hidden', wordWrap: 'break-word', wordBreak: 'break-all' }"
            slot="longTextShow"
            slot-scope="text, record"
          >{{record.params}}</div>
          <div slot="sendType" slot-scope="text">{{text==='0'?"自动":'手动'}}</div>
        </a-table>
      </a-modal>
    </a-layout-content>
  </div>
</template>
<script>
import "moment/locale/zh-cn";
import moment from "moment";
import { currency } from "../tool/filter/currency";
import { Table, Switch, Modal } from "ant-design-vue";
import { HttpService } from "../../services/HttpService";

export default {
  metaInfo() {
    return {
      title: "应用管理"
    };
  },
  data() {
    return {
      collapsed: false,
      tHead: [
        { title: "统一订单号", width: 180, dataIndex: "id" },
        {
          title: "应用名称",
          width: 150,
          dataIndex: "appId",
          scopedSlots: { customRender: "appDiv" }
        },
        { title: "订单标题", width: 150, dataIndex: "subjects" },
        { title: "应用订单号", width: 150, dataIndex: "appOrderNo" },
        { title: "支付订单号", width: 150, dataIndex: "paymentId" },
        { title: "订单金额", width: 150, dataIndex: "fee" },
        {
          title: "状态",
          width: 150,
          dataIndex: "status",
          scopedSlots: { customRender: "statusDiv" }
        },
        {
          title: "支付方式",
          width: 150,
          dataIndex: "payType",
          scopedSlots: { customRender: "payTypeDiv" }
        },
        { title: "创建于", width: 190, dataIndex: "createdAt" },
        {
          title: "通知记录",
          width: 120,
          scopedSlots: { customRender: "notifyLog" }
        },
        {
          title: "发送通知",
          width: 120,
          scopedSlots: { customRender: "sendMsg" }
        }
      ],
      innerColumns: [
        { title: "列名", width: 160, dataIndex: "desp" },
        { title: "值", dataIndex: "val" }
      ],
      searchInfo: {
        page: 1,
        per_page: 20
      },
      pagination: {
        pageNo: 1,
        pageSize: 20,
        total: 0,
        current: 1
      },
      searchForm: {
        appId: "",
        queryId: "",
        status: "",
        payType: ""
      },
      // 列表
      ListOfData: [],
      // 应用
      appListOfData: [],
      // 支付状态
      payStatusListOfData: [],
      // 支付方式
      payTypeListOfData: [],
      // 支付方式map key:code  value:label
      payStatusMap: new Map(),
      payTypeMap: new Map(),
      appMap: new Map(),
      modal_notifyLogVisible: false,
      // 通知记录表格
      notifyLogHd: [
        { title: "通知地址", width: 200, dataIndex: "notifyUrl" },
        {
          title: "参数",
          width: 250,
          dataIndex: "params",
          scopedSlots: { customRender: "longTextShow" }
        },
        { title: "响应", width: 180, dataIndex: "responseContent" },
        { title: "通知时间", width: 120, dataIndex: "noticeDatetime" },
        { title: "重试次数", width: 30, dataIndex: "repeatCount" },
        {
          title: "发送方式",
          width: 40,
          dataIndex: "sendType",
          scopedSlots: { customRender: "sendType" }
        },
        { title: "创建时间", width: 120, dataIndex: "createdAt" }
      ],
      // 通知记录列表
      notifyLogList: []
    };
  },

  // beforeCreate() {
  //   this.form = this.$form.createForm(this);
  // },
  mounted() {
    this.getPayStatusOptions();
    this.getAppOptions();
    this.getPayTypeOptions();
    setTimeout(() => {
      this.getList(this.pagination.pageNo, this.pagination.pageSize);
    }, 300);
  },
  methods: {
    chargeChange(newValue, chargeType) {
      // this.newCharge.push({ payType: chargeType, select: newValue });
      this.$set(this.newCharge, chargeType, newValue);
    },
    // 分页取
    pageList(pagination) {
      this.getList(pagination.current, this.pagination.pageSize);
    },
    // 获取列表数据
    getList(pageNo, pageSize) {
      this.ListOfData = [];
      var param1 = {
        pageNo: pageNo,
        pageSize: pageSize,
        params: {
          appId: this.searchForm.appId,
          queryId: this.searchForm.queryId,
          status: this.searchForm.status,
          payType: this.searchForm.payType
        }
      };
      HttpService.post("/web/order/queryOrder", param1).then(res => {
        const r = res.data;
        if (r.status === 0) {
          const listData = r.data.rows;
          this.pagination.total = r.data.totalCount;
          this.pagination.current = pageNo;
          for (const i in listData) {
            this.ListOfData.push({
              key: listData[i].id,
              id: listData[i].id,
              appId: listData[i].appId,
              appOrderNo: listData[i].appOrderNo,
              paymentId: listData[i].paymentId,
              fee: listData[i].fee,
              subjects: listData[i].subjects,
              syncUrl: listData[i].syncUrl,
              asyncUrl: listData[i].asyncUrl,
              status: listData[i].status,
              payType: listData[i].payType,
              createdAt: listData[i].createdAt,
              innerData: [
                {
                  key: "row" + i + listData[i].appId,
                  desp: "应用ID",
                  val: listData[i].appId
                },
                {
                  key: "row" + i + listData[i].appUserId + Math.random(),
                  desp: "应用用户号",
                  val: listData[i].appUserId
                },
                {
                  key: "row" + i + listData[i].paymentUid + Math.random(),
                  desp: "支付用户号",
                  val: listData[i].paymentUid
                },
                {
                  key:
                    "row" + i + listData[i].expiredTimeMinute + Math.random(),
                  desp: "有效分钟",
                  val: listData[i].expiredTimeMinute
                },
                {
                  key: "row" + i + listData[i].descp + Math.random(),
                  desp: "订单详细",
                  val: listData[i].descp
                },
                {
                  key: "row" + i + listData[i].extraParam + Math.random(),
                  desp: "扩展参数",
                  val: listData[i].extraParam
                },
                {
                  key: "row" + i + listData[i].orderPayDatetime + Math.random(),
                  desp: "支付完成时间",
                  val: listData[i].orderPayDatetime
                },
                {
                  key: "row" + i + listData[i].payFee + Math.random(),
                  desp: "支付金额",
                  val: listData[i].payFee
                },
                {
                  key: "row" + i + listData[i].arrFee + Math.random(),
                  desp: "到账金额",
                  val: listData[i].arrFee
                },
                {
                  key: "row" + i + listData[i].payTypeCategory + Math.random(),
                  desp: "支付类型",
                  val: listData[i].payTypeCategory
                },
                {
                  key: "row" + i + listData[i].chargeAccountId + Math.random(),
                  desp: "收款账号ID",
                  val: listData[i].chargeAccountId
                },
                {
                  key: "row" + i + listData[i].expiredTimeMinute,
                  desp: "订单有效分钟数",
                  val: listData[i].expiredTimeMinute
                },
                {
                  key: "row" + i + listData[i].apiVersion + Math.random(),
                  desp: "api版本",
                  val: listData[i].apiVersion
                },
                {
                  key: "row" + i + listData[i].dataVersion + Math.random(),
                  desp: "数据版本",
                  val: listData[i].dataVersion
                },
                {
                  key: "row" + i + listData[i].updatedAt + Math.random(),
                  desp: "更新于",
                  val: listData[i].updatedAt
                },
                {
                  key: "row" + i + listData[i].syncUrl + Math.random(),
                  desp: "默认同步地址",
                  val: listData[i].syncUrl
                },
                {
                  key: "row" + i + listData[i].asyncUrl + Math.random(),
                  desp: "默认异步地址",
                  val: listData[i].asyncUrl
                }
              ]
            });
          }
        } else {
          this.$message.error(r.message ? r.message : "系统错误");
        }
      });
      return [];
    },
    // 获取应用下拉框
    getAppOptions() {
      HttpService.get("/web/app/appCombo", {}).then(res => {
        if (res.data.status === 0) {
          this.appListOfData = res.data.data;
          for (const i in res.data.data) {
            this.appMap.set(res.data.data[i].value, res.data.data[i].label);
          }
        }
      });
    },
    // 获取订单状态列表
    getPayStatusOptions() {
      HttpService.get("/web/order/getOrderStatus", {}).then(res => {
        if (res.data.status === 0) {
          this.payStatusListOfData = res.data.data;
          for (const i in res.data.data) {
            this.payStatusMap.set(
              res.data.data[i].value,
              res.data.data[i].label
            );
          }
        }
      });
    },
    // 获取支付方式列表
    getPayTypeOptions() {
      HttpService.get("/web/app/getAccountTypes", {}).then(res => {
        if (res.data.status === 0) {
          this.payTypeListOfData = res.data.data;
          for (const i in res.data.data) {
            this.payTypeMap.set(res.data.data[i].value, res.data.data[i].label);
          }
        }
      });
    },
    // 查看异步通知记录
    showNotifyLog(item) {
      this.modal_notifyLogVisible = true;
      this.notifyLogList = [];
      HttpService.get("/web/order/getNotifyLog/" + item.id).then(res => {
        if (res.data.status === 0) {
          for (const i in res.data.data) {
            let record = res.data.data[i];
            this.notifyLogList.push({
              key: i,
              notifyUrl: record.notifyUrl,
              params: record.params,
              responseContent: record.responseContent,
              noticeDatetime: record.noticeDatetime,
              repeatCount: record.repeatCount,
              sendType: record.sendType,
              createdAt: record.createdAt
            });
          }
        } else {
          this.$message.error(res.data.message ? res.data.message : "系统错误");
        }
      });
    },
    modal_notify_close() {
      this.modal_notifyLogVisible = false;
    },
    sendMsg(item) {
      HttpService.get("/web/order/doNotify/" + item.id).then(res => {
        if (res.data.status === 0) {
          this.$message.success(res.data.message);
        } else {
          this.$message.error(res.data.message ? res.data.message : "系统错误");
        }
      });
    },
    formatStatus(v) {
      return this.payStatusMap.get(v + "");
    },
    formatApp(v) {
      return this.appMap.get(v);
    },
    formatPayType(v) {
      return this.payTypeMap.get(v);
    },
    pageChange(pagination) {
      // console.log(pagination)
    },
    moment
  },
  components: {
    ATable: Table,
    ASwitch: Switch,
    AModal: Modal
    // AMenu: Menu,
    // ABreadcrumb: Breadcrumb
  },
  filters: {
    currency
  }
};
</script>
<style scoped>
.actionBox button:last-child {
  margin-top: 5px;
}
.imgView {
  border: 1px dashed #d9d9d9;
  width: 104px;
  height: 104px;
  border-radius: 4px;
  background-color: #fafafa;
  text-align: center;
  cursor: pointer;
  -webkit-transition: border-color 0.3s ease;
  transition: border-color 0.3s ease;
  vertical-align: top;
  margin-right: 8px;
  margin-bottom: 8px;
  display: table;
  padding: 8px;
  position: relative;
}

.imgView img {
  width: 100%;
}

.imgView .hoverBox {
  display: none;
  width: 87px;
  height: 87px;
  background-color: rgba(0, 0, 0, 0.4);
  color: #fff;
  position: absolute;
  z-index: 500;
  top: 8px;
  left: 8px;
}

.imgView .hoverBox i {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 16px;
}

.imgView:hover .hoverBox {
  display: block;
}

.pUrl {
  max-width: 280px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 0;
}

input {
  width: 320px;
}

textarea {
  width: 320px;
}

.serchItem {
  margin-left: 10px;
  margin-right: 18px;
}
</style>

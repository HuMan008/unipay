<template>
  <div>
    <a-breadcrumb style="margin: 16px 0">
      <a-breadcrumb-item>退款管理</a-breadcrumb-item>
      <a-breadcrumb-item>退款订单</a-breadcrumb-item>
    </a-breadcrumb>
    <a-layout-content
      :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '300px' }"
    >
      <!-- 搜索框开始 -->
      <div class="row">
        <a-form-item label="退款申请时间" class="serchItem row">
          <a-range-picker
            :disabledDate="disabledDate"
            @change="onDickerChange"
            :showTime="{
              hideDisabledOptions: true,
              defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
            }"
            format="YYYY-MM-DD HH:mm:ss"
          />
        </a-form-item>

        <a-form-item label="订单号" class="serchItem row">
          <a-input
            title="退款查询订单号,统一订单号,应用订单号、应用退款申请订单号"
            placeholder="订单号"
            v-model="searchForm.queryId"
            style="width:180px"
          >{{searchForm.queryId}}</a-input>
        </a-form-item>

        <a-form-item label="描述" class="serchItem row">
          <a-input
            title="退款描述关键字"
            placeholder="退款描述"
            v-model="searchForm.descp"
            style="width:180px"
          >{{searchForm.descp}}</a-input>
        </a-form-item>

        <a-form-item label="状态" class="serchItem row">
          <a-select v-model="searchForm.status" style="width:150px">
            <a-select-option value>全部</a-select-option>
            <a-select-option
              v-for="item in refundStatusOptions"
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
        <div slot="refundStatusDiv" slot-scope="item">{{formatRefundStatus(item)}}</div>
        <div
          @dblclick="() => remoteQuery(item.refundOrderId)"
          slot="remoteqQueryDiv"
          slot-scope="text, item"
          title="双击我查看远程状态"
        >{{text}}</div>
        <a v-if="item.processResult===0"
          href="javaScript:void(0)"
          slot="notifyLog"
          slot-scope="item"
          @click="showNotifyLog(item)"
        >查看</a>
        <a v-if="item.processResult===0" href="#" slot="sendMsg" slot-scope="item" @click="sendMsg(item)">发送</a>
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
          <div :title="item.notifyUrl" slot="longTextShow_url" slot-scope="item">
            <p class="longtext" style="max-width:200px">  {{item.notifyUrl}}</p>
          </div>
          <div :title="item.params" slot="longTextShow_param" slot-scope="item">
            <p class="longtext" style="max-width:250px">  {{item.params}}</p>
          </div>
          <div :title="item.responseContent" slot="longTextShow_response" slot-scope="item">
            <p class="longtext" style="max-width:180px">  {{item.responseContent}}</p>
          </div>
          <div slot="sendType" slot-scope="text">{{text===0?"自动":'手动'}}</div>
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
      title: "退款管理"
    };
  },
  data() {
    return {
      collapsed: false,
      tHead: [
        {
          title: "统一退款号订单号",
          width: 180,
          dataIndex: "refundOrderId",
          scopedSlots: { customRender: "remoteqQueryDiv" }
        },
        {
          title: "应用订单号",
          width: 150,
          dataIndex: "appOrderNo"
        },
        // {
        //   title: "应用退款订单号",
        //   width: 150,
        //   dataIndex: "appOrderRefundNo"
        // },
        { title: "金额", width: 150, dataIndex: "fee" },
        {
          title: "状态",
          width: 150,
          dataIndex: "processResult",
          scopedSlots: { customRender: "refundStatusDiv" }
        },
        { title: "申请时间", width: 190, dataIndex: "applyDatetime" },
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
        endTime: "",
        beginTime: "",
        status: "",
        queryId: "",
        descp: ""
      },
      // 列表
      ListOfData: [],
      innerData: [],

      // 退款状态Options
      refundStatusOptions: [],
      // 支付方式map key:code  value:label
      refundStatusMap: new Map(),
      modal_notifyLogVisible: false,
      // 通知记录表格
      notifyLogHd: [
        { title: "通知地址", width: 200, scopedSlots: { customRender: "longTextShow_url" } },
        {
          title: "参数",
          width: 250,
          scopedSlots: { customRender: "longTextShow_param" }
        },
        { title: "响应", width: 180, scopedSlots: { customRender: "longTextShow_response" } },
        { title: "通知时间", width: 120, dataIndex: "noticeDatetime" },
        { title: "次数", width: 30, dataIndex: "repeatCount" },
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

  mounted() {
    // 加载退款状态下拉框
    this.getRefundStatusOptions();
    setTimeout(() => {
      this.getList(this.pagination.pageNo, this.pagination.pageSize);
    }, 300);
  },
  methods: {
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
          beginTime: this.searchForm.beginTime,
          endTime: this.searchForm.endTime,
          status: this.searchForm.status,
          queryId: this.searchForm.queryId,
          descp: this.searchForm.descp
        }
      };
      HttpService.post("/web/refund/list", param1).then(res => {
        const r = res.data;
        if (r.status === 0) {
          const listData = r.data.rows;
          this.pagination.total = r.data.totalCount;
          this.pagination.current = pageNo;
          for (const i in listData) {
            this.ListOfData.push({
              key: listData[i].refundOrderId,
              refundOrderId: listData[i].refundOrderId,
              orderId: listData[i].orderId,
              orderFee: listData[i].orderFee,
              appOrderRefundNo: listData[i].appOrderRefundNo,
              appOrderNo: listData[i].appOrderNo,
              applyFee: listData[i].applyFee,
              applyDatetime: listData[i].applyDatetime,
              descp: listData[i].descp,
              processResult: listData[i].processResult,
              fee: listData[i].fee,
              statusUpdateDatetime: listData[i].statusUpdateDatetime,
              innerData: [
                {
                  key: "row" + i + listData[i].orderId,
                  desp: "统一订单号",
                  val: listData[i].orderId
                },
                {
                  key: "row" + i + listData[i].orderFee,
                  desp: "支付金额",
                  val: listData[i].orderFee + "分"
                },
                {
                  key: "row" + i + listData[i].applyFee,
                  desp: "申请金额",
                  val: listData[i].applyFee + "分"
                },
                {
                  key: "row" + i + listData[i].fee,
                  desp: "成功金额",
                  val: listData[i].fee + "分"
                },
                {
                  key: "row" + i + listData[i].descp,
                  desp: "退款原因",
                  val: listData[i].descp
                },
                {
                  key: "row" + i + listData[i].createdAt,
                  desp: "创建时间",
                  val: listData[i].createdAt
                },
                {
                  key: "row" + i + listData[i].updateAt,
                  desp: "更新时间",
                  val: listData[i].updateAt
                },
                {
                  key: "row" + i + listData[i].statusUpdateDatetime,
                  desp: "状态更新时间",
                  val: listData[i].statusUpdateDatetime
                },
                {
                  key: "row" + i + listData[i].failMsg,
                  desp: "失败原因",
                  val: listData[i].failMsg
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
    // 查看异步通知记录
    showNotifyLog(item) {
      this.modal_notifyLogVisible = true;
      this.notifyLogList = [];
      HttpService.get("/web/order/getNotifyLog/" + item.orderId + "?type=REFUND").then(res => {
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
      HttpService.get("/web/order/doNotify/refund/" + item.refundOrderId).then(res => {
        if (res.data.status === 0) {
          this.$message.success(res.data.message);
        } else {
          this.$message.error(res.data.message ? res.data.message : "系统错误");
        }
      });
    },

    // 获取退款状态下拉框
    getRefundStatusOptions() {
      HttpService.get("/web/refund/status", {}).then(res => {
        if (res.data.status === 0) {
          this.refundStatusOptions = res.data.data;
          for (const i in res.data.data) {
            this.refundStatusMap.set(
              res.data.data[i].value,
              res.data.data[i].label
            );
          }
        }
      });
    },
    // 格式化退款状态
    formatRefundStatus(v) {
      return this.refundStatusMap.get(v + "");
    },
    remoteQuery(queryId) {
      HttpService.get("/web/order/queryOrderStatus?orderId=" + queryId + "&type=remoteRefund&appkey=font").then(
        res => {
          let dd = res.data;
          if (dd.status === 0) {
            let jsonData = dd.data;
            var t = jsonData.status === 0 ? "success" : jsonData.status === 1 ? "info" : "warning";
            console.log(jsonData.refundStatus)
            let refundStatus = jsonData.refundStatus === 0 ? "成功" : jsonData.refundStatus === 2 ? "失败" : "处理中/申请中";
            let description = "退款申请金额【" + jsonData.applyFee + "】分.\n" + "远程响应代码【" + jsonData.thirdCode + "】\n" + "远程响应描述【" + jsonData.thirdMsg + "】\n" + "本地状态解析【" + refundStatus + "】";
            if (jsonData.refundStatus === 0) {
              description =
                description + "通过金额【" + jsonData.passFee + "】分.";
            }
            this.$notification[t]({
              message: jsonData.orderRefundId,
              description: description
            });
          } else {
            this.$message.error(
              res.data.message ? res.data.message : "系统错误"
            );
          }
        }
      );
    },
    // 日历控件 相关开始
    disabledDate(current) {
      // 必须选今天之前的时间
      return current && current >= moment().endOf("day");
    },
    onDickerChange(date, dateString) {
      this.searchForm.beginTime = dateString[0];
      this.searchForm.endTime = dateString[1];
    },
    range(start, end) {
      const result = [];
      for (let i = start; i < end; i++) {
        result.push(i);
      }
      return result;
    },
    disabledRangeTime(_, type) {
      if (type === "start") {
        return {
          disabledHours: () => this.range(0, 60).splice(4, 20),
          disabledMinutes: () => this.range(30, 60),
          disabledSeconds: () => [55, 56]
        };
      }
      return {
        disabledHours: () => this.range(0, 60).splice(20, 4),
        disabledMinutes: () => this.range(0, 31),
        disabledSeconds: () => [55, 56]
      };
    },
    // 日历控件 相关结束
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
.row {
  flex-wrap: wrap;
}
</style>

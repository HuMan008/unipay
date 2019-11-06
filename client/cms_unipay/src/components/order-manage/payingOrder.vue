<template>
  <div>
    <a-breadcrumb style="margin: 16px 0">
      <a-breadcrumb-item>订单管理</a-breadcrumb-item>
      <a-breadcrumb-item>支付中</a-breadcrumb-item>
    </a-breadcrumb>
    <a-layout-content
      :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '300px' }"
    >
      <!-- 搜索框开始 -->
      <div class="row">
        <a-form-item label="下单时间" class="serchItem row">
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
        <a-form-item style="margin-left:30px">
          <a-button type="primary" @click="doSync">订单同步</a-button>
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
      </a-table>
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
      title: "订单管理"
    };
  },
  data() {
    return {
      collapsed: false,
      tHead: [
        {
          title: "统一订单号",
          width: 180,
          dataIndex: "id",
          scopedSlots: { customRender: "orderIdDiv" }
        },
        {
          title: "应用名称",
          width: 150,
          dataIndex: "appId",
          scopedSlots: { customRender: "appDiv" }
        },
        { title: "订单标题", width: 150, dataIndex: "subjects" },
        { title: "应用订单号", width: 150, dataIndex: "appOrderNo" },
        { title: "订单金额", width: 150, dataIndex: "fee" },
        {
          title: "支付方式",
          width: 150,
          dataIndex: "payType",
          scopedSlots: { customRender: "payTypeDiv" }
        },
        { title: "创建于", width: 190, dataIndex: "createdAt" }
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
        payType: ""
      },
      // 列表
      ListOfData: [],
      // 应用
      appListOfData: [],
      // 支付方式
      payTypeListOfData: [],
      // 支付方式map key:code  value:label
      payStatusMap: new Map(),
      payTypeMap: new Map(),
      appMap: new Map()
    };
  },

  // beforeCreate() {
  //   this.form = this.$form.createForm(this);
  // },
  mounted() {
    // this.getPayStatusOptions();
    this.getAppOptions();
    this.getPayTypeOptions();
    setTimeout(() => {
      this.getList(this.pagination.pageNo, this.pagination.pageSize);
    }, 300);
  },
  methods: {
    // 分页取
    pageList(pagination) {
      this.getList(pagination.current, this.pagination.pageSize);
    },
    // 订单同步
    doSync() {
      var param1 = {
        pageNo: 1,
        pageSize: 50,
        params: {
          beginTime: this.searchForm.beginTime,
          endTime: this.searchForm.endTime,
          payType: this.searchForm.payType
        }
      };
      HttpService.post("/web/order/doSyncOrder", param1).then(res => {
        const r = res.data;
        if (r.status === 0) {
          this.$message.success(r.message);
        } else {
          this.$message.error(r.message ? r.message : "系统错误");
        }
      });
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
          payType: this.searchForm.payType
        }
      };
      HttpService.post("/web/order/queryPayingOrder", param1).then(res => {
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
              createdAt: listData[i].createdAt
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
    formatApp(v) {
      return this.appMap.get(v);
    },
    formatPayType(v) {
      return this.payTypeMap.get(v);
    },
    pageChange(pagination) {
      // console.log(pagination)
    },
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

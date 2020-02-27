<template>
  <div>
    <a-breadcrumb style="margin: 16px 0">
      <a-breadcrumb-item>报表管理</a-breadcrumb-item>
      <a-breadcrumb-item>订单报表</a-breadcrumb-item>
    </a-breadcrumb>
    <a-layout-content
      :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '300px' }"
    >
      <!-- 搜索框开始 -->
      <div class="row">
        <a-divider orientation="left">数据筛选</a-divider>
        <a-form-item label="应用" class="serchItem row">
          <a-select v-model="searchForm.appId" style="width:250px" placeholder="默认全部应用" mode="multiple" >
            <a-select-option
              v-for="item in appListOfData"
              :key="item.value"
              :value="item.value"
            >{{item.label}}</a-select-option>
          </a-select>
        </a-form-item>
        <br>
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
          <a-select v-model="searchForm.payTypeCategory" style="width:250px"  placeholder="默认全部支付方式" mode="multiple">
            <a-select-option
              v-for="item in payTypeCategoryListOfData"
              :key="item.value"
              :value="item.value"
            >{{item.label}}</a-select-option>
          </a-select>
        </a-form-item >
        <a-divider orientation="left">分组依据</a-divider>
        <a-form-item label="日期分组" class="serchItem row">
          <a-radio-group @change="dateGroupByOnChange" v-model="searchForm.dateGroupBy" buttonStyle="solid">
            <a-radio-button value="N" checked="checked">不分</a-radio-button>
            <a-radio-button value="D">日</a-radio-button>
            <a-radio-button value="M">月</a-radio-button>
            <a-radio-button value="Y">年</a-radio-button>
          </a-radio-group>
        </a-form-item>
        <a-form-item>
          <a-checkbox @change="appGroupByOnChange" :defaultChecked="searchForm.appGroupBy=='0'">应用分组</a-checkbox>
          <a-checkbox @change="payTypeCategoryGroupByOnChange" :defaultChecked="searchForm.payTypeCategoryGroupBy=='0'">支付方式分组</a-checkbox>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="getList(1,20)">查询</a-button>
        </a-form-item>
      </div>
      <!-- 搜索框结束 -->
    <ve-histogram :data="chartData" :settings="chartSettings"></ve-histogram>
     <a-table
        :columns="tHead"
        :dataSource="listOfData"
        :pagination ="false"
      >
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
      title: "订单报表"
    };
  },
  data() {
    return {
      collapsed: false,
      tHead: [],
      listOfData: [],
      searchForm: {
        endTime: "",
        beginTime: "",
        // 默认的选中分组为按日期
        dateGroupBy: "N",
        appGroupBy: "1",
        payTypeCategoryGroupBy: "0",
        appId: [],
        payTypeCategory: []
      },
      groupSet: new Set(['支付方式']),
      // 应用
      appListOfData: [],
      // 支付状态
      // payStatusListOfData: [],
      // 支付方式
      payTypeListOfData: [],
      // 支付方式类别
      payTypeCategoryListOfData: [],
      chartData: {},
      chartSettings: {
        metrics: ['成功订单金额', '成功订单数量', '下单总金额', '下单总数', '失败订单数量', '失败订单金额', '订单成功率']
      }
    };
  },
  mounted() {
    this.getAppOptions();
    this.getPayTypeCategoryOptions();
    setTimeout(() => {
      this.getList();
    }, 300);
  },
  methods: {
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
    // 获取列表数据
    getList() {
      if (this.searchForm.dateGroupBy === 'N' && this.searchForm.appGroupBy === "1" && this.searchForm.payTypeCategoryGroupBy === "1") {
        this.$notification.warning({ message: '至少选择一个分组' });
        return;
      }
      this.ListOfData = [];
      var param1 = {
        params: {
          appId: this.searchForm.appId,
          beginTime: this.searchForm.beginTime,
          endTime: this.searchForm.endTime,
          payTypeCategory: this.searchForm.payTypeCategory,
          payTypeCategoryGroupBy: this.searchForm.payTypeCategoryGroupBy,
          dateGroupBy: this.searchForm.dateGroupBy,
          appGroupBy: this.searchForm.appGroupBy
        }
      };
      HttpService.post("/web/report/order", param1).then(res => {
        const r = res.data;
        this.chartSettings.dimension = Array.from(this.groupSet);
        if (r.status === 0) {
          // this.chartSettings.metrics = r.data.columns;
          this.chartData = r.data;
          this.tHead = [];
          for (const a in r.data.columns) {
            var x = { title: r.data.columns[a], width: 180, dataIndex: r.data.columns[a] };
            this.tHead.push(x)
          }
          this.listOfData = r.data.rows;
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
        }
        for (const a in res.data.data) {
          this.searchForm.appId.push(res.data.data[a].value)
        }
      });
    },
    // 获取支付方式列表
    getPayTypeCategoryOptions() {
      HttpService.get("/web/app/getPayTypeCategoryCombo", {}).then(res => {
        if (res.data.status === 0) {
          this.payTypeCategoryListOfData = res.data.data;
        }
        for (const a in res.data.data) {
          this.searchForm.payTypeCategory.push(res.data.data[a].value)
        }
      });
    },
    // 日期分组
    dateGroupByOnChange(e) {
      this.searchForm.dateGroupBy = e.target.value;
      if (!(this.searchForm.dateGroupBy === "N")) {
        this.groupSet.add("日期")
      } else {
        this.groupSet.delete("日期")
      }
    },
    // 应用分组
    appGroupByOnChange(e) {
      this.searchForm.appGroupBy = e.target.checked ? "0" : "1"
      if (this.searchForm.appGroupBy === "1") {
        this.groupSet.delete("应用名称")
      } else {
        this.groupSet.add("应用名称")
      }
    },
    // 支付方式分组
    payTypeCategoryGroupByOnChange(e) {
      this.searchForm.payTypeCategoryGroupBy = e.target.checked ? "0" : "1"
      if (this.searchForm.payTypeCategoryGroupBy === "1") {
        this.groupSet.delete("支付方式")
      } else {
        this.groupSet.add("支付方式")
      }
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
.row{
  flex-wrap: wrap;
}
.longText {
  max-width: 60vw;
  white-space: wrap;
  overflow: auto;
  text-overflow: inherit;
  margin-bottom: 0;
  word-wrap: break-word;
}
</style>

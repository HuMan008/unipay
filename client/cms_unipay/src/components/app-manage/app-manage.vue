<template>
  <div>
    <a-breadcrumb style="margin: 16px 0">
      <a-breadcrumb-item>应用管理</a-breadcrumb-item>
      <a-breadcrumb-item>应用列表</a-breadcrumb-item>
    </a-breadcrumb>
    <a-layout-content
      :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '300px' }"
    >
      <div class="row">
        <a-form-item label="应用名称" class="serchItem row">
          <a-input
            placeholder="应用名称关键字"
            v-model="searchForm.appName"
            style="width:180px"
          >{{searchForm.appName}}</a-input>
        </a-form-item>
        <a-form-item label="状态" class="serchItem row">
          <a-select v-model="searchForm.status">
            <a-select-option value>全部</a-select-option>
            <a-select-option
              v-for="item in status1Options"
              :key="item.value"
              :value="item.value"
            >{{item.label}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="getList(1,20)">查询</a-button>
        </a-form-item>
      </div>
      <a-form-item>
        <a-button type="primary" @click="openModal('', 1)">新增应用</a-button>
      </a-form-item>
      <a-table
        :columns="tHead"
        :dataSource="ListOfData"
        :pagination="pagination"
        @change="pageList"
      >
        <div slot="money" slot-scope="item">{{item.money / 100 | currency}}</div>
        <div slot="preset_money" slot-scope="item">{{item.preset_money / 100 | currency}}</div>
        <a-switch
          slot="modifyStatus"
          slot-scope="item"
          checkedChildren="有效"
          unCheckedChildren="无效"
          :checked="!item.status"
          :defaultChecked="!item.status"
          @click="changeStatus(item)"
        />
        <a href="javascript:void(0);" slot="editApp" slot-scope="item" @click="openModal(item,2)">编辑</a>
        <a
          href="javascript:void(0);"
          slot="payGrant"
          slot-scope="item"
          @click="openGrantPayModal(item.appKey)"
        >支付授权</a>
        <a-table
          :bordered="true"
          slot="expandedRowRender"
          slot-scope="record"
          :columns="innerColumns"
          :dataSource="record.innerData"
          :pagination="false"
        ></a-table>
      </a-table>
    </a-layout-content>

    <a-modal
      title="支付方式授权"
      :visible="grantModelVisible"
      @ok="doGrant"
      @cancel="cancelGrant"
      :confirmLoading="confirmLoading"
    >
      <template v-for="item in selectList">
        <!-- <a-card :key="item.cateName">{{item.cateName}} -->
        <a-divider orientation="left" :key="item.cateName" style="font-weight:bold;">{{item.cateName}}</a-divider>
        <a-form-item
          :label-col="{ span: 4}"
          :wrapper-col="{ span: 6 }"
          :label="item.payName"
          class="serchItem row"
          v-for="item in item.paytypeResponse"
          :key="item.payType"
        >
          <a-select
            style="width: 300px;margin-left:20px"
            :defaultValue="item.selected"
            @change="chargeChange($event,item.payType)"
          >
            <a-select-option value="0">禁用</a-select-option>
            <a-select-option
              v-for="configItem in item.chargeConfig"
              :key="configItem.id"
              :value="configItem.id"
            >{{configItem.name }}</a-select-option>
          </a-select>
        </a-form-item>
        <!-- </a-card> -->
      </template>
    </a-modal>

    <a-modal
      :title="modalTitle"
      :visible="modelVisible"
      @ok="saveOrUSubmit"
      :confirmLoading="confirmLoading"
      @cancel="closeModal"
      v-if="modelVisible"
    >
      <a-form id="saveOrUpdateForm" :form="form" @submit="saveOrUSubmit">
        <a-form-item
          label
          :label-col="{ span: 4 }"
          :wrapper-col="{ span: 12 }"
          v-if="modalType!==1"
          style="display: none"
        >
          <a-input maxlength="48" v-decorator="[
          'appKey',
          {}
        ]" />
        </a-form-item>
        <a-form-item
          label
          :label-col="{ span: 4 }"
          :wrapper-col="{ span: 12 }"
          v-if="modalType!==1"
          style="display: none"
        >
          <a-input maxlength="48" v-decorator="[
          'status',
          {}
        ]" />
        </a-form-item>

        <a-form-item label="应用名称" :label-col="{ span: 4 }" :wrapper-col="{ span: 12 }">
          <a-input
            maxlength="48"
            v-decorator="[
          'appName',
          {rules: [{ required: true, message: '请输入应用名称' }]}
        ]"
          />
        </a-form-item>
        <a-form-item label="备注" :label-col="{ span: 4 }" :wrapper-col="{ span: 12 }">
          <a-input
            type="textarea"
            maxlength="48"
            v-decorator="[
          'remark',
          {rules: [{ required: false, message: '应用备注信息' }]}
        ]"
          />
        </a-form-item>
        <a-form-item label="订单时长" :label-col="{ span: 4 }" :wrapper-col="{ span: 12 }">
          <a-input
            type="number"
            :min="5"
            :max="60"
            v-decorator="[
          'defaultOrderExpiredTime',
          {rules: [{ required: false, message: '订单有效分钟输' }]}
        ]"
          />
        </a-form-item>
        <a-form-item
          label="订单标题"
          title="默认交易标题"
          :label-col="{ span: 4 }"
          :wrapper-col="{ span: 12 }"
        >
          <a-input
            maxlength="40"
            v-decorator="[
          'orderHeader',
          {rules: [{ required: false, message: '默认交易标题' }]}
        ]"
          />
        </a-form-item>
        <a-form-item
          label="订单备注"
          title="默认交易备注"
          :label-col="{ span: 4 }"
          :wrapper-col="{ span: 12 }"
        >
          <a-input
            maxlength="100"
            v-decorator="[
          'orderDescp',
          {rules: [{ required: false, message: '默认交易备注' }]}
        ]"
          />
        </a-form-item>
        <a-form-item
          label="同步地址"
          title="默认同步地址"
          :label-col="{ span: 4 }"
          :wrapper-col="{ span: 12 }"
        >
          <a-input
            maxlength="200"
            v-decorator="[
          'syncUrl',
          {rules: [{ required: false, message: '默认交易备注' }]}
        ]"
          />
        </a-form-item>
        <a-form-item
          label="异步地址"
          title="默认异步地址"
          :label-col="{ span: 4 }"
          :wrapper-col="{ span: 12 }"
        >
          <a-input
            style="width: 320px"
            maxlength="200"
            v-decorator="[
          'asyncUrl',
          {rules: [{ required: false, message: '默认异步地址' }]}
        ]"
          />
        </a-form-item>
      </a-form>
    </a-modal>
    <!-- 状态修改确认框 -->
    <a-modal
      title="应用状态修改警告"
      v-model="modify_visible"
      :maskClosable="false"
      @ok="modify_modal_handleOk"
      @cancel="modify_modal_handleCancel"
    >
      <p>{{ modify_tipMsg}}</p>
    </a-modal>
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
      labelCol: {
        xs: { span: 3 },
        sm: { span: 4 }
      },
      status1Options: [],
      collapsed: false,
      tHead: [
        { title: "应用名称", width: 180, dataIndex: "appName" },
        { title: "应用ID", width: 150, dataIndex: "appKey" },
        {
          title: "备注",
          width: 150,
          dataIndex: "remark",
          slot: { customRender: "status" }
        },
        {
          title: "更新于",
          width: 190,
          dataIndex: "updatedAt",
          key: "updatedAt"
        },
        {
          title: "状态",
          // dataIndex: "status",
          // fixed: "left",
          key: "id",
          scopedSlots: { customRender: "modifyStatus" }
        },
        {
          title: "编辑",
          // key: "id",
          scopedSlots: { customRender: "editApp" }
        },
        {
          title: "支付方式授权",
          // key: "id",
          scopedSlots: { customRender: "payGrant" }
        }
      ],
      innerColumns: [
        { title: "列名", width: 120, dataIndex: "desp" },
        { title: "值", dataIndex: "val" }
      ],
      ListOfData: [],
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
      modelVisible: false,
      modify_visible: false,
      modify_tipMsg: "",
      modify_status_from: {
        index: "",
        key: "",
        oldStatus: "",
        newStatus: ""
      },
      confirmLoading: false,
      previewVisible: false,
      modalTitle: "",
      modalType: "",
      form: this.$form.createForm(this),
      searchForm: {
        appName: "",
        status: ""
      },
      grantModelVisible: false,
      selectList: [],
      newCharge: {},
      waitGrantAppId: "",
      editAppKey: ""
    };
  },

  // beforeCreate() {
  //   this.form = this.$form.createForm(this);
  // },
  mounted() {
    this.getStatus1Options();
    this.getList(this.pagination.pageNo, this.pagination.pageSize);
  },
  methods: {
    chargeChange(newValue, chargeType) {
      // this.newCharge.push({ payType: chargeType, select: newValue });
      this.$set(this.newCharge, chargeType, newValue);
      // this.$set(this.newCharge, this.firstCaseToLow(chargeType), newValue);
      // console.log(this.newCharge, chargeType, newValue);
    },
    firstCaseToLow(s) {
      return s.charAt(0).toLowerCase() + s.slice(1);
    },
    // 打开授权页面
    openGrantPayModal(id) {
      this.newCharge = {};
      this.selectList = [];
      this.waitGrantAppId = id;
      this.grantModelVisible = true;
      HttpService.get("/web/app/getAccounts?appkey=" + id).then(res => {
        if (res.data.status === 0) {
          this.selectList = res.data.data;
          for (const i in res.data.data) {
            let xx = res.data.data[i].paytypeResponse;
            for (const j in xx) {
              let yy = xx[j];
              this.$set(
                this.newCharge,
                yy.payType,
                yy.selected === null ? 0 : yy.selected
              );
            }
          }
        }
        // console.log(this.newCharge);
      });
    },
    // 授权对话框取消
    cancelGrant() {
      this.grantModelVisible = false;
    },
    // 授权点确认
    doGrant() {
      // this.newCharge.push("appKey", "aaa");
      this.$set(this.newCharge, "appKey", this.waitGrantAppId);
      HttpService.post("/web/app/grantpay", this.newCharge).then(
        res => {
          if (res.data.status === 0) {
            let _this = this;
            this.grantModelVisible = false;
            this.$notification.success({
              message: "操作成功",
              duration: 3,
              onClose: () => {
                _this.getList();
              }
            });
          } else {
            this.$notification.warning({ message: res.data.message });
          }
        }
      );
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
          appName: this.searchForm.appName,
          status: this.searchForm.status
        }
      };
      HttpService.post("/web/app/queryApp", param1).then(res => {
        const r = res.data;
        if (r.status === 0) {
          const listData = r.data.rows;
          this.pagination.total = r.data.totalCount;
          this.pagination.current = pageNo;
          for (const i in listData) {
            this.ListOfData.push({
              key: i,
              appKey: listData[i].appKey,
              appName: listData[i].appName,
              appSecret: listData[i].appSecret,
              status: listData[i].status,
              remark: listData[i].remark,
              syncUrl: listData[i].syncUrl,
              asyncUrl: listData[i].asyncUrl,
              orderHeader: listData[i].orderHeader,
              orderDescp: listData[i].orderDescp,
              defaultOrderExpiredTime: listData[i].defaultOrderExpiredTime,
              createdAt: listData[i].createdAt,
              updatedAt: listData[i].updatedAt,
              innerData: [
                {
                  key: "row" + i + listData[i].appSecret,
                  desp: "秘钥",
                  val: listData[i].appSecret
                },
                {
                  key: "row" + i + listData[i].orderHeader + Math.random(),
                  desp: "默认订单标题",
                  val: listData[i].orderHeader
                },
                {
                  key: "row" + i + listData[i].orderDescp + Math.random(),
                  desp: "默认订单描述",
                  val: listData[i].orderDescp
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
                },
                {
                  key: "row" + i + listData[i].defaultOrderExpiredTime,
                  desp: "默认订单分钟数",
                  val: listData[i].defaultOrderExpiredTime
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
    // 获取状态列表
    getStatus1Options() {
      HttpService.get("/web/app/getStatus", {}).then(res => {
        if (res.data.status === 0) {
          this.status1Options = res.data.data;
        }
      });
    },
    // 启用 禁用 应用
    changeStatus(item) {
      if (item.status === 1) {
        this.modify_tipMsg = "确认启用【" + item.appName + "】应用吗?";
      } else {
        this.modify_tipMsg = "确认禁用【" + item.appName + "】应用吗?";
      }
      this.modify_visible = true;
      this.modify_status_from.index = item.key;
      this.modify_status_from.key = item.appKey;
      this.modify_status_from.oldStatus = item.status;
      this.modify_status_from.newStatus = item.status === 0 ? 1 : 0;
    },
    // 状态修改框 确认按钮
    modify_modal_handleOk() {
      let url =
        "/web/app/updateStatus?appkey=" +
        this.modify_status_from.key +
        "&status=" +
        this.modify_status_from.newStatus;
      HttpService.get(url).then(res => {
        let r = res.data;
        this.modify_visible = false;
        if (r.status === 0) {
          let _this = this;
          this.$notification.success({
            message: "修改应用状态成功",
            duration: 3,
            onClose: () => {
              _this.getList();
            }
          });
        } else {
          this.$notification.error({ message: "修改应用状态失败" });
        }
      });
    },
    // 状态修改框 取消按钮
    modify_modal_handleCancel() {
      let cindex = this.modify_status_from.index;
      this.ListOfData[cindex].status = this.modify_status_from.oldStatus;
      // this.$set(this.ListOfData[cindex], status, 0);
      // this.ListOfData[cindex].appName = "FWWEFEFWEFW";
    },
    // 弹出新增或者修改对话框 item 行数据 ,type ==1 新增 其他为修改
    openModal(item, type) {
      // 开启新增编辑弹窗、
      this.editAppKey = "";
      this.modalType = type;
      this.modelVisible = true;
      if (this.modalType === 1) {
        this.modalTitle = "新增应用";
        setTimeout(() => {
          this.form.setFieldsValue({
            appName: "",
            remark: "",
            orderHeader: "",
            orderDescp: "",
            defaultOrderExpiredTime: 10,
            syncUrl: "",
            asyncUrl: ""
          });
        }, 100);
      } else {
        this.modalTitle = "编辑应用";
        this.editAppKey = item.appKey;
        setTimeout(() => {
          this.form.setFieldsValue({
            appKey: item.appKey,
            appName: item.appName,
            remark: item.remark,
            orderHeader: item.orderHeader,
            orderDescp: item.orderDescp,
            defaultOrderExpiredTime: item.defaultOrderExpiredTime,
            syncUrl: item.syncUrl,
            asyncUrl: item.asyncUrl,
            status: item.status
          });
        }, 100);
      }
    },
    // 关闭新增修改对话框
    closeModal(e) {
      // 关闭窗口 数据清除
      this.modelVisible = false;
      this.modalType = "";
    },
    // 新增对话框点确定
    saveOrUSubmit(e) {
      e.preventDefault();
      let _this = this;
      this.form.validateFields((err, values) => {
        if (!err) {
          if (_this.modalType === 1) {
            _this.submitAdd(values);
          } else {
            _this.submitEdit(values);
          }
        }
      });
    },
    pageChange(pagination) {},
    // 新增应用提交
    submitAdd(values) {
      HttpService.post("/web/app/addApp", values).then(res => {
        if (res.data.status === 0) {
          let _this = this;
          this.modelVisible = false;
          this.$notification.success({
            message: "添加应用成功",
            duration: 3,
            onClose: () => {
              _this.getList();
            }
          });
        } else {
          this.$notification.warning({ message: res.data.message });
        }
      });
    },
    // 编辑应用提交
    submitEdit(values) {
      HttpService.post("/web/app/updateApp", values).then(res => {
        if (res.data.status === 0) {
          let _this = this;
          this.modelVisible = false;
          this.$notification.success({
            message: "修改应用成功",
            duration: 3,
            onClose: () => {
              _this.getList();
            }
          });
        } else {
          this.$notification.warning({ message: res.data.message });
        }
      });
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
  width: 280px;
}

textarea {
  width: 280px;
}

.serchItem {
  margin-left: 10px;
  margin-right: 18px;
}

.ant-divider-inner-text {
    font-weight: bolder;
    display: inline-block;
    padding: 0 24px;
}
</style>

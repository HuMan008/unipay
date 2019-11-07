<template>
  <div>
    <a-breadcrumb style="margin: 16px 0">
      <a-breadcrumb-item>系统管理</a-breadcrumb-item>
      <a-breadcrumb-item>收款账号管理</a-breadcrumb-item>
    </a-breadcrumb>
    <a-layout-content
      :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '300px' }"
    >
      <div class="row">
        <a-form-item label="名称" class="serchItem row">
          <a-input
            placeholder="收款账号名称"
            v-model="searchForm.name"
            style="width:180px"
          >{{searchForm.name}}</a-input>
        </a-form-item>
        <a-form-item label="状态" class="serchItem row">
          <a-select v-model="searchForm.status">
            <a-select-option value>--全部--</a-select-option>
            <a-select-option
              v-for="item in statusCombo"
              :key="item.value"
              :value="item.value"
            >{{item.label}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="支付类型" class="serchItem row" style="width:250px">
          <a-select v-model="searchForm.payType" style="width:180px">
            <a-select-option value>--全部--</a-select-option>
            <a-select-option
              v-for="item in payTypeCombo"
              :key="item.value"
              :value="item.value"
            >{{item.label}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="getList()">查询</a-button>
        </a-form-item>
      </div>
      <a-form-item>
        <a-button type="primary" @click="openModal('', 1)">新增收款账号</a-button>
      </a-form-item>
      <a-table :columns="tHead" :dataSource="ListOfData" :pagination="false">
        <div slot="payTypeDiv" slot-scope="item">{{formatPayType(item)}}</div>
        <a-switch
          slot="modifyStatus"
          slot-scope="item"
          checkedChildren="有效"
          unCheckedChildren="无效"
          :checked="!item.status"
          :defaultChecked="!item.status"
          @click="changeStatus(item)"
        />
        <a
          href="javaScript:void(0);"
          slot="editConfig"
          slot-scope="item"
          @click="openModal(item,2)"
        >编辑</a>
        <a-table
          :bordered="true"
          slot="expandedRowRender"
          slot-scope="record"
          :columns="innerColumns"
          :dataSource="record.innerData"
          :pagination="false"
        >
          <div slot="longTextShow" slot-scope="item" :title="item.val">
            <p class="longText">{{item.val}}</p>
          </div>
        </a-table>
      </a-table>
    </a-layout-content>
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
          label="隐藏域"
          :label-col="{ span: 4 }"
          :wrapper-col="{ span: 12 }"
          style="display:none"
        >
          <a-input maxlength="48" v-decorator="[
          'id',
          {rules: []}
        ]" />
        </a-form-item>
        <a-form-item
          label="隐藏域"
          :label-col="{ span: 4 }"
          :wrapper-col="{ span: 12 }"
          style="display:none"
        >
          <a-input
            maxlength="48"
            v-decorator="[
          'status',
          {rules: []}
        ]"
          />
        </a-form-item>
        <a-form-item label="账号名称" :label-col="{ span: 4 }" :wrapper-col="{ span: 12 }">
          <a-input
            maxlength="48"
            v-decorator="[
          'name',
          {rules: [{ required: true, message: '请输入收款账号名称' }]}
        ]"
          />
        </a-form-item>
        <a-form-item label="支付类型" :label-col="{ span: 4 }" :wrapper-col="{ span: 12 }">
          <a-select
            v-decorator="['payType',{rules:[{required:true,message:'请选择支付账号类型'}]}]"
            :defaultValue="editItemPayTpye"
            :disabled="canEditItemPayType"
          >
            <a-select-option
              v-for="item in payTypeCombo"
              :key="item.value"
              :value="item.value"
            >{{item.label}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="配置JSON串" :label-col="{ span: 4 }" :wrapper-col="{ span: 12 }">
          <a-input
            type="textarea"
            v-decorator="[
          'configJson',
          {rules: [{ required: true, message: '配置JSON必填' }]}
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
      title: "收款账号管理"
    };
  },
  data() {
    return {
      payTypeCombo: [],
      payTypeComboMap: new Map(),
      statusCombo: [],
      searchForm: {
        name: "",
        payType: "",
        status: ""
      },
      collapsed: false,
      tHead: [
        // { title: "ID", width: 150, dataIndex: "id", display: "None" },
        { title: "账号名称", width: 180, dataIndex: "name" },
        { title: "支付方式", width: 150, dataIndex: "payType", scopedSlots: { customRender: "payTypeDiv" } },
        {
          title: "创建于",
          width: 190,
          dataIndex: "createdAt",
          key: "createdAt"
        },
        {
          title: "更新于",
          width: 190,
          dataIndex: "updatedAt",
          key: "updatedAt"
        },
        {
          title: "状态",
          key: "status",
          scopedSlots: { customRender: "modifyStatus" }
        },
        {
          title: "操作",
          // key: "id",
          scopedSlots: { customRender: "editConfig" }
        }
      ],
      innerColumns: [
        { title: "列名", width: 180, dataIndex: "desp" },
        {
          title: "值",
          key: "val",
          scopedSlots: { customRender: "longTextShow" }
        }
      ],
      ListOfData: [],
      modelVisible: false,
      modify_visible: false,
      modify_tipMsg: "",
      canEditItemPayType: true,
      editItemPayTpye: "",
      modify_status_form: {
        index: "",
        key: "",
        oldStatus: "",
        newStatus: ""
      },
      confirmLoading: false,
      previewVisible: false,
      modalTitle: "",
      modalType: "",
      form: this.$form.createForm(this)
    };
  },

  // beforeCreate() {
  //   this.form = this.$form.createForm(this);
  // },
  mounted() {
    this.getStatusCombo();
    this.getPayTypeCombo();
    this.getList();
  },
  methods: {
    // 获取列表数据
    getList() {
      this.ListOfData = [];
      let url =
        "/web/account/queryAccounts?name=" +
        this.searchForm.name +
        "&payType=" +
        this.searchForm.payType +
        "&status=" +
        this.searchForm.status;
      HttpService.get(url, {}).then(res => {
        const r = res.data;
        if (r.status === 0) {
          const listData = r.data;
          for (const i in listData) {
            this.ListOfData.push({
              key: i,
              id: listData[i].id,
              name: listData[i].name,
              payType: listData[i].payType,
              status: listData[i].status,
              configJson: listData[i].configJson,
              createdAt: listData[i].createdAt,
              innerData: [
                {
                  key: "row" + i + listData[i].configJson,
                  desp: "JSON串",
                  val: listData[i].configJson
                }
              ],
              updatedAt: listData[i].updatedAt
            });
          }
        } else {
          this.$message.error(r.message ? r.message : "系统错误");
        }
      });
      return [];
    },
    // 获取状态列表
    getStatusCombo() {
      HttpService.get("/web/app/getStatus", {}).then(res => {
        if (res.data.status === 0) {
          this.statusCombo = res.data.data;
        }
      });
    },
    // 获取支付方式列表
    getPayTypeCombo() {
      HttpService.get("/web/app/getAccountTypes", {}).then(res => {
        if (res.data.status === 0) {
          this.payTypeCombo = res.data.data;
          for (const i in res.data.data) {
            this.payTypeComboMap.set(res.data.data[i].value, res.data.data[i].label);
          }
        }
      });
    },
    formatPayType(v) {
      return this.payTypeComboMap.get(v);
    },
    // 启用 禁用 应用
    changeStatus(item) {
      if (item.status === 1) {
        this.modify_tipMsg = "确认启用【" + item.name + "】收款账号吗?";
      } else {
        this.modify_tipMsg = "确认禁用【" + item.name + "】收款账号吗?";
      }
      this.modify_visible = true;
      this.modify_status_form.index = item.key;
      this.modify_status_form.key = item.id;
      this.modify_status_form.oldStatus = item.status;
      this.modify_status_form.newStatus = item.status === 0 ? 1 : 0;
    },
    // 状态修改框 确认按钮
    modify_modal_handleOk() {
      let url =
        "/web/account/updateAccountStatus?id=" +
        this.modify_status_form.key +
        "&status=" +
        this.modify_status_form.newStatus;
      HttpService.post(url).then(res => {
        let r = res.data;
        this.modify_visible = false;
        if (r.status === 0) {
          let _this = this;
          this.$notification.success({
            message: "修改成功",
            duration: 3,
            onClose: () => {
              _this.getList();
            }
          });
        } else {
          this.$notification.error({ message: "修改状态失败" });
        }
      });
    },
    // 状态修改框 取消按钮
    modify_modal_handleCancel() {
      let cindex = this.modify_status_form.index;
      this.ListOfData[cindex].status = this.modify_status_form.oldStatus;
    },
    // 弹出新增或者修改对话框 item 行数据 ,type ==1 新增 其他为修改
    openModal(item, type) {
      // 开启新增编辑弹窗
      this.modalType = type;
      this.modelVisible = true;
      if (this.modalType === 1) {
        this.canEditItemPayType = false;
        this.modalTitle = "新增收款账号";
        setTimeout(() => {
          this.form.setFieldsValue({
            payType: "",
            configJson: "",
            name: "",
            status: 0
          });
        }, 200);
      } else {
        this.modalTitle = "编辑账号";
        this.canEditItemPayType = true;
        this.editItemPayTpye = item.payType;
        setTimeout(() => {
          this.form.setFieldsValue({
            payType: item.payType,
            configJson: item.configJson,
            name: item.name,
            id: item.id,
            status: item.stuatus
          });
        }, 200);
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
    // 新增应用提交
    submitAdd(values) {
      HttpService.post("/web/account/addAccount", values).then(res => {
        if (res.data.status === 0) {
          let _this = this;
          this.modelVisible = false;
          this.$notification.success({
            message: "添加收款账号成功",
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
      HttpService.post("/web/account/updateAccount", values).then(res => {
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

.longText {
  max-width: 780px;
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

<template>
  <a-layout class="myLayout">
    <a-layout-header class="header">
      <div class="logoBox">
        <div class="logo">国联支付管理平台</div>
        <!-- <a-icon class="manuTrager" :type="collapsed ? 'menu-unfold' : 'menu-fold'" @click="toggleCollapsed()" /> -->
      </div>
      <!-- <span class="manuTrager">
        <a-icon type="user"/>
        角色身份
      </span>-->
      <a-dropdown :trigger="['click']">
        <a href="#" class="ant-dropdown-link">
          <a-icon type="user" />
          {{nickName}}
          <a-icon type="down" />
        </a>
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="showModifyPwdModal">
            修改密码
            <a-modal
              title="修改密码"
              :visible="modifyPwdModalVisable"
              @ok="uppwd"
              @cancel="closeModifyPwdModal"
            >
              <a-form id="modifyPwdForm" :form="modifyPwdForm" @submit="uppwd">
                <a-form-item :label-col="{span:5}" :wrapper-col="{span:16}" label="旧密码">
                  <a-input maxlength="30"
                    placeholder="请输入当前登录用户密码"
                    v-decorator="[
          'oldPwd',
          {
            rules: [
              {
                required: true,
                message: '请输入当前密码',
              }
            ],
          },
        ]"
                    type="password"
                  />
                </a-form-item>
                <a-form-item :label-col="{ span: 5 }" :wrapper-col="{ span: 16 }" label="请密码">
                  <a-input maxlength="30"
                    placeholder="请输入新密码"
                    v-decorator="[
          'newPwd',
          {
            rules: [
              {
                required: true,
                message: '请输入新密码',
              },
              {
                validator: validateToNextPassword,
              },
            ],
          },
        ]"
                    type="password"
                  />
                </a-form-item>
                <a-form-item :label-col="{ span: 5 }" :wrapper-col="{ span:16 }" label="确认新密码">
                  <a-input maxlength="30"
                    placeholder="请再输入一次新密码"
                    v-decorator="[
          'confirm',
          {
            rules: [
              {
                required: true,
                message: '请确认新密码',
              },
              {
                validator: compareToFirstPassword,
              },
            ],
          },
        ]"
                    type="password"
                    @blur="handleConfirmBlur"
                  />
                </a-form-item>
              </a-form>
            </a-modal>
          </a-menu-item>
          <a-menu-item key="0" @click="logOut">退出登录</a-menu-item>
        </a-menu>
      </a-dropdown>
    </a-layout-header>
    <a-layout>
      <a-layout-sider v-model="collapsed">
        <a-menu
          mode="inline"
          :defaultSelectedKeys="['0']"
          :defaultOpenKeys="['sub1']"
          :style="{ height: '100%', borderRight: 0 }"
          :inlineCollapsed="collapsed"
        >
          <!-- <a-menu-item key="0" @click="routerChange('Dashboards')">
            <a-icon type="pie-chart" />
            <span>仪表盘</span>
          </a-menu-item>-->
          <a-sub-menu key="sub1" class="mySubMenu">
            <span slot="title">
              <a-icon type="user" />
              <span>系统管理</span>
            </span>
            <a-menu-item key="11" @click="routerChange('AppManage')" v-hasRole="['Admin']">应用管理</a-menu-item>
            <a-menu-item key="12" @click="routerChange('ChargeConfig')" v-hasRole="['Admin']">收款账号管理</a-menu-item>
            <a-menu-item key="13" @click="routerChange('OplogQuery')">日志列表</a-menu-item>
          </a-sub-menu>

          <a-sub-menu key="sub2" class="mySubMenu">
            <span slot="title">
              <a-icon type="user" />
              <span>订单管理</span>
            </span>
            <a-menu-item
              key="21"
              @click="routerChange('OrderManage')"
              v-hasRole="['Admin','Order']"
            >订单列表</a-menu-item>
            <a-menu-item
              key="22"
              @click="routerChange('PayingOrder')"
              v-hasRole="['Admin','Order']"
            >支付中订单</a-menu-item>
          </a-sub-menu>
        </a-menu>
      </a-layout-sider>
      <a-layout style="padding: 0 24px 24px">
        <router-view></router-view>
      </a-layout>
    </a-layout>
  </a-layout>
</template>
<script>
// import { Menu, Breadcrumb } from 'ant-design-vue'
import { Table, Switch, Modal, Dropdown } from "ant-design-vue";
import { HttpService } from "../../services/HttpService";

export default {
  name: "Layout",
  data() {
    return {
      collapsed: false,
      confirmDirty: false,
      nickName: "",
      modifyPwdModalVisable: false,
      modifyPwdForm: this.$form.createForm(this)
    };
  },
  mounted() {
    this.getInfo();
  },
  methods: {
    uppwd(e) {
      e.preventDefault();
      this.modifyPwdForm.validateFields((err, values) => {
        if (!err) {
          console.log(values);
          HttpService.post("/web/user/uppwd", values).then(res => {
            if (res.data.status === 0) {
              this.$notification.success({
                message: res.data.data,
                duration: 5,
                onClose: () => {
                  this.logOut();
                }
              });
            } else {
              this.$notification.warning({ message: res.data.message });
            }
          });
        }
      });
      console.log("点确定");
    },
    showModifyPwdModal() {
      this.modifyPwdModalVisable = true;
    },
    closeModifyPwdModal() {
      this.modifyPwdModalVisable = false;
    },
    handleConfirmBlur(e) {
      const value = e.target.value;
      this.confirmDirty = this.confirmDirty || !!value;
    },
    compareToFirstPassword(rule, value, callback) {
      const form = this.modifyPwdForm;
      if (value && value !== form.getFieldValue("newPwd")) {
        let msg = "两次新密码不一致";
        callback(msg);
      } else {
        callback();
      }
    },
    validateToNextPassword(rule, value, callback) {
      const form = this.modifyPwdForm;
      if (value && this.confirmDirty) {
        form.validateFields(["confirm"], { force: true });
      }
      callback();
    },
    toggleCollapsed() {
      this.collapsed = !this.collapsed;
    },
    routerChange(link) {
      this.$router.push({ name: link });
    },
    getInfo() {
      this.nickName = JSON.parse(
        window.sessionStorage.getItem("gt_user_info")
      ).nickName;
    },
    logOut() {
      this.$store.commit("CLEAN_USER");
      this.$router.push("/login");
    }
  },
  components: {
    ATable: Table,
    ASwitch: Switch,
    AModal: Modal,
    // AMenu: Menu,
    // ABreadcrumb: Breadcrumb
    ADropdown: Dropdown
  }
};
</script>
<style scoped>
.myLayout {
  height: 100%;
  width: 100%;
  position: absolute;
}
.header {
  background-color: #3b39aa;
  display: flex;
  flex-direction: row;
  align-items: center;
  padding-left: 0;
  justify-content: space-between;
  box-shadow: 0 2px 5px rgba(50, 50, 50, 0.4);
  z-index: 10;
}
.header .logoBox {
  display: flex;
  flex-direction: row;
}
.header .logo {
  width: 200px;
  font-size: 18px;
  color: #fff;
  /* text-align: center; */
  padding-left: 24px;
}
.manuTrager {
  font-size: 20px;
  color: #fff;
  cursor: pointer;
  line-height: 64px;
  margin-left: 24px;
}
.ant-dropdown-link {
  color: #fff;
  font-size: 16px;
  text-decoration: none !important;
}
.ant-dropdown-link svg {
  font-size: 18px;
}
.ant-menu:not(.ant-menu-horizontal) .ant-menu-item-selected {
  background-color: #f0f0f0;
}
</style>

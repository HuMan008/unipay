<template>
  <a-layout class="myLayout">
    <a-layout-header class="header">
      <div class="logoBox">
        <div class="logo" >
          国通支付管理平台
        </div>
        <!-- <a-icon class="manuTrager" :type="collapsed ? 'menu-unfold' : 'menu-fold'" @click="toggleCollapsed()" /> -->
      </div>
      <!-- <span class="manuTrager">
        <a-icon type="user"/>
        角色身份
      </span> -->
      <a-dropdown :trigger="['click']">
        <a href="#" class="ant-dropdown-link">
          <a-icon type="user"/>
          {{nickName}}
          <a-icon type="down" />
        </a>
        <a-menu slot="overlay">
          <a-menu-item key="0" @click="logOut">
            退出登录
          </a-menu-item>
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
          </a-menu-item> -->
          <a-sub-menu key="sub1" class="mySubMenu">
            <span slot="title">
              <a-icon type="user" />
              <span>系统管理</span>
            </span>
            <a-menu-item key="11" @click="routerChange('AppManage')" v-hasRole="['ADMIN']">应用管理</a-menu-item>
            <a-menu-item key="12" @click="routerChange('ChargeConfig')"  v-hasRole="['ADMIN']">收款账号管理</a-menu-item>
            <a-menu-item key="13" @click="routerChange('OplogQuery')" >日志列表</a-menu-item>
          </a-sub-menu>

           <a-sub-menu key="sub2" class="mySubMenu">
            <span slot="title">
              <a-icon type="user" />
              <span>订单管理</span>
            </span>
            <a-menu-item key="21" @click="routerChange('OrderManage')"  v-hasRole="['ADMIN','ORDER']">订单列表</a-menu-item>
            <a-menu-item key="22" @click="routerChange('PayingOrder')" v-hasRole="['ADMIN','ORDER']">支付中订单</a-menu-item>
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
import { Dropdown } from 'ant-design-vue'
export default {
  name: 'Layout',
  data() {
    return {
      collapsed: false,
      nickName: ''
    }
  },
  mounted() {
    this.getInfo()
  },
  methods: {
    toggleCollapsed () {
      this.collapsed = !this.collapsed
    },
    routerChange(link) {
      this.$router.push({name: link})
    },
    getInfo() {
      this.nickName = JSON.parse(window.sessionStorage.getItem('gt_user_info')).nickName
    },
    logOut() {
      this.$store.commit('CLEAN_USER')
      this.$router.push('/login')
    }
  },
  components: {
    // AMenu: Menu,
    // ABreadcrumb: Breadcrumb
    ADropdown: Dropdown
  }
}
</script>
<style scoped>
.myLayout{
  height: 100%;
  width: 100%;
  position: absolute;
}
.header{
  background-color: #3b39aa;
  display: flex;
  flex-direction: row;
  align-items: center;
  padding-left: 0;
  justify-content: space-between;
  box-shadow: 0 2px 5px rgba(50, 50, 50, .4);
  z-index: 10;
}
.header .logoBox{
  display: flex;
  flex-direction: row;
}
.header .logo{
  width: 200px;
  font-size: 18px;
  color: #fff;
  /* text-align: center; */
  padding-left: 24px;
}
.manuTrager{
  font-size: 20px;
  color: #fff;
  cursor: pointer;
  line-height: 64px;
  margin-left: 24px;
}
.ant-dropdown-link{
  color: #fff;
  font-size: 16px;
  text-decoration: none !important;
}
.ant-dropdown-link svg{
  font-size: 18px;
}
.ant-menu:not(.ant-menu-horizontal) .ant-menu-item-selected{
  background-color: #f0f0f0;
}
</style>

import Vue from 'vue'
import Router from 'vue-router'
// import HelloWorld from '@/components/HelloWorld'
import Dashboards from '@/components/dashboards/dashboards'
import Layout from '@/components/layout/layout'
import Login from '@/components/login/login'
import AppManage from '@/components/app-manage/app-manage';
import ChargeConfig from '@/components/charge-manage/chargeConfigManage';

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      redirect: 'Login'
    },
    {
      path: '/Login',
      name: 'Login',
      component: Login
    },
    {
      path: '/main',
      name: 'Layout',
      component: Layout,
      children: [
        {
          path: '/main/Dashboards',
          name: 'Dashboards',
          component: Dashboards
        },
        {
          path: '/main/AppManage',
          name: 'AppManage',
          component: AppManage
        },
        {
          path: '/main/ChargeConfig',
          name: 'ChargeConfig',
          component: ChargeConfig
        }
      ]
    }
  ]
})

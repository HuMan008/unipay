import Vue from 'vue'
import Router from 'vue-router'
// import HelloWorld from '@/components/HelloWorld'
import Dashboards from '@/components/dashboards/dashboards'
import Layout from '@/components/layout/layout'
import ActManage from '@/components/act-manage/act-manage'
import SelesRecord from '@/components/act-manage/seles-record'
import SelesStatistics from '@/components/act-manage/seles-statistics'
import Login from '@/components/login/login'

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
          path: '/main/ActManage',
          name: 'ActManage',
          component: ActManage
        },
        {
          path: '/main/SelesRecord',
          name: 'SelesRecord',
          component: SelesRecord
        },
        {
          path: '/main/SelesStatistics',
          name: 'SelesStatistics',
          component: SelesStatistics
        }
      ]
    }
  ]
})

// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import VCharts from "v-charts"
import store from './store'
import { Button, Layout, message, Icon, Breadcrumb, Form, Input, DatePicker, notification, Select, Divider, Card, Radio, Checkbox } from 'ant-design-vue'
import 'ant-design-vue/dist/antd.less'
import Menu from 'ant-design-vue/lib/menu'
// import Antd from 'ant-design-vue'
import Meta from 'vue-meta'
import App from './App'
import router from './router'
import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN'
import hasRole from "./components/tool/filter/hasRole"
// import Es6Promise from 'es6-promise'
// require('es6-promise').polyfill()
// Es6Promise.polyfill()

// Vue.use(Antd)
Vue.use(Meta, {
  keyName: 'metaInfo', // the component option name that vue-meta looks for meta info on.
  attribute: 'data-vue-meta', // the attribute name vue-meta adds to the tags it observes
  ssrAttribute: 'data-vue-meta-server-rendered', // the attribute name that lets vue-meta know that meta info has already been server-rendered
  tagIDKeyName: 'vmid' // the property name that vue-meta uses to determine whether to overwrite or append a tag
})
// Vue.component(Button.name, Button)
Vue.use(Layout).use(message).use(Form).use(Input).use(DatePicker).use(notification).use(Select).use(Radio).use(Checkbox)
Vue.use(Button)
Vue.use(Menu)
Vue.use(Icon)
Vue.use(Breadcrumb)
Vue.use(Divider)
Vue.use(Card)
Vue.use(hasRole)
Vue.use(VCharts)
Vue.config.productionTip = false

Vue.prototype.$message = message
Vue.prototype.$notification = notification

message.config({
  maxCount: 1
})
notification.config({
})
/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  VCharts,
  components: {
    App
  },
  template: '<App/>',
  locale: zhCN
})

<template>
  <div id="app">
    <a-locale-provider :locale="locale">
      <router-view/>
    </a-locale-provider>
  </div>
</template>

<script>
// import Vue from 'vue'
// import {HttpPlugin} from './services/HttpService.js'
// Vue.user(HttpPlugin)
import {LocaleProvider} from 'ant-design-vue'
import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN'
import moment from 'moment'
import 'moment/locale/zh-cn'
moment.locale('zh-cn')
export default {
  name: 'App',
  data() {
    return {
      locale: zhCN
    }
  },
  components: {
    ALocaleProvider: LocaleProvider
  }
}
</script>

<style>
@import url('./assets/style/main.less');
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  /* text-align: center; */
  /* color: #2c3e50; */
  /* margin-top: 60px; */
}
.ant-calendar-range .ant-calendar-in-range-cell:before{
  background: #d9d9e6;
}
</style>

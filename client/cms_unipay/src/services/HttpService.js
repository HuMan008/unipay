import Vue from 'vue'
import axios from 'axios'

import store from '../store'
import { message } from 'ant-design-vue'
import Router from '../router'

const Http = axios.create()

Http.interceptors.request.use(config => {
  if (store.getters.authenticated) {
    config.headers['gtToken'] = store.state.user.token
  }

  // if (config.autoLoading !== false) {
  //   store.dispatch('busy', true)
  // }

  return config
})

Http.interceptors.response.use(
  response => {
    // if (response.config.autoLoading !== false) {
    //   store.dispatch('busy', false)
    // }
    // if (response.data && response.data.status > 10001 && response.data.status <= 10007) {
    //   let tipMsg = ""
    //   switch (response.data.status) {
    //     case 10005:
    //       tipMsg = "TOKEN错误,请重新登录"
    //       break
    //     case 10006:
    //       tipMsg = "权限不足"
    //       break
    //     case 10007:
    //       tipMsg = "系统错误，请联系技术人员"
    //       break
    //     case 10001:
    //       tipMsg = "您未登录或登录超时"
    //       break
    //   }
    //   message.alert(tipMsg)
    //     .then(() => {
    //       store.commit('CLEAN_USER')
    //       Router.replace({ name: 'Login' })
    //     })
    //     .catch()
    //   return null;
    // }
    return response
  },
  err => {
    if (err.response) {
      switch (err.response.status) {
        case 401:
          message.alert('登录状态超时，请重新登录')
            .then(() => {
              store.commit('CLEAN_USER')
              Router.replace({name: 'Login'})
            })
            .catch()
          break
        case 504:
          message.toast({
            message: '网络连接异常，请重试',
            duration: 2000
          })
          return Promise.reject(err)
      }
    } else {
      // PopupService.toast({
      //   message: '网络连接异常，请重试',
      //   duration: 2000
      // })
    }

    return Promise.reject(err)
  }
)

export const HttpPlugin = {
  install() {
    Vue.prototype.$http = Http
  }
}

export const HttpService = Http

export const HTTP_SILENT = { autoLoading: false, alertError: false }

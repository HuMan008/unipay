import Vue from 'vue'
import Vuex from 'vuex'
// import { HTTP_SILENT } from '../services/HttpService'
// import { UserService } from '../services/UserService'

Vue.use(Vuex)
const USER_CACHE_KEY = 'gt_bike_user'

const store = new Vuex.Store({
  state: {
    busy: false,
    // 用户信息
    user: {
      token: '',
      real_name: ''
    }
  },
  mutations: {
    // ROUTER_LOADING(state, data) {
    //   state.routerLoading = data
    // },

    SET_USER(state, user) {
      state.user = user
      try {
        window.sessionStorage.setItem(USER_CACHE_KEY, JSON.stringify(user))
      } catch (error) {}
    },

    CLEAN_USER(state) {
      state.user = {}
      try {
        window.sessionStorage.setItem(
          USER_CACHE_KEY,
          JSON.stringify(state.user)
        )
      } catch (error) {}
    }
  },
  getters: {
    authenticated(state) {
      return state.user && state.user.token
    }
  },
  actions: {
    // busy({ commit }, busy) {
    //   commit('BUSY', busy)
    // },
    // routerLoading({ commit }, data) {
    //   commit('ROUTER_LOADING', data)
    // }
  }
})
try {
  const user = JSON.parse(window.sessionStorage.getItem(USER_CACHE_KEY))
  if (user) {
    store.commit('SET_USER', user)
  }
} catch (e) {}

export default store

import Vue from 'vue'
import Vuex from 'vuex'
// import { stat } from 'fs'

Vue.use(Vuex)
const CACHE_KEY_USER = 'gt_user_info'

const store = new Vuex.Store({
  state: {
    busy: false,
    user: {
      token: '',
      nickName: '',
      roleSet: {},
      permissionSet: {}
    }
  },
  mutations: {
    // ROUTER_LOADING(state, data) {
    //   state.routerLoading = data
    // },
    SET_USER(state, user) {
      this.state.user.token = user.token
      this.state.user.nickName = user.nickName
      this.state.user.roleSet = user.roleSet
      this.state.user.permissionSet = user.permissionSet
      try {
        window.sessionStorage.setItem(CACHE_KEY_USER, JSON.stringify(user))
      } catch (error) {
      }
    },

    CLEAN_USER(state) {
      state.user = {}
      try {
        window.sessionStorage.clear(CACHE_KEY_USER)
      } catch (error) {
      }
    }
  },
  getters: {
    authenticated(state) {
      return store.state.user && store.state.user.token
    },
    hasRole(state, roleCode) {
      return store.state.user.roleSet.lengh > 0 && store.state.user.roleSet.indexof(roleCode) !== -1
    },
    hasPermission(state, optCode) {
      return store.state.user.permissionSet.lengh > 0 && store.state.user.permissionSet.indexof(optCode) !== -1
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
  const user = JSON.parse(window.sessionStorage.getItem(CACHE_KEY_USER))
  if (user) {
    store.commit('SET_USER', user)
  }
} catch (e) {
}
export default store

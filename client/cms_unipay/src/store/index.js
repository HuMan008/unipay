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
      state.user = user;
      // state.user.token = user.token
      // state.user.nickName = user.nickName
      // state.user.roleSet = user.roleSet
      // state.user.permissionSet = user.permissionSet
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
      return state.user && state.user.token
    },
    hasRole(state, roleCode) {
      return state.user.roleSet.lengh > 0 && state.user.roleSet.indexof(roleCode) !== -1
    },
    hasPermission(state, optCode) {
      return state.user.permissionSet.lengh > 0 && state.user.permissionSet.indexof(optCode) !== -1
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

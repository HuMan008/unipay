
import Vue from 'vue';
import store from '../../../store/index'
const hasRole = needRoleExpirst => {
  if (typeof needRoleExpirst === "function" || typeof needRoleExpirst === "undefined") {
    return true;
  }
  let needRoleArray = [];
  if (Array.isArray(needRoleExpirst)) {
    needRoleArray = needRoleExpirst
  } else if (needRoleExpirst.indexof(",") > -1) {
    needRoleArray = needRoleExpirst.split(",");
  } else {
    needRoleArray = [needRoleExpirst];
  }
  // 当前用户的权限列表
  let roleSet = store.getters.roleSet;
  //   return needRoleList.some(e => roleSet.includes(e));
  for (let i = 0; i < needRoleArray.length; i++) {
    let roleStr = needRoleArray[i];
    if (roleSet.includes(roleStr)) {
      return true;
    }
  }
  return false;
};

// 指令
Vue.directive('hasRole', {
  bind: (el, binding, vnode) => {
    if (!hasRole(binding.value)) {
    //   el.parentNode.removeChild(el);
      el.setAttribute("style", "display:none")
    }
  }
});

// 全局判断方法
Vue.prototype.$_hasRole = hasRole;

export default hasRole

/* <div v-hasRole="[admin]">
    admin 可见
    是否为admin：{{$_hasRole('admin')}}   //true
<div></div> */

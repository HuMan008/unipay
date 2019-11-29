<template>
  <div>
     <a-breadcrumb style="margin: 16px 0">
      <a-breadcrumb-item>仪表盘</a-breadcrumb-item>
    </a-breadcrumb>
    <a-layout-content
      :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >Content
    </a-layout-content>
  </div>
</template>
<script>
// import { Menu, Breadcrumb } from 'ant-design-vue'
export default {
  data() {
    return {
      collapsed: false
    }
  },
  components: {
    // AMenu: Menu,
    // ABreadcrumb: Breadcrumb
  }
}
</script>
<style scoped>

</style>

<template>
  <div>
    <a-breadcrumb style="margin: 16px 0">
      <a-breadcrumb-item>会员卡拉新活动</a-breadcrumb-item>
      <a-breadcrumb-item>销售记录</a-breadcrumb-item>
    </a-breadcrumb>
    <a-layout-content
      :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '300px' }"
    >
      <a-form-item>
        <a-input
          class="searchInput"
          type="number"
          v-model="orderNo"
          placeholder="订单编号"
          v-decorator="[
          'orderNo',
          {rules: [{ required: true, message: 'Please input your note!' }]}
          ]"
        />
        <a-input
          class="searchInput"
          type="number"
          v-model="phone"
          placeholder="购买手机号"
          v-decorator="[
          'phone',
          {rules: [{ required: true, message: 'Please input your note!' }]}
          ]"
        />

        <!-- <a-select placeholder="购买渠道" :defaultValue="options[0].label" >
          <a-select-option v-for="item of options" :value="item.value" :key="item.value">
            {{item.label}}
          </a-select-option>
        </a-select> -->
        <select name="" id="" v-model="platform" placeholder="购买渠道" class="searchInput mySelect ant-select-selection__rendered ant-select-selection">
          <option v-for="item in options" :key="item.value" :value="item.value">
            {{item.label}}
          </option>
        </select>
        <a-range-picker
          @ok="getTime"
          showTime
          :placeholder="['开始时间','结束时间']"
          format="YYYY-MM-DD HH:mm:ss"
        />
        <a-button type="primary" style="margin-left:6px;" @click="search()">查询</a-button>
      </a-form-item>
      <a-table :columns="tHead" :dataSource="ListOfData" :scroll="{ x: 1300 }" :pagination="pagination">
        <div slot="platform" slot-scope="item">
          <span v-if="item.platform === 'app'">国通App</span>
        </div>
      </a-table>
    </a-layout-content>
  </div>
</template>
<script>
import { Table, Select } from 'ant-design-vue'
import { vipLx } from '../../services/vipLx'
import {serverApi} from '../../services/urlConfig'

const options = [
  {label: '全部', value: '0'},
  {label: '国通App', value: 'app'},
  {label: '微信', value: 'wechat'},
  {label: '第三方', value: '3'}
]
export default {
  metaInfo() {
    return {
      title: '销售记录'
    }
  },
  data() {
    return {
      orderNo: null,
      phone: null,
      selectVal: null,
      start_at: null,
      end_at: null,
      pagination: {
        pageNo: 1,
        pageSize: 10,
        total: 0
      },
      platform: null,
      options,
      tHead: [
        {title: '订单编号', width: '100', dataIndex: 'id', key: 'id'},
        {title: '购买手机号', width: '130', dataIndex: 'phone', key: 'phone'},
        {title: '购油卡号', width: '150', dataIndex: 'card_no', key: 'card_no'},
        {title: '购油时间', width: '150', dataIndex: 'created_at', key: 'created_at'},
        {title: '购油金额', width: '100', dataIndex: 'oil_money', key: 'oil_money'},
        {title: '赠油金额', width: '100', dataIndex: 'oil_present_money', key: 'oil_present_money'},
        {title: '购买渠道', width: '100', scopedSlots: { customRender: 'platform' }}
      ],
      ListOfData: []
    }
  },
  mounted() {
    this.search()
  },
  methods: {
    selectChange(val) {
      console.log(val)
    },
    getList(page, orderId, platform, phone, startAt, endAt, perPage) {
      console.log(perPage)
      vipLx.vipSelesOrder(page, perPage, orderId, platform, phone, startAt, endAt).then(res => {
        const r = res.data
        if (r.code === 0) {
          this.ListOfData = r.data.data
        }
      })
    },
    search() {
      const page = 1
      const perPage = this.pagination.pageSize
      const orderId = this.orderNo
      const phone = this.phone
      const startAt = this.start_at
      const endAt = this.end_at
      const platform = this.platform
      this.getList(page, orderId, platform, phone, startAt, endAt, perPage)
    },
    getTime(datas) {
      // console.log(this.formArr.start_time)
      // console.log(datas[0].format('YYYY-MM-DD HH:mm:ss'))
      this.start_at = datas[0].format('YYYY-MM-DD HH:mm')
      this.end_at = datas[1].format('YYYY-MM-DD HH:mm')
    },
    exportRecord() {
      window.open(`${serverApi.vipOrderExcel}?page=${this.pagination.pageNo}&per_page=${this.pagination.pageSize}&start_at=${this.start_at}&end_at=${this.end_at}&phone=${this.phone}&orderId=${this.orderNo}&platform=${this.platform}`)
    }
  },
  components: {
    ATable: Table,
    ASelect: Select
  }
}
</script>
<style scoped>
.searchInput {
  display: inline-block;
  margin-right: 7px;
  width: 160px;
  height: 32px;

}
.mySelect option{
  /* appearance: none; */
  -webkit-appearance: none;
  border: 2px solid #f0f0f0;
  border-radius: 4px;
  box-shadow: 0 2px 2px 3px rgba(0,0,0,.3);
}

</style>

<template>
  <div>
    <a-breadcrumb style="margin: 16px 0">
      <a-breadcrumb-item>会员卡拉新活动</a-breadcrumb-item>
      <a-breadcrumb-item>销售统计</a-breadcrumb-item>
    </a-breadcrumb>
        <a-layout-content
      :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '300px' }"
    >
      <a-form-item>
        <a-range-picker
          @change="getTime"
          :placeholder="['开始时间','结束时间']"
        />
        <a-button type="primary" style="margin-left: 6px;" @click="search()">查询</a-button>
        <a-button type="primary" style="margin-left: 6px;" @click="exportExcel()">导出Excel
        </a-button>
      </a-form-item>
      <a-table :columns="tHead" :dataSource="ListOfData" :scroll="{ x: 1300 }" :pagination="pagination">
        <div slot="order_money" slot-scope="item">{{item.order_money / 100 | currency}}</div>
        <div slot="order_give_money" slot-scope="item">{{item.order_give_money / 100 | currency}}</div>
      </a-table>
    </a-layout-content>
  </div>
</template>
<script>
import { Table } from 'ant-design-vue'
import { vipLx } from '../../services/vipLx'
import { currency } from '../tool/filter/currency'
import {serverApi} from '../../services/urlConfig'

export default {
  metaInfo() {
    return {
      title: '销售统计'
    }
  },
  data() {
    return {
      time: null,
      start_at: null,
      end_at: null,
      pagination: {
        pageNo: 1,
        pageSize: 10,
        total: 0
      },
      tHead: [
        {title: '日期', width: '120', dataIndex: 'record_date', key: 'record_date'},
        {title: '购油笔数', width: '100', dataIndex: 'order_num', key: 'order_num'},
        {title: '购油总金额', width: '100', scopedSlots: { customRender: 'order_money' }},
        {title: '赠油笔数', width: '100', dataIndex: 'order_give_num', key: 'order_give_num'},
        {title: '赠油总金额', width: '100', scopedSlots: { customRender: 'order_give_money' }}
      ],
      ListOfData: []
    }
  },
  mounted() {
    this.search()
  },
  methods: {
    getList(page, perPage, startAt, endAt) {
      vipLx.vipSelesStatistics(page, perPage, startAt, endAt).then(res => {
        const r = res.data
        if (r.code === 0) {
          this.ListOfData = r.data.data
        }
      })
    },
    search() {
      this.pagination.pageNo = 1
      // const page = this.pagination.pageNo
      // const perPage = this.pagination.pageSize
      console.log(this.start_at)
      this.getList(this.pagination.pageNo, this.pagination.pageSize, this.start_at, this.end_at)
    },
    getTime(datas) {
      // console.log(this.formArr.start_time)
      // console.log(datas[0].format('YYYY-MM-DD HH:mm:ss'))
      this.start_at = datas[0].format('YYYY-MM-DD')
      console.log(this.start_at)
      this.end_at = datas[1].format('YYYY-MM-DD')
    },
    exportExcel() {
      // http://bike.guotongshiyou.cn/api/v1/admin/viplx/sales/export?
      // window.location.href = `http://bike.guotongshiyou.cn/api/v1/admin/viplx/sales/export?page=${this.pagination.pageNo}`
      window.open(`${serverApi.vipStatisticsExcel}?page=${this.pagination.pageNo}&per_page=${this.pagination.pageSize}&start_at=${this.start_at}&end_at=${this.end_at}`)
      // window.location.href = `${serverApi.vipStatisticsExcel}&page=${this.pagination.pageNo}?per_page=${this.pagination.pageSize}?start_at=${this.start_at}?end_at=${this.end_at}`
      // vipLx.vipStatisticsExcel(1, 10).then(res => {

      // })
    }
  },
  components: {
    ATable: Table
  },
  filters: {
    currency
  }
}
</script>
<style scoped>

</style>

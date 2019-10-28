import {serverApi} from './urlConfig'
import { HttpService } from './HttpService'
// vip拉新活动接口
export const vipLx = {
  // 添加活动
  vipActAdd(opts) {
    return HttpService.post(serverApi.vipActAdd, opts)
  },
  // 编辑活动
  vipActEdit(opts) {
    return HttpService.post(serverApi.vipActEdit, opts)
  },
  // 活动列表
  vipActList(page, perpage) {
    return HttpService.get(`${serverApi.vipActList}?page=${page}&per_page=${perpage}`)
  },
  // 取消活动
  vipActCancel(opts) {
    return HttpService.post(serverApi.vipActCancel, opts)
  },
  // 订单列表
  vipSelesOrder(page, perPage, orderId, platform, phone, startAt, endAt) {
    let opt = {}
    opt.page = page
    opt.per_page = perPage
    if (phone) {
      opt.phone = phone
    }
    if (orderId) {
      opt.order_id = orderId
    }
    if (platform) {
      opt.platform = platform
    }
    if (startAt) {
      opt.start_at = startAt
    }
    if (endAt) {
      opt.end_at = endAt
    }
    const getUrl = serverApi.vipSelesOrder
    return HttpService({
      method: 'get',
      url: getUrl,
      params: opt
    })
  },
  // 销售统计
  vipSelesStatistics(page, perPage, startAt, endAt) {
    let opt = {}
    opt.page = page
    opt.per_page = perPage
    if (startAt) {
      opt.start_at = startAt
    }
    if (endAt) {
      opt.end_at = endAt
    }
    const getUrl = serverApi.vipSelesStatistics
    return HttpService({
      method: 'get',
      url: getUrl,
      params: opt
    })
  },
  // 销售统计导出excel
  vipStatisticsExcel(page, perPage, startAt, endAt) {
    console.log(page)
    let opt = {}
    opt.page = page
    opt.per_page = perPage
    if (startAt) {
      opt.start_at = startAt
    }
    if (endAt) {
      opt.end_at = endAt
    }
    const getUrl = serverApi.vipStatisticsExcel
    console.log(getUrl)
    return HttpService({
      method: 'get',
      url: getUrl,
      params: opt
    })
  },
  // 订单列表导出excel
  vipOrderExcel(page, perPage, orderId, platform, phone, startAt, endAt) {
    let opt = {}
    opt.page = page
    opt.per_page = perPage
    if (phone) {
      opt.phone = phone
    }
    if (orderId) {
      opt.order_id = orderId
    }
    if (platform) {
      opt.platform = platform
    }
    if (startAt) {
      opt.start_at = startAt
    }
    if (endAt) {
      opt.end_at = endAt
    }
    const getUrl = serverApi.vipOrderExcel
    return HttpService({
      method: 'get',
      url: getUrl,
      params: opt
    })
  }
}

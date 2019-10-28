// const serverRoot = 'http://127.0.0.1:9050'
// import config from '../../config'
const serverRoot = process.env.NODE_DOMAIN

export const serverApi = {
  login: serverRoot + '/login',
  vipActAdd: serverRoot + '/vipActAdd',
  vipActEdit: serverRoot + '/vipActEdit',
  vipActList: serverRoot + '/vipActList',
  vipActCancel: serverRoot + '/vipActCancel',
  vipSelesOrder: serverRoot + '/vipSelesOrder',
  vipSelesStatistics: serverRoot + '/vipSelesStatistics',
  vipOrderExcel: serverRoot + '/vipOrderExcel',
  vipStatisticsExcel: serverRoot + '/vipStatisticsExcel',
  upload: serverRoot + '/upload'
}

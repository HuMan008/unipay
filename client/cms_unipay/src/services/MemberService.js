import { HttpService } from './HttpService'
// import { serverApi } from './urlConfig'

export const MemberService = {
  login(opts) {
    return HttpService.post('/web/user/login?code=' + opts.code + '&pwd=' + opts.pwd)
  }
}

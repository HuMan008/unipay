import { HttpService } from './HttpService'
import { serverApi } from './urlConfig'

export const MemberService = {
  login(opts) {
    return HttpService.post(serverApi.login, opts)
  }
}

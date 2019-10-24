import { HttpService } from './HttpService'
import { serverApi } from './urlConfig'

export const UploadService = {
  upload(opts) {
    let config = {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }
    return HttpService.post(serverApi.upload, opts, config)
  }
}

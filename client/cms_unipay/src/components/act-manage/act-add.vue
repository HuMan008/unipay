<template>
  <div>
    <a-breadcrumb style="margin: 16px 0">
      <a-breadcrumb-item>会员卡拉新活动</a-breadcrumb-item>
      <a-breadcrumb-item>新建活动</a-breadcrumb-item>
    </a-breadcrumb>
    <a-layout-content
      :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-form-item label="活动名称" :label-col="{ span: 3 }" :wrapper-col="{ span: 10 }">
        <a-input
          v-model="formArr.name"
          v-decorator="[
          'name',
          {rules: [{ required: true, message: 'Please input your note!' }]}
        ]"
        />
      </a-form-item>
      <a-form-item label="活动时间" :label-col="{ span: 3 }" :wrapper-col="{ span: 10 }">
        <a-range-picker @change="onChange" @ok="getTime" showTime :placeholder="['开始时间','结束时间']"/>
      </a-form-item>
      <a-form-item label="交易金额" :label-col="{ span: 3 }" :wrapper-col="{ span: 10 }">
        <a-input
          type="number"
          v-model="formArr.product_info[0].money"
          v-decorator="[
          'money',
          {rules: [{ required: true, message: 'Please input your note!' }]}
        ]"
        />
      </a-form-item>
      <a-form-item label="赠送金额" :label-col="{ span: 3 }" :wrapper-col="{ span: 10 }">
        <a-input
          type="number"
          v-model="formArr.product_info[0].preset_money"
          v-decorator="[
          'preset_money',
          {rules: [{ required: true, message: 'Please input your note!' }]}
          ]"
        />
      </a-form-item>
      <a-form-item label="宣传图" :label-col="{ span: 3 }" :wrapper-col="{ span: 10 }">
        <a-upload
          accept=".png, .jpg, .jpeg"
          :action="uploadUrl"
          listType="picture-card"
          :fileList="fileList"
          @preview="handlePreview"
          @change="handleChange"
        >
          <div v-if="fileList.length < 1">
            <a-icon type="plus" />
            <div class="ant-upload-text">上传图片</div>
          </div>
        </a-upload>
        <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
          <img alt="example" style="width: 100%" :src="previewImage" />
        </a-modal>
      </a-form-item>
      <a-form-item :label-col="{ span: 3 }" :wrapper-col="{ span: 10 , offset: 3}">
        <a-button type="primary" class="login-form-button infoSub" @click="submit()">提交</a-button>
      </a-form-item>
    </a-layout-content>
  </div>
</template>
<script>
import 'moment/locale/zh-cn'
import { Upload, Modal } from 'ant-design-vue'
import { serverApi } from '../../services/urlConfig'
import { vipLx } from '../../services/vipLx'
import { UploadService } from '../../services/UploadService'
// import axios from 'axios'

export default {
  data() {
    return {
      collapsed: false,
      previewVisible: false,
      previewImage: '',
      fileList: [],
      uploadUrl: '',
      getfile: {},
      formArr: {
        name: '',
        start_time: '',
        end_time: '',
        product_info: [{
          money: '',
          preset_money: ''
        }],
        desc_picture: 'http:xxx.cloud.xxxxx.png'
      },
      time: ''
    }
  },
  mounted() {
    this.uploadUrl = serverApi.upload
  },
  methods: {
    onChange(date, dateString, timeString) {
      // console.log(dateString, timeString)
    },
    getTime(datas) {
      // console.log(datas[0].format('YYYY-MM-DD hh:mm:ss'))
      this.formArr.start_time = datas[0].format('YYYY-MM-DD hh:mm:ss')
      this.formArr.end_time = datas[1].format('YYYY-MM-DD hh:mm:ss')
    },
    handleCancel() {
      this.previewVisible = false
    },
    handlePreview(file) {
      this.previewImage = file.url || file.thumbUrl
      this.previewVisible = true
    },
    handleChange({ fileList, file }) {
      this.fileList = fileList
      // console.log(fileList[0])
      // console.log(file.status)
    },
    upload() {
      UploadService.upload(this.fileList[0]).then(res => {
        const r = res.data
        if (r.code === 0) {

        }
      })
    },
    submit() {
      // console.log(this.time)
      // console.log(this.formArr)
      vipLx.vipActAdd(this.formArr).then(res => {
        const r = res.data
        if (r.code === 0) {
          this.$notification.success({message: '添加成功'})
        } else {
          this.$notification.warning({message: r.message})
        }
      })
    }
    // getfile(file) {
    //   console.log(file)
    // }

  },
  components: {
    AUpload: Upload,
    AModal: Modal
    // ABreadcrumb: Breadcrumb
  }
}
</script>
<style scoped>
.infoSub {
  width: 140px;
}
</style>

<template>
  <div>
    <a-breadcrumb style="margin: 16px 0">
      <a-breadcrumb-item>会员卡拉新活动</a-breadcrumb-item>
      <a-breadcrumb-item>活动列表</a-breadcrumb-item>
    </a-breadcrumb>
    <a-layout-content
      :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '300px' }"
    >
      <a-form-item>
        <a-button type="primary" @click="openModal('', 1)">新增活动</a-button>
      </a-form-item>
      <a-table :columns="tHead" :dataSource="ListOfData" :scroll="{ x: 1300 }" :pagination="pagination">
        <div slot="money" slot-scope="item">{{item.money / 100 | currency}}</div>
        <div slot="preset_money" slot-scope="item">{{item.preset_money / 100 | currency}}</div>
        <div slot="desc_picture" slot-scope="item">
          <p class="pUrl">{{item.desc_picture}}</p>
        </div>
        <div slot="state" slot-scope="item">
          <span v-if="item.state === 1">初始状态</span>
          <span v-if="item.state === 5">进行中</span>
          <span v-if="item.state === 10">活动结束</span>
          <span v-if="item.state === 11">活动取消</span>
        </div>
        <div class="actionBox" slot="action" slot-scope="item">
          <a-button :disabled="item.key !== '0'" @click="cancelAct(item)" v-if="item.state !== 11">取消活动</a-button>
          <a-button :disabled="true" v-if="item.state === 11">已取消</a-button>
          <a-button
            type="primary"
            @click="openModal(item, 2)"
            :disabled="item.key !== '0'"
            v-if="item.state !== 11"
          >编辑</a-button>
          <a-button type="primary" :disabled="true" v-else>编辑</a-button>
        </div>
      </a-table>
    </a-layout-content>
    <a-modal
      :title="modalTitle"
      :visible="modelVisible"
      @ok="confirmModal"
      :confirmLoading="confirmLoading"
      @cancel="closeModal"
      v-if="modelVisible"
    >
      <a-form-item label="活动名称" :label-col="{ span: 4 }" :wrapper-col="{ span: 12 }">
        <a-input
          v-model="formArr.name"
          v-decorator="[
          'name',
          {rules: [{ required: true, message: 'Please input your note!' }]}
        ]"
        />
      </a-form-item>
      <a-form-item label="活动时间" :label-col="{ span: 4 }" :wrapper-col="{ span: 10 }">
        <a-range-picker
          @ok="getTime"
          showTime
          :placeholder="['开始时间','结束时间']"
          format="YYYY-MM-DD HH:mm:ss"
          :defaultValue="formArr.start_time ? [moment(formArr.start_time), moment(formArr.end_time)] : null"
        />
      </a-form-item>
      <a-form-item label="交易金额" :label-col="{ span: 4 }" :wrapper-col="{ span: 12 }">
        <a-input
          type="number"
          v-model="input_money"
          v-decorator="[
          'money',
          {rules: [{ required: true, message: 'Please input your note!' }]}
        ]"
        />
      </a-form-item>
      <a-form-item label="赠送金额" :label-col="{ span: 4 }" :wrapper-col="{ span: 12 }">
        <a-input
          type="number"
          v-model="input_preset_money"
          v-decorator="[
          'preset_money',
          {rules: [{ required: true, message: 'Please input your note!' }]}
          ]"
        />
      </a-form-item>
      <a-form-item label="宣传图" :label-col="{ span: 4 }" :wrapper-col="{ span: 12 }">
        <a-upload
          v-if="imgUpshow"
          accept=".png, .jpg, .jpeg"
          :header="uploadHeader"
          listType="picture-card"
          :fileList="fileList"
          @preview="handlePreview"
          @change="handleChange"
          :customRequest="myUpload"
        >
          <div v-if="fileList.length < 1">
            <a-icon type="plus" />
            <div class="ant-upload-text">上传图片</div>
          </div>
        </a-upload>
        <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
          <img alt="example" style="width: 100%" :src="previewImage" />
        </a-modal>
        <div class="imgView" v-if="!imgUpshow">
          <img :src="formArr.desc_picture" alt="">
          <div class="hoverBox">
            <a-icon type="delete" @click="deleteImg()" />
          </div>
        </div>
      </a-form-item>

    </a-modal>
  </div>
</template>
<script>
import 'moment/locale/zh-cn'
import moment from 'moment'
import { currency } from '../tool/filter/currency'
import { vipLx } from '../../services/vipLx'
import { Table, Switch, Modal, Upload } from 'ant-design-vue'
import { UploadService } from '../../services/UploadService'
import { serverApi } from '../../services/urlConfig'

export default {
  metaInfo() {
    return {
      title: 'VIP拉新活动管理'
    }
  },
  data() {
    return {
      collapsed: false,
      tHead: [
        { title: '活动名称', width: 150, dataIndex: 'name', key: 'name' },
        { title: '开始时间', width: 190, dataIndex: 'start_time', key: 'start_time' },
        { title: '结束时间', width: 190, dataIndex: 'end_time', key: 'end_time' },
        { title: '交易金额', width: 120, scopedSlots: { customRender: 'money' } },
        { title: '奖励金额', width: 120, scopedSlots: { customRender: 'preset_money' } },
        { title: '宣传图url', width: 160, scopedSlots: { customRender: 'desc_picture' } },
        { title: '活动状态', width: 100, scopedSlots: { customRender: 'state' } },
        { title: '操作', width: 150, scopedSlots: { customRender: 'action' } }
      ],
      ListOfData: [],
      searchInfo: {
        'page': 1,
        'per_page': 10
      },
      pagination: {
        pageNo: 1,
        pageSize: 10,
        total: 0
      },
      modelVisible: false,
      confirmLoading: false,
      previewVisible: false,
      modalTitle: '',
      modalType: '',
      previewImage: '',
      fileList: [],
      uploadUrl: '',
      getfile: {},
      formArr: {
        id: '',
        name: '',
        start_time: '',
        end_time: '',
        product_info: [{
          money: '',
          preset_money: ''
        }],
        desc_picture: ''
      },
      input_money: '',
      input_preset_money: '',
      uploadHeader: '',
      imgUpshow: true // 上传图片组件显示隐藏
    }
  },
  computed: {
    fen_money(money) {
      return money / 100
    }
    // fen_preset_money() {
    //   return this.input_preset_money = this.formArr.product_info[0].preset_money / 100
    // }
  },
  mounted() {
    this.uploadUrl = serverApi.upload
    this.uploadHeader = {
      authorization: JSON.parse(window.sessionStorage.getItem('gt_bike_user')).token
    }
    this.getList()
  },
  methods: {
    // 获取列表数据
    getList() {
      vipLx.vipActList(this.pagination.pageNo, this.pagination.pageSize).then(res => {
        const r = res.data
        if (r.code === 0) {
          const listData = r.data.data
          this.pagination.total = r.data.total
          // console.log(listData[0].name)
          for (const i in listData) {
            this.ListOfData.push({ 'key': i, 'id': listData[i].id, 'name': listData[i].name, 'start_time': listData[i].start_time, 'end_time': listData[i].end_time, 'money': listData[i].product_info[0].money, 'preset_money': listData[i].product_info[0].preset_money, 'desc_picture': listData[i].desc_picture, 'state': listData[i].state })
          }
        }
      })
    },
    openModal(item, type) {
      // 开启新增编辑弹窗
      this.modalType = type
      if (this.modalType === 1) {
        this.modalTitle = '新增活动'
        this.imgUpshow = true
      } else {
        this.modalTitle = '编辑活动'
        this.imgUpshow = false
        this.formArr = {
          id: item.id,
          name: item.name,
          start_time: item.start_time,
          end_time: item.end_time,
          product_info: [{
            money: item.money,
            preset_money: item.preset_money
          }],
          desc_picture: item.desc_picture
        }
        // const imgObj = {
        //   type: 'image/jpeg',
        //   status: 'done',
        //   url: item.desc_picture
        // }
        // this.fileList.push(imgObj)
        // this.fileList[0].status = 'done'
        // this.fileList[0].url = item.desc_picture
        this.fileList = [
          {url: item.desc_picture, status: 'done'}
        ]
        this.input_money = item.money / 100
        this.input_preset_money = item.preset_money / 100
      }
      // console.log(this.fileList)
      this.modelVisible = true
    },
    deleteImg() {
      // 在编辑活动中 首次显示的图片模块的删除方法
      this.imgUpshow = true
      this.fileList = []
      this.formArr.desc_picture = ''
    },
    closeModal(e) {
      // 关闭窗口 数据清除
      this.modelVisible = false
      this.modalType = ''
      this.formArr = {
        name: '',
        start_time: '',
        end_time: '',
        product_info: [{
          money: '',
          preset_money: ''
        }],
        desc_picture: ''
      }
      this.fileList = []
      this.input_money = ''
      this.input_preset_money = ''
      this.$set(this.formArr, 'start_time', null)
    },
    confirmModal() {
      // 确定提交新增

      if (!this.formArr.name) {
        this.$notification.warning({ message: '请填写活动名称' })
        return false
      } else if (!this.formArr.start_time) {
        this.$notification.warning({ message: '请选择开始时间' })
        return false
      } else if (!this.formArr.end_time) {
        this.$notification.warning({ message: '请选择结束时间' })
        return false
      }

      if (this.input_money) {
        this.formArr.product_info[0].money = this.input_money * 100
      } else {
        this.$notification.warning({ message: '请填写交易金额' })
        return false
      }

      if (this.input_preset_money) {
        this.formArr.product_info[0].preset_money = this.input_preset_money * 100
      } else {
        this.$notification.warning({ message: '请填写奖励金额' })
        return false
      }

      if (this.fileList[0]) {
        this.formArr.desc_picture = this.fileList[0].url
      } else {
        this.$notification.warning({ message: '请上传活动图片' })
        return false
      }

      if (this.modalType === 1) {
        this.submitAdd()
      } else {
        this.submitEdit()
      }
    },
    cancelAct(item) {
      // console.log(item.id)
      const postInfo = {
        'id': item.id
      }
      vipLx.vipActCancel(postInfo).then(res => {
        const r = res.data
        if (r.code === 0) {
          this.$notification.success({ message: '取消成功' })
          this.searchInfo.page = 1
          this.ListOfData = []
          this.getList()
        } else {
          this.$notification.success(r.message)
        }
      })
    },
    getTime(datas) {
      // console.log(this.formArr.start_time)
      // console.log(datas[0].format('YYYY-MM-DD HH:mm:ss'))
      this.formArr.start_time = datas[0].format('YYYY-MM-DD HH:mm:ss')
      this.formArr.end_time = datas[1].format('YYYY-MM-DD HH:mm:ss')
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
    pageChange(pagination) {
      // console.log(pagination)
    },
    myUpload(info) {
      let upFile = new FormData()
      upFile.append('ad_image', info.file)
      UploadService.upload(upFile).then(res => {
        const r = res.data
        if (r.code === 0) {
          this.$notification.success({message: '上传成功'})
          // console.log(this.fileList)
          this.fileList[0].status = 'done'
          this.fileList[0].url = r.data.path
        } else {
          this.$notification.warning({ message: r.message })
        }
      })
    },
    submitAdd() {
      vipLx.vipActAdd(this.formArr).then(res => {
        const r = res.data
        if (r.code === 0) {
          this.$notification.success({ message: '添加成功' })
          this.closeModal()
          this.modalType = ''
          this.searchInfo.page = 1
          this.ListOfData = []
          this.getList()
        } else {
          this.$notification.warning({ message: r.message })
        }
      })
    },
    submitEdit() {
      vipLx.vipActEdit(this.formArr).then(res => {
        const r = res.data
        if (r.code === 0) {
          this.$notification.success({ message: '编辑成功' })
          this.closeModal()
          this.modalType = ''
          this.searchInfo.page = 1
          this.ListOfData = []
          this.getList()
        } else {
          this.$notification.warning({ message: r.message })
        }
      })
    },
    moment
  },
  components: {
    ATable: Table,
    ASwitch: Switch,
    AModal: Modal,
    AUpload: Upload
    // AMenu: Menu,
    // ABreadcrumb: Breadcrumb
  },
  filters: {
    currency
  }
}
</script>
<style scoped>
.actionBox button:last-child {
  margin-top: 5px;
}
.imgView{
  border: 1px dashed #d9d9d9;
  width: 104px;
  height: 104px;
  border-radius: 4px;
  background-color: #fafafa;
  text-align: center;
  cursor: pointer;
  -webkit-transition: border-color 0.3s ease;
  transition: border-color 0.3s ease;
  vertical-align: top;
  margin-right: 8px;
  margin-bottom: 8px;
  display: table;
  padding: 8px;
  position: relative;
}
.imgView img{
  width: 100%;
}
.imgView .hoverBox{
  display: none;
  width: 87px;
  height: 87px;
  background-color: rgba(0, 0, 0, .4);
  color: #fff;
  position: absolute;
  z-index: 500;
  top: 8px;
  left: 8px;
}
.imgView .hoverBox i{
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 16px;
}
.imgView:hover .hoverBox{
  display: block;
}
.pUrl{
  max-width: 280px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 0;
}
</style>

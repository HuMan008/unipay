<template>
  <div>
    <a-breadcrumb style="margin: 16px 0">
      <a-breadcrumb-item>系统管理</a-breadcrumb-item>
      <a-breadcrumb-item>日志列表</a-breadcrumb-item>
    </a-breadcrumb>
    <a-layout-content
      :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '300px' }"
    >
      <!-- 搜索框开始 -->
      <div class="row">
        <a-form-item label="操作人" class="serchItem row">
          <a-input
            title="操作人"
            placeholder="操作人"
            v-model="searchForm.name"
            style="width:100px"
          >{{searchForm.name}}</a-input>
        </a-form-item>
        <a-form-item label="操作描述" class="serchItem row">
          <a-input
            title="操作描述"
            placeholder="操作描述"
            v-model="searchForm.descp"
            style="width:180px"
          >{{searchForm.descp}}</a-input>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="getList(1,20)">查询</a-button>
        </a-form-item>
      </div>
      <!-- 搜索框结束 -->
      <a-table
        :columns="tHead"
        :dataSource="ListOfData"
        :pagination="pagination"
        @change="pageList"
      >
      <a-table
          :bordered="true"
          slot="expandedRowRender"
          slot-scope="record"
          :columns="innerColumns"
          :dataSource="record.innerData"
          :pagination="false"
        >
          <div slot="longTextShow" slot-scope="item" :title="item.methodReturn">
            <p class="longText">{{item.methodReturn}}</p>
          </div>
        </a-table>
      </a-table>
    </a-layout-content>
  </div>
</template>
<script>
import { HttpService } from "../../services/HttpService";
import { Table } from "ant-design-vue";

export default {
  metaInfo() {
    return {
      title: "日志查询"
    };
  },
  data() {
    return {
      collapsed: false,
      tHead: [
        { title: "用户名", width: 100, dataIndex: "opUserName" },
        { title: "描述", width: 150, dataIndex: "descp" },
        { title: "方法", width: 150, dataIndex: "callMethod" },
        { title: "返回值", width: 100, dataIndex: "methodReturn" },
        { title: "创建于", width: 190, dataIndex: "createdAt" }
      ],
      innerColumns: [
        { title: "列名", width: 180, dataIndex: "desp" },
        {
          title: "值",
          key: "val",
          dataIndex: "val"
        }
      ],
      searchInfo: {
        page: 1,
        per_page: 20
      },
      pagination: {
        pageNo: 1,
        pageSize: 20,
        total: 0,
        current: 1
      },
      searchForm: {
        name: "",
        descp: ""
      },
      // 列表
      ListOfData: []
    };
  },

  // beforeCreate() {
  //   this.form = this.$form.createForm(this);
  // },
  mounted() {
    // this.getPayStatusOptions();
    setTimeout(() => {
      this.getList(this.pagination.pageNo, this.pagination.pageSize);
    }, 300);
  },
  methods: {
    // 分页取
    pageList(pagination) {
      this.getList(pagination.current, this.pagination.pageSize);
    },
    // 获取列表数据
    getList(pageNo, pageSize) {
      this.ListOfData = [];
      var param1 = {
        pageNo: pageNo,
        pageSize: pageSize,
        params: {
          name: this.searchForm.name,
          descp: this.searchForm.descp
        }
      };
      HttpService.post("/web/log/queryOpLogList", param1).then(res => {
        const r = res.data;
        if (r.status === 0) {
          const listData = r.data.rows;
          this.pagination.total = r.data.totalCount;
          this.pagination.current = pageNo;
          for (const i in listData) {
            this.ListOfData.push({
              opUserName: listData[i].opUserName,
              descp: listData[i].descp,
              callMethod: listData[i].callMethod,
              methodReturn: listData[i].methodReturn,
              createdAt: listData[i].createdAt,
              innerData: [
                {
                  key: "row" + i + listData[i].methodArgs,
                  desp: "参数",
                  val: listData[i].methodArgs
                }
              ]
            });
          }
        } else {
          this.$message.error(r.message ? r.message : "系统错误");
        }
      });
      return [];
    }
  },
  components: {
    ATable: Table
    // AMenu: Menu,
    // ABreadcrumb: Breadcrumb
  }
};

</script>

<style scoped>
.actionBox button:last-child {
  margin-top: 5px;
}
.imgView {
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

.imgView img {
  width: 100%;
}

.imgView .hoverBox {
  display: none;
  width: 87px;
  height: 87px;
  background-color: rgba(0, 0, 0, 0.4);
  color: #fff;
  position: absolute;
  z-index: 500;
  top: 8px;
  left: 8px;
}

.imgView .hoverBox i {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 16px;
}

.imgView:hover .hoverBox {
  display: block;
}

.pUrl {
  max-width: 280px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 0;
}

input {
  width: 320px;
}

textarea {
  width: 320px;
}

.serchItem {
  margin-left: 10px;
  margin-right: 18px;
}
</style>

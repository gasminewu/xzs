<template>
  <div class="app-container">
    <el-form :model="queryParam" ref="queryForm" :inline="true">
      <el-form-item label="阶段：">
        <el-select v-model="queryParam.level" placeholder="阶段"  @change="levelChange" clearable>
          <el-option v-for="item in levelEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="模块：">
        <el-select v-model="queryParam.subjectId" clearable>
          <el-option v-for="item in subjectFilter" :key="item.id" :value="item.id"
                     :label="item.name+' ( '+item.levelName+' )'"></el-option>
        </el-select>
      </el-form-item>
       <el-form-item label="状态：">
        <el-select v-model="queryParam.status" clearable>
          <el-option v-for="item in statusEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
        </el-select>
      </el-form-item>
       <el-form-item label="知识点：">
        <el-input v-model="queryParam.title" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">查询</el-button>
        <router-link :to="{path:'/book/edit'}" class="link-left">
          <el-button type="primary">添加</el-button>
        </router-link>
        <el-button type="primary" @click="importinsertFullBook">导入</el-button>
          <el-button type="primary" @click="taskInit">重置任务</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="listLoading" :data="tableData" border fit highlight-current-row style="width: 100%">
      <el-table-column prop="sn" label="序号" width="50px"/>
      <el-table-column prop="subjectId" label="模块" :formatter="subjectFormatter" width="200px"/>
      <el-table-column prop="title" label="知识点" show-overflow-tooltip/>
      <el-table-column prop="status" label="状态" :formatter="statusFormatter" width="70px"/>
      <el-table-column prop="createTime" label="创建时间" width="100px"/>
      <el-table-column prop="finishTime" label="完成时间" width="100px" />
      <el-table-column label="操作" align="center" width="156px">
        <template slot-scope="{row}">
         <router-link :to="{path:'/book/edit', query:{id:row.id}}" class="link-left">
            <el-button size="mini">编辑</el-button>
          </router-link>
          <el-button size="mini"  type="danger" @click="deleteQuestion(row)" class="link-left">删除</el-button>
         <!--

           <router-link :to="{path:'/book/finish', query:{id:row.id}}" class="link-left">
            <el-button size="mini">状态</el-button>
          </router-link>
         -->
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParam.pageIndex" :limit.sync="queryParam.pageSize"
                @pagination="search"/>
  </div>
</template>

<script>
import { mapGetters, mapState, mapActions } from 'vuex'
import Pagination from '@/components/Pagination'

import bookApi from '@/api/book'

export default {
  components: { Pagination },
  data () {
    return {
      queryParam: {
        id: null,
        questionType: null,
        level: null,
        subjectId: null,
        status: null,
        pageIndex: 1,
        pageSize: 10
      },
      subjectFilter: null,
      listLoading: true,
      tableData: [],
      total: 0
    }
  },
  created () {
    this.initSubject()
    this.search()
  },
  methods: {
    submitForm () {
      this.queryParam.pageIndex = 1
      this.search()
    },
    search () {
      this.listLoading = true
      bookApi.pageList(this.queryParam).then(data => {
        const re = data.response
        this.tableData = re.list
        this.total = re.total
        this.queryParam.pageIndex = re.pageNum
        this.listLoading = false
      })
    },
    importinsertFullBook () {
      this.listLoading = true
      bookApi.importinsertFullBook().then(re => {
        this.search()
        this.listLoading = false
      })
    },
    taskInit () {
      this.listLoading = true
      bookApi.taskInit(this.queryParam).then(re => {
        this.search()
        this.listLoading = false
      })
    },
    levelChange () {
      this.queryParam.subjectId = null
      this.subjectFilter = this.subjects.filter(data => data.level === this.queryParam.level)
    },
    deleteQuestion (row) {
      let _this = this
      bookApi.deletebook(row.id).then(re => {
        if (re.code === 1) {
          _this.search()
          _this.$message.success(re.message)
        } else {
          _this.$message.error(re.message)
        }
      })
    },
    pinyinFormatter (row, column, cellValue, index) {
      return this.enumFormat(this.pinyinEnum, cellValue)
    },
    statusFormatter (row, column, cellValue, index) {
      if (row.priority) {
        return this.enumFormat(this.statusEnum, cellValue) + ' (' + row.priority + '%) '
      } else {
        return this.enumFormat(this.statusEnum, cellValue)
      }
    },
    subjectFormatter (row, column, cellValue, index) {
      return this.subjectEnumFormat(cellValue)
    },
    setFormatter (row, column, cellValue, index) {
      if (cellValue && parseInt(cellValue) !== 0) {
        return '是'
      }
      return '否'
    },
    ...mapActions('exam', { initSubject: 'initSubject' })
  },
  computed: {
    ...mapGetters('enumItem', ['enumFormat']),
    ...mapState('enumItem', {
      nationEnum: state => state.book.nationEnum,
      buyEnum: state => state.book.buyEnum,
      pinyinEnum: state => state.book.pinyinEnum,
      statusEnum: state => state.book.statusEnum,
      levelEnum: state => state.user.levelEnum
    }),
    ...mapGetters('exam', ['subjectEnumFormat']),
    ...mapState('exam', { subjects: state => state.subjects })
  }
}
</script>

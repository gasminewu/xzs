<template>
  <div class="app-container">
    <el-form :model="queryParam" ref="queryForm" :inline="true">
      <el-form-item label="阶段：">
        <el-select v-model="queryParam.gradeLevel" placeholder="阶段" clearable>
          <el-option v-for="item in levelEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
        </el-select>
      </el-form-item>
        <el-form-item label="状态：">
        <el-select v-model="queryParam.status" placeholder="状态" clearable>
          <el-option v-for="item in statusEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
        </el-select>
      </el-form-item>
       <el-form-item label="优先级：">
        <el-select v-model="queryParam.priority" placeholder="优先级" clearable>
          <el-option v-for="item in priorityEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
        </el-select>
      </el-form-item>
        <el-form-item label="类别：">
        <el-select v-model="queryParam.tasktype" placeholder="任务类别" clearable>
          <el-option v-for="item in taskTypeEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
        </el-select>
      </el-form-item>
        <el-form-item label="是否开始：">
        <el-select v-model="queryParam.timetype" placeholder="是否开始" clearable>
          <el-option v-for="item in timetypeEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">查询</el-button>
         <router-link :to="{path:'/day/edit'}" class="link-left">
          <el-button type="primary">添加</el-button>
        </router-link>
        <el-button type="primary" @click="exportTask">导出</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="listLoading" :data="tableData" border fit highlight-current-row style="width: 100%">
      <el-table-column   label="序号" align="center" width="60px">
        <template slot-scope="scop">
          {{scop.$index+1}}
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="gradeLevel" label="阶段"  :formatter="levelFormatter" width="80"/>
       <el-table-column  label="时间" align="center" width="80"><template slot-scope="scope">
          <el-popover placement="top" trigger="hover">
            <div style="padding:10px">开始时间: {{ scope.row.tasktimestart }}</div>
            <div style="padding:10px">结束时间：{{scope.row.tasktimeend }}</div>
            <div style="padding:10px">归档时间：{{scope.row.finishtime }}</div>
            <div style="padding:10px">创建时间：{{scope.row.createTime }}</div>
            <div slot="reference" class="name-wrapper">
               <div>{{ scope.row.time }}</div>
            </div>
          </el-popover>
     </template>
      </el-table-column>
      <el-table-column prop="tasktimestart" label="开始时间"  width="100px"/>
      <el-table-column prop="finishtime" label="归档时间"  width="100px"/>
      <el-table-column prop="priority" label="优先级" :formatter="priorityFormatter" width="70px"/>
      <el-table-column prop="tasktype" label="类别" :formatter="tasktypeFormatter" width="60px"/>
      <el-table-column  label="操作" align="center"  width="160px">
        <template slot-scope="{row}">
          <el-button size="mini" @click="$router.push({path:'/day/edit',query:{id:row.id}})" >编辑</el-button>
          <el-button size="mini"  type="danger" @click="deleteTask(row)" class="link-left">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParam.pageIndex" :limit.sync="queryParam.pageSize"
                @pagination="search"/>
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex'
import Pagination from '@/components/Pagination'
import taskApi from '@/api/task'

export default {
  components: { Pagination },
  data () {
    return {
      queryParam: {
        gradeLevel: null,
        status: null,
        timetype: null,
        pageIndex: 1,
        pageSize: 10
      },
      listLoading: true,
      tableData: [],
      total: 0
    }
  },
  created () {
    this.search()
  },
  methods: {
    search () {
      this.listLoading = true
      taskApi.pageList(this.queryParam).then(data => {
        const re = data.response
        this.tableData = re.list
        this.total = re.total
        this.queryParam.pageIndex = re.pageNum
        this.listLoading = false
      })
    },
    exportTask () {
      this.listLoading = true
      debugger
      taskApi.exportList(this.queryParam).then(re => {
        this.listLoading = false
      })
    },
    submitForm () {
      this.queryParam.pageIndex = 1
      this.search()
    },
    deleteTask (row) {
      let _this = this
      taskApi.deleteTask(row.id).then(re => {
        if (re.code === 1) {
          _this.search()
          _this.$message.success(re.message)
        } else {
          _this.$message.error(re.message)
        }
      })
    },
    statusFormatter (row, column, cellValue, index) {
      return this.enumFormat(this.statusEnum, cellValue)
    },
    tasktypeFormatter (row, column, cellValue, index) {
      return this.enumFormat(this.taskTypeEnum, cellValue)
    },
    levelFormatter  (row, column, cellValue, index) {
      return this.enumFormat(this.levelEnum, cellValue)
    },
    priorityFormatter  (row, column, cellValue, index) {
      return this.enumFormat(this.priorityEnum, cellValue)
    },
    timeFormatter  (row, column, cellValue, index) {
      return 'ewewee'
    }
  },
  computed: {
    ...mapGetters('enumItem', ['enumFormat']),
    ...mapState('enumItem', {
      priorityEnum: state => state.task.priorityEnum,
      taskTypeEnum: state => state.task.taskTypeEnum,
      statusEnum: state => state.task.statusEnum,
      timetypeEnum: state => state.task.timetypeEnum,
      levelEnum: state => state.user.levelEnum
    })
  }
}
</script>

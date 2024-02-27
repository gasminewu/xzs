<template>
  <div class="app-container">

    <el-form :model="form" ref="form" label-width="100px" v-loading="formLoading" :rules="rules">
      <el-form-item label="阶段：" prop="gradeLevel"  required>
        <el-select v-model="form.gradeLevel" placeholder="阶段" @change="levelChange" >
          <el-option v-for="item in levelEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="标题："  prop="title" required>
        <el-input v-model="form.title"></el-input>
     </el-form-item>
     <el-form-item label="优先级：" prop="priority" required>
        <el-select v-model="form.priority" placeholder="优先级"  clearable>
          <el-option v-for="item in priorityEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
        </el-select>
      </el-form-item>
        <el-form-item label="时间限制："  prop="limitDateTime"  required >
        <el-date-picker v-model="form.limitDateTime" value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange"
                        range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期">
        </el-date-picker>
      </el-form-item>
        <el-form-item label="进度(%)：" prop="process" >
        <el-input-number v-model="form.process" :precision="0" :step="1" :max="100"></el-input-number>
      </el-form-item>
         <el-form-item label="顺序号：" prop="sn" required >
        <el-input-number v-model="form.sn" :precision="0" :step="1" :max="100"></el-input-number>
      </el-form-item>
      <el-form-item label="任务描述：" prop="taskcontent" >
        <el-input v-model="form.taskcontent"  @focus="inputClick(form,'taskcontent')" />
      </el-form-item>
        <el-form-item label="状态：" prop="status" required>
        <el-select v-model="form.status" placeholder="状态"  clearable>
          <el-option v-for="item in statusEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
        </el-select>
      </el-form-item>
       <el-form-item label="任务类别：" prop="tasktype" required>
        <el-select v-model="form.tasktype" placeholder="任务类别"  clearable>
          <el-option v-for="item in taskTypeEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
        </el-select>
      </el-form-item>
      <!--++++++++++++++++++++++++book start+++++++++++++++++++++++++++++++++++-->
      <el-form-item label="知识点："  required v-show="form.tasktype===1">
        <el-table  :data="form.bookItems" border fit highlight-current-row style="width: 100%">
          <el-table-column prop="subjectId" label="模块" :formatter="subjectFormatter" width="120px" />
          <el-table-column prop="title" label="知识点"  />
      <el-table-column prop="status" label="状态" :formatter="statusFormatter" width="80px"/>
      <el-table-column prop="createTime" label="创建时间" width="150px"/>
          <el-table-column  label="操作" align="center"  width="160px">
            <template slot-scope="{row}">
              <el-button size="mini" type="danger" @click="removeBook(row)" class="link-left">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
      <!--++++++++++++++++++++++++book end+++++++++++++++++++++++++++++++++++-->
       <!--*************************************************paper start*******************************-->
      <el-form-item label="试卷："  required v-show="form.tasktype===3">
        <el-table  :data="form.paperItems" border fit highlight-current-row style="width: 100%">
          <el-table-column prop="subjectId" label="模块" :formatter="subjectFormatter" width="120px" />
          <el-table-column prop="name" label="名称"  />
          <el-table-column  label="操作" align="center"  width="160px">
            <template slot-scope="{row}">
              <el-button size="mini" type="danger" @click="removePaper(row)" class="link-left">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
      <!--*************************************************paper end*******************************-->
      <el-form-item>
        <el-button type="primary" @click="submitForm">提交</el-button>
        <el-button @click="resetForm">重置</el-button>
        <el-button type="success" @click="addPaper" v-show="form.tasktype===3">添加试卷</el-button>
        <el-button type="success" @click="addBook" v-show="form.tasktype===1">添加知识点</el-button>
      </el-form-item>
    </el-form>
    <!--++++++++++++++++++++++++book start+++++++++++++++++++++++++++++++++++***************-->
    <el-dialog :visible.sync="bookPage.showDialog" width="70%">
      <el-form :model="bookPage.queryParam" ref="queryForm" :inline="true">
        <el-form-item label="模块：" >
          <el-select v-model="bookPage.queryParam.subjectId"  clearable>
            <el-option v-for="item in subjectFilter" :key="item.id" :value="item.id" :label="item.name+' ( '+item.levelName+' )'"></el-option>
          </el-select>
        </el-form-item>
         <el-form-item label="状态：">
        <el-select v-model="bookPage.queryParam.status" clearable>
          <el-option v-for="item in bookstatusEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
        </el-select>
      </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="examBookSubmitForm">查询</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="bookPage.listLoading" :data="bookPage.tableData"
                @selection-change="handleSelectionChangeBook" border fit highlight-current-row style="width: 100%">
        <el-table-column type="selection" width="45"></el-table-column>
        <el-table-column prop="subjectId" label="模块" :formatter="subjectFormatter" width="150px" />
        <el-table-column prop="title" label="知识点"  />
      <el-table-column prop="status" label="状态" :formatter="statusFormatter" width="80px"/>
      <el-table-column prop="createTime" label="创建时间" width="150px"/>
      </el-table>
      <pagination v-show="bookPage.total>0" :total="bookPage.total"
                  :page.sync="bookPage.queryParam.pageIndex" :limit.sync="bookPage.queryParam.pageSize"
                  @pagination="searchBook"/>
      <span slot="footer" class="dialog-footer">
          <el-button @click="bookPage.showDialog = false">取 消</el-button>
          <el-button type="primary" @click="confirmBookSelect">确定</el-button>
     </span>
    </el-dialog>
    <!--*********************************************************book end**********************************************-->
    <!-----------------------------------------paper start--------------------------------------------->
    <el-dialog :visible.sync="paperPage.showDialog" width="70%">
      <el-form :model="paperPage.queryParam" ref="queryForm" :inline="true">
        <el-form-item label="模块：" >
          <el-select v-model="paperPage.queryParam.subjectId"  clearable>
            <el-option v-for="item in subjectFilter" :key="item.id" :value="item.id" :label="item.name+' ( '+item.levelName+' )'"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="examPaperSubmitForm">查询</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="paperPage.listLoading" :data="paperPage.tableData"
                @selection-change="handleSelectionChange" border fit highlight-current-row style="width: 100%">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="Id" width="90px"/>
        <el-table-column prop="subjectId" label="模块" :formatter="subjectFormatter" width="120px" />
        <el-table-column prop="name" label="名称"  />
        <el-table-column prop="createTime" label="创建时间" width="160px"/>
      </el-table>
      <pagination v-show="paperPage.total>0" :total="paperPage.total"
                  :page.sync="paperPage.queryParam.pageIndex" :limit.sync="paperPage.queryParam.pageSize"
                  @pagination="search"/>
      <span slot="footer" class="dialog-footer">
          <el-button @click="paperPage.showDialog = false">取 消</el-button>
          <el-button type="primary" @click="confirmPaperSelect">确定</el-button>
     </span>
    </el-dialog>
    <!----------------------------------paper end------------------------------------------>
     <el-dialog  :visible.sync="richEditor.dialogVisible"  append-to-body :close-on-click-modal="false" style="width: 100%;height: 100%"   :show-close="false" center>
      <Ueditor @ready="editorReady"/>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="editorConfirm">确 定</el-button>
        <el-button @click="richEditor.dialogVisible = false">取 消</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import Ueditor from '@/components/Ueditor'
import taskApi from '@/api/task'
import examPaperApi from '@/api/examPaper'
import bookApi from '@/api/book'
import Pagination from '@/components/Pagination'
import { mapGetters, mapState, mapActions } from 'vuex'

export default {
  components: { Pagination, Ueditor },
  data () {
    return {
      form: {
        id: null,
        gradeLevel: null,
        priority: 1,
        tasktype: 0,
        status: 1,
        title: '',
        process: 0,
        limitDateTime: [],
        sn: 1,
        bookItems: [],
        paperItems: []
      },
      subjectFilter: null,
      formLoading: false,
      paperPage: {
        multipleSelection: [],
        showDialog: false,
        queryParam: {
          subjectId: null,
          level: null,
          pageIndex: 1,
          pageSize: 5
        },
        listLoading: true,
        tableData: [],
        total: 0
      },
      bookPage: {
        multipleSelection: [],
        showDialog: false,
        queryParam: {
          subjectId: null,
          level: null,
          status: 1,
          pageIndex: 1,
          pageSize: 5
        },
        listLoading: true,
        tableData: [],
        total: 0
      },
      rules: {
        gradeLevel: [{ required: true, message: '请输入项目', trigger: 'change' }],
        priority: [{ required: true, message: '请输入优先级', trigger: 'change' }],
        taskType: [{ required: true, message: '请输入任务类别', trigger: 'change' }],
        title: [{ required: true, message: '请输入任务标题', trigger: 'blur' }]
      },
      richEditor: {
        dialogVisible: false,
        object: null,
        parameterName: '',
        instance: null
      }
    }
  },
  created () {
    let _this = this
    this.initSubject(function () {
      _this.subjectFilter = _this.subjects
    })

    let id = this.$route.query.id
    if (id && parseInt(id) !== 0) {
      _this.formLoading = true
      taskApi.select(id).then(re => {
        _this.form = re.response
        _this.formLoading = false
      })
    }
  },
  methods: {
    editorReady (instance) {
      this.richEditor.instance = instance
      let currentContent = this.richEditor.object[this.richEditor.parameterName]
      this.richEditor.instance.setContent(currentContent)
      // 光标定位到Ueditor
      this.richEditor.instance.focus(true)
    },
    inputClick (object, parameterName) {
      this.richEditor.object = object
      this.richEditor.parameterName = parameterName
      this.richEditor.dialogVisible = true
    },
    editorConfirm () {
      let content = this.richEditor.instance.getContent()
      this.richEditor.object[this.richEditor.parameterName] = content
      this.richEditor.dialogVisible = false
    },
    addPaper () {
      this.paperPage.queryParam.level = this.form.gradeLevel
      this.paperPage.showDialog = true
      this.search()
    },
    addBook () {
      this.bookPage.queryParam.level = this.form.gradeLevel
      this.bookPage.showDialog = true
      this.searchBook()
    },
    confirmPaperSelect () {
      this.paperPage.multipleSelection.forEach(ep => this.form.paperItems.push(ep))
      this.paperPage.showDialog = false
    },
    confirmBookSelect () {
      this.bookPage.multipleSelection.forEach(ep => this.form.bookItems.push(ep))
      this.bookPage.showDialog = false
      debugger
    },
    search () {
      this.paperPage.showDialog = true
      this.listLoading = true
      examPaperApi.taskExamPage(this.paperPage.queryParam).then(data => {
        const re = data.response
        this.paperPage.tableData = re.list
        this.paperPage.total = re.total
        this.paperPage.queryParam.pageIndex = re.pageNum
        this.paperPage.listLoading = false
      })
    },
    searchBook () {
      this.bookPage.showDialog = true
      this.listLoading = true
      bookApi.pageList(this.bookPage.queryParam).then(data => {
        const re = data.response
        this.bookPage.tableData = re.list
        this.bookPage.total = re.total
        this.bookPage.queryParam.pageIndex = re.pageNum
        this.bookPage.listLoading = false
      })
    },
    handleSelectionChange (val) {
      this.paperPage.multipleSelection = val
    },
    handleSelectionChangeBook (val) {
      this.bookPage.multipleSelection = val
    },
    examPaperSubmitForm () {
      this.bookPage.queryParam.pageIndex = 1
      this.search()
    },
    examBookSubmitForm () {
      this.paperPage.queryParam.pageIndex = 1
      this.searchBook()
    },
    levelChange () {
      debugger
      this.paperPage.queryParam.subjectId = null
      this.bookPage.queryParam.subjectId = null
      this.subjectFilter = this.subjects.filter(data => data.level === this.form.gradeLevel)
    },
    removePaper (row) {
      this.form.paperItems.forEach((item, index, arr) => {
        if (item.id === row.id) {
          arr.splice(index, 1)
        }
      })
    },
    removeBook (row) {
      this.form.bookItems.forEach((item, index, arr) => {
        if (item.id === row.id) {
          arr.splice(index, 1)
        }
      })
    },
    submitForm () {
      let _this = this
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.formLoading = true
          taskApi.edit(this.form).then(data => {
            if (data.code === 1) {
              _this.$message.success(data.message)
              _this.delCurrentView(_this).then(() => {
                _this.$router.push('/day/list')
              })
            } else {
              _this.$message.error(data.message)
            }
            _this.formLoading = false
          }).catch(e => {
            _this.formLoading = false
          })
        } else {
          return false
        }
      })
    },
    pinyinFormatter (row, column, cellValue, index) {
      return this.enumFormat(this.pinyinEnum, cellValue)
    },
    statusFormatter (row, column, cellValue, index) {
      return this.enumFormat(this.bookstatusEnum, cellValue)
    },
    setFormatter (row, column, cellValue, index) {
      if (cellValue && parseInt(cellValue) !== 0) {
        return '是'
      }
      return '否'
    },
    resetForm () {
      let lastId = this.form.id
      this.$refs['form'].resetFields()
      this.form = {
        id: null,
        gradeLevel: null,
        priority: 1,
        tasktype: 0,
        status: 1,
        title: '',
        process: 0,
        sn: 1,
        bookItems: [],
        paperItems: []
      }
      this.form.id = lastId
    },
    subjectFormatter (row, column, cellValue, index) {
      return this.subjectEnumFormat(cellValue)
    },
    ...mapActions('exam', { initSubject: 'initSubject' }),
    ...mapActions('tagsView', { delCurrentView: 'delCurrentView' })
  },
  computed: {
    ...mapGetters('enumItem', ['enumFormat']),
    ...mapState('enumItem', {
      priorityEnum: state => state.task.priorityEnum,
      statusEnum: state => state.task.statusEnum,
      taskTypeEnum: state => state.task.taskTypeEnum,
      pinyinEnum: state => state.book.pinyinEnum,
      bookstatusEnum: state => state.book.statusEnum,
      levelEnum: state => state.user.levelEnum
    }),
    ...mapGetters('exam', ['subjectEnumFormat']),
    ...mapState('exam', { subjects: state => state.subjects })
  }
}
</script>

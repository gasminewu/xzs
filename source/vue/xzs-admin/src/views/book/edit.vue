<template>
  <div class="app-container">
    <el-form :model="form" ref="form" label-width="100px" v-loading="formLoading" :rules="rules">
      <el-form-item label="阶段：" prop="gradeLevel" required>
        <el-select v-model="form.gradeLevel" placeholder="阶段"  @change="levelChange" clearable>
          <el-option v-for="item in levelEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="模块：" prop="subjectId" required>
        <el-select v-model="form.subjectId" placeholder="模块" >
          <el-option v-for="item in subjectFilter" :key="item.id" :value="item.id" :label="item.name+' ( '+item.levelName+' )'"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="知识点：" prop="title" required>
        <el-input v-model="form.title" />
      </el-form-item>
       <el-form-item label="详解：" prop="bz">
        <el-input v-model="form.bz"  @focus="inputClick(form,'bz')" />
      </el-form-item>
 <el-form-item label="作者：" prop="autor" v-show="form.tasktype===1">
        <el-input v-model="form.autor"   />
      </el-form-item>
        <el-form-item label="喜欢程度：" v-show="form.tasktype===1">
        <el-rate v-model="form.lovel" class="question-item-rate"></el-rate>
      </el-form-item>
      <el-form-item label="拼音：" prop="pinyin" v-show="form.tasktype===1">
        <el-radio-group v-model="form.pinyin">
          <el-radio  v-for="item in pinyinEnum"  :key="item.key"  :label="item.key">{{item.value}}</el-radio>
        </el-radio-group>
      </el-form-item>
       <el-form-item label="国家：" prop="nation" v-show="form.tasktype===1">
        <el-select v-model="form.nation" placeholder="国家"   clearable>
          <el-option v-for="item in nationEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
        </el-select>
      </el-form-item>
        <el-form-item label="拥有方式：" prop="buy" v-show="form.tasktype===1">
         <el-radio-group v-model="form.buy">
          <el-radio  v-for="item in buyEnum"  :key="item.key"  :label="item.key">{{item.value}}</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="难度：" >
        <el-rate v-model="form.difficult" class="question-item-rate"></el-rate>
      </el-form-item>
         <el-form-item label="顺序号：" prop="sn" >
        <el-input-number v-model="form.sn" :precision="0" :step="1" :max="20000"></el-input-number>
      </el-form-item>
         <el-form-item label="学习情况：" prop="finishcontent">
        <el-input type="textarea" rows="3"  v-model="form.finishcontent"></el-input>
      </el-form-item>
      <el-form-item label="状态：" required>
        <el-select v-model="form.status" placeholder="状态">
          <el-option v-for="item in statusEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
        </el-select>
      </el-form-item>
        <el-form-item label="进度%：" prop="priority" >
        <el-input v-model="form.priority"  />
      </el-form-item>
        <el-form-item label="任务模式：" required>
        <el-select v-model="form.taskTimeType" placeholder="任务模块">
          <el-option v-for="item in taskTimeTypeEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
        </el-select>
      </el-form-item>
       <el-form-item label="任务：" >
        <el-table  :data="form.taskItems" border fit highlight-current-row style="width: 100%">
              <el-table-column   label="序号" align="center" width="60px">
        <template slot-scope="scop">
            {{scop.$index+1}}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="tasktimestart" label="开始时间" />
        <el-table-column prop="finishtime" label="归档时间" />
      <el-table-column prop="status" label="状态" :formatter="taskstatusFormatter" width="50px"/>
        </el-table>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitForm">提交</el-button>
      </el-form-item>
    </el-form>
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
import { mapGetters, mapState, mapActions } from 'vuex'
import bookApi from '@/api/book'

export default {
  components: {
    Ueditor
  },
  data () {
    return {
      form: {
        id: null,
        parentid: null,
        gradeLevel: null,
        subjectId: null,
        status: 1,
        title: '',
        autor: '',
        difficult: 0,
        lovel: 0,
        buy: null,
        pinyin: null,
        nation: '',
        sn: '',
        bz: ''
      },
      parentbook: {
      },
      subjectFilter: null,
      formLoading: false,
      rules: {
        gradeLevel: [
          { required: true, message: '请选择项目', trigger: 'change' }
        ],
        subjectId: [
          { required: true, message: '请选择模块', trigger: 'change' }
        ],
        title: [
          { required: true, message: '请输入书名', trigger: 'blur' }
        ]
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
    let id = this.$route.query.id
    let parentid = this.$route.query.parentid
    let _this = this
    this.initSubject(function () {
      _this.subjectFilter = _this.subjects
    })
    if (parentid && parseInt(parentid) !== 0) {
      _this.formLoading = true
      bookApi.select(parentid).then(re => {
        _this.parentbook = re.response
        _this.form = _this.parentbook
        _this.form.id = null
        _this.form.status = 1
        _this.form.parentid = parentid
        _this.formLoading = false
      })
    }

    if (id && parseInt(id) !== 0) {
      _this.formLoading = true
      bookApi.select(id).then(re => {
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
    submitForm () {
      let _this = this
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.formLoading = true
          bookApi.edit(this.form).then(re => {
            if (re.code === 1) {
              _this.$message.success(re.message)
              _this.delCurrentView(_this).then(() => {
                _this.$router.push('/book/list')
              })
            } else {
              _this.$message.error(re.message)
              this.formLoading = false
            }
          }).catch(e => {
            this.formLoading = false
          })
        } else {
          return false
        }
      })
    },
    levelChange () {
      this.form.subjectId = null
      this.subjectFilter = this.subjects.filter(data => data.level === this.form.gradeLevel)
    },
    taskstatusFormatter (row, column, cellValue, index) {
      return this.enumFormat(this.taskstatusEnum, cellValue)
    },
    ...mapActions('exam', { initSubject: 'initSubject' }),
    ...mapActions('tagsView', { delCurrentView: 'delCurrentView' })
  },
  computed: {
    ...mapGetters('enumItem', ['enumFormat']),
    ...mapState('enumItem', {
      nationEnum: state => state.book.nationEnum,
      buyEnum: state => state.book.buyEnum,
      pinyinEnum: state => state.book.pinyinEnum,
      taskstatusEnum: state => state.task.statusEnum,
      levelEnum: state => state.user.levelEnum,
      taskTimeTypeEnum: state => state.book.taskTimeTypeEnum,
      statusEnum: state => state.book.statusEnum
    }),
    ...mapState('exam', { subjects: state => state.subjects })
  }
}
</script>

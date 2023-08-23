// initial state
const state = {
  user: {
    tfEnum: [{ key: 0, value: '否' }, { key: 1, value: '是' }],
    sexEnum: [{ key: 1, value: '男' }, { key: 2, value: '女' }],
    statusEnum: [{ key: 1, value: '启用' }, { key: 2, value: '禁用' }],
    levelEnum: [{ key: 1, value: '绘本' }, { key: 2, value: '运动' }, { key: 3, value: '古诗' }, { key: 4, value: '语文' }, { key: 5, value: '数学' }],
    roleEnum: [{ key: 1, value: '学生' }, { key: 2, value: '教师' }, { key: 3, value: '管理员' }],
    statusTag: [{ key: 1, value: 'success' }, { key: 2, value: 'danger' }],
    statusBtn: [{ key: 1, value: '禁用' }, { key: 2, value: '启用' }]
  },
  exam: {
    examPaper: {
      paperTypeEnum: [{ key: 1, value: '固定试卷' }, { key: 4, value: '时段试卷' }, { key: 6, value: '任务试卷' }]
    },
    question: {
      typeEnum: [{ key: 1, value: '单选题' }, { key: 2, value: '多选题' }, { key: 3, value: '判断题' }, { key: 4, value: '填空题' }, { key: 5, value: '简答题' }],
      editUrlEnum: [{ key: 1, value: '/exam/question/edit/singleChoice', name: '单选题' },
        { key: 2, value: '/exam/question/edit/multipleChoice', name: '多选题' },
        { key: 3, value: '/exam/question/edit/trueFalse', name: '判断题' },
        { key: 4, value: '/exam/question/edit/gapFilling', name: '填空题' },
        { key: 5, value: '/exam/question/edit/shortAnswer', name: '简答题' }]
    }
  },
  book: {
    pinyinEnum: [{ key: 0, value: '无' }, { key: 1, value: '有' }],
    nationEnum: [{ key: 'home', value: '国内' }, { key: 'Japan', value: '日本' }, { key: 'UK', value: '英国' }, { key: 'French', value: '法国' },
      { key: 'America', value: '美国' }],
    statusEnum: [{ key: 1, value: '正常' }, { key: 2, value: '归档' }, { key: 3, value: '计划购买' }],
    buyEnum: [{ key: 1, value: '购买' }, { key: 2, value: '借阅' }]
  }
}

// getters
const getters = {
  enumFormat: (state) => (arrary, key) => {
    return format(arrary, key)
  }
}

// actions
const actions = {}

// mutations
const mutations = {}

const format = function (array, key) {
  for (let item of array) {
    if (item.key === key) {
      return item.value
    }
  }
  return null
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}

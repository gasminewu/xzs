import subjectApi from '@/api/subject'

const state = {
  subjects: []
}

const getters = {
  subjectEnumFormat: (state) => (key) => {
    for (let item of state.subjects) {
      if (item.id === key) {
        return item.levelName + '-' + item.name
      }
    }
    return null
  }
}

// actions
const actions = {
  initSubject ({ commit }, action) {
    subjectApi.list().then(re => {
      commit('setSubjects', re.response)
      if (action !== undefined) {
        action()
      }
    })
  }
}

// mutations
const mutations = {
  setSubjects: (state, subjects) => {
    state.subjects = subjects
  }
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}

import Vue from 'vue'
import Router from 'vue-router'
import Layout from '@/layout'

Vue.use(Router)

const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: false,
    children: [
      {
        path: '/redirect/:path*',
        component: () => import('@/views/redirect/index')
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    hidden: true,
    component: () => import('@/views/login/index'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index'),
        name: 'Dashboard',
        meta: { title: '主页', icon: 'home', affix: true }
      }
    ]
  },
  {
    path: '/day',
    component: Layout,
    name: 'DayPage',
    meta: {
      title: '日程',
      icon: 'task'
    },
    alwaysShow: true,
    children: [
      {
        path: 'list',
        component: () => import('@/views/day/list'),
        name: 'DayPage',
        meta: { title: '日程列表', noCache: true }
      },
      {
        path: 'edit',
        component: () => import('@/views/day/edit'),
        name: 'DayEditPage',
        meta: { title: '日程编辑', noCache: true, activeMenu: '/day/list' },
        hidden: false
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    name: 'UserPage',
    hidden: true,
    meta: {
      title: '用户管理',
      icon: 'users'
    },
    children: [
      {
        path: 'student/list',
        component: () => import('@/views/user/student/list'),
        name: 'UserStudentPageList',
        meta: { title: '学生列表', noCache: true }
      },
      {
        path: 'student/edit',
        component: () => import('@/views/user/student/edit'),
        name: 'UserStudentEdit',
        meta: { title: '学生编辑', noCache: true, activeMenu: '/user/student/list' },
        hidden: false
      },
      {
        path: 'admin/list',
        component: () => import('@/views/user/admin/list'),
        name: 'UserAdminPageList',
        meta: { title: '管理员列表', noCache: true }
      },
      {
        path: 'admin/edit',
        component: () => import('@/views/user/admin/edit'),
        name: 'UserAdminEdit',
        meta: { title: '管理员编辑', noCache: true, activeMenu: '/user/admin/list' },
        hidden: false
      }
    ]
  },
  {
    path: '/exam',
    component: Layout,
    name: 'ExamPage',
    meta: {
      title: '卷题管理',
      icon: 'exam'
    },
    children: [
      {
        path: 'paper/list',
        component: () => import('@/views/exam/paper/list'),
        name: 'ExamPaperPageList',
        meta: { title: '试卷列表', noCache: true }
      },
      {
        path: 'paper/edit',
        component: () => import('@/views/exam/paper/edit'),
        name: 'ExamPaperEdit',
        meta: { title: '试卷编辑', noCache: true, activeMenu: '/exam/paper/list' },
        hidden: false
      },
      {
        path: 'question/list',
        component: () => import('@/views/exam/question/list'),
        name: 'ExamQuestionPageList',
        meta: { title: '题目列表', noCache: true }
      },
      {
        path: 'question/edit/singleChoice',
        component: () => import('@/views/exam/question/edit/single-choice'),
        name: 'singleChoicePage',
        meta: { title: '单选题编辑', noCache: true, activeMenu: '/exam/question/list' },
        hidden: false
      },
      {
        path: 'question/edit/multipleChoice',
        component: () => import('@/views/exam/question/edit/multiple-choice'),
        name: 'multipleChoicePage',
        meta: { title: '多选题编辑', noCache: true, activeMenu: '/exam/question/list' },
        hidden: false
      },
      {
        path: 'question/edit/trueFalse',
        component: () => import('@/views/exam/question/edit/true-false'),
        name: 'trueFalsePage',
        meta: { title: '判断题编辑', noCache: true, activeMenu: '/exam/question/list' },
        hidden: false
      },
      {
        path: 'question/edit/gapFilling',
        component: () => import('@/views/exam/question/edit/gap-filling'),
        name: 'gapFillingPage',
        meta: { title: '填空题编辑', noCache: true, activeMenu: '/exam/question/list' },
        hidden: false
      },
      {
        path: 'question/edit/shortAnswer',
        component: () => import('@/views/exam/question/edit/short-answer'),
        name: 'shortAnswerPage',
        meta: { title: '简答题编辑', noCache: true, activeMenu: '/exam/question/list' },
        hidden: false
      }
    ]
  },
  {
    path: '/task',
    component: Layout,
    hidden: true,
    name: 'TaskPage',
    meta: {
      title: '任务管理',
      icon: 'task'
    },
    children: [
      {
        path: 'list',
        component: () => import('@/views/task/list'),
        name: 'TaskListPage',
        hidden: false,
        meta: { title: '任务列表', noCache: true }
      },
      {
        path: 'edit',
        component: () => import('@/views/task/edit'),
        name: 'TaskEditPage',
        hidden: false,
        meta: { title: '任务创建', noCache: true }
      }
    ]
  },
  {
    path: '/book',
    component: Layout,
    name: 'BookPage',
    meta: {
      title: '知识库',
      icon: 'education'
    },
    alwaysShow: true,
    children: [
      {
        path: 'list',
        component: () => import('@/views/book/list'),
        name: 'BookPage',
        meta: { title: '知识列表', noCache: true }
      },
      {
        path: 'edit',
        component: () => import('@/views/book/edit'),
        name: 'BookEditPage',
        meta: { title: '知识编辑', noCache: true, activeMenu: '/book/list' },
        hidden: false
      },
      {
        path: 'finish',
        component: () => import('@/views/book/finish'),
        name: 'BookFinishPage',
        meta: { title: '更改状态', noCache: true, activeMenu: '/book/list' },
        hidden: true
      }
    ]
  },
  {
    path: '/answer',
    component: Layout,
    hidden: true,
    name: 'AnswerPage',
    meta: {
      title: '成绩管理',
      icon: 'answer'
    },
    alwaysShow: true,
    children: [
      {
        path: 'list',
        component: () => import('@/views/answer/list'),
        name: 'AnswerPageList',
        meta: { title: '答卷列表', noCache: true }
      }
    ]
  },
  {
    path: '/education',
    component: Layout,
    name: 'EducationPage',
    meta: {
      title: '模块管理',
      icon: 'education'
    },
    alwaysShow: true,
    children: [
      {
        path: 'subject/list',
        component: () => import('@/views/education/subject/list'),
        name: 'EducationSubjectPage',
        meta: { title: '模块列表', noCache: true }
      },
      {
        path: 'subject/edit',
        component: () => import('@/views/education/subject/edit'),
        name: 'EducationSubjectEditPage',
        meta: { title: '模块编辑', noCache: true, activeMenu: '/education/subject/list' },
        hidden: true
      }
    ]
  },
  {
    path: '/message',
    component: Layout,
    name: 'MessagePage',
    hidden: true,
    meta: {
      title: '消息中心',
      icon: 'message'
    },
    alwaysShow: true,
    children: [
      {
        path: 'list',
        component: () => import('@/views/message/list'),
        name: 'MessageListPage',
        meta: { title: '消息列表', noCache: true }
      },
      {
        path: 'send',
        component: () => import('@/views/message/send'),
        name: 'MessageSendPage',
        meta: { title: '消息发送', noCache: true }
      }
    ]
  },
  {
    path: '/log',
    component: Layout,
    name: 'LogPage',
    hidden: true,
    meta: {
      title: '日志中心',
      icon: 'log'
    },
    alwaysShow: true,
    children: [
      {
        path: 'user/list',
        component: () => import('@/views/log/list'),
        name: 'LogUserPage',
        meta: { title: '用户日志', noCache: true }
      }
    ]
  },
  {
    path: '/profile',
    component: Layout,
    hidden: true,
    children: [
      {
        path: 'index',
        component: () => import('@/views/profile/index'),
        name: 'Profile',
        meta: { title: '个人简介', icon: 'user', noCache: true }
      }
    ]
  },
  { path: '*',
    hidden: true,
    component: () => import('@/views/error-page/404'),
    meta: { title: '404', noCache: true }
  }
]

const router = new Router({
  routes: constantRoutes
})

export {
  constantRoutes,
  router
}

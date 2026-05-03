import Vue from 'vue'
import VueRouter from 'vue-router'
import LoginPage from '../views/login/LoginPage.vue'
import LayoutContainer from '../views/layout/LayoutContainer.vue'
import AccountManagement from '@/views/admin/AccountManagement.vue'
import HealthItemManagement from '@/views/admin/HealthItemManagement.vue'
import HealthRecordManagement from '@/views/admin/HealthRecordManagement.vue'
import HealthRecord from '@/views/user/HealthRecord.vue'
import HealthData from '@/views/user/HealthData.vue'
import AIChat from '@/views/user/AIChat.vue'
import DashboardPage from '@/views/admin/DashboardPage.vue'
import store from '@/store'
import { Message } from 'element-ui'
import PersonCenter from '@/views/common/PersonCenter.vue'
import UpdatePassword from '@/views/common/UpdatePassword.vue'
import NotFound from '@/views/common/NotFound.vue'

Vue.use(VueRouter)

// 所有路由预先定义好
const routes = [
  { path: '/login', component: LoginPage, meta: { title: '登录' } },
  {
    path: '/',
    component: LayoutContainer,
    children: [
      // 用户路由
      {
        path: 'healthData',
        component: HealthData,
        meta: { breadcrumb: '首页', roleName: '用户', title: '首页' }
      },
      {
        path: 'healthRecord',
        component: HealthRecord,
        meta: { breadcrumb: '健康记录', roleName: '用户', title: '健康记录' }
      },
      {
        path: 'aiChat',
        component: AIChat,
        meta: { breadcrumb: '健康智能助手', roleName: '用户', title: '健康智能助手' }
      },
      // 管理员路由
      {
        path: 'dashboardPage',
        component: DashboardPage,
        meta: { breadcrumb: '首页', roleName: '管理员', title: '首页' }
      },
      {
        path: 'accountManagement',
        component: AccountManagement,
        meta: { breadcrumb: '账号管理', roleName: '管理员', title: '账号管理' }
      },
      {
        path: 'healthItemManagement',
        component: HealthItemManagement,
        meta: {
          breadcrumb: '健康项管理',
          roleName: '管理员',
          title: '健康项管理'
        }
      },
      {
        path: 'healthRecordManagement',
        component: HealthRecordManagement,
        meta: {
          breadcrumb: '健康记录管理',
          roleName: '管理员',
          title: '健康记录管理'
        }
      },
      // 公共路由
      {
        path: 'personCenter',
        component: PersonCenter,
        meta: { breadcrumb: '个人中心', roleName: 'all', title: '个人中心' }
      },
      {
        path: 'updatePassword',
        component: UpdatePassword,
        meta: { breadcrumb: '修改密码', roleName: 'all', title: '修改密码' }
      }
    ]
  },
  { path: '*', component: NotFound, meta: { title: '页面未找到 - 404' } }
]

const router = new VueRouter({
  mode: 'history',
  base: import.meta.env.BASE_URL,
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 自动设置页面标题
  document.title = to.meta.title || '健康管理系统'

  // 登录页直接放行
  if (to.path === '/login') {
    next()
    return
  }

  const accountRoleName = store.state.user.userInfo?.role

  // 如果未登录一律跳转到登录页
  if (!accountRoleName) {
    next('/login')
    return
  }

  // 根据账号角色重定向
  if (to.path === '/') {
    const redirectPath =
      accountRoleName === '管理员' ? '/dashboardPage' : '/healthData'
    next(redirectPath)
    return
  }

  // 账号角色权限校验
  if (to.meta.roleName === 'all' || to.meta.roleName === accountRoleName) {
    next()
  } else {
    // 无权限访问，显示提示并返回跳转前的页面
    Message.error('无访问权限')
    next(from.path === to.path ? false : from.path)
  }
})

export default router

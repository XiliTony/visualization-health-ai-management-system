<script>
export default {
  name: 'LayoutContainer',
  data() {
    return {
      isCollapse: true,
      user: this.$store.state.user.userInfo
    }
  },
  computed: {
    // 根据用户角色返回对应的菜单项
    menuItems() {
      const isAdmin = this.user.role === '管理员'

      if (isAdmin) {
        return [
          {
            index: '/dashboardPage',
            icon: 'el-icon-house',
            title: '首页',
            breadcrumb: '首页'
          },
          {
            index: '/accountManagement',
            icon: 'el-icon-user',
            title: '账号管理',
            breadcrumb: '账号管理'
          },
          {
            index: '/healthItemManagement',
            icon: 'el-icon-takeaway-box',
            title: '健康项管理',
            breadcrumb: '健康项管理'
          },
          {
            index: '/healthRecordManagement',
            icon: 'el-icon-s-order',
            title: '健康记录管理',
            breadcrumb: '健康记录管理'
          },
          {
            index: '/personCenter',
            icon: 'el-icon-user-solid',
            title: '个人中心',
            breadcrumb: '个人中心'
          },
          {
            index: '/updatePassword',
            icon: 'el-icon-lock',
            title: '修改密码',
            breadcrumb: '修改密码'
          }
        ]
      } else {
        return [
          {
            index: '/healthData',
            icon: 'el-icon-data-analysis',
            title: '首页',
            breadcrumb: '首页'
          },
          {
            index: '/healthRecord',
            icon: 'el-icon-s-order',
            title: '健康记录',
            breadcrumb: '健康记录'
          },
          {
            index: '/aiChat',
            icon: 'el-icon-chat-dot-round',
            title: '健康智能助手',
            breadcrumb: '健康智能助手'
          },
          {
            index: '/personCenter',
            icon: 'el-icon-user-solid',
            title: '个人中心',
            breadcrumb: '个人中心'
          },
          {
            index: '/updatePassword',
            icon: 'el-icon-lock',
            title: '修改密码',
            breadcrumb: '修改密码'
          }
        ]
      }
    },

    breadcrumbs() {
      const matchedRoutes = this.$route.matched
      const breadcrumbs = []
      const isAdmin = this.user.role === '管理员'

      // 根据角色添加对应的首页面包屑
      const homePath = isAdmin ? '/dashboardPage' : '/healthData'
      const homeLabel = '首页'

      breadcrumbs.push({
        label: homeLabel,
        to: homePath
      })

      matchedRoutes.forEach((route) => {
        // 过滤根路由和首页路由
        if (
          route.path === '/' ||
          route.path === homePath ||
          !route.meta?.breadcrumb ||
          route.meta.breadcrumb === homeLabel
        )
          return

        const breadcrumb = {
          label: route.meta.breadcrumb,
          to: route.path
        }
        breadcrumbs.push(breadcrumb)
      })

      return breadcrumbs
    },

    // 当前用户是否是管理员
    isAdmin() {
      return this.user.role === '管理员'
    }
  },
  methods: {
    logout() {
      this.$store.commit('user/resetUserInfo')
      this.$router.push('/login')
    },
    toPersonCenter() {
      this.$router.push('/personCenter')
    },
    toUpdatePassword() {
      this.$router.push('/updatePassword')
    },
    updateUser() {
      this.user = this.$store.state.user.userInfo
    }
  }
}
</script>

<template>
  <div class="app-container">
    <!-- 独立固定的头像容器 -->
    <div class="independent-avatar">
      <div class="box">
        <img
          :src="
            user.avatar ||
            'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
          "
          alt="头像"
        />
        <div class="userInfo">
          <!-- 用户名 -->
          <div class="username">{{ user.username }}</div>
          <!-- 个人中心菜单项 -->
          <div class="logout-menu" @click="toPersonCenter">
            <i class="el-icon-user-solid"></i>
            <span>个人中心</span>
          </div>
          <div class="logout-menu" @click="toUpdatePassword">
            <i class="el-icon-lock"></i>
            <span>修改密码</span>
          </div>
          <!-- 退出登录菜单项 -->
          <div class="logout-menu" @click="logout">
            <i class="el-icon-switch-button"></i>
            <span>退出登录</span>
          </div>
        </div>
      </div>
    </div>

    <el-container style="border: 1px solid #c8e6c9; height: 100vh">
      <el-aside :width="isCollapse ? '64px' : '260px'" class="aside-container">
        <!-- 垂直导航栏 -->
        <el-menu
          :default-active="$route.path"
          class="el-menu-vertical-demo"
          :unique-opened="true"
          :router="true"
          background-color="#fff"
          text-color="#333"
          active-text-color="#4caf50"
        >
          <!-- 顶部标题 -->
          <div class="top">
            <img class="logo" src="../../assets/logo.jpg" alt="logo" />
            <div v-if="!isCollapse" class="title">系统导航</div>
          </div>

          <!-- 动态生成导航菜单 -->
          <el-menu-item
            v-for="item in menuItems"
            :key="item.index"
            :index="item.index"
          >
            <i :class="item.icon"></i>
            <span slot="title" v-if="!isCollapse">{{ item.title }}</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container class="main-container">
        <el-header class="header-container">
          <!-- 水平导航栏 -->
          <div class="horizontal-menu">
            <!-- 导航栏左侧 -->
            <div class="left">
              <!-- 折叠按钮 -->
              <el-button
                @click="isCollapse = !isCollapse"
                type="text"
                class="collapse-btn"
                :class="isCollapse ? 'fold' : 'unfold'"
              >
                <i class="el-icon-s-fold"></i>
              </el-button>
              <!-- 面包屑 -->
              <el-breadcrumb
                class="title"
                separator-class="el-icon-d-arrow-right"
              >
                <el-breadcrumb-item
                  v-for="(crumb, index) in breadcrumbs"
                  :key="index"
                >
                  <router-link :to="crumb.to">{{ crumb.label }}</router-link>
                </el-breadcrumb-item>
              </el-breadcrumb>
            </div>
            <!-- 导航栏右侧 -->
            <div class="right">
              <!-- 显示当前用户角色 -->
              <div class="role-tag">
                <el-tag :type="isAdmin ? 'danger' : 'success'">
                  {{ isAdmin ? '管理员' : '用户' }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-header>

        <el-main class="content-container">
          <!-- 路由视图 -->
          <router-view @updateUser="updateUser" />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style scoped>
.app-container {
  height: 100vh;
  overflow: hidden;
  background-color: #e6f7e6;
  position: relative;
}

/* 独立固定的头像容器 */
.independent-avatar {
  position: fixed;
  top: 14px;
  right: 140px;
  z-index: 999;
  padding: 5px;
}

.box {
  width: 40px;
  height: 40px;
}

.box > img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  transform-origin: right top;
  position: relative;
  z-index: 2;
  background-color: white;
  transition: all 0.5s;
  border: 2px solid #81c784;
}

.box > .userInfo {
  width: 300px;
  height: 250px;
  background-color: whitesmoke;
  border-top: 2px solid #81c784;
  border-radius: 10px;
  transform: translate(-50%, 0);
  display: none;
}

.box:hover > img {
  transform: scale(2);
}

.box:hover > .userInfo {
  display: block;
}

.username {
  padding-top: 38px;
  text-align: center;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid #ddd;
  color: #333;
}

.user-role {
  padding: 10px 0;
  text-align: center;
  font-size: 14px;
  color: #666;
  border-bottom: 1px solid #ddd;
}

.logout-menu {
  padding: 15px 20px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: background-color 0.3s;
}

.logout-menu:hover {
  background-color: #f0f0f0;
}

.logout-menu i {
  margin-right: 8px;
  color: #666;
}

.logout-menu span {
  font-size: 14px;
  color: #333;
}

.aside-container {
  height: 100%;
  background-color: #fff;
  border-radius: 0 20px 20px 0;
  box-shadow: 0 0 4px 0 rgba(76, 175, 80, 0.2);
  transition: width 0.3s ease;
}

.top {
  display: flex;
  padding: 20px 26px 10px 6px;
  align-items: center;
}

.logo {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: 2px solid #81c784;
}

.title {
  color: #2e7d32;
  font-family: 'SimSun';
  font-size: 24px;
  margin-left: 12px;
  font-weight: 600;
}

.header-container {
  margin: 10px;
  height: 65px;
  box-shadow: 0 0 4px 2px rgba(76, 175, 80, 0.2);
  border-radius: 20px;
  background-color: #fff;
  padding: 0;
}

.horizontal-menu {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  font-size: 20px;
  margin-right: 20px;
  border: none !important;
  outline: none !important;
  color: #4caf50 !important;
}

.fold i {
  transform: rotate(0deg);
  transition: transform 0.3s ease;
}

.unfold i {
  transform: rotate(180deg);
  transition: transform 0.3s ease;
}

.horizontal-menu .title {
  color: #4caf50;
  font-family: 'SimSun';
  font-size: 16px;
  font-weight: 500;
}

.right {
  display: flex;
  align-items: center;
}

.role-tag {
  margin-right: 10px;
}

.content-container {
  margin: 10px;
  padding: 20px;
  background-color: #fff;
  border-radius: 20px;
  box-shadow: 0 0 4px 0 rgba(76, 175, 80, 0.1);
  overflow-y: auto;
}

::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

::-webkit-scrollbar-thumb {
  background: #81c784;
  border-radius: 10px;
}

::-webkit-scrollbar-thumb:hover {
  background: #4caf50;
}

:deep(.el-menu-vertical-demo .el-menu-item:hover) {
  background-color: rgba(76, 175, 80, 0.1) !important;
}

:deep(.el-menu-vertical-demo .el-menu-item.is-active) {
  background-color: rgba(76, 175, 80, 0.2) !important;
}
</style>

import { getInfo, removeInfo, setInfo } from '@/utils/storage'

export default {
  namespaced: true,
  state() {
    return {
      // 个人权证相关
      userInfo: getInfo()
    }
  },
  mutations: {
    setUserInfo(state, obj) {
      state.userInfo = obj
      setInfo(obj)
    },
    resetUserInfo(state) {
      state.userInfo = {
        token: '',
        id: '',
        account: '',
        username: '',
        avatar: '',
        roleName: ''
      }
      removeInfo() // 同时清除 localStorage
    }
  },
  actions: {},
  getters: {}
}

import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: {},
    permissions: []
  }),
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },
    setUserInfo(info) {
      this.userInfo = info
    },
    setPermissions(permissions) {
      this.permissions = permissions
    },
    logout() {
      this.token = ''
      this.userInfo = {}
      this.permissions = []
      localStorage.removeItem('token')
    }
  }
})

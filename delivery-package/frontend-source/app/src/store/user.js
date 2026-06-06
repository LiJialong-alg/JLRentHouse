import { defineStore } from 'pinia'
import { loginApi } from '../api'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null,
    loading: false
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token)
  },
  actions: {
    async getCode(phone) {
      await loginApi.getCode(phone)
      return true
    },
    async login(payload) {
      this.loading = true
      try {
        const res = await loginApi.login(payload)
        this.token = res.data
        localStorage.setItem('token', res.data)
        await this.fetchUserInfo()
        return true
      } finally {
        this.loading = false
      }
    },
    async fetchUserInfo() {
      const res = await loginApi.getUserInfo()
      this.userInfo = res.data
      localStorage.setItem('userInfo', JSON.stringify(res.data || {}))
      return res.data
    },
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})

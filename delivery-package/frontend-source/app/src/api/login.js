import request from '../utils/request'

export const getCode = (phone) => {
  return request({
    url: '/login/getCode',
    method: 'get',
    params: { phone }
  })
}

export const login = (data) => {
  return request({
    url: '/login',
    method: 'post',
    data
  })
}

export const getUserInfo = () => {
  return request({
    url: '/info',
    method: 'get'
  })
}
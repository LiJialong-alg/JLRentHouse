import request from '../utils/request'

export const getAgreementList = () =>
  request({
    url: '/agreement/listItem',
    method: 'get'
  })

export const getAgreementDetail = (id) =>
  request({
    url: '/agreement/getDetailById',
    method: 'get',
    params: { id }
  })

export const updateAgreementStatus = (id, leaseStatus) =>
  request({
    url: '/agreement/updateStatusById',
    method: 'post',
    params: { id, leaseStatus }
  })

export const getPaymentsByRoomId = (id) =>
  request({
    url: '/agreement/listByRoomId',
    method: 'get',
    params: { id }
  })

export const getLeaseTermsByRoomId = (id) =>
  request({
    url: '/agreement/listByRoomId2',
    method: 'get',
    params: { id }
  })

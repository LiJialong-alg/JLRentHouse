import request from '../utils/request'

export const saveAppointment = (data) =>
  request({
    url: '/appointment/saveOrUpdate',
    method: 'post',
    data
  })

export const getAppointmentList = () =>
  request({
    url: '/appointment/listItem',
    method: 'get'
  })

export const getAppointmentDetail = (id) =>
  request({
    url: '/appointment/getDetailById',
    method: 'get',
    params: { id }
  })

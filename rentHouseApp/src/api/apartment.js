import request from '../utils/request'

export const getApartmentDetail = (id) =>
  request({
    url: '/apartment/getDetailById',
    method: 'get',
    params: { id }
  })

export const getRoomsByApartmentId = (id, current = 1, size = 100) =>
  request({
    url: '/room/pageItemByApartmentId',
    method: 'get',
    params: { id, current, size }
  })

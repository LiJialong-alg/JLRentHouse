import request from '../utils/request'

export const getRoomPage = (params) => {
  return request({
    url: '/room/pageItem',
    method: 'get',
    params
  })
}

export const getRoomDetail = (id) => {
  return request({
    url: '/room/getDetailById',
    method: 'get',
    params: { id }
  })
}

export const getRoomsByApartmentId = (apartmentId, params = {}) => {
  return request({
    url: '/room/pageItemByApartmentId',
    method: 'get',
    params: { id: apartmentId, ...params }
  })
}
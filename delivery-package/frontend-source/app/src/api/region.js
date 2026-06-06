import request from '../utils/request'

export const getProvinces = () => {
  return request({
    url: '/region/province/list',
    method: 'get'
  })
}

export const getCities = (provinceId) => {
  return request({
    url: '/region/city/listByProvinceId',
    method: 'get',
    params: { id: provinceId }
  })
}

export const getDistricts = (cityId) => {
  return request({
    url: '/region/district/listByCityId',
    method: 'get',
    params: { id: cityId }
  })
}
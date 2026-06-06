import request from '../utils/request'

export const getPaymentList = () => {
    return request({
        url: '/payment/list',
        method: 'get'
    })
}

export const getPaymentByRoomId = (id) => {
    return request({
        url: '/payment/listByRoomId',
        method: 'get',
        params: { id }
    })
}

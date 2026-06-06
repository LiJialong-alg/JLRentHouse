import request from '../utils/request'

export const getLeaseTermByRoomId = (id) => {
    return request({
        url: '/term/listByRoomId',
        method: 'get',
        params: { id }
    })
}

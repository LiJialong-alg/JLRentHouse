import request from '../utils/request'

export const getHistoryPage = (current = 1, size = 20) =>
  request({
    url: '/history/pageItem',
    method: 'get',
    params: { current, size }
  })

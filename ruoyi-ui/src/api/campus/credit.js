import request from '@/utils/request'

// 查询信誉变更记录列表
export function listCredit(query) {
  return request({
    url: '/campus/credit/list',
    method: 'get',
    params: query
  })
}

// 查询信誉变更记录详细
export function getCredit(logId) {
  return request({
    url: '/campus/credit/' + logId,
    method: 'get'
  })
}

// 查询我的信誉变更记录
export function getMyCreditLogs() {
  return request({
    url: '/campus/credit/mine',
    method: 'get'
  })
}

// 查询指定用户的信誉变更记录
export function getCreditByUserId(userId) {
  return request({
    url: '/campus/credit/user/' + userId,
    method: 'get'
  })
}

// 删除信誉变更记录
export function delCredit(logIds) {
  return request({
    url: '/campus/credit/remove/' + logIds,
    method: 'post'
  })
}

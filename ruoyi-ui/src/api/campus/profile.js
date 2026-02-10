import request from '@/utils/request'

// 查询校园用户信息列表
export function listProfile(query) {
  return request({
    url: '/campus/profile/list',
    method: 'get',
    params: query
  })
}

// 查询校园用户信息详细
export function getProfile(profileId) {
  return request({
    url: '/campus/profile/' + profileId,
    method: 'get'
  })
}

// 查询当前登录用户的学生信息
export function getMyProfile() {
  return request({
    url: '/campus/profile/mine',
    method: 'get'
  })
}

// 根据用户ID查询校园信息
export function getProfileByUserId(userId) {
  return request({
    url: '/campus/profile/user/' + userId,
    method: 'get'
  })
}

// 新增校园用户信息
export function addProfile(data) {
  return request({
    url: '/campus/profile',
    method: 'post',
    data: data
  })
}

// 修改校园用户信息
export function updateProfile(data) {
  return request({
    url: '/campus/profile/edit',
    method: 'post',
    data: data
  })
}

// 删除校园用户信息
export function delProfile(profileIds) {
  return request({
    url: '/campus/profile/remove/' + profileIds,
    method: 'post'
  })
}

// 审核校园用户认证
export function auditProfile(data) {
  return request({
    url: '/campus/profile/audit',
    method: 'post',
    data: data
  })
}

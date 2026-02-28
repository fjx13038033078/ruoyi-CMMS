import request from '@/utils/request'

export function getDashboardStats() {
  return request({
    url: '/campus/dashboard/stats',
    method: 'get'
  })
}

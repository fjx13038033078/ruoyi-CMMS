import request from '@/utils/request'

// 查询跑腿订单列表
export function listOrder(query) {
  return request({
    url: '/campus/order/list',
    method: 'get',
    params: query
  })
}

// 查询跑腿订单详细
export function getOrder(orderId) {
  return request({
    url: '/campus/order/' + orderId,
    method: 'get'
  })
}

// 新增跑腿订单
export function addOrder(data) {
  return request({
    url: '/campus/order',
    method: 'post',
    data: data
  })
}

// 修改跑腿订单
export function updateOrder(data) {
  return request({
    url: '/campus/order/edit',
    method: 'post',
    data: data
  })
}

// 删除跑腿订单
export function delOrder(orderIds) {
  return request({
    url: '/campus/order/remove/' + orderIds,
    method: 'post'
  })
}

// 查询我接的订单(跑腿员视角)
export function listMyTaken(query) {
  return request({
    url: '/campus/order/myTaken',
    method: 'get',
    params: query
  })
}

// 查询大厅可接订单
export function listHall(query) {
  return request({
    url: '/campus/order/hall',
    method: 'get',
    params: query
  })
}

// 智能推荐订单
export function recommendOrders(query) {
  return request({
    url: '/campus/order/recommend',
    method: 'get',
    params: query
  })
}

// 接单
export function acceptOrder(orderId) {
  return request({
    url: '/campus/order/accept/' + orderId,
    method: 'post'
  })
}

// 开始配送
export function deliverOrder(orderId) {
  return request({
    url: '/campus/order/deliver/' + orderId,
    method: 'post'
  })
}

// 完成订单
export function completeOrder(orderId) {
  return request({
    url: '/campus/order/complete/' + orderId,
    method: 'post'
  })
}

// 取消订单
export function cancelOrder(orderId) {
  return request({
    url: '/campus/order/cancel/' + orderId,
    method: 'post'
  })
}

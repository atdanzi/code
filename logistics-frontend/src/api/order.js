import request from '@/utils/request'

export function getOrders(params) {
  return request.get('/orders', { params })
}

export function getOrder(id) {
  return request.get(`/orders/${id}`)
}

export function createOrder(data) {
  return request.post('/orders', data)
}

export function cancelOrder(id, data) {
  return request.post(`/orders/${id}/cancel`, data)
}

export function exchangeOrder(id, data) {
  return request.post(`/orders/${id}/exchange`, data)
}

export function returnOrder(id, data) {
  return request.post(`/orders/${id}/return`, data)
}

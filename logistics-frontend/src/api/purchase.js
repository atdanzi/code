import request from '@/utils/request'

export function checkShortage(params) {
  return request.get('/purchase/shortage-check', { params })
}

export function createPurchase(data) {
  return request.post('/purchase', data)
}

export function getPurchaseOrders(params) {
  return request.get('/purchase', { params })
}

export function getPurchaseOrder(id) {
  return request.get(`/purchase/${id}`)
}

export function createReturn(data) {
  return request.post('/purchase/return', data)
}

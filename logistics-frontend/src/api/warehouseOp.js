import request from '@/utils/request'

export function purchaseIn(data) {
  return request.post('/warehouse-op/purchase-in', data)
}

export function transferOut(data) {
  return request.post('/warehouse-op/transfer-out', data)
}

export function transferIn(data) {
  return request.post('/warehouse-op/transfer-in', data)
}

export function pickGoods(data) {
  return request.post('/warehouse-op/pick', data)
}

export function returnRegister(data) {
  return request.post('/warehouse-op/return-register', data)
}

export function stationReturnOut(data) {
  return request.post('/warehouse-op/station-return-out', data)
}

export function centerReturnIn(data) {
  return request.post('/warehouse-op/center-return-in', data)
}

export function getTransferOrders(params) {
  return request.get('/warehouse-op/transfer-orders', { params })
}

export function getPurchaseOrders(params) {
  return request.get('/warehouse-op/purchase-orders', { params })
}

export function getPurchaseOrderDetail(id) {
  return request.get(`/warehouse-op/purchase-orders/${id}`)
}

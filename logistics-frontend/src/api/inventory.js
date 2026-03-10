import request from '@/utils/request'

export function getStock(params) {
  return request.get('/inventory', { params })
}

export function getFlow(params) {
  return request.get('/inventory/flow', { params })
}

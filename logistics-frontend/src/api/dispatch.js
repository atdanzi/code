import request from '@/utils/request'

export function getDispatchOrders(params) {
  return request.get('/dispatch/orders', { params })
}

export function manualDispatch(data) {
  return request.post('/dispatch/manual', data)
}

export function autoDispatch(data) {
  return request.post('/dispatch/auto', data)
}

export function getShortageOrders(params) {
  return request.get('/dispatch/shortage', { params })
}

export function resolveShortage(data) {
  return request.post('/dispatch/shortage/resolve', data)
}

export function getTasks(params) {
  return request.get('/dispatch/tasks', { params })
}

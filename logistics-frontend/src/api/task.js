import request from '@/utils/request'

export function getTasks(params) {
  return request.get('/tasks', { params })
}

export function getTask(id) {
  return request.get(`/tasks/${id}`)
}

export function assignTask(id, data) {
  return request.post(`/tasks/${id}/assign`, data)
}

export function enterReceipt(id, data) {
  return request.post(`/tasks/${id}/receipt`, data)
}

export function getPaymentQuery(params) {
  return request.get('/tasks/payment', { params })
}

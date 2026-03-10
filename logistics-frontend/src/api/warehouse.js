import request from '@/utils/request'

export function getWarehouses(params) {
  return request.get('/warehouses', { params })
}

export function getWarehouse(id) {
  return request.get(`/warehouses/${id}`)
}

export function createWarehouse(data) {
  return request.post('/warehouses', data)
}

export function updateWarehouse(id, data) {
  return request.put(`/warehouses/${id}`, data)
}

export function deleteWarehouse(id) {
  return request.delete(`/warehouses/${id}`)
}

export function getReserve(id) {
  return request.get(`/warehouses/${id}/reserve`)
}

export function updateReserve(id, data) {
  return request.put(`/warehouses/${id}/reserve`, data)
}

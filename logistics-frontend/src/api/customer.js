import request from '@/utils/request'

export function getCustomers(params) {
  return request.get('/customers', { params })
}

export function getCustomer(id) {
  return request.get(`/customers/${id}`)
}

export function createCustomer(data) {
  return request.post('/customers', data)
}

export function updateCustomer(id, data) {
  return request.put(`/customers/${id}`, data)
}

export function deleteCustomer(id) {
  return request.delete(`/customers/${id}`)
}

export function getCustomerOrders(id, params) {
  return request.get(`/customers/${id}/orders`, { params })
}

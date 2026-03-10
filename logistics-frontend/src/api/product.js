import request from '@/utils/request'

export function getCategories1(params) {
  return request.get('/product/categories1', { params })
}

export function getCategory1(id) {
  return request.get(`/product/categories1/${id}`)
}

export function createCategory1(data) {
  return request.post('/product/categories1', data)
}

export function updateCategory1(id, data) {
  return request.put(`/product/categories1/${id}`, data)
}

export function deleteCategory1(id) {
  return request.delete(`/product/categories1/${id}`)
}

export function getCategories2(params) {
  return request.get('/product/categories2', { params })
}

export function getCategory2(id) {
  return request.get(`/product/categories2/${id}`)
}

export function createCategory2(data) {
  return request.post('/product/categories2', data)
}

export function updateCategory2(id, data) {
  return request.put(`/product/categories2/${id}`, data)
}

export function deleteCategory2(id) {
  return request.delete(`/product/categories2/${id}`)
}

export function getProducts(params) {
  return request.get('/products', { params })
}

export function getProduct(id) {
  return request.get(`/products/${id}`)
}

export function createProduct(data) {
  return request.post('/products', data)
}

export function updateProduct(id, data) {
  return request.put(`/products/${id}`, data)
}

export function deleteProduct(id) {
  return request.delete(`/products/${id}`)
}

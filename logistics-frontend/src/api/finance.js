import request from '@/utils/request'

export function registerInvoice(data) {
  return request.post('/finance/invoice/register', data)
}

export function assignInvoiceStation(data) {
  return request.post('/finance/invoice/assign-station', data)
}

export function assignInvoiceCustomer(data) {
  return request.post('/finance/invoice/assign-customer', data)
}

export function voidInvoice(data) {
  return request.post('/finance/invoice/void', data)
}

export function getInvoices(params) {
  return request.get('/finance/invoices', { params })
}

export function getSupplierSettlement(params) {
  return request.get('/finance/supplier-settlement', { params })
}

export function settleSupplier(data) {
  return request.post('/finance/supplier-settlement', data)
}

export function getStationSettlement(params) {
  return request.get('/finance/station-settlement', { params })
}

export function settleStation(data) {
  return request.post('/finance/station-settlement', data)
}

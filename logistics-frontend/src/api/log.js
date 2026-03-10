import request from '@/utils/request'

export function getOperationLogs(params) {
  return request.get('/logs/operation', { params })
}

export function getLoginLogs(params) {
  return request.get('/logs/login', { params })
}

export function backupLogs(data) {
  return request.post('/logs/backup', data)
}

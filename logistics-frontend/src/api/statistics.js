import request from '@/utils/request'

export function getOrderRanking(params) {
  return request.get('/statistics/ranking', { params })
}

export function getStationDelivery(params) {
  return request.get('/statistics/station-delivery', { params })
}

export function getSatisfaction(params) {
  return request.get('/statistics/satisfaction', { params })
}

export function getOperatorWorkload(params) {
  return request.get('/statistics/operator-workload', { params })
}

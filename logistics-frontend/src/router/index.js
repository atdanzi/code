import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/Layout.vue'),
    redirect: '/order/manage',
    children: [
      { path: 'customer/manage', name: 'CustomerManage', component: () => import('@/views/customer/CustomerManage.vue'), meta: { title: '客户信息管理' } },
      { path: 'customer/workload', name: 'CustomerWorkload', component: () => import('@/views/customer/CustomerWorkload.vue'), meta: { title: '工作量查询' } },
      { path: 'order/manage', name: 'OrderManage', component: () => import('@/views/order/OrderManage.vue'), meta: { title: '订单管理' } },
      { path: 'dispatch/manual', name: 'DispatchManual', component: () => import('@/views/dispatch/DispatchManual.vue'), meta: { title: '手工调度' } },
      { path: 'dispatch/auto', name: 'DispatchAuto', component: () => import('@/views/dispatch/DispatchAuto.vue'), meta: { title: '自动调度' } },
      { path: 'dispatch/shortage', name: 'DispatchShortage', component: () => import('@/views/dispatch/DispatchShortage.vue'), meta: { title: '缺货订单' } },
      { path: 'dispatch/task', name: 'DispatchTask', component: () => import('@/views/dispatch/DispatchTask.vue'), meta: { title: '任务单查询' } },
      { path: 'station/task', name: 'StationTask', component: () => import('@/views/station/StationTask.vue'), meta: { title: '任务查询' } },
      { path: 'station/assign', name: 'StationAssign', component: () => import('@/views/station/StationAssign.vue'), meta: { title: '任务分配' } },
      { path: 'station/receipt', name: 'StationReceipt', component: () => import('@/views/station/StationReceipt.vue'), meta: { title: '回执录入' } },
      { path: 'station/payment', name: 'StationPayment', component: () => import('@/views/station/StationPayment.vue'), meta: { title: '缴款查询' } },
      { path: 'warehouse-op/purchase-in', name: 'PurchaseIn', component: () => import('@/views/warehouse-op/PurchaseIn.vue'), meta: { title: '购货入库' } },
      { path: 'warehouse-op/transfer-out', name: 'TransferOut', component: () => import('@/views/warehouse-op/TransferOut.vue'), meta: { title: '调拨出库' } },
      { path: 'warehouse-op/transfer-in', name: 'TransferIn', component: () => import('@/views/warehouse-op/TransferIn.vue'), meta: { title: '调拨入库' } },
      { path: 'warehouse-op/pick', name: 'PickGoods', component: () => import('@/views/warehouse-op/PickGoods.vue'), meta: { title: '领货' } },
      { path: 'warehouse-op/return', name: 'ReturnManage', component: () => import('@/views/warehouse-op/ReturnManage.vue'), meta: { title: '退货管理' } },
      { path: 'product/category1', name: 'ProductCategory1', component: () => import('@/views/product/ProductCategory1.vue'), meta: { title: '一级分类' } },
      { path: 'product/category2', name: 'ProductCategory2', component: () => import('@/views/product/ProductCategory2.vue'), meta: { title: '二级分类' } },
      { path: 'product/manage', name: 'ProductManage', component: () => import('@/views/product/ProductManage.vue'), meta: { title: '商品管理' } },
      { path: 'supplier/manage', name: 'SupplierManage', component: () => import('@/views/supplier/SupplierManage.vue'), meta: { title: '供应商管理' } },
      { path: 'purchase/manage', name: 'PurchaseManage', component: () => import('@/views/purchase/PurchaseManage.vue'), meta: { title: '进货管理' } },
      { path: 'purchase/return', name: 'PurchaseReturn', component: () => import('@/views/purchase/PurchaseReturn.vue'), meta: { title: '退货安排' } },
      { path: 'warehouse/setup', name: 'WarehouseSetup', component: () => import('@/views/warehouse/WarehouseSetup.vue'), meta: { title: '库房设置' } },
      { path: 'warehouse/reserve', name: 'WarehouseReserve', component: () => import('@/views/warehouse/WarehouseReserve.vue'), meta: { title: '储备设置' } },
      { path: 'warehouse/stock', name: 'WarehouseStock', component: () => import('@/views/warehouse/WarehouseStock.vue'), meta: { title: '库存查询' } },
      { path: 'warehouse/flow', name: 'WarehouseFlow', component: () => import('@/views/warehouse/WarehouseFlow.vue'), meta: { title: '出入库查询' } },
      { path: 'finance/supplier-settle', name: 'SupplierSettle', component: () => import('@/views/finance/SupplierSettle.vue'), meta: { title: '供应商结算' } },
      { path: 'finance/station-settle', name: 'StationSettle', component: () => import('@/views/finance/StationSettle.vue'), meta: { title: '分站结算' } },
      { path: 'finance/invoice', name: 'InvoiceManage', component: () => import('@/views/finance/InvoiceManage.vue'), meta: { title: '发票管理' } },
      { path: 'finance/station-invoice', name: 'StationInvoice', component: () => import('@/views/finance/StationInvoice.vue'), meta: { title: '分站发票' } },
      { path: 'statistics/rank', name: 'StatisticsRank', component: () => import('@/views/statistics/StatisticsRank.vue'), meta: { title: '订购排行' } },
      { path: 'statistics/station', name: 'StatisticsStation', component: () => import('@/views/statistics/StatisticsStation.vue'), meta: { title: '分站配送' } },
      { path: 'statistics/satisfaction', name: 'StatisticsSatisfaction', component: () => import('@/views/statistics/StatisticsSatisfaction.vue'), meta: { title: '满意度分析' } },
      { path: 'system/permission', name: 'SystemPermission', component: () => import('@/views/system/SystemPermission.vue'), meta: { title: '权限管理' } },
      { path: 'system/role', name: 'SystemRole', component: () => import('@/views/system/SystemRole.vue'), meta: { title: '角色管理' } },
      { path: 'system/user', name: 'SystemUser', component: () => import('@/views/system/SystemUser.vue'), meta: { title: '用户管理' } },
      { path: 'log/operation', name: 'LogOperation', component: () => import('@/views/log/LogOperation.vue'), meta: { title: '操作日志' } },
      { path: 'log/login', name: 'LogLogin', component: () => import('@/views/log/LogLogin.vue'), meta: { title: '登录日志' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (!token && to.path !== '/login') {
    next('/login')
  } else {
    next()
  }
})

export default router

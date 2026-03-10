<template>
  <div class="page-container">
    <el-form :model="queryParams" inline class="search-form">
      <el-form-item label="开始日期">
        <el-date-picker v-model="queryParams.startDate" type="date" placeholder="选择开始日期" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item label="结束日期">
        <el-date-picker v-model="queryParams.endDate" type="date" placeholder="选择结束日期" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item label="订单类型">
        <el-select v-model="queryParams.orderType" placeholder="请选择" clearable>
          <el-option label="新订" value="新订" />
          <el-option label="退订" value="退订" />
          <el-option label="换货" value="换货" />
          <el-option label="退货" value="退货" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="orderCode" label="订单编号" width="160" />
      <el-table-column prop="customerName" label="客户姓名" width="120" />
      <el-table-column prop="orderType" label="订单类型" width="100" />
      <el-table-column prop="receiverName" label="收货人" width="120" />
      <el-table-column prop="receiverAddress" label="收货地址" show-overflow-tooltip />
      <el-table-column prop="requireDate" label="要求送达日期" width="130" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleDispatch(row)">调度</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pagination"
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.size"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="fetchData"
      @current-change="fetchData"
    />

    <el-dialog v-model="dialogVisible" title="手动调度 - 选择分站" width="500px" destroy-on-close>
      <el-form :model="dispatchForm" label-width="80px">
        <el-form-item label="订单编号">
          <el-input :model-value="dispatchForm.orderCode" disabled />
        </el-form-item>
        <el-form-item label="选择分站">
          <el-select v-model="dispatchForm.stationId" placeholder="请选择分站" style="width: 100%">
            <el-option v-for="s in stationList" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定调度</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDispatchOrders, manualDispatch } from '@/api/dispatch'
import request from '@/utils/request'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const dialogVisible = ref(false)
const stationList = ref([])

const queryParams = reactive({
  page: 1,
  size: 10,
  startDate: '',
  endDate: '',
  orderType: ''
})

const dispatchForm = reactive({
  orderId: null,
  orderCode: '',
  stationId: null
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getDispatchOrders(queryParams)
    tableData.value = res.data.records || res.data.list || res.data || []
    total.value = res.data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchStations = async () => {
  try {
    const res = await request.get('/stations')
    stationList.value = res.data.records || res.data.list || res.data || []
  } catch (e) {
    console.error(e)
  }
}

const handleSearch = () => {
  queryParams.page = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(queryParams, { page: 1, size: 10, startDate: '', endDate: '', orderType: '' })
  fetchData()
}

const handleDispatch = (row) => {
  dispatchForm.orderId = row.id
  dispatchForm.orderCode = row.orderCode
  dispatchForm.stationId = null
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!dispatchForm.stationId) {
    ElMessage.warning('请选择分站')
    return
  }
  await manualDispatch({ orderId: dispatchForm.orderId, stationId: dispatchForm.stationId })
  ElMessage.success('调度成功')
  dialogVisible.value = false
  fetchData()
}

onMounted(() => {
  fetchData()
  fetchStations()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}
.search-form {
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>

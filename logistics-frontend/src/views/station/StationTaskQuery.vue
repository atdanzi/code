<template>
  <div class="page-container">
    <el-form :model="queryParams" inline class="search-form">
      <el-form-item label="开始日期">
        <el-date-picker v-model="queryParams.startDate" type="date" placeholder="选择开始日期" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item label="结束日期">
        <el-date-picker v-model="queryParams.endDate" type="date" placeholder="选择结束日期" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item label="任务类型">
        <el-select v-model="queryParams.taskType" placeholder="请选择" clearable>
          <el-option label="配送" value="配送" />
          <el-option label="退货" value="退货" />
          <el-option label="换货" value="换货" />
        </el-select>
      </el-form-item>
      <el-form-item label="任务状态">
        <el-select v-model="queryParams.taskStatus" placeholder="请选择" clearable>
          <el-option label="待分配" value="待分配" />
          <el-option label="配送中" value="配送中" />
          <el-option label="已完成" value="已完成" />
          <el-option label="已失败" value="已失败" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="taskCode" label="任务编号" width="160" />
      <el-table-column prop="orderCode" label="订单编号" width="160" />
      <el-table-column prop="taskType" label="任务类型" width="100" />
      <el-table-column prop="taskStatus" label="任务状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.taskStatus)">{{ row.taskStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="receiverName" label="收货人" width="120" />
      <el-table-column prop="receiverPhone" label="收货人电话" width="130" />
      <el-table-column prop="requireDate" label="要求送达日期" width="130" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getTasks } from '@/api/task'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 10,
  startDate: '',
  endDate: '',
  taskType: '',
  taskStatus: ''
})

const statusType = (status) => {
  const map = { '待分配': 'info', '配送中': 'warning', '已完成': 'success', '已失败': 'danger' }
  return map[status] || 'info'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getTasks(queryParams)
    tableData.value = res.data.records || res.data.list || res.data || []
    total.value = res.data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.page = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(queryParams, { page: 1, size: 10, startDate: '', endDate: '', taskType: '', taskStatus: '' })
  fetchData()
}

onMounted(() => fetchData())
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

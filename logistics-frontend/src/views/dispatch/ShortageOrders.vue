<template>
  <div class="page-container">
    <el-form :model="queryParams" inline class="search-form">
      <el-form-item label="开始日期">
        <el-date-picker v-model="queryParams.startDate" type="date" placeholder="选择开始日期" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item label="结束日期">
        <el-date-picker v-model="queryParams.endDate" type="date" placeholder="选择结束日期" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <el-button type="success" :disabled="!selectedIds.length" @click="handleResolve">
        处理选中缺货 ({{ selectedIds.length }})
      </el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" border stripe @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" />
      <el-table-column prop="orderCode" label="订单编号" width="160" />
      <el-table-column prop="customerName" label="客户姓名" width="120" />
      <el-table-column prop="products" label="商品信息" show-overflow-tooltip />
      <el-table-column prop="orderDate" label="下单日期" width="120" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getShortageOrders, resolveShortage } from '@/api/dispatch'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const selectedIds = ref([])

const queryParams = reactive({
  page: 1,
  size: 10,
  startDate: '',
  endDate: ''
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getShortageOrders(queryParams)
    tableData.value = res.data.records || res.data.list || res.data || []
    total.value = res.data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleSelectionChange = (rows) => {
  selectedIds.value = rows.map(r => r.id)
}

const handleSearch = () => {
  queryParams.page = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(queryParams, { page: 1, size: 10, startDate: '', endDate: '' })
  fetchData()
}

const handleResolve = async () => {
  await ElMessageBox.confirm(`确认处理选中的 ${selectedIds.value.length} 个缺货订单?`, '提示', { type: 'warning' })
  await resolveShortage({ orderIds: selectedIds.value })
  ElMessage.success('处理成功')
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
.table-toolbar {
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>

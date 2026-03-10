<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-select v-model="queryParams.warehouseId" placeholder="选择仓库" clearable class="filter-item">
          <el-option v-for="item in warehouses" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-input v-model="queryParams.productName" placeholder="商品名称" style="width: 200px;" class="filter-item" />
        <el-select v-model="queryParams.type" placeholder="类型" clearable class="filter-item">
          <el-option label="入库" value="IN" />
          <el-option label="出库" value="OUT" />
        </el-select>
        <el-date-picker v-model="queryParams.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" class="filter-item" />
        <el-button type="primary" class="filter-item" @click="handleQuery">查询</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border style="width: 100%">
        <el-table-column prop="warehouseName" label="仓库" />
        <el-table-column prop="productName" label="商品" />
        <el-table-column prop="type" label="类型">
          <template #default="scope">
            <el-tag :type="scope.row.type === 'IN' ? 'success' : 'danger'">
              {{ scope.row.type === 'IN' ? '入库' : '出库' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="subType" label="子类型" /> <!-- e.g. 采购入库, 调拨入库 -->
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="operator" label="操作人" />
        <el-table-column prop="time" label="时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getFlow } from '@/api/inventory'
import { getWarehouses } from '@/api/warehouse'

const loading = ref(false)
const list = ref([])
const warehouses = ref([])
const queryParams = reactive({
  warehouseId: null,
  productName: '',
  type: '',
  dateRange: []
})

onMounted(() => {
  getWarehouses().then(res => {
    warehouses.value = res.data || []
  })
  handleQuery()
})

const handleQuery = () => {
  loading.value = true
  const params = { ...queryParams }
  if (queryParams.dateRange && queryParams.dateRange.length === 2) {
    params.startDate = queryParams.dateRange[0]
    params.endDate = queryParams.dateRange[1]
  }
  delete params.dateRange

  getFlow(params).then(res => {
    list.value = res.data || []
  }).finally(() => {
    loading.value = false
  })
}
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
}
.filter-item {
  margin-right: 10px;
}
</style>

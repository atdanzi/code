<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-select v-model="queryParams.warehouseId" placeholder="选择仓库" clearable class="filter-item">
          <el-option v-for="item in warehouses" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-input v-model="queryParams.productName" placeholder="商品名称" style="width: 200px;" class="filter-item" @keyup.enter="handleQuery" />
        <el-button type="primary" class="filter-item" @click="handleQuery">查询</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border stripe style="width: 100%">
        <el-table-column prop="warehouseName" label="仓库" />
        <el-table-column prop="productName" label="商品" />
        <el-table-column prop="totalQuantity" label="总库存" />
        <el-table-column prop="allocatedQuantity" label="已分配" />
        <el-table-column prop="availableQuantity" label="可用库存" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getStock } from '@/api/inventory'
import { getWarehouses } from '@/api/warehouse'

const loading = ref(false)
const list = ref([])
const warehouses = ref([])
const queryParams = reactive({
  warehouseId: null,
  productName: ''
})

onMounted(() => {
  getWarehouses().then(res => {
    warehouses.value = res.data || []
  })
  handleQuery()
})

const handleQuery = () => {
  loading.value = true
  getStock(queryParams).then(res => {
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

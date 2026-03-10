<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-select v-model="selectedWarehouse" placeholder="请选择仓库" @change="handleWarehouseChange">
          <el-option
            v-for="item in warehouses"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
        <el-button type="primary" class="filter-item" @click="handleSave" :disabled="!selectedWarehouse">保存设置</el-button>
      </div>

      <el-table v-loading="loading" :data="products" border style="width: 100%">
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column label="预警值">
          <template #default="scope">
            <el-input-number v-model="scope.row.warningValue" :min="0" />
          </template>
        </el-table-column>
        <el-table-column label="最大储备值">
          <template #default="scope">
            <el-input-number v-model="scope.row.maxValue" :min="0" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getWarehouses, getReserve, updateReserve } from '@/api/warehouse'
import { ElMessage } from 'element-plus'

const warehouses = ref([])
const selectedWarehouse = ref(null)
const products = ref([])
const loading = ref(false)

onMounted(() => {
  getWarehouses().then(res => {
    warehouses.value = res.data || []
  })
})

const handleWarehouseChange = (val) => {
  if (!val) return
  loading.value = true
  getReserve(val).then(res => {
    products.value = res.data || []
  }).finally(() => {
    loading.value = false
  })
}

const handleSave = () => {
  if (!selectedWarehouse.value) return
  updateReserve(selectedWarehouse.value, { products: products.value }).then(() => {
    ElMessage.success('保存成功')
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

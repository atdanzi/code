<template>
  <div class="page-container">
    <div class="toolbar">
      <el-form inline>
        <el-form-item label="中心库房">
          <el-select v-model="selectedWarehouse" placeholder="请选择库房" style="min-width: 220px" @change="fetchData">
            <el-option v-for="w in warehouses" :key="w.id" :label="w.name" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品名称">
          <el-input v-model="keyword" placeholder="输入商品名称过滤" clearable @input="filterList" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :disabled="!selectedWarehouse" @click="saveAll">保存全部</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="filteredData" v-loading="loading" border stripe>
      <el-table-column prop="productName" label="商品" min-width="160" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column prop="availableQuantity" label="可用库存" width="100" />
      <el-table-column prop="warningValue" label="预警值" width="140">
        <template #default="{ row }">
          <el-input-number v-model="row.warningValue" :min="0" :max="row.maxValue || 999999" :step="1" />
        </template>
      </el-table-column>
      <el-table-column prop="maxValue" label="最高库存量" width="160">
        <template #default="{ row }">
          <el-input-number v-model="row.maxValue" :min="row.warningValue || 0" :step="1" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="saveRow(row)">保存</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getWarehouses, getReserve, updateReserve } from '@/api/warehouse'

const warehouses = ref([])
const selectedWarehouse = ref(null)
const tableData = ref([])
const filteredData = computed(() =>
  keyword.value
    ? tableData.value.filter(item => (item.productName || '').includes(keyword.value))
    : tableData.value
)
const keyword = ref('')
const loading = ref(false)

const fetchWarehouses = async () => {
  const res = await getWarehouses()
  warehouses.value = res.data || []
  if (!selectedWarehouse.value && warehouses.value.length) {
    selectedWarehouse.value = warehouses.value[0].id
    fetchData()
  }
}

const fetchData = async () => {
  if (!selectedWarehouse.value) return
  loading.value = true
  try {
    const res = await getReserve(selectedWarehouse.value)
    tableData.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const filterList = () => {
  // computed handles filtering; trigger by changing keyword
}

const validateRow = (row) => {
  if (row.maxValue != null && row.warningValue != null && row.maxValue < row.warningValue) {
    ElMessage.warning(`商品 ${row.productName} 的最高库存应不小于预警值`)
    return false
  }
  return true
}

const saveRow = async (row) => {
  if (!selectedWarehouse.value) {
    ElMessage.warning('请先选择库房')
    return
  }
  if (!validateRow(row)) return
  try {
    await updateReserve(selectedWarehouse.value, {
      productId: row.productId,
      warningValue: row.warningValue ?? 0,
      maxValue: row.maxValue ?? 0
    })
    ElMessage.success('保存成功')
  } catch (e) {
    console.error(e)
    if (e && e.message) ElMessage.error(e.message)
  }
}

const saveAll = async () => {
  for (const row of filteredData.value) {
    const ok = validateRow(row)
    if (!ok) return
  }
  try {
    for (const row of filteredData.value) {
      await updateReserve(selectedWarehouse.value, {
        productId: row.productId,
        warningValue: row.warningValue ?? 0,
        maxValue: row.maxValue ?? 0
      })
    }
    ElMessage.success('全部保存成功')
    fetchData()
  } catch (e) {
    console.error(e)
    if (e && e.message) ElMessage.error(e.message)
  }
}

onMounted(() => {
  fetchWarehouses()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}
.toolbar {
  margin-bottom: 16px;
}
</style>

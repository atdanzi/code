<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" class="filter-item" />
        <el-button type="primary" class="filter-item" @click="handleQuery">查询</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border stripe style="width: 100%">
        <el-table-column prop="rank" label="排名" width="80" align="center" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="quantity" label="订购数量" sortable />
        <el-table-column prop="amount" label="订购金额" sortable />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrderRanking } from '@/api/statistics'

const loading = ref(false)
const list = ref([])
const dateRange = ref([])

onMounted(() => {
  handleQuery()
})

const handleQuery = () => {
  loading.value = true
  const params = {}
  if (dateRange.value && dateRange.value.length === 2) {
    params.startDate = dateRange.value[0]
    params.endDate = dateRange.value[1]
  }
  
  getOrderRanking(params).then(res => {
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

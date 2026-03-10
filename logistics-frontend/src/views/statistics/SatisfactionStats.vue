<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" class="filter-item" />
        <el-button type="primary" class="filter-item" @click="handleQuery">查询</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border stripe style="width: 100%">
        <el-table-column prop="name" label="对象(分站/个人)" />
        <el-table-column prop="type" label="类型" width="100">
             <template #default="scope">
                {{ scope.row.type === 'station' ? '分站' : '配送员' }}
             </template>
        </el-table-column>
        <el-table-column prop="total" label="总调查数" sortable />
        <el-table-column prop="verySatisfied" label="非常满意" sortable />
        <el-table-column prop="satisfied" label="满意" sortable />
        <el-table-column prop="average" label="一般" sortable />
        <el-table-column prop="dissatisfied" label="不满意" sortable />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSatisfaction } from '@/api/statistics'

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
  
  getSatisfaction(params).then(res => {
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

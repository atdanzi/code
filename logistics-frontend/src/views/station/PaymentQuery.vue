<template>
  <div class="page-container">
    <el-form :model="queryParams" inline class="search-form">
      <el-form-item label="开始日期">
        <el-date-picker v-model="queryParams.startDate" type="date" placeholder="选择开始日期" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item label="结束日期">
        <el-date-picker v-model="queryParams.endDate" type="date" placeholder="选择结束日期" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item label="商品名称">
        <el-input v-model="queryParams.productName" placeholder="请输入商品名称" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" border stripe show-summary :summary-method="getSummary">
      <el-table-column prop="productName" label="商品名称" />
      <el-table-column prop="deliverQuantity" label="送货数量" width="120" />
      <el-table-column prop="collectAmount" label="代收金额" width="140" />
      <el-table-column prop="returnQuantity" label="退货数量" width="120" />
      <el-table-column prop="refundAmount" label="退款金额" width="140" />
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
import { getPaymentQuery } from '@/api/task'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 10,
  startDate: '',
  endDate: '',
  productName: ''
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getPaymentQuery(queryParams)
    tableData.value = res.data.records || res.data.list || res.data || []
    total.value = res.data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const getSummary = ({ columns, data }) => {
  const sums = []
  columns.forEach((col, idx) => {
    if (idx === 0) {
      sums[idx] = '合计'
      return
    }
    const prop = col.property
    if (prop) {
      const values = data.map(item => Number(item[prop]))
      if (values.every(v => !isNaN(v))) {
        sums[idx] = values.reduce((a, b) => a + b, 0).toFixed(2)
      } else {
        sums[idx] = ''
      }
    } else {
      sums[idx] = ''
    }
  })
  return sums
}

const handleSearch = () => {
  queryParams.page = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(queryParams, { page: 1, size: 10, startDate: '', endDate: '', productName: '' })
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

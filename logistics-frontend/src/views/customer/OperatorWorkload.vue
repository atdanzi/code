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
          <el-option label="全部" value="" />
          <el-option label="新订" value="新订" />
          <el-option label="退订" value="退订" />
          <el-option label="换货" value="换货" />
          <el-option label="退货" value="退货" />
        </el-select>
      </el-form-item>
      <el-form-item label="操作员">
        <el-input v-model="queryParams.operatorId" placeholder="请输入操作员ID" clearable />
      </el-form-item>
      <el-form-item label="商品名称">
        <el-input v-model="queryParams.productName" placeholder="请输入商品名称" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" border stripe show-summary>
      <el-table-column prop="operatorName" label="操作员" width="120" />
      <el-table-column prop="category1Name" label="一级分类" width="120" />
      <el-table-column prop="category2Name" label="二级分类" width="120" />
      <el-table-column prop="productName" label="商品名称" width="150" />
      <el-table-column prop="newCount" label="新订笔数" width="100" />
      <el-table-column prop="newQuantity" label="新订数量" width="100" />
      <el-table-column prop="newAmount" label="新订金额" width="120" />
      <el-table-column prop="returnCount" label="退货笔数" width="100" />
      <el-table-column prop="returnQuantity" label="退货数量" width="100" />
      <el-table-column prop="returnAmount" label="退货金额" width="120" />
      <el-table-column prop="netIncome" label="净收入" width="120" />
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
import { getOperatorWorkload } from '@/api/statistics'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 10,
  startDate: '',
  endDate: '',
  orderType: '',
  operatorId: '',
  productName: ''
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getOperatorWorkload(queryParams)
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
  Object.assign(queryParams, {
    page: 1, size: 10, startDate: '', endDate: '',
    orderType: '', operatorId: '', productName: ''
  })
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

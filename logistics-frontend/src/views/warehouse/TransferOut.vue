<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-date-picker v-model="queryParams.startDate" type="date" placeholder="开始日期" class="filter-item" value-format="YYYY-MM-DD" />
        <el-date-picker v-model="queryParams.endDate" type="date" placeholder="结束日期" class="filter-item" value-format="YYYY-MM-DD" />
        <el-button type="primary" class="filter-item" @click="handleQuery">查询</el-button>
        <el-button type="success" class="filter-item" @click="handleBatchOutbound" :disabled="!selectedRows.length">批量出库</el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="list"
        border
        fit
        highlight-current-row
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="transferCode" label="调拨单号" />
        <el-table-column prop="fromWarehouseName" label="调出仓库" />
        <el-table-column prop="toWarehouseName" label="调入仓库" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column prop="status" label="状态" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getTransferOrders, transferOut } from '@/api/warehouseOp'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const list = ref([])
const queryParams = reactive({
  startDate: '',
  endDate: '',
  type: 'outbound_pending'
})
const selectedRows = ref([])

onMounted(() => {
  handleQuery()
})

const handleQuery = () => {
  loading.value = true
  getTransferOrders(queryParams).then(res => {
    list.value = res.data || []
  }).finally(() => {
    loading.value = false
  })
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleBatchOutbound = () => {
  ElMessageBox.confirm('确认对选中的调拨单进行出库操作吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const ids = selectedRows.value.map(row => row.id)
    transferOut({ ids }).then(() => {
      ElMessage.success('出库成功')
      handleQuery()
    })
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

<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-select v-model="queryParams.stationId" placeholder="选择分站" class="filter-item">
            <!-- 假设有分站API，此处略 -->
            <el-option label="分站A" :value="1" />
            <el-option label="分站B" :value="2" />
        </el-select>
        <el-date-picker v-model="queryParams.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" class="filter-item" />
        <el-button type="primary" class="filter-item" @click="handleQuery">查询结算单</el-button>
      </div>

      <el-table :data="list" border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="deliverQty" label="配送数量" />
        <el-table-column prop="returnQty" label="退货数量" />
        <el-table-column prop="amount" label="结算金额" />
        <el-table-column prop="status" label="状态">
             <template #default="scope">
                <el-tag :type="scope.row.status === 'settled' ? 'success' : 'warning'">
                    {{ scope.row.status === 'settled' ? '已结算' : '未结算' }}
                </el-tag>
             </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 20px; text-align: right;">
          <el-button type="primary" @click="handleSettle" :disabled="!selectedRows.length">结算</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { getStationSettlement, settleStation } from '@/api/finance'
import { ElMessage } from 'element-plus'

const list = ref([])
const selectedRows = ref([])
const queryParams = reactive({
    stationId: null,
    dateRange: []
})

const handleQuery = () => {
    const params = { ...queryParams }
    if (queryParams.dateRange && queryParams.dateRange.length === 2) {
        params.startDate = queryParams.dateRange[0]
        params.endDate = queryParams.dateRange[1]
    }
    delete params.dateRange
    
    getStationSettlement(params).then(res => {
        list.value = res.data || []
    })
}

const handleSelectionChange = (val) => {
    selectedRows.value = val
}

const handleSettle = () => {
    const ids = selectedRows.value.map(r => r.id)
    settleStation({ ids }).then(() => {
        ElMessage.success('结算成功')
        handleQuery()
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

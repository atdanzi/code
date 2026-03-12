<template>
  <div class="page-container">
    <el-card class="search-card" shadow="never">
      <div class="filter-container">
        <el-input
          v-model="queryParams.purchaseCode"
          placeholder="请输入购货单号（后端暂不支持过滤，仅前端筛选）"
          clearable
          class="filter-item"
          style="width: 280px"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" class="filter-item" @click="handleSearch">查询</el-button>
        <el-button class="filter-item" @click="handleReset">重置</el-button>
      </div>
    </el-card>

    <el-table :data="filteredTableData" v-loading="loading" border stripe style="margin-top: 12px">
      <el-table-column prop="purchaseCode" label="购货单号" width="180" />
      <el-table-column prop="supplierName" label="供应商" width="160" />
      <el-table-column prop="purchaseDate" label="购货日期" width="140" />
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'COMPLETED' ? 'success' : 'warning'">
            {{ statusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openDialog(row)" :disabled="row.status === 'COMPLETED'">入库</el-button>
        </template>
      </el-table-column>
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

    <el-dialog v-model="dialogVisible" title="购货入库" width="900px" destroy-on-close>
      <el-form :model="inboundForm" :rules="rules" ref="formRef" label-width="120px">
        <el-descriptions :column="3" border size="small" style="margin-bottom: 12px">
          <el-descriptions-item label="购货单号">{{ inboundForm.purchaseCode }}</el-descriptions-item>
          <el-descriptions-item label="供应商">{{ inboundForm.supplierName || '—' }}</el-descriptions-item>
          <el-descriptions-item label="购货日期">{{ inboundForm.purchaseDate || '—' }}</el-descriptions-item>
        </el-descriptions>

        <el-alert
          v-if="!inboundForm.items.length"
          type="info"
          show-icon
          title="当前接口未返回商品明细，将按照订购数量全额入库。如需调整实际到货数量，请在后端提供明细接口或让列表返回 items。"
          style="margin-bottom: 10px"
        />

        <el-table
          v-else
          :data="inboundForm.items"
          border
          size="small"
          max-height="360"
          v-loading="detailLoading"
        >
          <el-table-column prop="productName" label="商品名称" min-width="200" />
          <el-table-column prop="orderQuantity" label="应入库数量" width="140" />
          <el-table-column label="实际到货数量" width="180">
            <template #default="{ row }">
              <el-input-number
                v-model="row.actualQuantity"
                :min="0"
                :max="row.orderQuantity"
                :step="1"
                :controls="false"
              />
            </template>
          </el-table-column>
        </el-table>

        <el-form-item label="实际入库日期" prop="inboundDate" style="margin-top: 12px">
          <el-date-picker v-model="inboundForm.inboundDate" type="date" value-format="YYYY-MM-DD" style="width: 240px" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="inboundForm.remark"
            type="textarea"
            rows="2"
            placeholder="当实际入库数量与购货单不符时，可在此说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确认入库</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { purchaseIn, getPurchaseOrders } from '@/api/warehouseOp'

const queryParams = reactive({ page: 1, size: 10, purchaseCode: '' })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

const dialogVisible = ref(false)
const detailLoading = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const inboundForm = reactive({
  purchaseOrderId: null,
  purchaseCode: '',
  supplierName: '',
  purchaseDate: '',
  inboundDate: '',
  remark: '',
  items: []
})

const rules = {
  inboundDate: [{ required: true, message: '请选择实际入库日期', trigger: 'change' }]
}

const today = () => new Date().toISOString().slice(0, 10)

const statusLabel = (status) => {
  if (status === 'COMPLETED') return '已入库'
  if (status === 'PENDING') return '待入库'
  return status || '未知'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getPurchaseOrders(queryParams)
    const data = res.data.records || res.data.list || res.data || []
    tableData.value = data
    total.value = res.data.total || data.length || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const filteredTableData = computed(() => {
  if (!queryParams.purchaseCode) return tableData.value
  const code = queryParams.purchaseCode.trim()
  return tableData.value.filter(r => (r.purchaseCode || '').includes(code))
})

const handleSearch = () => {
  // 后端未提供按单号过滤，这里做前端过滤；如后端支持可直接调用 fetchData()
  fetchData()
}

const handleReset = () => {
  Object.assign(queryParams, { page: 1, size: 10, purchaseCode: '' })
  fetchData()
}

const openDialog = (row) => {
  dialogVisible.value = true
  Object.assign(inboundForm, {
    purchaseOrderId: row.id,
    purchaseCode: row.purchaseCode,
    supplierName: row.supplierName || '',
    purchaseDate: row.purchaseDate || '',
    inboundDate: today(),
    remark: '',
    items: []
  })

  detailLoading.value = true
  const items = row.items || row.orderItems || row.products || []
  inboundForm.items = items.map(it => ({
    productId: it.productId,
    productName: it.productName || '',
    orderQuantity: it.orderQuantity ?? it.quantity ?? 0,
    actualQuantity: it.actualQuantity ?? it.orderQuantity ?? it.quantity ?? 0
  }))
  detailLoading.value = false
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    const payload = {
      purchaseOrderId: inboundForm.purchaseOrderId,
      inboundDate: inboundForm.inboundDate,
      remark: inboundForm.remark,
      actualQuantities: inboundForm.items.reduce((acc, cur) => {
        acc[cur.productId] = cur.actualQuantity ?? 0
        return acc
      }, {})
    }
    submitLoading.value = true
    await purchaseIn(payload)
    ElMessage.success('入库成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    console.error(e)
    if (e && e.message) ElMessage.error(e.message)
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 12px;
}
.filter-container {
  display: flex;
  align-items: center;
}
.filter-item {
  margin-right: 10px;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>

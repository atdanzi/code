<template>
  <div class="page-container">
    <el-card class="search-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="购货单号">
          <el-input v-model="queryParams.orderCode" placeholder="请输入购货单号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-table :data="tableData" v-loading="loading" border stripe style="margin-top: 12px">
      <el-table-column prop="purchaseCode" label="购货单号" width="160" />
      <el-table-column prop="supplierName" label="供应商" width="150" />
      <el-table-column prop="warehouseName" label="入库仓" width="150" />
      <el-table-column prop="createTime" label="下单时间" width="160" />
      <el-table-column prop="status" label="状态" width="100" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openDialog(row)">入库</el-button>
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
      <el-form :model="inboundForm" :rules="rules" ref="formRef" label-width="110px">
        <el-descriptions :column="2" border size="small" style="margin-bottom: 12px">
          <el-descriptions-item label="购货单号">{{ inboundForm.purchaseCode }}</el-descriptions-item>
          <el-descriptions-item label="供应商">{{ inboundForm.supplierName }}</el-descriptions-item>
          <el-descriptions-item label="入库仓">{{ inboundForm.warehouseName }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ inboundForm.createTime }}</el-descriptions-item>
        </el-descriptions>

        <el-table :data="inboundForm.items" border size="small" max-height="360" v-loading="detailLoading">
          <el-table-column prop="productName" label="商品名称" min-width="180" />
          <el-table-column prop="orderQuantity" label="订购数量" width="120" />
          <el-table-column label="实际到货数量" width="160">
            <template #default="{ row }">
              <el-input-number
                v-model="row.actualQuantity"
                :min="0"
                :max="row.orderQuantity"
                :step="1"
              />
            </template>
          </el-table-column>
        </el-table>

        <el-form-item label="实际入库日期" prop="inboundDate" style="margin-top: 12px">
          <el-date-picker v-model="inboundForm.inboundDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="inboundForm.remark" type="textarea" rows="2" placeholder="实际到货与订购不符时填写" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">入库</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { purchaseIn, getPurchaseOrders } from '@/api/warehouseOp'

const queryParams = reactive({ page: 1, size: 10, orderCode: '' })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

const dialogVisible = ref(false)
const detailLoading = ref(false)
const formRef = ref(null)
const inboundForm = reactive({
  purchaseOrderId: null,
  purchaseCode: '',
  supplierName: '',
  warehouseName: '',
  createTime: '',
  inboundDate: '',
  remark: '',
  items: []
})

const rules = {
  inboundDate: [{ required: true, message: '请选择实际入库日期', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getPurchaseOrders(queryParams)
    const data = res.data.records || res.data.list || res.data || []
    tableData.value = data
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
  Object.assign(queryParams, { page: 1, size: 10, orderCode: '' })
  fetchData()
}

const openDialog = (row) => {
  dialogVisible.value = true
  detailLoading.value = true
  Object.assign(inboundForm, {
    purchaseOrderId: row.id,
    purchaseCode: row.purchaseCode,
    supplierName: row.supplierName,
    warehouseName: row.warehouseName,
    createTime: row.createTime,
    inboundDate: '',
    remark: '',
    items: []
  })
  // 明细已包含在列表的 items/或需再查。此处先用列表行的 items，如果没有则默认订购数量为到货数量。
  const items = row.items || row.orderItems || []
  inboundForm.items = items.map(it => ({
    productId: it.productId,
    productName: it.productName,
    orderQuantity: it.quantity || it.orderQuantity || it.expectedQuantity || 0,
    actualQuantity: it.quantity || it.orderQuantity || it.expectedQuantity || 0
  }))
  detailLoading.value = false
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (!inboundForm.items.length) {
      ElMessage.warning('无商品明细，无法入库')
      return
    }
    const payload = {
      purchaseOrderId: inboundForm.purchaseOrderId,
      inboundDate: inboundForm.inboundDate,
      remark: inboundForm.remark,
      actualQuantities: inboundForm.items.reduce((acc, cur) => {
        acc[cur.productId] = cur.actualQuantity ?? 0
        return acc
      }, {})
    }
    await purchaseIn(payload)
    ElMessage.success('入库成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    console.error(e)
    if (e && e.message) ElMessage.error(e.message)
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
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>

<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-input v-model="queryParams.purchaseCode" placeholder="购货单号" style="width: 200px;" class="filter-item" @keyup.enter="handleQuery" />
        <el-button type="primary" class="filter-item" @click="handleQuery">查询</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border fit highlight-current-row style="width: 100%">
        <el-table-column prop="purchaseCode" label="购货单号" />
        <el-table-column prop="supplierName" label="供应商" />
        <el-table-column prop="status" label="状态" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="text" @click="handleInbound(scope.row)">入库</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog title="购货入库" v-model="dialogVisible" width="60%">
      <el-form :model="form" label-width="100px">
        <el-form-item label="入库日期">
          <el-date-picker v-model="form.inboundDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-table :data="form.products" border style="width: 100%">
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="quantity" label="采购数量" />
          <el-table-column label="实际入库数量">
            <template #default="scope">
              <el-input-number v-model="scope.row.actualQuantity" :min="0" />
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitInbound">确认入库</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getPurchaseOrders, purchaseIn } from '@/api/warehouseOp'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const list = ref([])
const queryParams = reactive({
  purchaseCode: '',
  type: 'inbound_pending' // 假设后端支持此过滤
})

const dialogVisible = ref(false)
const form = reactive({
  purchaseId: null,
  inboundDate: '',
  products: []
})

onMounted(() => {
  handleQuery()
})

const handleQuery = () => {
  loading.value = true
  getPurchaseOrders(queryParams).then(res => {
    list.value = res.data || []
  }).finally(() => {
    loading.value = false
  })
}

const handleInbound = (row) => {
  form.purchaseId = row.id
  form.inboundDate = new Date().toISOString().split('T')[0]
  form.products = row.products.map(p => ({
    productId: p.productId,
    productName: p.productName,
    quantity: p.quantity,
    actualQuantity: p.quantity // 默认全部入库
  }))
  dialogVisible.value = true
}

const submitInbound = () => {
  if (!form.inboundDate) {
    ElMessage.warning('请选择入库日期')
    return
  }
  purchaseIn(form).then(() => {
    ElMessage.success('入库成功')
    dialogVisible.value = false
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

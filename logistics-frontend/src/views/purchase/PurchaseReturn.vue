<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-select v-model="supplierId" placeholder="选择供应商" class="filter-item">
           <el-option v-for="s in suppliers" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
        <el-button type="primary" class="filter-item" @click="handleQuery">查询可退商品</el-button>
      </div>

      <el-table :data="list" border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="purchaseDate" label="进货日期" />
        <el-table-column prop="purchasePrice" label="进货单价" />
        <el-table-column prop="quantity" label="进货数量" />
        <el-table-column prop="stockQuantity" label="当前库存" />
        <el-table-column label="退货数量">
           <template #default="scope">
               <el-input-number v-model="scope.row.returnQty" :min="0" :max="scope.row.stockQuantity" />
           </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 20px; text-align: right;">
          <el-button type="danger" @click="handleCreateReturn" :disabled="!selectedRows.length">生成退货单</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { createReturn, getPurchaseOrders } from '@/api/purchase'
import { getSuppliers } from '@/api/supplier'
import { ElMessage } from 'element-plus'

const suppliers = ref([])
const supplierId = ref(null)
const list = ref([])
const selectedRows = ref([])

onMounted(() => {
    getSuppliers().then(res => {
        suppliers.value = res.data || []
    })
})

const handleQuery = () => {
    if (!supplierId.value) return
    // 假设 getPurchaseOrders 可以返回该供应商的可退货明细，或者需要专门的API
    getPurchaseOrders({ supplierId: supplierId.value, status: 'received' }).then(res => {
        // 模拟数据处理，实际应由后端返回可退货列表（包含库存信息）
        list.value = res.data.map(item => ({
            ...item,
            returnQty: 0,
            stockQuantity: 100 // 模拟库存
        })) || []
    })
}

const handleSelectionChange = (val) => {
    selectedRows.value = val
}

const handleCreateReturn = () => {
    const products = selectedRows.value
        .filter(r => r.returnQty > 0)
        .map(r => ({
            productId: r.productId,
            quantity: r.returnQty,
            purchaseId: r.id
        }))
    
    if (products.length === 0) {
        ElMessage.warning('请输入退货数量')
        return
    }

    createReturn({ supplierId: supplierId.value, products }).then(() => {
        ElMessage.success('退货单生成成功')
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

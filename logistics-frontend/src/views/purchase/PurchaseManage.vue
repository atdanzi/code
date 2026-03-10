<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="clearfix">
          <span>缺货查询</span>
        </div>
      </template>
      <div class="filter-container">
        <el-select v-model="shortageParams.categoryId" placeholder="一级分类" clearable class="filter-item">
            <!-- 假设有分类API，此处略 -->
            <el-option label="分类A" :value="1" />
        </el-select>
        <el-input v-model="shortageParams.productName" placeholder="商品名称" style="width: 200px;" class="filter-item" />
        <el-button type="primary" class="filter-item" @click="handleCheckShortage">查询缺货</el-button>
      </div>
      <el-table :data="shortageList" border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="stockQuantity" label="当前库存" />
        <el-table-column prop="shortageQuantity" label="缺货数量" />
        <el-table-column label="建议采购量">
             <template #default="scope">
                 <el-input-number v-model="scope.row.purchaseQty" :min="1" />
             </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card class="box-card" style="margin-top: 20px;">
       <template #header>
        <div class="clearfix">
          <span>生成购货单</span>
        </div>
      </template>
      <el-form :inline="true">
          <el-form-item label="供应商">
              <el-select v-model="supplierId" placeholder="选择供应商">
                  <el-option v-for="s in suppliers" :key="s.id" :label="s.name" :value="s.id" />
              </el-select>
          </el-form-item>
          <el-form-item>
              <el-button type="success" @click="handleCreatePurchase" :disabled="!selectedRows.length || !supplierId">生成购货单</el-button>
          </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { checkShortage, createPurchase } from '@/api/purchase'
import { getSuppliers } from '@/api/supplier'
import { ElMessage } from 'element-plus'

const shortageParams = reactive({
  categoryId: null,
  productName: ''
})
const shortageList = ref([])
const selectedRows = ref([])
const suppliers = ref([])
const supplierId = ref(null)

onMounted(() => {
    getSuppliers().then(res => {
        suppliers.value = res.data || []
    })
})

const handleCheckShortage = () => {
  checkShortage(shortageParams).then(res => {
    shortageList.value = res.data.map(item => ({
        ...item,
        purchaseQty: item.shortageQuantity > 0 ? item.shortageQuantity : 10 // 默认值
    })) || []
  })
}

const handleSelectionChange = (val) => {
    selectedRows.value = val
}

const handleCreatePurchase = () => {
    const products = selectedRows.value.map(row => ({
        productId: row.productId,
        quantity: row.purchaseQty
    }))
    createPurchase({ supplierId: supplierId.value, products }).then(() => {
        ElMessage.success('购货单生成成功')
        shortageList.value = [] // 清空列表
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

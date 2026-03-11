<template>
  <div class="page-container">
    <el-card class="search-card" shadow="never">
      <el-form :model="query" inline>
        <el-form-item label="一级分类">
          <el-select v-model="query.category1Id" placeholder="请选择" clearable @change="onCategory1Change">
            <el-option v-for="c in category1List" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="二级分类">
          <el-select v-model="query.category2Id" placeholder="请选择" clearable>
            <el-option v-for="c in category2List" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品名称">
          <el-input v-model="query.productName" placeholder="请输入商品名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" style="margin-top: 12px">
      <div class="table-toolbar">
        <el-date-picker v-model="purchaseDate" type="date" value-format="YYYY-MM-DD" placeholder="选择进货日期" />
        <el-button type="primary" :disabled="!selectedRows.length" @click="openConfirm">进货登记</el-button>
      </div>
      <el-table
        :data="tableData"
        v-loading="loading"
        border
        stripe
        @selection-change="selectedRows = $event"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="category1Name" label="一级分类" width="140" />
        <el-table-column prop="category2Name" label="二级分类" width="140" />
        <el-table-column prop="productName" label="商品名称" min-width="160" />
        <el-table-column prop="supplierName" label="供应商" width="160" />
        <el-table-column prop="availableQuantity" label="现有库存" width="100" />
        <el-table-column prop="warningValue" label="预警库存" width="100" />
        <el-table-column prop="shortageQuantity" label="缺货数量" width="100" />
        <el-table-column label="进货量" width="140">
          <template #default="{ row }">
            <el-input-number v-model="row.purchaseQuantity" :min="0" :step="1" />
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="80" />
      </el-table>
    </el-card>

    <el-dialog v-model="confirmVisible" title="进货确认" width="720px" destroy-on-close>
      <el-descriptions :column="2" border size="small" style="margin-bottom: 10px">
        <el-descriptions-item label="供应商">{{ confirmData.supplierName }}</el-descriptions-item>
        <el-descriptions-item label="进货日期">{{ confirmData.purchaseDate }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="confirmData.items" border size="small" max-height="320">
        <el-table-column prop="productName" label="商品" min-width="160" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="purchaseQuantity" label="进货量" width="100" />
      </el-table>
      <el-form label-width="80px" style="margin-top: 12px">
        <el-form-item label="备注">
          <el-input v-model="remark" type="textarea" rows="2" placeholder="可填写进货备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="confirmVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">生成购货单</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="printVisible" title="购货单" width="780px" destroy-on-close>
      <el-descriptions :column="2" border size="small" style="margin-bottom: 10px">
        <el-descriptions-item label="购货单号">{{ printOrder.purchaseCode }}</el-descriptions-item>
        <el-descriptions-item label="供应商">{{ printOrder.supplierName }}</el-descriptions-item>
        <el-descriptions-item label="进货日期">{{ printOrder.purchaseDate }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="printOrder.items" border size="small">
        <el-table-column prop="productName" label="商品" min-width="180" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="orderQuantity" label="进货量" width="100" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { checkShortage, createPurchase } from '@/api/purchase'
import { getCategories1, getCategories2 } from '@/api/product'
import { getSuppliers } from '@/api/supplier'

const query = reactive({ category1Id: '', category2Id: '', productName: '' })
const category1List = ref([])
const category2List = ref([])
const suppliers = ref([])
const tableData = ref([])
const loading = ref(false)
const selectedRows = ref([])
const purchaseDate = ref('')

const confirmVisible = ref(false)
const confirmData = reactive({ supplierId: null, supplierName: '', purchaseDate: '', items: [] })
const remark = ref('')

const printVisible = ref(false)
const printOrder = reactive({ purchaseCode: '', supplierName: '', purchaseDate: '', items: [] })

const fetchCategories = async () => {
  const res = await getCategories1({ size: 1000 })
  category1List.value = res.data.records || res.data.list || res.data || []
}

const onCategory1Change = async (val) => {
  query.category2Id = ''
  if (!val) {
    category2List.value = []
    return
  }
  const res = await getCategories2({ category1Id: val, size: 1000 })
  category2List.value = res.data.records || res.data.list || res.data || []
}

const fetchSuppliers = async () => {
  const res = await getSuppliers({ size: 1000 })
  suppliers.value = res.data.records || res.data.list || res.data || []
}

const mapSupplierName = (supplierId) => suppliers.value.find(s => s.id === supplierId)?.name || ''

const fetchShortage = async () => {
  loading.value = true
  try {
    const res = await checkShortage({
      category1Id: query.category1Id || undefined,
      category2Id: query.category2Id || undefined,
      productName: query.productName || undefined
    })
    const list = res.data || []
    tableData.value = list.map(it => ({
      ...it,
      supplierName: mapSupplierName(it.supplierId),
      purchaseQuantity: it.shortageQuantity ?? 0
    }))
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  fetchShortage()
}

const handleReset = () => {
  Object.assign(query, { category1Id: '', category2Id: '', productName: '' })
  category2List.value = []
  fetchShortage()
}

const openConfirm = () => {
  if (!purchaseDate.value) {
    ElMessage.warning('请选择进货日期')
    return
  }
  if (!selectedRows.value.length) {
    ElMessage.warning('请先选择需要进货的商品')
    return
  }
  const supplierIds = Array.from(new Set(selectedRows.value.map(r => r.supplierId).filter(Boolean)))
  if (supplierIds.length > 1) {
    ElMessage.warning('所选商品属于不同供应商，请分开进货登记')
    return
  }
  if (selectedRows.value.some(r => (r.maxValue != null && r.availableQuantity != null && r.purchaseQuantity != null && (r.purchaseQuantity + r.availableQuantity > r.maxValue)))) {
    ElMessage.warning('进货量+现有库存需小于等于最高库存，请检查')
    return
  }
  const supId = supplierIds[0] || null
  Object.assign(confirmData, {
    supplierId: supId,
    supplierName: mapSupplierName(supId),
    purchaseDate: purchaseDate.value,
    items: selectedRows.value.map(r => ({
      productId: r.productId,
      productName: r.productName,
      unit: r.unit,
      purchaseQuantity: r.purchaseQuantity || 0
    }))
  })
  remark.value = ''
  confirmVisible.value = true
}

const handleCreate = async () => {
  try {
    if (!confirmData.items.length) {
      ElMessage.warning('没有可提交的商品')
      return
    }
    const payload = {
      supplierId: confirmData.supplierId,
      purchaseDate: confirmData.purchaseDate,
      remark: remark.value,
      items: confirmData.items.map(i => ({
        productId: i.productId,
        orderQuantity: i.purchaseQuantity,
        remark: ''
      }))
    }
    const res = await createPurchase(payload)
    ElMessage.success('生成购货单成功')
    confirmVisible.value = false
    Object.assign(printOrder, {
      purchaseCode: res.data?.purchaseCode || '',
      supplierName: confirmData.supplierName,
      purchaseDate: confirmData.purchaseDate,
      items: confirmData.items.map(i => ({
        productName: i.productName,
        unit: i.unit,
        orderQuantity: i.purchaseQuantity
      }))
    })
    printVisible.value = true
    fetchShortage()
  } catch (e) {
    console.error(e)
    if (e && e.message) ElMessage.error(e.message)
  }
}

onMounted(async () => {
  await Promise.all([fetchCategories(), fetchSuppliers()])
  fetchShortage()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 12px;
}
.table-toolbar {
  margin-bottom: 8px;
  display: flex;
  gap: 10px;
  align-items: center;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>

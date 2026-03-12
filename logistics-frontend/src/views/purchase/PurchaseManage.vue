<template>
  <div class="page-container">
    <el-card class="search-card" shadow="never">
      <el-form :model="query" inline>
        <el-form-item label="商品名称">
          <el-input v-model="query.productName" placeholder="用于列表筛选购货单" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询购货单</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <div class="table-toolbar" style="margin-top:8px">
        <el-button type="primary" @click="openSelector">进货登记</el-button>
      </div>
    </el-card>

    <el-card shadow="never" style="margin-top: 12px">
      <p style="margin-bottom:8px">已有购货单（按商品名称筛选展示，创建后可用于入库）：</p>
      <el-table :data="orderList" border stripe v-loading="orderLoading">
        <el-table-column prop="purchaseCode" label="购货单号" width="160" />
        <el-table-column prop="supplierName" label="供应商" width="150" />
        <el-table-column prop="purchaseDate" label="进货日期" width="140" />
        <el-table-column prop="remark" label="备注" min-width="160" />
      </el-table>
    </el-card>

    <el-dialog v-model="selectorVisible" title="进货登记" width="900px" destroy-on-close>
      <div style="margin-bottom:8px; display:flex; gap:10px; align-items:center">
        <el-select v-model="query.category1Id" placeholder="一级分类" clearable style="width:160px" @change="onCategory1Change">
          <el-option v-for="c in category1List" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
        <el-select v-model="query.category2Id" placeholder="二级分类" clearable style="width:160px">
          <el-option v-for="c in category2List" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
        <el-input v-model="query.productName" placeholder="商品名称" clearable style="width:200px" />
        <el-select v-model="selectorSupplierId" placeholder="供应商" clearable style="width:180px">
          <el-option v-for="s in suppliers" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
        <el-date-picker v-model="purchaseDate" type="date" value-format="YYYY-MM-DD" placeholder="选择进货日期" />
        <el-button type="primary" @click="fetchShortage">刷新可选商品</el-button>
      </div>
      <el-table
        :data="tableData"
        v-loading="loading"
        border
        stripe
        @selection-change="selectedRows = $event"
        height="420px"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="category1Name" label="一级分类" width="140" />
        <el-table-column prop="category2Name" label="二级分类" width="140" />
        <el-table-column prop="productName" label="商品名称" min-width="160" />
        <el-table-column prop="supplierName" label="供应商" width="150" />
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
      <template #footer>
        <el-button @click="selectorVisible = false">取消</el-button>
        <el-button type="primary" :disabled="!selectedRows.length" @click="openConfirm">下一步</el-button>
      </template>
    </el-dialog>

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
import { checkShortage, createPurchase, getPurchaseOrders } from '@/api/purchase'
import { getCategories1, getCategories2, getProduct, getProducts } from '@/api/product'
import { getSuppliers } from '@/api/supplier'

const query = reactive({ category1Id: '', category2Id: '', productName: '' })
const category1List = ref([])
const category2List = ref([])
const suppliers = ref([])
const tableData = ref([])
const loading = ref(false)
const selectedRows = ref([])
const purchaseDate = ref(new Date().toISOString().slice(0, 10))
const selectorVisible = ref(false)
const selectorSupplierId = ref(null)

const confirmVisible = ref(false)
const confirmData = reactive({ supplierId: null, supplierName: '', purchaseDate: '', items: [] })
const remark = ref('')

const printVisible = ref(false)
const printOrder = reactive({ purchaseCode: '', supplierName: '', purchaseDate: '', items: [] })

const orderList = ref([])
const orderLoading = ref(false)

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

const fetchProductMap = async (productIds) => {
  const uniq = Array.from(new Set(productIds.filter(Boolean)))
  const entries = await Promise.all(
    uniq.map(async (id) => {
      try {
        const res = await getProduct(id)
        return [id, res.data || {}]
      } catch (e) {
        console.error('getProduct failed', id, e)
        return [id, {}]
      }
    })
  )
  return Object.fromEntries(entries)
}

const calcSuggestQty = (item) => {
  if (item.shortageQuantity != null) return item.shortageQuantity
  if (item.warningValue != null && item.availableQuantity != null) {
    return Math.max((item.warningValue || 0) - (item.availableQuantity || 0), 0)
  }
  return 0
}

const applyFilters = (list) => {
  return list.filter(it => {
    if (query.category1Id && it.category1Id !== query.category1Id) return false
    if (query.category2Id && it.category2Id !== query.category2Id) return false
    if (query.productName && !(it.productName || '').includes(query.productName.trim())) return false
    return true
  })
}

const fetchShortage = async () => {
  loading.value = true
  try {
    // 1) 首选按照缺货/预警接口获取（后端支持时优先使用）
    const res = await checkShortage({
      category1Id: query.category1Id || undefined,
      category2Id: query.category2Id || undefined,
      productName: query.productName || undefined
    })
    let rawList = res.data || []

    // 2) 若缺货接口返回空，降级为直接取商品列表，方便先录入采购
    if (!rawList.length) {
      const fallback = await getProducts({
        category1Id: query.category1Id || undefined,
        category2Id: query.category2Id || undefined,
        name: query.productName || undefined,
        size: 1000,
        page: 1
      })
      const list = fallback.data.records || fallback.data.list || fallback.data || []
      rawList = list.map(p => ({
        productId: p.id,
        productName: p.name,
        unit: p.unit,
        category1Id: p.category1Id,
        category2Id: p.category2Id,
        category1Name: p.category1Name,
        category2Name: p.category2Name,
        supplierId: p.supplierId,
        supplierName: p.supplierName,
        availableQuantity: p.availableQuantity ?? 0,
        warningValue: p.warningValue ?? 0,
        shortageQuantity: p.shortageQuantity ?? 0,
        purchaseQuantity: p.shortageQuantity ?? 0
      }))
    }

    const productMap = await fetchProductMap(rawList.map(it => it.productId))
    const enriched = rawList.map(it => {
      const p = productMap[it.productId] || {}
      const supplierId = p.supplierId || it.supplierId
      return {
        ...it,
        productId: it.productId,
        productName: p.name || it.productName || it.idLabel || '',
        unit: p.unit || it.unit || '',
        category1Id: p.category1Id,
        category2Id: p.category2Id,
        category1Name: p.category1Name || it.category1Name,
        category2Name: p.category2Name || it.category2Name,
        supplierId,
        supplierName: mapSupplierName(supplierId),
        availableQuantity: it.availableQuantity,
        warningValue: it.warningValue,
        shortageQuantity: it.shortageQuantity,
        purchaseQuantity: calcSuggestQty(it)
      }
    })

    tableData.value = applyFilters(enriched)
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  loadOrders()
}

const handleReset = () => {
  Object.assign(query, { category1Id: '', category2Id: '', productName: '' })
  category2List.value = []
  loadOrders()
}

const openSelector = () => {
  selectorVisible.value = true
  selectorSupplierId.value = null
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
  // 供应商确定逻辑：优先用户选择；否则从商品推断（必须单一）
  let supplierIds = Array.from(new Set(selectedRows.value.map(r => r.supplierId).filter(Boolean)))
  if (selectorSupplierId.value) {
    supplierIds = [selectorSupplierId.value]
  }
  if (supplierIds.length === 0) {
    ElMessage.warning('请选择供应商')
    return
  }
  if (supplierIds.length > 1) {
    ElMessage.warning('所选商品属于不同供应商，请分开进货登记或指定供应商')
    return
  }
  selectorSupplierId.value = supplierIds[0]
  if (selectedRows.value.some(r => (r.maxValue != null && r.availableQuantity != null && r.purchaseQuantity != null && (r.purchaseQuantity + r.availableQuantity > r.maxValue)))) {
    ElMessage.warning('进货量+现有库存需小于等于最高库存，请检查')
    return
  }
  if (selectedRows.value.every(r => !r.purchaseQuantity || r.purchaseQuantity <= 0)) {
    ElMessage.warning('请填写大于0的进货量')
    return
  }
  const supId = selectorSupplierId.value
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
    const validItems = confirmData.items.filter(i => i.purchaseQuantity > 0)
    if (!validItems.length) {
      ElMessage.warning('进货量需大于0')
      return
    }
    const payload = {
      supplierId: confirmData.supplierId,
      purchaseDate: confirmData.purchaseDate,
      remark: remark.value,
      items: validItems.map(i => ({
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
    selectorVisible.value = false
    fetchShortage()
    loadOrders()
  } catch (e) {
    console.error(e)
    if (e && e.message) ElMessage.error(e.message)
  }
}

const loadOrders = async () => {
  orderLoading.value = true
  try {
    const res = await getPurchaseOrders({ size: 100, page: 1 })
    const list = (res.data.records || res.data.list || res.data || []).map(o => ({
      ...o,
      supplierName: o.supplierName || mapSupplierName(o.supplierId)
    }))
    const keyword = query.productName?.trim()
    orderList.value = keyword ? list.filter(o => (o.remark || '').includes(keyword) || (o.purchaseCode || '').includes(keyword)) : list
  } catch (e) {
    console.error(e)
  } finally {
    orderLoading.value = false
  }
}

onMounted(async () => {
  await Promise.all([fetchCategories(), fetchSuppliers()])
  loadOrders()
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

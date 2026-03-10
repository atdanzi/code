<template>
  <div class="page-container">
    <el-form :model="queryParams" inline class="search-form">
      <el-form-item label="订单编号">
        <el-input v-model="queryParams.orderCode" placeholder="请输入订单编号" clearable />
      </el-form-item>
      <el-form-item label="订单类型">
        <el-select v-model="queryParams.orderType" placeholder="请选择" clearable>
          <el-option label="新订" value="新订" />
          <el-option label="退订" value="退订" />
          <el-option label="换货" value="换货" />
          <el-option label="退货" value="退货" />
        </el-select>
      </el-form-item>
      <el-form-item label="订单状态">
        <el-select v-model="queryParams.orderStatus" placeholder="请选择" clearable>
          <el-option label="待处理" value="待处理" />
          <el-option label="已调度" value="已调度" />
          <el-option label="配送中" value="配送中" />
          <el-option label="已完成" value="已完成" />
          <el-option label="已取消" value="已取消" />
        </el-select>
      </el-form-item>
      <el-form-item label="客户ID">
        <el-input v-model="queryParams.customerId" placeholder="请输入客户ID" clearable />
      </el-form-item>
      <el-form-item label="开始日期">
        <el-date-picker v-model="queryParams.startDate" type="date" placeholder="选择开始日期" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item label="结束日期">
        <el-date-picker v-model="queryParams.endDate" type="date" placeholder="选择结束日期" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <el-button type="primary" @click="handleAdd">新增订单</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="orderCode" label="订单编号" width="160" />
      <el-table-column prop="customerName" label="客户姓名" width="120" />
      <el-table-column prop="orderType" label="订单类型" width="100" />
      <el-table-column prop="orderStatus" label="订单状态" width="100" />
      <el-table-column prop="totalAmount" label="总金额" width="120" />
      <el-table-column prop="orderDate" label="下单日期" width="120" />
      <el-table-column prop="requireDate" label="要求送达日期" width="130" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button type="warning" link @click="handleCancel(row)">退订</el-button>
          <el-button type="primary" link @click="handleExchange(row)">换货</el-button>
          <el-button type="danger" link @click="handleReturn(row)">退货</el-button>
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

    <!-- 新增订单对话框 -->
    <el-dialog v-model="dialogVisible" title="新增订单" width="900px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="110px">
        <el-divider content-position="left">客户信息</el-divider>
        <el-form-item label="搜索客户">
          <el-input v-model="customerSearch" placeholder="输入姓名或手机号搜索" style="width: 300px" />
          <el-button type="primary" style="margin-left: 10px" @click="searchCustomer">搜索</el-button>
        </el-form-item>
        <el-table v-if="customerList.length" :data="customerList" size="small" border style="margin-bottom: 16px" max-height="200">
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="mobile" label="手机号" width="130" />
          <el-table-column prop="address" label="地址" />
          <el-table-column label="操作" width="80">
            <template #default="{ row }">
              <el-button type="primary" link @click="selectCustomer(row)">选择</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-form-item label="已选客户">
          <el-tag v-if="formData.customerId" type="success">{{ formData.customerName }}</el-tag>
          <span v-else style="color: #999">请搜索并选择客户</span>
        </el-form-item>

        <el-divider content-position="left">商品信息</el-divider>
        <el-form-item label="搜索商品">
          <el-input v-model="productSearch" placeholder="输入商品名称搜索" style="width: 300px" />
          <el-button type="primary" style="margin-left: 10px" @click="searchProduct">搜索</el-button>
        </el-form-item>
        <el-table v-if="productList.length" :data="productList" size="small" border style="margin-bottom: 16px" max-height="200">
          <el-table-column prop="name" label="商品名称" width="150" />
          <el-table-column prop="originalPrice" label="单价" width="100" />
          <el-table-column prop="unit" label="单位" width="80" />
          <el-table-column label="数量" width="150">
            <template #default="{ row }">
              <el-input-number v-model="row.buyQuantity" :min="1" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{ row }">
              <el-button type="primary" link @click="addToCart(row)">添加</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-table v-if="formData.items.length" :data="formData.items" size="small" border style="margin-bottom: 16px">
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="price" label="单价" width="100" />
          <el-table-column prop="quantity" label="数量" width="120">
            <template #default="{ row }">
              <el-input-number v-model="row.quantity" :min="1" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="小计" width="100">
            <template #default="{ row }">{{ (row.price * row.quantity).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{ $index }">
              <el-button type="danger" link @click="formData.items.splice($index, 1)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-divider content-position="left">收货信息</el-divider>
        <el-form-item label="收货人姓名" prop="receiverName">
          <el-input v-model="formData.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="收货人电话" prop="receiverPhone">
          <el-input v-model="formData.receiverPhone" placeholder="请输入收货人电话" />
        </el-form-item>
        <el-form-item label="收货地址" prop="receiverAddress">
          <el-input v-model="formData.receiverAddress" placeholder="请输入收货地址" />
        </el-form-item>
        <el-form-item label="要求送达日期" prop="requireDate">
          <el-date-picker v-model="formData.requireDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="是否需要发票">
          <el-switch v-model="formData.needInvoice" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交订单</el-button>
      </template>
    </el-dialog>

    <!-- 退订对话框 -->
    <el-dialog v-model="cancelDialogVisible" title="退订" width="500px" destroy-on-close>
      <el-form :model="cancelForm" label-width="80px">
        <el-form-item label="退订原因">
          <el-input v-model="cancelForm.reason" type="textarea" rows="3" placeholder="请输入退订原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCancel">确定</el-button>
      </template>
    </el-dialog>

    <!-- 换货对话框 -->
    <el-dialog v-model="exchangeDialogVisible" title="换货" width="500px" destroy-on-close>
      <el-form :model="exchangeForm" label-width="80px">
        <el-form-item label="换货原因">
          <el-input v-model="exchangeForm.reason" type="textarea" rows="3" placeholder="请输入换货原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="exchangeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitExchange">确定</el-button>
      </template>
    </el-dialog>

    <!-- 退货对话框 -->
    <el-dialog v-model="returnDialogVisible" title="退货" width="500px" destroy-on-close>
      <el-form :model="returnForm" label-width="80px">
        <el-form-item label="退货原因">
          <el-input v-model="returnForm.reason" type="textarea" rows="3" placeholder="请输入退货原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="returnDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReturn">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrders, createOrder, cancelOrder, exchangeOrder, returnOrder } from '@/api/order'
import { getCustomers } from '@/api/customer'
import { getProducts } from '@/api/product'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const customerSearch = ref('')
const productSearch = ref('')
const customerList = ref([])
const productList = ref([])

const queryParams = reactive({
  page: 1,
  size: 10,
  orderCode: '',
  orderType: '',
  orderStatus: '',
  customerId: '',
  startDate: '',
  endDate: ''
})

const formData = reactive({
  customerId: null,
  customerName: '',
  items: [],
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  requireDate: '',
  needInvoice: false
})

const rules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [{ required: true, message: '请输入收货人电话', trigger: 'blur' }],
  receiverAddress: [{ required: true, message: '请输入收货地址', trigger: 'blur' }],
  requireDate: [{ required: true, message: '请选择要求送达日期', trigger: 'change' }]
}

const cancelDialogVisible = ref(false)
const exchangeDialogVisible = ref(false)
const returnDialogVisible = ref(false)
const currentRow = ref(null)
const cancelForm = reactive({ reason: '' })
const exchangeForm = reactive({ reason: '' })
const returnForm = reactive({ reason: '' })

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getOrders(queryParams)
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
    page: 1, size: 10, orderCode: '', orderType: '',
    orderStatus: '', customerId: '', startDate: '', endDate: ''
  })
  fetchData()
}

const handleAdd = () => {
  Object.assign(formData, {
    customerId: null, customerName: '', items: [],
    receiverName: '', receiverPhone: '', receiverAddress: '',
    requireDate: '', needInvoice: false
  })
  customerList.value = []
  productList.value = []
  customerSearch.value = ''
  productSearch.value = ''
  dialogVisible.value = true
}

const searchCustomer = async () => {
  if (!customerSearch.value) return
  const res = await getCustomers({ name: customerSearch.value, mobile: customerSearch.value, size: 10 })
  customerList.value = res.data.records || res.data.list || res.data || []
}

const selectCustomer = (row) => {
  formData.customerId = row.id
  formData.customerName = row.name
  formData.receiverName = row.name
  formData.receiverPhone = row.mobile || row.phone
  formData.receiverAddress = row.address
  customerList.value = []
}

const searchProduct = async () => {
  if (!productSearch.value) return
  const res = await getProducts({ name: productSearch.value, size: 20 })
  const list = res.data.records || res.data.list || res.data || []
  productList.value = list.map(p => ({ ...p, buyQuantity: 1 }))
}

const addToCart = (row) => {
  const exist = formData.items.find(i => i.productId === row.id)
  if (exist) {
    exist.quantity += row.buyQuantity
  } else {
    formData.items.push({
      productId: row.id,
      productName: row.name,
      price: row.originalPrice,
      quantity: row.buyQuantity
    })
  }
  ElMessage.success('已添加')
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (!formData.customerId) {
    ElMessage.warning('请选择客户')
    return
  }
  if (!formData.items.length) {
    ElMessage.warning('请添加商品')
    return
  }
  await createOrder(formData)
  ElMessage.success('订单创建成功')
  dialogVisible.value = false
  fetchData()
}

const handleCancel = (row) => {
  currentRow.value = row
  cancelForm.reason = ''
  cancelDialogVisible.value = true
}

const submitCancel = async () => {
  await cancelOrder(currentRow.value.id, cancelForm)
  ElMessage.success('退订成功')
  cancelDialogVisible.value = false
  fetchData()
}

const handleExchange = (row) => {
  currentRow.value = row
  exchangeForm.reason = ''
  exchangeDialogVisible.value = true
}

const submitExchange = async () => {
  await exchangeOrder(currentRow.value.id, exchangeForm)
  ElMessage.success('换货申请成功')
  exchangeDialogVisible.value = false
  fetchData()
}

const handleReturn = (row) => {
  currentRow.value = row
  returnForm.reason = ''
  returnDialogVisible.value = true
}

const submitReturn = async () => {
  await returnOrder(currentRow.value.id, returnForm)
  ElMessage.success('退货申请成功')
  returnDialogVisible.value = false
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
.table-toolbar {
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>

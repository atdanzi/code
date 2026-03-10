<template>
  <div class="page-container">
    <el-form :model="queryParams" inline class="search-form">
      <el-form-item label="客户姓名">
        <el-input v-model="queryParams.name" placeholder="请输入客户姓名" clearable />
      </el-form-item>
      <el-form-item label="身份证号">
        <el-input v-model="queryParams.idCard" placeholder="请输入身份证号" clearable />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="queryParams.mobile" placeholder="请输入手机号" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <el-button type="primary" @click="handleAdd">新增客户</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="customerCode" label="客户编码" width="130" />
      <el-table-column prop="name" label="客户姓名" width="120" />
      <el-table-column prop="idCard" label="身份证号" width="180" />
      <el-table-column prop="phone" label="电话" width="130" />
      <el-table-column prop="mobile" label="手机" width="130" />
      <el-table-column prop="address" label="地址" show-overflow-tooltip />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="客户姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入客户姓名" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="formData.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="公司" prop="company">
          <el-input v-model="formData.company" placeholder="请输入公司名称" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="手机" prop="mobile">
          <el-input v-model="formData.mobile" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="formData.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="邮编" prop="zipCode">
          <el-input v-model="formData.zipCode" placeholder="请输入邮编" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCustomers, createCustomer, updateCustomer, deleteCustomer } from '@/api/customer'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const queryParams = reactive({
  page: 1,
  size: 10,
  name: '',
  idCard: '',
  mobile: ''
})

const formData = reactive({
  id: null,
  name: '',
  idCard: '',
  company: '',
  phone: '',
  mobile: '',
  address: '',
  zipCode: '',
  email: ''
})

const rules = {
  name: [{ required: true, message: '请输入客户姓名', trigger: 'blur' }],
  mobile: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getCustomers(queryParams)
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
  queryParams.name = ''
  queryParams.idCard = ''
  queryParams.mobile = ''
  handleSearch()
}

const resetForm = () => {
  Object.assign(formData, {
    id: null, name: '', idCard: '', company: '', phone: '',
    mobile: '', address: '', zipCode: '', email: ''
  })
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增客户'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  Object.assign(formData, row)
  dialogTitle.value = '编辑客户'
  dialogVisible.value = true
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除该客户?', '提示', { type: 'warning' }).then(async () => {
    await deleteCustomer(id)
    ElMessage.success('删除成功')
    fetchData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (formData.id) {
    await updateCustomer(formData.id, formData)
    ElMessage.success('更新成功')
  } else {
    await createCustomer(formData)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
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

<template>
  <div class="page-container">
    <el-form :model="queryParams" inline class="search-form">
      <el-form-item label="商品名称">
        <el-input v-model="queryParams.name" placeholder="请输入商品名称" clearable />
      </el-form-item>
      <el-form-item label="一级分类">
        <el-select v-model="queryParams.category1Id" placeholder="请选择" clearable @change="onCategory1Change">
          <el-option v-for="c in category1List" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="二级分类">
        <el-select v-model="queryParams.category2Id" placeholder="请选择" clearable>
          <el-option v-for="c in category2List" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <el-button type="primary" @click="handleAdd">新增商品</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="productCode" label="商品编码" width="130" />
      <el-table-column prop="name" label="商品名称" width="160" />
      <el-table-column prop="category1Name" label="一级分类" width="120" />
      <el-table-column prop="category2Name" label="二级分类" width="120" />
      <el-table-column prop="originalPrice" label="零售价" width="100" />
      <el-table-column prop="costPrice" label="成本价" width="100" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column prop="supplierName" label="供应商" width="150" />
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="650px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="一级分类" prop="category1Id">
          <el-select v-model="formData.category1Id" placeholder="请选择" style="width: 100%" @change="onFormCategory1Change">
            <el-option v-for="c in category1List" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="二级分类" prop="category2Id">
          <el-select v-model="formData.category2Id" placeholder="请选择" style="width: 100%">
            <el-option v-for="c in formCategory2List" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="零售价" prop="originalPrice">
          <el-input-number v-model="formData.originalPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="成本价" prop="costPrice">
          <el-input-number v-model="formData.costPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="formData.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="供应商" prop="supplierId">
          <el-select v-model="formData.supplierId" placeholder="请选择供应商" style="width: 100%">
            <el-option v-for="s in supplierList" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="规格" prop="spec">
          <el-input v-model="formData.spec" placeholder="请输入规格" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" rows="3" placeholder="请输入描述" />
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
import { getCategories1, getCategories2, getProducts, createProduct, updateProduct, deleteProduct } from '@/api/product'
import { getSuppliers } from '@/api/supplier'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const category1List = ref([])
const category2List = ref([])
const formCategory2List = ref([])
const supplierList = ref([])

const queryParams = reactive({
  page: 1,
  size: 10,
  name: '',
  category1Id: '',
  category2Id: ''
})

const formData = reactive({
  id: null,
  name: '',
  category1Id: '',
  category2Id: '',
  originalPrice: 0,
  costPrice: 0,
  unit: '',
  supplierId: '',
  spec: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  category1Id: [{ required: true, message: '请选择一级分类', trigger: 'change' }],
  category2Id: [{ required: true, message: '请选择二级分类', trigger: 'change' }],
  originalPrice: [{ required: true, message: '请输入零售价', trigger: 'blur' }]
}

const fetchCategory1 = async () => {
  const res = await getCategories1({ size: 1000 })
  category1List.value = res.data.records || res.data.list || res.data || []
}

const fetchCategory2 = async (category1Id) => {
  const res = await getCategories2({ category1Id, size: 1000 })
  return res.data.records || res.data.list || res.data || []
}

const fetchSuppliers = async () => {
  const res = await getSuppliers({ size: 1000 })
  supplierList.value = res.data.records || res.data.list || res.data || []
}

const onCategory1Change = async (val) => {
  queryParams.category2Id = ''
  category2List.value = val ? await fetchCategory2(val) : []
}

const onFormCategory1Change = async (val) => {
  formData.category2Id = ''
  formCategory2List.value = val ? await fetchCategory2(val) : []
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getProducts(queryParams)
    tableData.value = res.data.records || res.data.list || res.data || []
    total.value = res.data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  Object.assign(formData, {
    id: null, name: '', category1Id: '', category2Id: '',
    originalPrice: 0, costPrice: 0, unit: '', supplierId: '', spec: '', description: ''
  })
  formCategory2List.value = []
}

const handleSearch = () => {
  queryParams.page = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(queryParams, { page: 1, size: 10, name: '', category1Id: '', category2Id: '' })
  category2List.value = []
  fetchData()
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增商品'
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  resetForm()
  Object.assign(formData, row)
  if (formData.category1Id) {
    formCategory2List.value = await fetchCategory2(formData.category1Id)
  }
  dialogTitle.value = '编辑商品'
  dialogVisible.value = true
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除该商品?', '提示', { type: 'warning' }).then(async () => {
    await deleteProduct(id)
    ElMessage.success('删除成功')
    fetchData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (formData.id) {
    await updateProduct(formData.id, formData)
    ElMessage.success('更新成功')
  } else {
    await createProduct(formData)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  fetchData()
}

onMounted(() => {
  fetchCategory1()
  fetchSuppliers()
  fetchData()
})
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

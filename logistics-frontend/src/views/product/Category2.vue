<template>
  <div class="page-container">
    <el-form :model="queryParams" inline class="search-form">
      <el-form-item label="一级分类">
        <el-select v-model="queryParams.category1Id" placeholder="请选择一级分类" clearable>
          <el-option v-for="c in category1List" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <el-button type="primary" @click="handleAdd">新增二级分类</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" width="200" />
      <el-table-column prop="category1Name" label="所属一级分类" width="200" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="一级分类" prop="category1Id">
          <el-select v-model="formData.category1Id" placeholder="请选择一级分类" style="width: 100%">
            <el-option v-for="c in category1List" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
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
import { getCategories1, getCategories2, createCategory2, updateCategory2, deleteCategory2 } from '@/api/product'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const category1List = ref([])

const queryParams = reactive({ page: 1, size: 10, category1Id: '' })

const formData = reactive({ id: null, name: '', description: '', category1Id: '' })

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  category1Id: [{ required: true, message: '请选择一级分类', trigger: 'change' }]
}

const fetchCategory1 = async () => {
  try {
    const res = await getCategories1({ size: 1000 })
    category1List.value = res.data.records || res.data.list || res.data || []
  } catch (e) {
    console.error(e)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getCategories2(queryParams)
    tableData.value = res.data.records || res.data.list || res.data || []
    total.value = res.data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  Object.assign(formData, { id: null, name: '', description: '', category1Id: '' })
}

const handleSearch = () => {
  queryParams.page = 1
  fetchData()
}

const handleReset = () => {
  queryParams.category1Id = ''
  handleSearch()
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增二级分类'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  Object.assign(formData, row)
  dialogTitle.value = '编辑二级分类'
  dialogVisible.value = true
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除该分类?', '提示', { type: 'warning' }).then(async () => {
    await deleteCategory2(id)
    ElMessage.success('删除成功')
    fetchData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (formData.id) {
    await updateCategory2(formData.id, formData)
    ElMessage.success('更新成功')
  } else {
    await createCategory2(formData)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  fetchData()
}

onMounted(() => {
  fetchCategory1()
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

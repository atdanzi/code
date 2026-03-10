<template>
  <div class="page-container">
    <div class="table-toolbar">
      <el-button type="primary" @click="handleAdd">新增一级分类</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" width="200" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="180" />
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
import { getCategories1, createCategory1, updateCategory1, deleteCategory1 } from '@/api/product'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const queryParams = reactive({ page: 1, size: 10 })

const formData = reactive({ id: null, name: '', description: '' })

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getCategories1(queryParams)
    tableData.value = res.data.records || res.data.list || res.data || []
    total.value = res.data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  Object.assign(formData, { id: null, name: '', description: '' })
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增一级分类'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  Object.assign(formData, row)
  dialogTitle.value = '编辑一级分类'
  dialogVisible.value = true
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除该分类?', '提示', { type: 'warning' }).then(async () => {
    await deleteCategory1(id)
    ElMessage.success('删除成功')
    fetchData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (formData.id) {
    await updateCategory1(formData.id, formData)
    ElMessage.success('更新成功')
  } else {
    await createCategory1(formData)
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
.table-toolbar {
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>

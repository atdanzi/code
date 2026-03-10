<template>
  <div class="page-container">
    <div class="table-toolbar">
      <el-button type="primary" @click="handleAdd">新增权限</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" border row-key="id" :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
      <el-table-column prop="permName" label="权限名称" width="200" />
      <el-table-column prop="permCode" label="权限编码" width="200" />
      <el-table-column prop="path" label="路径" width="200" />
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.type === '菜单' ? '' : row.type === '目录' ? 'warning' : 'info'">
            {{ row.type }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="权限名称" prop="permName">
          <el-input v-model="formData.permName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="permCode">
          <el-input v-model="formData.permCode" placeholder="请输入权限编码" />
        </el-form-item>
        <el-form-item label="上级权限" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="treeSelectData"
            :props="{ label: 'permName', value: 'id', children: 'children' }"
            placeholder="请选择上级权限（留空为顶级）"
            clearable
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="路径" prop="path">
          <el-input v-model="formData.path" placeholder="请输入路径" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="formData.icon" placeholder="请输入图标" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="目录" value="目录" />
            <el-option label="菜单" value="菜单" />
            <el-option label="按钮" value="按钮" />
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
import { getPermissions, createPermission, updatePermission, deletePermission } from '@/api/system'

const tableData = ref([])
const treeSelectData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const formData = reactive({
  id: null,
  permName: '',
  permCode: '',
  parentId: null,
  path: '',
  icon: '',
  sortOrder: 0,
  type: '菜单'
})

const rules = {
  permName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  permCode: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

const buildTree = (list, parentId = null) => {
  return list
    .filter(item => item.parentId === parentId || (!parentId && !item.parentId))
    .map(item => ({
      ...item,
      children: buildTree(list, item.id)
    }))
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getPermissions({ size: 1000 })
    const list = res.data.records || res.data.list || res.data || []
    tableData.value = buildTree(list)
    treeSelectData.value = [{ id: null, permName: '顶级权限', children: tableData.value }]
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  Object.assign(formData, {
    id: null, permName: '', permCode: '', parentId: null,
    path: '', icon: '', sortOrder: 0, type: '菜单'
  })
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增权限'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  Object.assign(formData, {
    id: row.id,
    permName: row.permName,
    permCode: row.permCode,
    parentId: row.parentId,
    path: row.path,
    icon: row.icon,
    sortOrder: row.sortOrder,
    type: row.type
  })
  dialogTitle.value = '编辑权限'
  dialogVisible.value = true
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除该权限? 子权限也将被删除。', '提示', { type: 'warning' }).then(async () => {
    await deletePermission(id)
    ElMessage.success('删除成功')
    fetchData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (formData.id) {
    await updatePermission(formData.id, formData)
    ElMessage.success('更新成功')
  } else {
    await createPermission(formData)
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
</style>

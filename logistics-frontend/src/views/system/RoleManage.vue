<template>
  <div class="page-container">
    <div class="table-toolbar">
      <el-button type="primary" @click="handleAdd">新增角色</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="roleName" label="角色名称" width="150" />
      <el-table-column prop="roleCode" label="角色编码" width="150" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="isSystem" label="系统角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isSystem ? 'danger' : 'info'">{{ row.isSystem ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row.id)" :disabled="row.isSystem">删除</el-button>
          <el-button type="warning" link @click="handlePermission(row)">配置权限</el-button>
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

    <!-- 新增/编辑角色对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="formData.roleCode" placeholder="请输入角色编码" />
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

    <!-- 配置权限对话框 -->
    <el-dialog v-model="permDialogVisible" title="配置权限" width="500px" destroy-on-close>
      <el-tree
        ref="treeRef"
        :data="permTree"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedKeys"
        :props="{ label: 'permName', children: 'children' }"
      />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPermissions">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getRoles, createRole, updateRole, deleteRole,
  getPermissions, getRolePermissions, assignPermissions
} from '@/api/system'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const permDialogVisible = ref(false)
const treeRef = ref(null)
const permTree = ref([])
const checkedKeys = ref([])
const currentRoleId = ref(null)

const queryParams = reactive({ page: 1, size: 10 })

const formData = reactive({ id: null, roleName: '', roleCode: '', description: '' })

const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getRoles(queryParams)
    tableData.value = res.data.records || res.data.list || res.data || []
    total.value = res.data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchPermTree = async () => {
  try {
    const res = await getPermissions({ size: 1000 })
    const list = res.data.records || res.data.list || res.data || []
    permTree.value = buildTree(list)
  } catch (e) {
    console.error(e)
  }
}

const buildTree = (list, parentId = null) => {
  return list
    .filter(item => item.parentId === parentId || (!parentId && !item.parentId))
    .map(item => ({
      ...item,
      children: buildTree(list, item.id)
    }))
}

const resetForm = () => {
  Object.assign(formData, { id: null, roleName: '', roleCode: '', description: '' })
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增角色'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  Object.assign(formData, row)
  dialogTitle.value = '编辑角色'
  dialogVisible.value = true
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除该角色?', '提示', { type: 'warning' }).then(async () => {
    await deleteRole(id)
    ElMessage.success('删除成功')
    fetchData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (formData.id) {
    await updateRole(formData.id, formData)
    ElMessage.success('更新成功')
  } else {
    await createRole(formData)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  fetchData()
}

const handlePermission = async (row) => {
  currentRoleId.value = row.id
  try {
    const res = await getRolePermissions(row.id)
    const perms = res.data || []
    checkedKeys.value = Array.isArray(perms) ? perms.map(p => p.id || p) : []
  } catch (e) {
    checkedKeys.value = []
  }
  permDialogVisible.value = true
}

const submitPermissions = async () => {
  const checked = treeRef.value.getCheckedKeys()
  const halfChecked = treeRef.value.getHalfCheckedKeys()
  await assignPermissions(currentRoleId.value, { permissionIds: [...checked, ...halfChecked] })
  ElMessage.success('权限配置成功')
  permDialogVisible.value = false
}

onMounted(() => {
  fetchData()
  fetchPermTree()
})
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

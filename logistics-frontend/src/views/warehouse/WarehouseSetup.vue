<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-button type="primary" class="filter-item" @click="handleCreate">新增库房</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border fit highlight-current-row style="width: 100%">
        <el-table-column prop="code" label="库房编码" />
        <el-table-column prop="name" label="库房名称" />
        <el-table-column prop="level" label="级别">
          <template #default="scope">
            {{ scope.row.level === 'CENTRAL' ? '中心库房' : '分站库房' }}
          </template>
        </el-table-column>
        <el-table-column prop="stationName" label="所属分站" />
        <el-table-column prop="manager" label="负责人" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" style="color: red" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="库房名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" />
        </el-form-item>
        <el-form-item label="负责人" prop="manager">
          <el-input v-model="form.manager" />
        </el-form-item>
        <el-form-item label="级别" prop="level">
          <el-select v-model="form.level" placeholder="请选择级别">
            <el-option label="中心库房" value="CENTRAL" />
            <el-option label="分站库房" value="STATION" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属分站" prop="stationId" v-if="form.level === 'STATION'">
          <el-select v-model="form.stationId" placeholder="请选择分站">
            <!-- 模拟分站数据 -->
            <el-option label="分站A" :value="1" />
            <el-option label="分站B" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getWarehouses, createWarehouse, updateWarehouse, deleteWarehouse } from '@/api/warehouse'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const list = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = reactive({
  id: null,
  name: '',
  address: '',
  manager: '',
  level: 'STATION',
  stationId: null
})

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  level: [{ required: true, message: '请选择级别', trigger: 'change' }]
}

onMounted(() => {
  getList()
})

const getList = () => {
  loading.value = true
  getWarehouses().then(res => {
    list.value = res.data || []
  }).finally(() => {
    loading.value = false
  })
}

const handleCreate = () => {
  dialogTitle.value = '新增库房'
  form.id = null
  form.name = ''
  form.address = ''
  form.manager = ''
  form.level = 'STATION'
  form.stationId = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑库房'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该库房吗？', '警告', {
    type: 'warning'
  }).then(() => {
    deleteWarehouse(row.id).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  })
}

const submitForm = () => {
  formRef.value.validate(valid => {
    if (valid) {
      const api = form.id ? updateWarehouse(form.id, form) : createWarehouse(form)
      api.then(() => {
        ElMessage.success('操作成功')
        dialogVisible.value = false
        getList()
      })
    }
  })
}
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
}
</style>

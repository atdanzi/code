<template>
  <div class="page-container">
    <el-form :model="queryParams" inline class="search-form">
      <el-form-item label="用户名">
        <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable />
      </el-form-item>
      <el-form-item label="操作">
        <el-input v-model="queryParams.operation" placeholder="请输入操作" clearable />
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
      <el-button type="warning" @click="backupDialogVisible = true">备份日志</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="username" label="用户名" width="130" />
      <el-table-column prop="operation" label="操作" width="200" />
      <el-table-column prop="method" label="方法" show-overflow-tooltip />
      <el-table-column prop="ip" label="IP地址" width="150" />
      <el-table-column prop="operateTime" label="操作时间" width="180" />
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

    <el-dialog v-model="backupDialogVisible" title="备份日志" width="500px" destroy-on-close>
      <el-form :model="backupForm" label-width="100px">
        <el-form-item label="备份截止日期">
          <el-date-picker v-model="backupForm.endDate" type="date" placeholder="选择截止日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="backupDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBackup">确定备份</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOperationLogs, backupLogs } from '@/api/log'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const backupDialogVisible = ref(false)

const queryParams = reactive({
  page: 1,
  size: 10,
  username: '',
  operation: '',
  startDate: '',
  endDate: ''
})

const backupForm = reactive({ endDate: '' })

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getOperationLogs(queryParams)
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
    page: 1, size: 10, username: '', operation: '', startDate: '', endDate: ''
  })
  fetchData()
}

const handleBackup = async () => {
  if (!backupForm.endDate) {
    ElMessage.warning('请选择备份截止日期')
    return
  }
  await backupLogs(backupForm)
  ElMessage.success('备份成功')
  backupDialogVisible.value = false
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

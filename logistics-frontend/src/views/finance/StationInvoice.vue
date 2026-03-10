<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-input v-model="queryParams.invoiceNo" placeholder="发票号" style="width: 200px;" class="filter-item" @keyup.enter="handleQuery" />
        <el-button type="primary" class="filter-item" @click="handleQuery">查询</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border fit highlight-current-row style="width: 100%">
        <el-table-column prop="invoiceNo" label="发票号" />
        <el-table-column prop="amount" label="金额" />
        <el-table-column prop="status" label="状态" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="text" @click="handleAssign(scope.row)">分发给客户</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog title="分发发票" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="关联任务单">
          <el-select v-model="form.taskId" placeholder="请选择任务单" filterable>
            <!-- 模拟任务单 -->
            <el-option v-for="t in tasks" :key="t.id" :label="t.taskCode" :value="t.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAssign">确认分发</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getInvoices, assignInvoiceCustomer } from '@/api/finance'
import { getTasks } from '@/api/task'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const list = ref([])
const queryParams = reactive({
  invoiceNo: '',
  status: 'assigned_station' // 假设状态为已分发到分站
})

const dialogVisible = ref(false)
const form = reactive({
  invoiceId: null,
  taskId: null
})
const tasks = ref([])

onMounted(() => {
  handleQuery()
  // 获取可分发发票的任务列表
  getTasks({ status: 'completed' }).then(res => {
      tasks.value = res.data || []
  })
})

const handleQuery = () => {
  loading.value = true
  getInvoices(queryParams).then(res => {
    list.value = res.data || []
  }).finally(() => {
    loading.value = false
  })
}

const handleAssign = (row) => {
  form.invoiceId = row.id
  dialogVisible.value = true
}

const submitAssign = () => {
  if (!form.taskId) {
      ElMessage.warning('请选择任务单')
      return
  }
  assignInvoiceCustomer(form).then(() => {
    ElMessage.success('分发成功')
    dialogVisible.value = false
    handleQuery()
  })
}
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
}
.filter-item {
  margin-right: 10px;
}
</style>

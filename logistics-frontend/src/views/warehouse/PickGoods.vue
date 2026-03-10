<template>
  <div class="app-container">
    <el-card>
      <div class="filter-container">
        <el-input v-model="queryParams.taskCode" placeholder="任务单号" style="width: 200px;" class="filter-item" @keyup.enter="handleQuery" />
        <el-button type="primary" class="filter-item" @click="handleQuery">查询</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border fit highlight-current-row style="width: 100%">
        <el-table-column prop="taskCode" label="任务单号" />
        <el-table-column prop="orderCode" label="订单号" />
        <el-table-column prop="status" label="状态" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="text" @click="handlePick(scope.row)">领货</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog title="领货确认" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="领货人">
          <el-select v-model="form.pickerId" placeholder="请选择领货人">
            <el-option v-for="item in pickers" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="领货日期">
          <el-date-picker v-model="form.pickDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPick">确认领货</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { pickGoods } from '@/api/warehouseOp'
import { getTasks } from '@/api/task' // 假设任务列表可以从这里获取
// import { getPickers } from '@/api/user' // 假设有获取领货人的API

const loading = ref(false)
const list = ref([])
const queryParams = reactive({
  taskCode: '',
  status: 'pending_pick'
})

const dialogVisible = ref(false)
const form = reactive({
  taskId: null,
  pickerId: null,
  pickDate: ''
})
const pickers = ref([
    { id: 1, name: '配送员A' },
    { id: 2, name: '配送员B' }
]) // 模拟数据

onMounted(() => {
  handleQuery()
})

const handleQuery = () => {
  loading.value = true
  getTasks(queryParams).then(res => {
    list.value = res.data || []
  }).finally(() => {
    loading.value = false
  })
}

const handlePick = (row) => {
  form.taskId = row.id
  form.pickDate = new Date().toISOString().split('T')[0]
  dialogVisible.value = true
}

const submitPick = () => {
  if (!form.pickerId) {
      ElMessage.warning('请选择领货人')
      return
  }
  pickGoods(form).then(() => {
    ElMessage.success('领货成功')
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

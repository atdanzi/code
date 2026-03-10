<template>
  <div class="page-container">
    <el-form :model="queryParams" inline class="search-form">
      <el-form-item label="开始日期">
        <el-date-picker v-model="queryParams.startDate" type="date" placeholder="选择开始日期" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item label="结束日期">
        <el-date-picker v-model="queryParams.endDate" type="date" placeholder="选择结束日期" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item label="任务类型">
        <el-select v-model="queryParams.taskType" placeholder="请选择" clearable>
          <el-option label="配送" value="配送" />
          <el-option label="退货" value="退货" />
          <el-option label="换货" value="换货" />
        </el-select>
      </el-form-item>
      <el-form-item label="任务状态">
        <el-select v-model="queryParams.taskStatus" placeholder="请选择" clearable>
          <el-option label="配送中" value="配送中" />
          <el-option label="已完成" value="已完成" />
          <el-option label="已失败" value="已失败" />
        </el-select>
      </el-form-item>
      <el-form-item label="配送员">
        <el-input v-model="queryParams.deliveryPerson" placeholder="请输入配送员" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="taskCode" label="任务编号" width="160" />
      <el-table-column prop="orderCode" label="订单编号" width="160" />
      <el-table-column prop="deliveryPersonName" label="配送员" width="120" />
      <el-table-column prop="receiverName" label="收货人" width="120" />
      <el-table-column prop="taskType" label="任务类型" width="100" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleReceipt(row)">录入回执</el-button>
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

    <el-dialog v-model="dialogVisible" title="录入回执" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="完成状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择" style="width: 100%">
            <el-option label="完成" value="完成" />
            <el-option label="部分完成" value="部分完成" />
            <el-option label="失败" value="失败" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户满意度" prop="satisfaction">
          <el-select v-model="formData.satisfaction" placeholder="请选择" style="width: 100%">
            <el-option label="非常满意" value="非常满意" />
            <el-option label="满意" value="满意" />
            <el-option label="一般" value="一般" />
            <el-option label="不满意" value="不满意" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" rows="3" placeholder="请输入备注" />
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
import { ElMessage } from 'element-plus'
import { getTasks, enterReceipt } from '@/api/task'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const currentTaskId = ref(null)

const queryParams = reactive({
  page: 1,
  size: 10,
  startDate: '',
  endDate: '',
  taskType: '',
  taskStatus: '',
  deliveryPerson: ''
})

const formData = reactive({
  status: '',
  satisfaction: '',
  remark: ''
})

const rules = {
  status: [{ required: true, message: '请选择完成状态', trigger: 'change' }],
  satisfaction: [{ required: true, message: '请选择客户满意度', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getTasks(queryParams)
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
    page: 1, size: 10, startDate: '', endDate: '',
    taskType: '', taskStatus: '', deliveryPerson: ''
  })
  fetchData()
}

const handleReceipt = (row) => {
  currentTaskId.value = row.id
  Object.assign(formData, { status: '', satisfaction: '', remark: '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  await enterReceipt(currentTaskId.value, formData)
  ElMessage.success('回执录入成功')
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
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>

<template>
  <div class="app-container">
    <el-card>
      <el-tabs v-model="activeTab">
        <!-- Tab 1: 发票登记 -->
        <el-tab-pane label="发票登记" name="register">
          <el-form :model="registerForm" label-width="100px" style="width: 400px; margin-top: 20px;">
            <el-form-item label="开始票号" required>
              <el-input v-model="registerForm.startNo" />
            </el-form-item>
            <el-form-item label="结束票号" required>
              <el-input v-model="registerForm.endNo" />
            </el-form-item>
            <el-form-item label="单张金额" required>
              <el-input-number v-model="registerForm.amount" :min="0" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleRegister">登记</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- Tab 2: 发票领用 -->
        <el-tab-pane label="发票领用" name="assign">
          <div class="filter-container">
            <el-input v-model="assignQuery.invoiceNo" placeholder="发票号" style="width: 200px;" class="filter-item" />
            <el-button type="primary" class="filter-item" @click="handleAssignQuery">查询</el-button>
          </div>
          <el-table :data="assignList" border>
            <el-table-column prop="invoiceNo" label="发票号" />
            <el-table-column prop="amount" label="金额" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="text" @click="openAssignDialog(scope.row)">领用</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- Tab 3: 作废/查询 -->
        <el-tab-pane label="查询/作废" name="query">
          <div class="filter-container">
            <el-input v-model="queryForm.invoiceNo" placeholder="发票号" style="width: 200px;" class="filter-item" />
            <el-button type="primary" class="filter-item" @click="handleQuery">查询</el-button>
          </div>
          <el-table :data="queryList" border>
            <el-table-column prop="invoiceNo" label="发票号" />
            <el-table-column prop="status" label="状态" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="text" style="color: red" @click="handleVoid(scope.row)" v-if="scope.row.status !== 'void'">作废</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 领用弹窗 -->
    <el-dialog title="发票领用" v-model="assignDialogVisible" width="400px">
      <el-form>
        <el-form-item label="领用分站">
          <el-select v-model="assignForm.stationId" placeholder="选择分站">
             <el-option label="分站A" :value="1" />
             <el-option label="分站B" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="assignDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAssign">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { registerInvoice, assignInvoiceStation, voidInvoice, getInvoices } from '@/api/finance'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('register')

// 1. 登记
const registerForm = reactive({ startNo: '', endNo: '', amount: 0 })
const handleRegister = () => {
    registerInvoice(registerForm).then(() => {
        ElMessage.success('登记成功')
    })
}

// 2. 领用
const assignQuery = reactive({ invoiceNo: '', status: 'registered' })
const assignList = ref([])
const assignDialogVisible = ref(false)
const assignForm = reactive({ invoiceId: null, stationId: null })

const handleAssignQuery = () => {
    getInvoices(assignQuery).then(res => {
        assignList.value = res.data || []
    })
}
const openAssignDialog = (row) => {
    assignForm.invoiceId = row.id
    assignDialogVisible.value = true
}
const submitAssign = () => {
    assignInvoiceStation(assignForm).then(() => {
        ElMessage.success('领用成功')
        assignDialogVisible.value = false
        handleAssignQuery()
    })
}

// 3. 查询/作废
const queryForm = reactive({ invoiceNo: '' })
const queryList = ref([])
const handleQuery = () => {
    getInvoices(queryForm).then(res => {
        queryList.value = res.data || []
    })
}
const handleVoid = (row) => {
    ElMessageBox.confirm('确认作废该发票吗？', '警告', { type: 'warning' }).then(() => {
        voidInvoice({ id: row.id }).then(() => {
            ElMessage.success('作废成功')
            handleQuery()
        })
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

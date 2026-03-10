<template>
  <div class="app-container">
    <el-card>
      <el-tabs v-model="activeTab">
        <!-- Tab 1: 退货登记 -->
        <el-tab-pane label="退货登记" name="register">
          <div class="filter-container">
            <el-input v-model="registerQuery.taskCode" placeholder="任务单号" style="width: 200px;" class="filter-item" />
            <el-button type="primary" class="filter-item" @click="handleRegisterQuery">查询</el-button>
          </div>
          <el-table :data="registerList" border>
            <el-table-column prop="taskCode" label="任务单号" />
            <el-table-column prop="customerName" label="客户名称" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="text" @click="handleRegister(scope.row)">登记</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- Tab 2: 分站退货出库 -->
        <el-tab-pane label="分站退货出库" name="stationOut">
          <div class="filter-container">
            <el-date-picker v-model="stationOutQuery.date" type="date" placeholder="日期" class="filter-item" value-format="YYYY-MM-DD" />
            <el-input v-model="stationOutQuery.station" placeholder="分站名称" style="width: 200px;" class="filter-item" />
            <el-button type="primary" class="filter-item" @click="handleStationOutQuery">查询</el-button>
            <el-button type="success" class="filter-item" @click="handleStationReturnOut" :disabled="!stationOutSelection.length">退货到中心</el-button>
          </div>
          <el-table :data="stationOutList" border @selection-change="handleStationOutSelection">
            <el-table-column type="selection" width="55" />
            <el-table-column prop="returnCode" label="退货单号" />
            <el-table-column prop="productName" label="商品" />
            <el-table-column prop="quantity" label="数量" />
            <el-table-column prop="reason" label="退货原因" />
          </el-table>
        </el-tab-pane>

        <!-- Tab 3: 中心退货入库 -->
        <el-tab-pane label="中心退货入库" name="centerIn">
          <div class="filter-container">
            <el-input v-model="centerInQuery.returnCode" placeholder="退货单号" style="width: 200px;" class="filter-item" />
            <el-button type="primary" class="filter-item" @click="handleCenterInQuery">查询</el-button>
          </div>
          <el-table :data="centerInList" border>
            <el-table-column prop="returnCode" label="退货单号" />
            <el-table-column prop="fromStation" label="来源分站" />
            <el-table-column prop="status" label="状态" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="text" @click="handleCenterIn(scope.row)">入库</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 退货登记弹窗 -->
    <el-dialog title="退货登记" v-model="registerDialogVisible" width="500px">
      <el-form :model="registerForm" label-width="100px">
        <el-form-item label="退货数量">
           <el-input-number v-model="registerForm.quantity" :min="1" />
        </el-form-item>
        <el-form-item label="退货原因">
           <el-input v-model="registerForm.reason" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="registerDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRegister">确认登记</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { returnRegister, stationReturnOut, centerReturnIn } from '@/api/warehouseOp'
import { getTasks } from '@/api/task'
import { ElMessage } from 'element-plus'

const activeTab = ref('register')

// 1. 退货登记
const registerQuery = reactive({ taskCode: '' })
const registerList = ref([])
const registerDialogVisible = ref(false)
const registerForm = reactive({ taskId: null, quantity: 1, reason: '' })

const handleRegisterQuery = () => {
  // 模拟查询可退货任务
  getTasks({ ...registerQuery, status: 'completed' }).then(res => {
      registerList.value = res.data || []
  })
}
const handleRegister = (row) => {
    registerForm.taskId = row.id
    registerDialogVisible.value = true
}
const submitRegister = () => {
    returnRegister(registerForm).then(() => {
        ElMessage.success('登记成功')
        registerDialogVisible.value = false
        handleRegisterQuery()
    })
}

// 2. 分站退货出库
const stationOutQuery = reactive({ date: '', station: '' })
const stationOutList = ref([])
const stationOutSelection = ref([])

const handleStationOutQuery = () => {
    // 模拟查询
    stationOutList.value = [] // 需调用API
}
const handleStationOutSelection = (val) => {
    stationOutSelection.value = val
}
const handleStationReturnOut = () => {
    const ids = stationOutSelection.value.map(r => r.id)
    stationReturnOut({ ids }).then(() => {
        ElMessage.success('已生成退货出库单')
        handleStationOutQuery()
    })
}

// 3. 中心退货入库
const centerInQuery = reactive({ returnCode: '' })
const centerInList = ref([])

const handleCenterInQuery = () => {
    // 模拟查询
    centerInList.value = [] // 需调用API
}
const handleCenterIn = (row) => {
    centerReturnIn({ id: row.id }).then(() => {
        ElMessage.success('入库成功')
        handleCenterInQuery()
    })
}

onMounted(() => {
    handleRegisterQuery()
})
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
}
.filter-item {
  margin-right: 10px;
}
</style>

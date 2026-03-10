<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '210px'" class="layout-aside">
      <div class="logo-area">
        <el-icon :size="24" color="#409EFF"><Box /></el-icon>
        <span v-show="!isCollapse" class="logo-text">物流管理系统</span>
      </div>
      <el-scrollbar>
        <el-menu
          :default-active="$route.path"
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <el-sub-menu index="customer">
            <template #title>
              <el-icon><User /></el-icon>
              <span>客户管理</span>
            </template>
            <el-menu-item index="/customer/manage">客户信息管理</el-menu-item>
            <el-menu-item index="/customer/workload">工作量查询</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="order">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>订单管理</span>
            </template>
            <el-menu-item index="/order/manage">订单管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="dispatch">
            <template #title>
              <el-icon><Van /></el-icon>
              <span>调度管理</span>
            </template>
            <el-menu-item index="/dispatch/manual">手工调度</el-menu-item>
            <el-menu-item index="/dispatch/auto">自动调度</el-menu-item>
            <el-menu-item index="/dispatch/shortage">缺货订单</el-menu-item>
            <el-menu-item index="/dispatch/task">任务单查询</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="station">
            <template #title>
              <el-icon><OfficeBuilding /></el-icon>
              <span>分站管理</span>
            </template>
            <el-menu-item index="/station/task">任务查询</el-menu-item>
            <el-menu-item index="/station/assign">任务分配</el-menu-item>
            <el-menu-item index="/station/receipt">回执录入</el-menu-item>
            <el-menu-item index="/station/payment">缴款查询</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="warehouse-op">
            <template #title>
              <el-icon><Box /></el-icon>
              <span>货物出入库</span>
            </template>
            <el-menu-item index="/warehouse-op/purchase-in">购货入库</el-menu-item>
            <el-menu-item index="/warehouse-op/transfer-out">调拨出库</el-menu-item>
            <el-menu-item index="/warehouse-op/transfer-in">调拨入库</el-menu-item>
            <el-menu-item index="/warehouse-op/pick">领货</el-menu-item>
            <el-menu-item index="/warehouse-op/return">退货管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="product">
            <template #title>
              <el-icon><Goods /></el-icon>
              <span>商品管理</span>
            </template>
            <el-menu-item index="/product/category1">一级分类</el-menu-item>
            <el-menu-item index="/product/category2">二级分类</el-menu-item>
            <el-menu-item index="/product/manage">商品管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="supplier">
            <template #title>
              <el-icon><Shop /></el-icon>
              <span>供应商管理</span>
            </template>
            <el-menu-item index="/supplier/manage">供应商管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="purchase">
            <template #title>
              <el-icon><ShoppingCart /></el-icon>
              <span>进货管理</span>
            </template>
            <el-menu-item index="/purchase/manage">进货管理</el-menu-item>
            <el-menu-item index="/purchase/return">退货安排</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="warehouse">
            <template #title>
              <el-icon><House /></el-icon>
              <span>库房管理</span>
            </template>
            <el-menu-item index="/warehouse/setup">库房设置</el-menu-item>
            <el-menu-item index="/warehouse/reserve">储备设置</el-menu-item>
            <el-menu-item index="/warehouse/stock">库存查询</el-menu-item>
            <el-menu-item index="/warehouse/flow">出入库查询</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="finance">
            <template #title>
              <el-icon><Money /></el-icon>
              <span>财务管理</span>
            </template>
            <el-menu-item index="/finance/supplier-settle">供应商结算</el-menu-item>
            <el-menu-item index="/finance/station-settle">分站结算</el-menu-item>
            <el-menu-item index="/finance/invoice">发票管理</el-menu-item>
            <el-menu-item index="/finance/station-invoice">分站发票</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="statistics">
            <template #title>
              <el-icon><DataAnalysis /></el-icon>
              <span>业务统计</span>
            </template>
            <el-menu-item index="/statistics/rank">订购排行</el-menu-item>
            <el-menu-item index="/statistics/station">分站配送</el-menu-item>
            <el-menu-item index="/statistics/satisfaction">满意度分析</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/system/permission">权限管理</el-menu-item>
            <el-menu-item index="/system/role">角色管理</el-menu-item>
            <el-menu-item index="/system/user">用户管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="log">
            <template #title>
              <el-icon><Notebook /></el-icon>
              <span>日志管理</span>
            </template>
            <el-menu-item index="/log/operation">操作日志</el-menu-item>
            <el-menu-item index="/log/login">登录日志</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon
            class="collapse-btn"
            :size="20"
            @click="isCollapse = !isCollapse"
          >
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              <el-icon><UserFilled /></el-icon>
              <span class="username">{{ userStore.userInfo.username || '管理员' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store'

const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

function handleCommand(command) {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.layout-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.logo-area {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background-color: #263445;
}

.logo-text {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
}

.layout-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  cursor: pointer;
  color: #333;
}

.collapse-btn:hover {
  color: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #333;
  font-size: 14px;
}

.user-dropdown:hover {
  color: #409eff;
}

.username {
  margin: 0 4px;
}

.layout-main {
  background-color: #f0f2f5;
  padding: 20px;
}

.el-menu {
  border-right: none;
}
</style>

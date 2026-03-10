-- ============================================================
-- 物流管理系统 数据库初始化脚本
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS logistics_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE logistics_db;

-- ============================================================
-- 一、系统管理相关表
-- ============================================================

-- 1. 系统用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) DEFAULT NULL,
    email VARCHAR(100) DEFAULT NULL,
    station_id BIGINT DEFAULT NULL COMMENT '所属分站ID(分站人员)',
    warehouse_id BIGINT DEFAULT NULL COMMENT '所属库房ID(库房人员)',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '系统用户表';

-- 2. 角色表
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) DEFAULT NULL,
    is_system TINYINT NOT NULL DEFAULT 0 COMMENT '是否系统内置角色: 1是 0否',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT '角色表';

-- 3. 权限选项表
CREATE TABLE sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    perm_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    perm_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    path VARCHAR(200) DEFAULT NULL COMMENT '前端路由路径',
    icon VARCHAR(50) DEFAULT NULL COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '类型: 1菜单 2按钮',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT '权限选项表';

-- 4. 用户-角色关联表
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    UNIQUE KEY uk_user_role (user_id, role_id)
) COMMENT '用户角色关联表';

-- 5. 角色-权限关联表
CREATE TABLE sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    UNIQUE KEY uk_role_perm (role_id, permission_id)
) COMMENT '角色权限关联表';

-- ============================================================
-- 二、基础数据表
-- ============================================================

-- 6. 客户表
CREATE TABLE customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_code VARCHAR(32) NOT NULL UNIQUE COMMENT '客户编号',
    name VARCHAR(50) NOT NULL COMMENT '客户姓名',
    id_card VARCHAR(18) NOT NULL COMMENT '身份证号码',
    company VARCHAR(100) DEFAULT NULL COMMENT '工作单位',
    phone VARCHAR(20) DEFAULT NULL COMMENT '座机',
    mobile VARCHAR(20) DEFAULT NULL COMMENT '移动电话',
    address VARCHAR(200) NOT NULL COMMENT '联系地址',
    zip_code VARCHAR(10) DEFAULT NULL COMMENT '邮编',
    email VARCHAR(100) DEFAULT NULL,
    create_by BIGINT DEFAULT NULL COMMENT '创建人(客服ID)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '客户表';

-- 7. 供应商表
CREATE TABLE supplier (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    supplier_code VARCHAR(32) NOT NULL UNIQUE COMMENT '供应商编号',
    name VARCHAR(100) NOT NULL COMMENT '供应商名称',
    address VARCHAR(200) NOT NULL,
    contact_person VARCHAR(50) NOT NULL COMMENT '联系人',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    bank VARCHAR(100) NOT NULL COMMENT '开户行',
    bank_account VARCHAR(50) NOT NULL COMMENT '银行账号',
    fax VARCHAR(20) DEFAULT NULL COMMENT '传真',
    zip_code VARCHAR(10) DEFAULT NULL,
    legal_person VARCHAR(50) DEFAULT NULL COMMENT '法人',
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '供应商表';

-- 8. 商品一级分类表
CREATE TABLE product_category1 (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '分类名称',
    description VARCHAR(200) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT '商品一级分类表';

-- 9. 商品二级分类表
CREATE TABLE product_category2 (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    description VARCHAR(200) DEFAULT NULL,
    category1_id BIGINT NOT NULL COMMENT '所属一级分类ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_name_cat1 (name, category1_id)
) COMMENT '商品二级分类表';

-- 10. 商品表
CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_code VARCHAR(32) NOT NULL UNIQUE COMMENT '商品代码',
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    category1_id BIGINT NOT NULL COMMENT '一级分类ID',
    category2_id BIGINT NOT NULL COMMENT '二级分类ID',
    unit VARCHAR(20) NOT NULL COMMENT '计量单位',
    original_price DECIMAL(10,2) NOT NULL COMMENT '原价/售价',
    discount DECIMAL(3,2) NOT NULL DEFAULT 1.00 COMMENT '折扣',
    cost_price DECIMAL(10,2) NOT NULL COMMENT '成本价',
    model VARCHAR(50) DEFAULT NULL COMMENT '型号',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    manufacturer VARCHAR(100) NOT NULL COMMENT '厂商',
    shelf_life INT DEFAULT NULL COMMENT '保质期(天)',
    can_return TINYINT NOT NULL DEFAULT 1 COMMENT '可否退货',
    can_exchange TINYINT NOT NULL DEFAULT 1 COMMENT '可否换货',
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '商品表';

-- 11. 分站表
CREATE TABLE station (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    station_code VARCHAR(32) NOT NULL UNIQUE COMMENT '分站编号',
    name VARCHAR(100) NOT NULL COMMENT '分站名称',
    address VARCHAR(200) NOT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    manager_id BIGINT DEFAULT NULL COMMENT '分站管理员ID',
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT '分站表';

-- 12. 库房表
CREATE TABLE warehouse (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    warehouse_code VARCHAR(32) NOT NULL UNIQUE COMMENT '库房编号',
    name VARCHAR(100) NOT NULL COMMENT '库房名称',
    address VARCHAR(200) NOT NULL,
    manager_id BIGINT DEFAULT NULL COMMENT '库管员ID',
    level VARCHAR(10) NOT NULL COMMENT '库房级别: CENTER中心 STATION分站',
    station_id BIGINT DEFAULT NULL COMMENT '所属分站ID(分站库房)',
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT '库房表';

-- ============================================================
-- 三、订单相关表
-- ============================================================

-- 13. 订单表
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_code VARCHAR(32) NOT NULL UNIQUE COMMENT '订单编号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    order_type VARCHAR(20) NOT NULL COMMENT '订单类型: NORMAL普通 REMOTE_PAY异地收费 EXCHANGE换货 RETURN退货',
    order_status VARCHAR(20) NOT NULL COMMENT '订单状态: AVAILABLE可分配 SHORTAGE缺货 DISPATCHED已调度 CENTER_OUT中心库房出库 STATION_ARRIVED分站库房到货 TASK_ASSIGNED任务已分配 PICKED已领货 COMPLETED已完成 FAILED失败 CANCELLED已退订',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    receiver_address VARCHAR(200) NOT NULL COMMENT '送货地址',
    receiver_zip_code VARCHAR(10) DEFAULT NULL,
    delivery_station_id BIGINT DEFAULT NULL COMMENT '投递分站ID',
    require_date DATE NOT NULL COMMENT '要求完成日期',
    order_date DATE NOT NULL COMMENT '订单生成日期',
    need_invoice TINYINT NOT NULL DEFAULT 0 COMMENT '是否要发票',
    payer_name VARCHAR(50) DEFAULT NULL COMMENT '付款人(异地收费)',
    payer_address VARCHAR(200) DEFAULT NULL,
    payer_phone VARCHAR(20) DEFAULT NULL,
    payer_zip_code VARCHAR(10) DEFAULT NULL,
    cancel_reason VARCHAR(200) DEFAULT NULL COMMENT '退订/退货/换货原因',
    cancel_date DATE DEFAULT NULL,
    related_order_id BIGINT DEFAULT NULL COMMENT '关联原订单ID(换货/退货)',
    operator_id BIGINT NOT NULL COMMENT '操作员ID',
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '订单总额',
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '订单表';

-- 14. 订单明细表
CREATE TABLE order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    amount DECIMAL(12,2) NOT NULL COMMENT '金额',
    remark VARCHAR(200) DEFAULT NULL,
    INDEX idx_order_id (order_id)
) COMMENT '订单明细表';

-- 15. 缺货记录表
CREATE TABLE shortage_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    shortage_quantity INT NOT NULL COMMENT '缺货数量',
    resolved TINYINT NOT NULL DEFAULT 0 COMMENT '是否已解决: 1是 0否',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolve_time DATETIME DEFAULT NULL,
    INDEX idx_order_id (order_id)
) COMMENT '缺货记录表';

-- ============================================================
-- 四、调度/任务相关表
-- ============================================================

-- 16. 任务单表
CREATE TABLE task_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_code VARCHAR(32) NOT NULL UNIQUE COMMENT '任务单号',
    order_id BIGINT NOT NULL COMMENT '关联订单ID',
    station_id BIGINT NOT NULL COMMENT '执行任务分站ID',
    delivery_person_id BIGINT DEFAULT NULL COMMENT '配送员ID',
    task_type VARCHAR(20) NOT NULL COMMENT '任务类型: DELIVER_PAY送货收款 DELIVER送货 COLLECT收款 EXCHANGE换货 RETURN退货',
    task_status VARCHAR(20) NOT NULL COMMENT '任务状态: DISPATCHED已调度 ASSIGNABLE任务可分配 ASSIGNED任务已分配 PICKED已领货 COMPLETED已完成 PARTIAL部分完成 FAILED失败',
    receiver_name VARCHAR(50) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    receiver_address VARCHAR(200) NOT NULL,
    require_date DATE NOT NULL COMMENT '要求完成日期',
    assign_date DATE DEFAULT NULL COMMENT '任务分配日期',
    complete_date DATE DEFAULT NULL COMMENT '完成日期',
    satisfaction VARCHAR(20) DEFAULT NULL COMMENT '客户满意度: VERY_SATISFIED非常满意 SATISFIED满意 NORMAL一般 UNSATISFIED不满意',
    receipt_remark VARCHAR(500) DEFAULT NULL COMMENT '回执备注',
    dispatch_type VARCHAR(10) DEFAULT NULL COMMENT '调度方式: MANUAL手工 AUTO自动',
    operator_id BIGINT DEFAULT NULL COMMENT '调度操作员ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id),
    INDEX idx_station_id (station_id)
) COMMENT '任务单表';

-- 17. 任务单明细表
CREATE TABLE task_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_order_id BIGINT NOT NULL COMMENT '任务单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(10,2) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    INDEX idx_task_order_id (task_order_id)
) COMMENT '任务单明细表';

-- 18. 调拨单表
CREATE TABLE transfer_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transfer_code VARCHAR(32) NOT NULL UNIQUE COMMENT '调拨单号',
    from_warehouse_id BIGINT NOT NULL COMMENT '出库库房ID',
    to_warehouse_id BIGINT NOT NULL COMMENT '入库库房ID',
    status VARCHAR(20) NOT NULL DEFAULT 'INIT' COMMENT '状态: INIT初始 OUT_STOCK已出库 IN_STOCK已入库',
    related_order_code VARCHAR(32) DEFAULT NULL COMMENT '关联订单号',
    related_task_code VARCHAR(32) DEFAULT NULL COMMENT '关联任务单号',
    plan_date DATE DEFAULT NULL COMMENT '计划出库日期',
    actual_out_date DATE DEFAULT NULL COMMENT '实际出库日期',
    actual_in_date DATE DEFAULT NULL COMMENT '实际入库日期',
    operator_id BIGINT DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT '调拨单表';

-- 19. 调拨单明细表
CREATE TABLE transfer_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transfer_order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL COMMENT '调拨数量',
    INDEX idx_transfer_id (transfer_order_id)
) COMMENT '调拨单明细表';

-- ============================================================
-- 五、库房操作相关表
-- ============================================================

-- 20. 库存量明细表
CREATE TABLE inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    warehouse_id BIGINT NOT NULL COMMENT '库房ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    total_quantity INT NOT NULL DEFAULT 0 COMMENT '总库存量',
    returned_quantity INT NOT NULL DEFAULT 0 COMMENT '退回数量',
    allocated_quantity INT NOT NULL DEFAULT 0 COMMENT '已分配量(预备出库)',
    available_quantity INT NOT NULL DEFAULT 0 COMMENT '可分配量',
    warning_value INT NOT NULL DEFAULT 0 COMMENT '预警值',
    max_value INT NOT NULL DEFAULT 9999 COMMENT '最高库存量',
    UNIQUE KEY uk_warehouse_product (warehouse_id, product_id)
) COMMENT '库存量明细表';

-- 21. 库房流水表
CREATE TABLE warehouse_flow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    warehouse_id BIGINT NOT NULL COMMENT '库房ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    type VARCHAR(10) NOT NULL COMMENT '类型: IN入库 OUT出库',
    sub_type VARCHAR(30) DEFAULT NULL COMMENT '子类型: PURCHASE购货入库 TRANSFER_IN调拨入库 TRANSFER_OUT调拨出库 PICK领货出库 RETURN_IN退货入库 RETURN_OUT退货出库',
    quantity INT NOT NULL COMMENT '数量',
    related_order_no VARCHAR(32) DEFAULT NULL COMMENT '关联单号',
    operator_id BIGINT DEFAULT NULL,
    operate_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    remark VARCHAR(200) DEFAULT NULL,
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_operate_time (operate_time)
) COMMENT '库房流水表';

-- ============================================================
-- 六、进货相关表
-- ============================================================

-- 22. 购货单表
CREATE TABLE purchase_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    purchase_code VARCHAR(32) NOT NULL UNIQUE COMMENT '购货单号',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING待入库 PARTIAL部分入库 COMPLETED已入库',
    purchase_date DATE NOT NULL COMMENT '进货日期',
    operator_id BIGINT DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT '购货单表';

-- 23. 购货单明细表
CREATE TABLE purchase_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    purchase_order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    order_quantity INT NOT NULL COMMENT '订购数量',
    actual_quantity INT DEFAULT NULL COMMENT '实际到货数量',
    remark VARCHAR(200) DEFAULT NULL,
    INDEX idx_purchase_id (purchase_order_id)
) COMMENT '购货单明细表';

-- 24. 退货单表(向供应商退货)
CREATE TABLE return_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    return_code VARCHAR(32) NOT NULL UNIQUE COMMENT '退货单号',
    supplier_id BIGINT DEFAULT NULL COMMENT '供应商ID',
    type VARCHAR(20) NOT NULL COMMENT '类型: TO_SUPPLIER退给供应商 STATION_TO_CENTER分站退中心 CUSTOMER_RETURN客户退货登记',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING待处理 COMPLETED已完成',
    from_warehouse_id BIGINT DEFAULT NULL,
    to_warehouse_id BIGINT DEFAULT NULL,
    return_date DATE DEFAULT NULL,
    operator_id BIGINT DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT '退货单表';

-- 25. 退货单明细表
CREATE TABLE return_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    return_order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    remark VARCHAR(200) DEFAULT NULL,
    INDEX idx_return_id (return_order_id)
) COMMENT '退货单明细表';

-- ============================================================
-- 七、财务相关表
-- ============================================================

-- 26. 发票表
CREATE TABLE invoice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_no VARCHAR(50) NOT NULL UNIQUE COMMENT '发票号码',
    amount DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '发票金额',
    status VARCHAR(20) NOT NULL DEFAULT 'NORMAL' COMMENT '状态: NORMAL正常 STATION_USED分站已领用 CUSTOMER_USED客户已领用 VOID已作废',
    register_date DATE DEFAULT NULL COMMENT '登记日期',
    station_receive_date DATE DEFAULT NULL COMMENT '分站领用日期',
    customer_receive_date DATE DEFAULT NULL COMMENT '客户领用日期',
    void_date DATE DEFAULT NULL COMMENT '作废日期',
    station_id BIGINT DEFAULT NULL COMMENT '领用分站ID',
    task_order_id BIGINT DEFAULT NULL COMMENT '关联任务单ID',
    lost_person VARCHAR(50) DEFAULT NULL COMMENT '丢失人',
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT '发票表';

-- 27. 结算记录表
CREATE TABLE settlement_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(20) NOT NULL COMMENT '结算类型: SUPPLIER与供应商结算 STATION与分站结算',
    target_id BIGINT NOT NULL COMMENT '结算对象ID(供应商ID或分站ID)',
    target_name VARCHAR(100) DEFAULT NULL COMMENT '结算对象名称',
    total_amount DECIMAL(12,2) NOT NULL COMMENT '结算总额',
    settlement_date DATE NOT NULL COMMENT '结算日期',
    start_date DATE DEFAULT NULL COMMENT '结算开始日期',
    end_date DATE DEFAULT NULL COMMENT '结算结束日期',
    operator_id BIGINT DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT '结算记录表';

-- 28. 结算明细表
CREATE TABLE settlement_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    settlement_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(100) DEFAULT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    in_quantity INT NOT NULL DEFAULT 0 COMMENT '进货/送货数量',
    out_quantity INT NOT NULL DEFAULT 0 COMMENT '退货数量',
    settle_quantity INT NOT NULL DEFAULT 0 COMMENT '结算数量',
    settle_amount DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '结算金额',
    INDEX idx_settlement_id (settlement_id)
) COMMENT '结算明细表';

-- ============================================================
-- 八、日志相关表
-- ============================================================

-- 29. 操作日志表
CREATE TABLE operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT DEFAULT NULL,
    username VARCHAR(50) DEFAULT NULL,
    operation VARCHAR(100) NOT NULL COMMENT '操作描述',
    method VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    params TEXT DEFAULT NULL COMMENT '请求参数',
    ip VARCHAR(50) DEFAULT NULL,
    operate_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_operate_time (operate_time),
    INDEX idx_user_id (user_id)
) COMMENT '操作日志表';

-- 30. 登录日志表
CREATE TABLE login_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT DEFAULT NULL,
    username VARCHAR(50) DEFAULT NULL,
    ip VARCHAR(50) DEFAULT NULL,
    login_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1成功 0失败',
    INDEX idx_login_time (login_time)
) COMMENT '登录日志表';

-- 31. 日志备份表
CREATE TABLE operation_log_backup (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT DEFAULT NULL,
    username VARCHAR(50) DEFAULT NULL,
    operation VARCHAR(100) NOT NULL,
    method VARCHAR(200) DEFAULT NULL,
    params TEXT DEFAULT NULL,
    ip VARCHAR(50) DEFAULT NULL,
    operate_time DATETIME NOT NULL,
    backup_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT '操作日志备份表';

-- ============================================================
-- 九、初始化数据
-- ============================================================

-- 初始化角色
INSERT INTO sys_role (role_name, role_code, description, is_system) VALUES
('超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1),
('客服人员', 'CUSTOMER_SERVICE', '负责接收客户配送请求，生成订单', 1),
('调度中心管理员', 'DISPATCH_ADMIN', '负责订单调度与库房调度', 1),
('分站管理员', 'STATION_ADMIN', '负责分站任务分配与配送管理', 1),
('中心库房管理员', 'CENTER_WAREHOUSE', '负责中心库房出入库管理', 1),
('分站库房管理员', 'STATION_WAREHOUSE', '负责分站库房出入库管理', 1),
('配送中心管理员', 'DISTRIBUTION_ADMIN', '负责配送业务与进货管理', 1),
('财务管理员', 'FINANCE_ADMIN', '负责结算与发票管理', 1),
('配送员', 'DELIVERY_PERSON', '负责货物配送', 1);

-- 初始化权限菜单
INSERT INTO sys_permission (perm_name, perm_code, parent_id, path, icon, sort_order, type) VALUES
('客户管理', 'customer', 0, '/customer', 'User', 1, 1),
('客户信息管理', 'customer:manage', 1, '/customer/manage', NULL, 1, 1),
('操作员工作量查询', 'customer:workload', 1, '/customer/workload', NULL, 2, 1),
('订单管理', 'order', 0, '/order', 'Document', 2, 1),
('订单管理', 'order:manage', 4, '/order/manage', NULL, 1, 1),
('调度管理', 'dispatch', 0, '/dispatch', 'SetUp', 3, 1),
('手工调度', 'dispatch:manual', 6, '/dispatch/manual', NULL, 1, 1),
('自动调度', 'dispatch:auto', 6, '/dispatch/auto', NULL, 2, 1),
('缺货订单处理', 'dispatch:shortage', 6, '/dispatch/shortage', NULL, 3, 1),
('任务单查询', 'dispatch:task', 6, '/dispatch/task', NULL, 4, 1),
('分站管理', 'station', 0, '/station', 'OfficeBuilding', 4, 1),
('任务单查询', 'station:task', 11, '/station/task', NULL, 1, 1),
('任务分配', 'station:assign', 11, '/station/assign', NULL, 2, 1),
('打印签收单', 'station:print', 11, '/station/print', NULL, 3, 1),
('回执录入', 'station:receipt', 11, '/station/receipt', NULL, 4, 1),
('缴款查询', 'station:payment', 11, '/station/payment', NULL, 5, 1),
('货物出入库管理', 'warehouse_op', 0, '/warehouse-op', 'Box', 5, 1),
('购货入库', 'warehouse_op:purchase_in', 17, '/warehouse-op/purchase-in', NULL, 1, 1),
('调拨出库', 'warehouse_op:transfer_out', 17, '/warehouse-op/transfer-out', NULL, 2, 1),
('调拨入库', 'warehouse_op:transfer_in', 17, '/warehouse-op/transfer-in', NULL, 3, 1),
('领货管理', 'warehouse_op:pick', 17, '/warehouse-op/pick', NULL, 4, 1),
('退货管理', 'warehouse_op:return', 17, '/warehouse-op/return', NULL, 5, 1),
('商品管理', 'product', 0, '/product', 'Goods', 6, 1),
('一级分类管理', 'product:cat1', 23, '/product/category1', NULL, 1, 1),
('二级分类管理', 'product:cat2', 23, '/product/category2', NULL, 2, 1),
('商品信息管理', 'product:manage', 23, '/product/manage', NULL, 3, 1),
('供应商管理', 'supplier', 0, '/supplier', 'Shop', 7, 1),
('供应商管理', 'supplier:manage', 27, '/supplier/manage', NULL, 1, 1),
('进货结算管理', 'purchase', 0, '/purchase', 'ShoppingCart', 8, 1),
('进货管理', 'purchase:manage', 29, '/purchase/manage', NULL, 1, 1),
('退货安排', 'purchase:return', 29, '/purchase/return', NULL, 2, 1),
('库房管理', 'warehouse', 0, '/warehouse', 'House', 9, 1),
('库房设置', 'warehouse:setup', 32, '/warehouse/setup', NULL, 1, 1),
('库房储备设置', 'warehouse:reserve', 32, '/warehouse/reserve', NULL, 2, 1),
('库存量查询', 'warehouse:stock', 32, '/warehouse/stock', NULL, 3, 1),
('出入库查询', 'warehouse:flow', 32, '/warehouse/flow', NULL, 4, 1),
('财务管理', 'finance', 0, '/finance', 'Money', 10, 1),
('供应商结算', 'finance:supplier_settle', 37, '/finance/supplier-settle', NULL, 1, 1),
('分站结算', 'finance:station_settle', 37, '/finance/station-settle', NULL, 2, 1),
('发票管理', 'finance:invoice', 37, '/finance/invoice', NULL, 3, 1),
('分站发票管理', 'finance:station_invoice', 37, '/finance/station-invoice', NULL, 4, 1),
('业务统计', 'statistics', 0, '/statistics', 'DataAnalysis', 11, 1),
('订购排行榜', 'statistics:rank', 42, '/statistics/rank', NULL, 1, 1),
('分站配送情况', 'statistics:station', 42, '/statistics/station', NULL, 2, 1),
('客户满意度分析', 'statistics:satisfaction', 42, '/statistics/satisfaction', NULL, 3, 1),
('系统管理', 'system', 0, '/system', 'Setting', 12, 1),
('权限选项管理', 'system:permission', 46, '/system/permission', NULL, 1, 1),
('角色管理', 'system:role', 46, '/system/role', NULL, 2, 1),
('用户管理', 'system:user', 46, '/system/user', NULL, 3, 1),
('日志管理', 'log', 0, '/log', 'Notebook', 13, 1),
('操作日志', 'log:operation', 50, '/log/operation', NULL, 1, 1),
('登录日志', 'log:login', 50, '/log/login', NULL, 2, 1);

-- 给超级管理员分配所有权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- 初始化超级管理员用户 (密码: admin123, BCrypt加密)
INSERT INTO sys_user (username, password, real_name, phone, status) VALUES
('admin', '$2a$10$N.ZOn9G6LjCTGYCbMtKNfuJ8B0eLBxoNGfeAHfxHb6eJPyHzSp5Ly', '系统管理员', '13800000000', 1);

INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

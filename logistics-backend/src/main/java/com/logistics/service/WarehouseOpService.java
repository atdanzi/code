package com.logistics.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logistics.entity.TransferOrder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface WarehouseOpService {

    void purchaseInbound(Long purchaseOrderId, Map<Long, Integer> actualQuantities,
                         LocalDate inboundDate, String remark, Long operatorId);

    IPage<TransferOrder> pageTransferOrders(Page<TransferOrder> page, String status,
                                            LocalDate startDate, LocalDate endDate);

    void transferOutbound(List<Long> transferOrderIds, Long operatorId);

    void transferInbound(Long transferOrderId, Map<Long, Integer> actualQuantities,
                         LocalDate inboundDate, String remark, Long operatorId);

    void pickGoods(Long taskOrderId, String picker, LocalDate pickDate, Long operatorId);

    void returnRegister(Long taskOrderId, Long operatorId);

    void stationReturnOut(List<Long> returnOrderItemIds, Long operatorId);

    void centerReturnIn(Long returnOrderId, Long operatorId);
}

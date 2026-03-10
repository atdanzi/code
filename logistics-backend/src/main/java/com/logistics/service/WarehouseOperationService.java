package com.logistics.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface WarehouseOperationService {

    void purchaseInbound(Long purchaseOrderId, Map<Long, Integer> actualQuantities, LocalDate inboundDate, String remark);

    void transferOut(List<Long> transferOrderIds, Long operatorId);

    void transferIn(Long transferOrderId, Map<Long, Integer> actualQuantities, LocalDate inboundDate, String remark);

    void pickGoods(Long taskOrderId, Long pickerId, LocalDate pickDate);

    void returnRegister(Long taskOrderId);

    void stationReturnOut(List<Long> returnItemIds, Long fromWarehouseId);

    void centerReturnIn(Long returnOrderId);
}

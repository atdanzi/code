package com.logistics.service;

import java.util.List;

public interface DispatchService {

    void manualDispatch(Long orderId, Long stationId, Long operatorId);

    void autoDispatch(List<Long> orderIds, Long operatorId);

    void resolveShortage(List<Long> orderIds);
}

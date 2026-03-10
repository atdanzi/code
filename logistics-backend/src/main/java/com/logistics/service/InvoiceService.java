package com.logistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.logistics.entity.Invoice;

import java.math.BigDecimal;

public interface InvoiceService extends IService<Invoice> {

    void batchRegister(String startNo, String endNo, BigDecimal amount);

    void assignToStation(String invoiceNo, Long stationId);

    void assignToCustomer(String invoiceNo, Long taskOrderId);

    void voidInvoice(String invoiceNo, String reason);
}

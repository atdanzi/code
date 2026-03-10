package com.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.logistics.entity.Invoice;
import com.logistics.mapper.InvoiceMapper;
import com.logistics.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl extends ServiceImpl<InvoiceMapper, Invoice> implements InvoiceService {

    @Override
    @Transactional
    public void batchRegister(String startNo, String endNo, BigDecimal amount) {
        long start = Long.parseLong(startNo);
        long end = Long.parseLong(endNo);
        for (long i = start; i <= end; i++) {
            Invoice invoice = new Invoice();
            invoice.setInvoiceNo(String.valueOf(i));
            invoice.setAmount(amount);
            invoice.setStatus("REGISTERED");
            invoice.setRegisterDate(LocalDate.now());
            invoice.setCreateTime(LocalDateTime.now());
            save(invoice);
        }
    }

    @Override
    public void assignToStation(String invoiceNo, Long stationId) {
        Invoice invoice = getOne(new LambdaQueryWrapper<Invoice>().eq(Invoice::getInvoiceNo, invoiceNo));
        if (invoice == null) {
            throw new RuntimeException("发票不存在");
        }
        invoice.setStationId(stationId);
        invoice.setStatus("STATION_ASSIGNED");
        invoice.setStationReceiveDate(LocalDate.now());
        updateById(invoice);
    }

    @Override
    public void assignToCustomer(String invoiceNo, Long taskOrderId) {
        Invoice invoice = getOne(new LambdaQueryWrapper<Invoice>().eq(Invoice::getInvoiceNo, invoiceNo));
        if (invoice == null) {
            throw new RuntimeException("发票不存在");
        }
        invoice.setTaskOrderId(taskOrderId);
        invoice.setStatus("CUSTOMER_ASSIGNED");
        invoice.setCustomerReceiveDate(LocalDate.now());
        updateById(invoice);
    }

    @Override
    public void voidInvoice(String invoiceNo, String reason) {
        Invoice invoice = getOne(new LambdaQueryWrapper<Invoice>().eq(Invoice::getInvoiceNo, invoiceNo));
        if (invoice == null) {
            throw new RuntimeException("发票不存在");
        }
        invoice.setStatus("VOID");
        invoice.setRemark(reason);
        invoice.setVoidDate(LocalDate.now());
        updateById(invoice);
    }
}

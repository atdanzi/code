package com.logistics.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logistics.common.PageResult;
import com.logistics.common.Result;
import com.logistics.entity.Invoice;
import com.logistics.entity.SettlementDetail;
import com.logistics.entity.SettlementRecord;
import com.logistics.service.InvoiceService;
import com.logistics.service.SettlementService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final InvoiceService invoiceService;
    private final SettlementService settlementService;

    @PostMapping("/invoice/register")
    public Result<?> registerInvoice(@RequestBody Map<String, Object> body) {
        String startNo = (String) body.get("startNo");
        String endNo = (String) body.get("endNo");
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        invoiceService.batchRegister(startNo, endNo, amount);
        return Result.success();
    }

    @PostMapping("/invoice/assign-station")
    public Result<?> assignStation(@RequestBody Map<String, Object> body) {
        String invoiceNo = (String) body.get("invoiceNo");
        Long stationId = ((Number) body.get("stationId")).longValue();
        invoiceService.assignToStation(invoiceNo, stationId);
        return Result.success();
    }

    @PostMapping("/invoice/assign-customer")
    public Result<?> assignCustomer(@RequestBody Map<String, Object> body) {
        String invoiceNo = (String) body.get("invoiceNo");
        Long taskOrderId = ((Number) body.get("taskOrderId")).longValue();
        invoiceService.assignToCustomer(invoiceNo, taskOrderId);
        return Result.success();
    }

    @PostMapping("/invoice/void")
    public Result<?> voidInvoice(@RequestBody Map<String, String> body) {
        invoiceService.voidInvoice(body.get("invoiceNo"), body.get("reason"));
        return Result.success();
    }

    @GetMapping("/invoices")
    public Result<?> invoices(@RequestParam(required = false) String status,
                              @RequestParam(required = false) Long stationId,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size) {
        LambdaQueryWrapper<Invoice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(status), Invoice::getStatus, status)
               .eq(stationId != null, Invoice::getStationId, stationId)
               .orderByDesc(Invoice::getCreateTime);
        IPage<Invoice> result = invoiceService.page(new Page<>(page, size), wrapper);
        return Result.success(new PageResult<>(result));
    }

    @GetMapping("/supplier-settlement")
    public Result<?> querySupplierSettlement(@RequestParam(required = false) Long supplierId,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(settlementService.querySupplierSettlement(supplierId, startDate, endDate));
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/supplier-settlement")
    public Result<?> settleSupplier(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long operatorId = (Long) request.getAttribute("userId");

        SettlementRecord record = new SettlementRecord();
        record.setTargetId(((Number) body.get("targetId")).longValue());
        record.setTargetName((String) body.get("targetName"));
        record.setTotalAmount(new BigDecimal(body.get("totalAmount").toString()));
        record.setOperatorId(operatorId);
        record.setRemark((String) body.get("remark"));
        if (body.get("startDate") != null) {
            record.setStartDate(LocalDate.parse((String) body.get("startDate")));
        }
        if (body.get("endDate") != null) {
            record.setEndDate(LocalDate.parse((String) body.get("endDate")));
        }

        List<SettlementDetail> details = parseDetails((List<Map<String, Object>>) body.get("details"));

        settlementService.settleWithSupplier(record, details);
        return Result.success();
    }

    @GetMapping("/station-settlement")
    public Result<?> queryStationSettlement(@RequestParam(required = false) Long stationId,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(settlementService.queryStationSettlement(stationId, startDate, endDate));
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/station-settlement")
    public Result<?> settleStation(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long operatorId = (Long) request.getAttribute("userId");

        SettlementRecord record = new SettlementRecord();
        record.setTargetId(((Number) body.get("targetId")).longValue());
        record.setTargetName((String) body.get("targetName"));
        record.setTotalAmount(new BigDecimal(body.get("totalAmount").toString()));
        record.setOperatorId(operatorId);
        record.setRemark((String) body.get("remark"));
        if (body.get("startDate") != null) {
            record.setStartDate(LocalDate.parse((String) body.get("startDate")));
        }
        if (body.get("endDate") != null) {
            record.setEndDate(LocalDate.parse((String) body.get("endDate")));
        }

        List<SettlementDetail> details = parseDetails((List<Map<String, Object>>) body.get("details"));

        settlementService.settleWithStation(record, details);
        return Result.success();
    }

    private List<SettlementDetail> parseDetails(List<Map<String, Object>> detailMaps) {
        List<SettlementDetail> details = new ArrayList<>();
        if (detailMaps != null) {
            for (Map<String, Object> dm : detailMaps) {
                SettlementDetail d = new SettlementDetail();
                d.setProductId(((Number) dm.get("productId")).longValue());
                d.setProductName((String) dm.get("productName"));
                d.setUnitPrice(new BigDecimal(dm.get("unitPrice").toString()));
                if (dm.get("inQuantity") != null) d.setInQuantity(((Number) dm.get("inQuantity")).intValue());
                if (dm.get("outQuantity") != null) d.setOutQuantity(((Number) dm.get("outQuantity")).intValue());
                d.setSettleQuantity(((Number) dm.get("settleQuantity")).intValue());
                d.setSettleAmount(new BigDecimal(dm.get("settleAmount").toString()));
                details.add(d);
            }
        }
        return details;
    }
}

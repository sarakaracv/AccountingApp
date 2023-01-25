package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.enums.InvoiceType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface InvoiceService {

    InvoiceDto findById(Long id) throws Exception;

    List<InvoiceDto> listPurchaseInvoices();

    List<InvoiceDto> listSalesInvoices();

    void delete(Long id);

    void approveSalesInvoice(Long id);

    void approvePurchaseInvoice(Long id);

    InvoiceDto create(InvoiceDto invoice, InvoiceType invoiceType);

    String generateInvoiceNo(InvoiceType invoiceType);

    Integer calculateTaxByTaxRate(Integer taxRate, BigDecimal price);

    InvoiceDto update(InvoiceDto invoice);

    List<InvoiceDto> findAllInvoiceByClientVendorId(Long id); // to find all invoices linked to clientVendor

    List<InvoiceDto> listLastThreeApprovedInvoices();

    //BigDecimal totalInvoicesProfitLoss(Long invoiceId);
   public Map<String,BigDecimal> invoiceMonthlyProfitLost();
}
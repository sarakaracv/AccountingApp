package com.cydeo.service.impl;


import com.cydeo.dto.InvoiceDto;
import com.cydeo.service.DashboardService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.SecurityService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;
    private final SecurityService securityService;

    public DashboardServiceImpl(InvoiceService invoiceService, InvoiceProductService invoiceProductService, SecurityService securityService) {
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
        this.securityService = securityService;
    }

    @Override
    public BigDecimal totalCostOfAllApprovedPurchaseInvoiceWithTax() {
        return invoiceService.listPurchaseInvoices().
                        stream().filter(p->p.getInvoiceStatus().getValue().equals("Approved"))
                        .map(InvoiceDto::getTotal)
                        .reduce(BigDecimal.ZERO,BigDecimal::add);
    }

    @Override
    public BigDecimal totalSalesWithTax() {
        return invoiceService.listSalesInvoices().
                stream().filter(p->p.getInvoiceStatus().getValue().equals("Approved"))
                .map(InvoiceDto::getTotal)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }

    @Override
    public BigDecimal totalProfitLoss() {
     return  invoiceProductService.findAllByCompany().
             stream().map(invoiceProductDto -> invoiceProductDto.getProfitLoss())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
       // securityService.getLoggedInUser().getCompany();


    }
}
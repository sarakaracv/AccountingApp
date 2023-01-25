package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.ReportingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportingServiceImpl implements ReportingService {

    private final InvoiceProductService invoiceProductService;

    public ReportingServiceImpl(InvoiceProductService invoiceProductService) {
        this.invoiceProductService = invoiceProductService;
    }


    public List<InvoiceProductDto> getStockReport() {

        return invoiceProductService.findAllByInvoiceStatus(InvoiceStatus.APPROVED);

    }


}

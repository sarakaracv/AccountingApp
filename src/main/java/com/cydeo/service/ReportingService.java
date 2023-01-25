package com.cydeo.service;

import com.cydeo.dto.InvoiceProductDto;

import java.util.List;

public interface ReportingService {

    List<InvoiceProductDto> getStockReport();

}

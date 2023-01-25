package com.cydeo.service;

import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface InvoiceProductService {

    InvoiceProductDto findById(Long id);

    List<InvoiceProductDto> findAllByInvoice(Long id);

    void addToInvoice(InvoiceProductDto invoiceProduct, Long invoiceId) throws Exception;

    void delete(Long invoiceId, Long invoiceProductId) throws Exception;

    void deleteAllByInvoice(Long invoiceId);

    List<InvoiceProductDto> findAllByProductAndInvoiceType(Long id, InvoiceType invoiceType);

    InvoiceProductDto update(InvoiceProductDto invoiceProduct);

    List<InvoiceProductDto> findAllByInvoiceStatus(InvoiceStatus invoiceStatus);
    public List<InvoiceProductDto> findAllInvoiceProductsByProductId(Long id);

    List<InvoiceProductDto> findAllByCompany();

    Map<String, BigDecimal> invoiceMonthlyProfitLost();


}

package com.cydeo.repository;

import com.cydeo.entity.Company;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceProductRepository extends JpaRepository<InvoiceProduct, Long> {

    List<InvoiceProduct> findAllByInvoice_Id(Long id);

    List<InvoiceProduct> findAllByProductIdAndInvoice_InvoiceTypeOrderByInvoiceInvoiceNo(Long productId, InvoiceType invoiceType);

    List<InvoiceProduct> findAllByInvoice_InvoiceStatusAndInvoice_CompanyOrderByInvoiceDateDesc(InvoiceStatus invoiceStatus, Company company);

    List<InvoiceProduct> findAllInvoiceProductsByProductId(Long id);

    List<InvoiceProduct> findAllByProductCategoryCompany(Company company);

    List<InvoiceProduct> findAllByProfitLossIsNot(BigDecimal bigDecimal);


}

package com.cydeo.repository;

import com.cydeo.entity.Company;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findAllByInvoiceTypeAndCompanyOrderByInvoiceNoDesc(InvoiceType invoiceType, Company company);


    @Query(value = "select * from invoices where invoice_type=?1 and company_id=?2 and ( is_deleted = true or is_deleted = false) order by invoice_no desc", nativeQuery = true)
    List<Invoice> findAllIncludeDeleted(String invoiceType, Long companyId); // this is same as above derived query, except it includes the deleted ones by overriding the @Where clause in invoice entity

    @Query(value = "select * from invoices where id=?1 and ( is_deleted = true or is_deleted = false)", nativeQuery = true)
    Invoice findByIdIncludeDeleted(Long id); // only used during testing

    List<Invoice> findInvoiceByClientVendor_Id(Long id);

}

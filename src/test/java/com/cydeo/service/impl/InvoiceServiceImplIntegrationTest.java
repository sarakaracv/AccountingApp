package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplIntegrationTest {

    @Autowired
    InvoiceRepository invoiceRepository;
    @Autowired
    MapperUtil mapperUtil;
    @Autowired
    InvoiceProductService invoiceProductService;
    @Autowired
    ProductService productService;
    @Autowired
    SecurityService securityService;
    @Autowired
    InvoiceServiceImpl invoiceService;
    @Autowired
    CompanyService companyService;
    @Autowired
    ClientVendorService clientVendorService;


    @Test
    @Transactional
    void findById() throws Exception {

        InvoiceDto invoice = invoiceService.findById(1L);

        assertNotNull(invoice);

        assertEquals("P-001", invoice.getInvoiceNo());

    }

    @Test
    @WithUserDetails("admin@greentech.com")
    @Transactional
    void listPurchaseInvoices() {

        List<InvoiceDto> list = invoiceService.listPurchaseInvoices();

        assertNotNull(list);

        assertEquals(InvoiceType.PURCHASE, list.get(0).getInvoiceType());

    }

    @Test
    @WithUserDetails("admin@greentech.com")
    @Transactional
    void listSalesInvoices() {

        List<InvoiceDto> list = invoiceService.listSalesInvoices();

        assertNotNull(list);

        assertEquals(InvoiceType.SALES, list.get(0).getInvoiceType());
    }

    @Test
    @Transactional
    @WithUserDetails("admin@greentech.com")
    void delete() {

        invoiceService.delete(3L);

        Invoice found = invoiceRepository.findByIdIncludeDeleted(3L); // the regular findById will not return if its deleted because of @Where

        assertTrue(found.getIsDeleted());

    }

    @Test
    @Transactional
    @WithUserDetails("admin@greentech.com")
    void approveSalesInvoice() throws Exception {

        invoiceService.approveSalesInvoice(12L);

        assertEquals(InvoiceStatus.APPROVED, invoiceService.findById(12L).getInvoiceStatus());

    }

    @Test
    @Transactional
    @WithUserDetails("admin@greentech.com")
    void approvePurchaseInvoice() throws Exception {

        invoiceService.approvePurchaseInvoice(13L);

        assertEquals(InvoiceStatus.APPROVED, invoiceService.findById(13L).getInvoiceStatus());

    }

    @Test
    @WithUserDetails("admin@greentech.com")
    @Transactional
    void create() {

        InvoiceDto invoiceDto = new InvoiceDto();

        invoiceDto.setClientVendor(clientVendorService.findAllClients().get(0));
        invoiceDto.setDate(LocalDate.now());
        invoiceDto.setInvoiceNo("S-111");

        InvoiceDto created = invoiceService.create(invoiceDto, InvoiceType.SALES);

        assertNotNull(created);
        assertEquals("S-111", created.getInvoiceNo());
        assertEquals(InvoiceStatus.AWAITING_APPROVAL, created.getInvoiceStatus());

    }

    @Test
    @WithUserDetails("admin@greentech.com")
    @Transactional
    void listAllInvoices() {

        List<InvoiceDto> list = invoiceService.listAllInvoices(InvoiceType.PURCHASE);

        assertNotNull(list);

        assertEquals(InvoiceType.PURCHASE, list.get(0).getInvoiceType());

    }

    @Test
    @WithUserDetails("admin@bluetech.com")
    @Transactional
    void generateInvoiceNo() {

        String invoiceNo = invoiceService.generateInvoiceNo(InvoiceType.SALES);

        assertNotNull(invoiceNo);

        assertEquals("S-006", invoiceNo);

    }

    @Test
    void calculateTaxByTaxRate() {

        Integer result = invoiceService.calculateTaxByTaxRate(12, BigDecimal.valueOf(133));

        assertEquals(15, result);
    }

    @Test
    @WithUserDetails("admin@greentech.com")
    @Transactional
    void update() {

        InvoiceDto invoice = new InvoiceDto();
        invoice.setDate(LocalDate.now());
        invoice.setInvoiceNo("S-112");
        invoice.setClientVendor(clientVendorService.findAllClients().get(0));

        InvoiceDto saved = invoiceService.create(invoice, InvoiceType.SALES);

        InvoiceDto updated = new InvoiceDto();
        updated.setId(saved.getId());
        updated.setInvoiceNo(invoice.getInvoiceNo());
        updated.setDate(invoice.getDate());
        updated.setClientVendor(clientVendorService.findAllClients().get(1));


        InvoiceDto result = invoiceService.update(updated);

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals(invoice.getInvoiceNo(), result.getInvoiceNo());
    }

    @Test
    @Transactional
    void findAllInvoiceByClientVendorId() {

        List<InvoiceDto> list = invoiceService.findAllInvoiceByClientVendorId(1L);

        assertNotNull(list);

        assertEquals(1L, list.get(0).getClientVendor().getId());

    }

    @Test
    @WithUserDetails("admin@greentech.com")
    @Transactional
    void listLastThreeApprovedInvoices() {

        List<InvoiceDto> list = invoiceService.listLastThreeApprovedInvoices();

        assertNotNull(list);

        assertEquals(InvoiceStatus.APPROVED, list.get(0).getInvoiceStatus());

        assertTrue(list.size() <= 3);

    }

    @Test
    @Transactional
    void invoiceMonthlyProfitLost() {

        Map<String, BigDecimal> map = invoiceService.invoiceMonthlyProfitLost();

        assertNotNull(map);

        assertNotNull(map.values().stream().findAny());
    }
}
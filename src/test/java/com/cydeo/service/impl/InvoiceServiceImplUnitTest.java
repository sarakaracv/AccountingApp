package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.ProductService;
import com.cydeo.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplUnitTest {

    @Mock
    InvoiceRepository invoiceRepository;

    @Mock
    MapperUtil mapperUtil;

    @Mock
    InvoiceProductService invoiceProductService;

    @Mock
    ProductService productService;

    @Mock
    SecurityService securityService;

    @InjectMocks
    InvoiceServiceImpl invoiceService;


    Class<Invoice> invoiceClass = Invoice.class;
    Class<InvoiceDto> invoiceDtoClass = InvoiceDto.class;
    Class<Company> companyClass = Company.class;
    Class<CompanyDto> companyDtoClass = CompanyDto.class;
    Class<InvoiceType> invoiceTypeClass = InvoiceType.class;

    static Invoice invoice;
    static InvoiceDto invoiceDto;
    static Company company;
    static CompanyDto companyDto;
    static List<Invoice> invoiceList;
    static List<InvoiceProductDto> invoiceProductDtoList;

    @BeforeEach
    public void setUp() {

        invoice = new Invoice();
        invoice.setId(12L);
        invoice.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoice.setInvoiceType(InvoiceType.PURCHASE);

        invoiceDto = new InvoiceDto();
        invoiceDto.setInvoiceNo("P-005");

        company = new Company();

        companyDto = new CompanyDto();

        invoiceList = new ArrayList<>(List.of(invoice));

        invoiceProductDtoList = new ArrayList<>();

    }

    @Test
    void findById() throws Exception {

        // given

        Long id = invoice.getId();

        when(invoiceRepository.findById(id)).thenReturn(Optional.of(invoice));
        when(mapperUtil.convert(any(invoiceClass), any(invoiceDtoClass))).thenReturn(invoiceDto);

        // when

        InvoiceDto result = invoiceService.findById(id);

        // then

        InOrder inOrder = inOrder(invoiceRepository, mapperUtil);

        inOrder.verify(invoiceRepository).findById(id);
        inOrder.verify(mapperUtil).convert(any(invoiceClass), any(invoiceDtoClass));

        assertNotNull(result);
    }

    @Test
    void listPurchaseInvoices() {

        when(mapperUtil.convert(any(companyDtoClass), any(companyClass))).thenReturn(company);
        when(securityService.getLoggedInCompany()).thenReturn(companyDto);
        when(invoiceRepository.findAllByInvoiceTypeAndCompanyOrderByInvoiceNoDesc(any(invoiceTypeClass), any(companyClass))).thenReturn(new ArrayList<>(List.of(new Invoice())));
        when(mapperUtil.convert(any(invoiceClass), any(invoiceDtoClass))).thenReturn(invoiceDto);
        when(invoiceProductService.findAllByInvoice(any())).thenReturn(invoiceProductDtoList);

        List<InvoiceDto> list = invoiceService.listPurchaseInvoices();

        assertNotNull(list);

    }

    @Test
    void listSalesInvoices() {

        when(mapperUtil.convert(any(companyDtoClass), any(companyClass))).thenReturn(company);
        when(securityService.getLoggedInCompany()).thenReturn(companyDto);
        when(invoiceRepository.findAllByInvoiceTypeAndCompanyOrderByInvoiceNoDesc(any(invoiceTypeClass), any(companyClass))).thenReturn(invoiceList);
        when(mapperUtil.convert(any(invoiceClass), any(invoiceDtoClass))).thenReturn(invoiceDto);
        when(invoiceProductService.findAllByInvoice(any())).thenReturn(invoiceProductDtoList);

        List<InvoiceDto> list = invoiceService.listPurchaseInvoices();

        assertNotNull(list);

    }


    @Test
    void delete() {

        // given

        when(invoiceRepository.findById(invoice.getId())).thenReturn(Optional.of(invoice));

        // when

        invoiceService.delete(invoice.getId());

        //  then

        InOrder inOrder = inOrder(invoiceRepository, invoiceProductService);

        inOrder.verify(invoiceRepository).findById(invoice.getId());
        inOrder.verify(invoiceRepository).save(invoice);
        inOrder.verify(invoiceProductService).deleteAllByInvoice(invoice.getId());

        assertEquals(invoice.getIsDeleted(), true);

    }

    @Test
    void approveSalesInvoice() {

        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(invoice)).thenReturn(invoice);
        when(invoiceProductService.findAllByInvoice(invoice.getId())).thenReturn(invoiceProductDtoList);
        when(invoiceProductService.findAllByInvoice(invoice.getId())).thenReturn(invoiceProductDtoList);

        invoiceService.approveSalesInvoice(invoice.getId());

        verify(invoiceRepository).findById(anyLong());
        verify(invoiceRepository).save(invoice);
        verify(invoiceProductService, times(2)).findAllByInvoice(invoice.getId());
    }

    @Test
    void approvePurchaseInvoice() {

        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(invoice)).thenReturn(invoice);
        when(invoiceProductService.findAllByInvoice(invoice.getId())).thenReturn(invoiceProductDtoList);

        invoiceService.approvePurchaseInvoice(invoice.getId());

        verify(invoiceRepository).findById(anyLong());
        verify(invoiceRepository).save(invoice);
        verify(invoiceProductService, times(2)).findAllByInvoice(invoice.getId());

    }

    @Test
    void create() {

        when(securityService.getLoggedInCompany()).thenReturn(companyDto);
        when(mapperUtil.convert(any(invoiceDtoClass), any(invoiceClass))).thenReturn(invoice);
        when(invoiceRepository.save(any(invoiceClass))).thenReturn(invoice);
        when(mapperUtil.convert(any(invoiceClass), any(invoiceDtoClass))).thenReturn(invoiceDto);

        InvoiceDto result = invoiceService.create(invoiceDto, InvoiceType.PURCHASE);

        assertNotNull(result);
        assertEquals(InvoiceStatus.AWAITING_APPROVAL, result.getInvoiceStatus());
        assertEquals(InvoiceType.PURCHASE, result.getInvoiceType());

    }

    @Test
    void listAllInvoices() {

        when(invoiceRepository.findAllIncludeDeleted(anyString(), any())).thenReturn(invoiceList);
        when(mapperUtil.convert(any(companyDtoClass), any(companyClass))).thenReturn(company);
        when(securityService.getLoggedInCompany()).thenReturn(companyDto);
        when(mapperUtil.convert(any(invoiceClass), any(invoiceDtoClass))).thenReturn(invoiceDto);

        List<InvoiceDto> result = invoiceService.listAllInvoices(InvoiceType.PURCHASE);

        assertNotNull(result);
        assertNotNull(result.get(0));

    }

    @Test
    void generateInvoiceNo() {

        when(invoiceRepository.findAllIncludeDeleted(any(), any())).thenReturn(invoiceList);
        when(mapperUtil.convert(any(invoiceClass), any(invoiceDtoClass))).thenReturn(invoiceDto);
        when(mapperUtil.convert(any(companyDtoClass), any(companyClass))).thenReturn(company);
        when(securityService.getLoggedInCompany()).thenReturn(companyDto);

        String result = invoiceService.generateInvoiceNo(InvoiceType.PURCHASE);

        assertEquals("P-006", result);

    }

    @Test
    void calculateTaxByTaxRate() {

        Integer result = invoiceService.calculateTaxByTaxRate(12, BigDecimal.valueOf(133));

        assertEquals(15, result);

    }

    @Test
    void update() {

        // given

        InvoiceDto invoice = new InvoiceDto();
        Invoice notUpdatedInvoice = new Invoice();
        Invoice updatedInvoice = new Invoice();

        // stubbing

        when(invoiceRepository.findById(invoice.getId())).thenReturn(Optional.of(notUpdatedInvoice));
        when(mapperUtil.convert(any(invoiceDtoClass), any(invoiceClass))).thenReturn(updatedInvoice);
        when(securityService.getLoggedInCompany()).thenReturn(companyDto);
        when(mapperUtil.convert(any(companyDtoClass), any(companyClass))).thenReturn(company);
        when(invoiceRepository.save(updatedInvoice)).thenReturn(updatedInvoice);
        when(mapperUtil.convert(any(invoiceClass), any(invoiceDtoClass))).thenReturn(invoiceDto);

        // when

        InvoiceDto invoiceDto = invoiceService.update(invoice);

        // then

        InOrder inOrder = inOrder(invoiceRepository, mapperUtil, securityService);

        inOrder.verify(invoiceRepository).findById(invoice.getId());
        inOrder.verify(mapperUtil).convert(any(invoiceDtoClass), any(invoiceClass));
        inOrder.verify(securityService).getLoggedInCompany();
        inOrder.verify(mapperUtil).convert(any(companyDtoClass), any(companyClass));
        inOrder.verify(invoiceRepository).save(updatedInvoice);
        inOrder.verify(mapperUtil).convert(any(invoiceClass), any(invoiceDtoClass));


        assertNotNull(invoiceDto);

    }

    @Test
    void findAllInvoiceByClientVendorId() {

        when(invoiceRepository.findInvoiceByClientVendor_Id(anyLong())).thenReturn(invoiceList);
        when(mapperUtil.convert(any(invoiceClass), any(invoiceDtoClass))).thenReturn(invoiceDto);

        List<InvoiceDto> list = invoiceService.findAllInvoiceByClientVendorId(anyLong());

        assertNotNull(list);
        assertEquals(1, list.size());

    }


    @Test
    void listLastThreeApprovedInvoices() {

        when(invoiceRepository.findAll()).thenReturn(invoiceList);

        List<InvoiceDto> list = invoiceService.listLastThreeApprovedInvoices();

        assertNotNull(list);
        assertTrue(list.size() <= 3);

    }

    @Test
    void invoiceMonthlyProfitLost() {

        when(invoiceRepository.findAll()).thenReturn(invoiceList);

        Map<String, BigDecimal> map = invoiceService.invoiceMonthlyProfitLost();

        assertNotNull(map);
        assertTrue(map.isEmpty()); // because inside the actual method, the APPROVED and SALES ones are filtered out

    }
}
package com.cydeo.controller;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@Controller
@RequestMapping("/purchaseInvoices")
public class PurchaseInvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;
    private final ClientVendorService clientVendorService;
    private final ProductService productService;

    public PurchaseInvoiceController(InvoiceService invoiceService, InvoiceProductService invoiceProductService, ClientVendorService clientVendorService, ProductService productService) {
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
        this.clientVendorService = clientVendorService;
        this.productService = productService;
    }

    @GetMapping("/list")
    public String listPurchaseInvoices(Model model) {

        model.addAttribute("invoices", invoiceService.listPurchaseInvoices());

        return "invoice/purchase-invoice-list";

    }

    @GetMapping("/delete/{id}")
    public String deletePurchaseInvoice(@PathVariable Long id) {

        invoiceService.delete(id);

        return "redirect:/purchaseInvoices/list";

    }

    @GetMapping("/approve/{id}")
    public String approvePurchaseInvoice(@PathVariable Long id) {

        invoiceService.approvePurchaseInvoice(id);

        return "redirect:/purchaseInvoices/list";
    }

    @GetMapping("/create")
    public String createPurchaseInvoice(Model model) {

        InvoiceDto invoiceDTO = new InvoiceDto();

        invoiceDTO.setDate(LocalDate.now());
        invoiceDTO.setInvoiceNo(invoiceService.generateInvoiceNo(InvoiceType.PURCHASE));

        model.addAttribute("newPurchaseInvoice", invoiceDTO);

        model.addAttribute("vendors", clientVendorService.findAllVendors());

        return "invoice/purchase-invoice-create";
    }

    @PostMapping("/create")
    public String insertPurchaseInvoice(@ModelAttribute("newPurchaseInvoice") @Valid InvoiceDto invoice, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("vendors", clientVendorService.findAllVendors());

            return "invoice/purchase-invoice-create";

        }

        InvoiceDto invoiceDTO = invoiceService.create(invoice, InvoiceType.PURCHASE);

        model.addAttribute("invoice", invoiceDTO); // i cant use the invoice coming from argument here, because it has no id - and from this page, i redirect to the /addInvoiceProduct/{id} when the "add product" button is clicked, which requires the id
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        model.addAttribute("invoiceProducts", invoiceProductService.findAllByInvoice(invoice.getId()));
        model.addAttribute("products", productService.findAll());
        model.addAttribute("vendors", clientVendorService.findAllVendors());


        return "invoice/purchase-invoice-update";
    }

    @GetMapping("/update/{id}")
    public String editPurchaseInvoice(@PathVariable Long id, Model model) throws Exception {

        model.addAttribute("invoice", invoiceService.findById(id));
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        model.addAttribute("invoiceProducts", invoiceProductService.findAllByInvoice(id));
        model.addAttribute("products", productService.findAll());
        model.addAttribute("vendors", clientVendorService.findAllVendors());

        return "invoice/purchase-invoice-update";
    }

    @PostMapping("/update/{id}")
    public String updatePurchaseInvoice(@PathVariable Long id, @ModelAttribute InvoiceDto invoice) { // even tho this invoiceDto does not come with an id, if the arg with @ModelAttribute has a field same named as the path variable, in this case {id}, it sets it by default

        invoiceService.update(invoice);

        return "redirect:/purchaseInvoices/update/" + id;
    }

    @PostMapping("/addInvoiceProduct/{invoiceId}")
    public String addProductToInvoice(@PathVariable Long invoiceId, @ModelAttribute("newInvoiceProduct") @Valid InvoiceProductDto newInvoiceProduct, BindingResult bindingResult, Model model) throws Exception {

        if (bindingResult.hasErrors()){

            model.addAttribute("invoice", invoiceService.findById(invoiceId));
            model.addAttribute("invoiceProducts", invoiceProductService.findAllByInvoice(invoiceId));
            model.addAttribute("products", productService.findAll());
            model.addAttribute("vendors", clientVendorService.findAllVendors());

            return "invoice/purchase-invoice-update";
        }

        invoiceProductService.addToInvoice(newInvoiceProduct, invoiceId);

        return "redirect:/purchaseInvoices/update/" + invoiceId;

    }

    @GetMapping("/removeInvoiceProduct/{invoiceId}/{invoiceProductId}")
    public String removeInvoiceProduct(@PathVariable Long invoiceId, @PathVariable Long invoiceProductId) throws Exception {

        invoiceProductService.delete(invoiceId, invoiceProductId);

        return "redirect:/purchaseInvoices/update/" + invoiceId;
    }

    @GetMapping("/print/{id}")
    public String printPurchaseInvoice(@PathVariable Long id, Model model) throws Exception {

        InvoiceDto invoiceDto = invoiceService.findById(id);

        model.addAttribute("invoice", invoiceDto);
        model.addAttribute("invoiceProducts", invoiceProductService.findAllByInvoice(id));
        model.addAttribute("company", invoiceDto.getCompany()); // we can also use the getCompanyByLoggedInUser() from CompanyService when it's ready

        return "invoice/invoice_print";

    }

}

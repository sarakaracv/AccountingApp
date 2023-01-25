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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;

@Controller
@RequestMapping("/salesInvoices")
public class SalesInvoiceController {
    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;
    private final ProductService productService;
    private final ClientVendorService clientVendorService;

    public SalesInvoiceController(InvoiceService invoiceService, InvoiceProductService invoiceProductService, ProductService productService, ClientVendorService clientVendorService) {
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
        this.productService = productService;
        this.clientVendorService = clientVendorService;
    }

    @GetMapping("/list")
    public String listSalesInvoices(Model model) {

        model.addAttribute("invoices", invoiceService.listSalesInvoices());

        return "invoice/sales-invoice-list";
    }

    @GetMapping("/create")
    public String createSalesInvoice(Model model) {

        InvoiceDto invoiceDTO = new InvoiceDto();

        invoiceDTO.setDate(LocalDate.now());
        invoiceDTO.setInvoiceNo(invoiceService.generateInvoiceNo(InvoiceType.SALES));

        model.addAttribute("newSalesInvoice", invoiceDTO);

        model.addAttribute("clients", clientVendorService.findAllClients());

        return "invoice/sales-invoice-create";
    }

    @PostMapping("/create")
    public String insertSalesInvoice(@ModelAttribute("newSalesInvoice") @Valid InvoiceDto invoice, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("clients", clientVendorService.findAllClients());

            return "invoice/sales-invoice-create";

        }

        InvoiceDto invoiceDTO = invoiceService.create(invoice, InvoiceType.SALES);  // i cant use the invoice coming from argument here, because it has no id - and from this page, i redirect to the /addInvoiceProduct/{id} when the "add product" button is clicked, which requires the id

        model.addAttribute("invoice", invoiceDTO);
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        model.addAttribute("invoiceProducts", invoiceProductService.findAllByInvoice(invoice.getId()));
        model.addAttribute("products", productService.findAll());
        model.addAttribute("clients", clientVendorService.findAllClients());


        return "invoice/sales-invoice-update";
    }

    @GetMapping("/update/{id}")
    public String editSalesInvoice(@PathVariable Long id, Model model) throws Exception {

        model.addAttribute("invoice", invoiceService.findById(id));
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        model.addAttribute("invoiceProducts", invoiceProductService.findAllByInvoice(id));
        model.addAttribute("products", productService.findAll());
        model.addAttribute("clients", clientVendorService.findAllClients());

        return "invoice/sales-invoice-update";
    }


    @PostMapping("/update/{id}")
    public String updateSalesInvoice(@PathVariable Long id, @ModelAttribute InvoiceDto invoice) { // even tho this invoiceDto does not come with an id, if the arg with @ModelAttribute has a field same named as the path variable, in this case {id}, it sets it by default

        invoiceService.update(invoice);

        return "redirect:/salesInvoices/update/" + id;
    }

    @PostMapping("/addInvoiceProduct/{invoiceId}")
    public String addProductToInvoice(@PathVariable Long invoiceId, @ModelAttribute("newInvoiceProduct") @Valid InvoiceProductDto newInvoiceProduct, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) throws Exception {

        if (bindingResult.hasErrors()) {

            model.addAttribute("invoice", invoiceService.findById(invoiceId));
            model.addAttribute("invoiceProducts", invoiceProductService.findAllByInvoice(invoiceId));
            model.addAttribute("products", productService.findAll());
            model.addAttribute("clients", clientVendorService.findAllClients());

            return "invoice/sales-invoice-update";
        }

        try {
            invoiceProductService.addToInvoice(newInvoiceProduct, invoiceId);
        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/salesInvoices/update/" + invoiceId;

    }

    @GetMapping("/approve/{id}")
    public String approveSalesInvoice(@PathVariable Long id) {

        invoiceService.approveSalesInvoice(id);

        return "redirect:/salesInvoices/list";
    }

    @GetMapping("/print/{id}")
    public String printSalesInvoice(@PathVariable Long id, Model model) throws Exception {

        InvoiceDto invoiceDto = invoiceService.findById(id);

        model.addAttribute("invoice", invoiceDto);
        model.addAttribute("invoiceProducts", invoiceProductService.findAllByInvoice(id));
        model.addAttribute("company", invoiceDto.getCompany()); // we can also use the getCompanyByLoggedInUser() from CompanyService when it's ready

        return "invoice/invoice_print";

    }


    @GetMapping("/removeInvoiceProduct/{invoiceId}/{invoiceProductId}")
    public String removeInvoiceProduct(@PathVariable Long invoiceId, @PathVariable Long invoiceProductId) throws Exception {

        invoiceProductService.delete(invoiceId, invoiceProductId);

        return "redirect:/salesInvoices/update/" + invoiceId;
    }

    @GetMapping("/delete/{id}")
    public String deleteSalesInvoice(@PathVariable Long id) {

        invoiceService.delete(id);

        return "redirect:/salesInvoices/list";

    }

}

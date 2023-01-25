package com.cydeo.controller;


import com.cydeo.dto.ClientVendorDto;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.service.ClientVendorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;


@Controller
@RequestMapping("/clientVendors")
public class ClientVendorController {




    private final ClientVendorService clientVendorService;

    public ClientVendorController(ClientVendorService clientVendorService) {
        this.clientVendorService = clientVendorService;
    }

    @GetMapping("/list")
    public String getClientVendorsList(Model model) {
        model.addAttribute("clientVendors", clientVendorService.listAllClientVendors());
        return "/clientVendor/clientVendor-list";
    }

    @GetMapping("/create")
    public String createClientVendor(Model model) {
        model.addAttribute("newClientVendor", new ClientVendorDto());
        model.addAttribute("clientVendorTypes", List.of(ClientVendorType.values()));
        return "/clientVendor/clientVendor-create";
    }

    @PostMapping("/create")
    public String insertClientVendor(@Valid @ModelAttribute("newClientVendor") ClientVendorDto clientVendorDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()){

          model.addAttribute("clientVendorTypes", List.of(ClientVendorType.values()));
            return "/clientVendor/clientVendor-create";
        }
        clientVendorService.save(clientVendorDto);
        return "redirect:/clientVendors/list";
    }

    @GetMapping("/update/{id}")
    public String editClientVendor(@PathVariable("id") Long id, Model model) {
        model.addAttribute("clientVendor", clientVendorService.findById(id));
        model.addAttribute("clientVendorTypes",List.of(ClientVendorType.values()));
        return "/clientVendor/clientVendor-update";
    }

    @PostMapping("/update/{id}")
    public String updateClientVendor(@Valid @ModelAttribute("clientVendor") ClientVendorDto clientVendorDto,BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("clientVendor", clientVendorDto);
            model.addAttribute("clientVendorTypes",List.of(ClientVendorType.values()));
            return "/clientVendor/clientVendor-update";
        }
        clientVendorService.update(clientVendorDto);
        return "redirect:/clientVendors/list";

        //made minor changed to update controller


    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes){
        if(clientVendorService.clientVendorWithInvoice(id)) {
            redirectAttributes.addFlashAttribute("error",
                    "The client/vendor can not be deleted because he/she has an invoice ");
            return "redirect:/clientVendors/list";
        }
        clientVendorService.delete(id);
        return "redirect:/clientVendors/list";

/*
 I redirect the to the clientVendorList... If an invoice id does not exist in the DELETE method,
  I want to redirect to the GET method to show an error message.Further,
  we can add the clientVendorWithInvoice() c method in our controller class to help us
validate that the fields don't allow empty strings:
        RedirectAttributes addFlashAttribute(String attributeName,
                @Nullable
                        Object attributeValue)
        Add the given flash attribute.
                Parameters:
        attributeName - the attribute name; never null
        attributeValue - the attribute value; may be null
        RedirectAttributes addFlashAttribute(String attributeName, @Nullable Object attributeValue);

RedirectAttributes addFlashAttribute(Object attributeValue);

Map<String, ?> getFlashAttributes();
FlashMap class inherits its behavior from the HashMap class
As such, a FlashMap instance can store a key-value mapping of the attributes.
## Input FlashMap is used in the final GET request to access the read-only flash attributes that
 were sent by the previous POST request before the redirect
 ##Output FlashMap is used in the POST request to temporarily save
 the flash attributes and send them to the next GET request after the redirect
*/


    }
}

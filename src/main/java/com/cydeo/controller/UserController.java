package com.cydeo.controller;

import com.cydeo.dto.UserDto;
import com.cydeo.service.CompanyService;
import com.cydeo.service.RoleService;
import com.cydeo.service.SecurityService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final SecurityService securityService;
    private final CompanyService companyService;

    public UserController(UserService userService, RoleService roleService, SecurityService securityService, CompanyService companyService) {
        this.userService = userService;
        this.roleService = roleService;
        this.securityService = securityService;
        this.companyService = companyService;
    }

    @GetMapping("/list")
    public String listAllUsers(Model model) {
        model.addAttribute("users", userService.listAllUsers());
        return "/user/user-list";
    }

    @GetMapping("/create")
    public String createUser(Model model) {
        model.addAttribute("newUser", new UserDto());
        model.addAttribute("userRoles", roleService.listAllRoles());
        model.addAttribute("companies", companyService.getAllByUsers());

        return "/user/user-create";
    }

    @PostMapping("/create")
    public String saveUser(@Valid @ModelAttribute("newUser") UserDto userDto, BindingResult bindingResult, Model model) {
//        if (bindingResult.hasErrors() || userService.emailExists(userDto.getUsername())) {
////            if (userDto.getPassword().equals(userDto.getConfirmPassword())){
////
////            }
////            if (!userDto.getConfirmPassword().equals(userDto.getPassword())){
////                bindingResult.rejectValue("password", " ","Password should match");
////            }
            if (userService.emailExists(userDto.getUsername())) {
                bindingResult.rejectValue("username", " ", "A user with this email already exists. Please try with different email.");
            }

            if (bindingResult.hasErrors()) {
                model.addAttribute("userRoles", roleService.listAllRoles());
                model.addAttribute("companies", companyService.getAllByUsers());
                return "/user/user-create";
            }
//
//            model.addAttribute("companies", companyService.getAllByUsers());
//            return "/user/user-create";
//
//        }

        userService.save(userDto);
        return "redirect:/users/list";
    }

    @GetMapping("/update/{id}")
    public String editUser(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("userRoles", roleService.listAllRoles());
        model.addAttribute("companies",companyService.getAllByUsers());

        return "/user/user-update";
    }

    @PostMapping("/update/{id}")
    public String update(@Valid @ModelAttribute("user") UserDto userDto, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("userRoles", roleService.listAllRoles());
            model.addAttribute("companies", companyService.getAllByUsers());
            return "/user/user-update";
        }
        userService.update(userDto);
        return "redirect:/users/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);
        return "redirect:/users/list";
    }
}
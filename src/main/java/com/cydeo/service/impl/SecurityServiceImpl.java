package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.entity.common.UserPrincipal;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.SecurityService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final CompanyService companyService;


    public SecurityServiceImpl(UserRepository userRepository, @Lazy UserService userService, UserRepository userRepository1, MapperUtil mapperUtil, @Lazy CompanyService companyService) {
        this.userRepository = userRepository1;
        this.userService = userService;
        this.companyService = companyService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("This user does not exist");
        }
        return new UserPrincipal(user);
    }
    @Override
    public UserDto getLoggedInUser() {
        var currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByUserName(currentUsername);
    }

    @Override
    public CompanyDto getLoggedInCompany() {
        CompanyDto company = getLoggedInUser().getCompany();
        return companyService.findById(company.getId());
    }

}

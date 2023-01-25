package com.cydeo.service.impl;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.User;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.SecurityService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;
    private final SecurityService securityService;
    private final PasswordEncoder passwordEncoder;
    private final CompanyService companyService;

    public UserServiceImpl(UserRepository userRepository, MapperUtil mapperUtil, SecurityService securityService, PasswordEncoder passwordEncoder, @Lazy CompanyService companyService) {
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
        this.securityService = securityService;
        this.passwordEncoder = passwordEncoder;
        this.companyService = companyService;
    }

    @Override
    public UserDto findByUserName(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NoSuchElementException("User was not found");
        }
        return mapperUtil.convert(user, new UserDto());
    }

    @Override
    public List<UserDto> listAllUsers() {
        if (securityService.getLoggedInUser().getRole().getDescription().equals("Root User")) {
            return userRepository.findAllByRoleDescriptionOrderByCompanyTitle("Admin").stream()
                    .map(user -> mapperUtil.convert(user, new UserDto()))
                    .peek(userDto -> userDto.setIsOnlyAdmin(isOnlyAdmin(userDto)))
                    .collect(Collectors.toList());
        } else {
            Company company = mapperUtil.convert(companyService.getCompanyByLoggedInUser(), new Company());
            return userRepository.findAllByCompanyOrderByRoleDescription(company).stream()
                    .map(user -> mapperUtil.convert(user, new UserDto()))
                    .peek(userDto -> userDto.setIsOnlyAdmin(isOnlyAdmin(userDto)))
                    .collect(Collectors.toList());
        }
    }

    private boolean isOnlyAdmin(UserDto userDto) {
        Company company = mapperUtil.convert(userDto.getCompany(), new Company());
        List<User> admins = userRepository.findAllByRoleDescriptionAndCompanyOrderByCompanyTitleAscRoleDescription("Admin", company);
        return userDto.getRole().getDescription().equals("Admin") && admins.size() == 1;
    }

    @Override
    public void save(UserDto userDto) {
        User user = mapperUtil.convert(userDto, new User());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void update(UserDto userDto) {
        User user = userRepository.findById(userDto.getId()).get();
        User convertedUser = mapperUtil.convert(userDto, new User());
        convertedUser.setId(user.getId());
        convertedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        convertedUser.setEnabled(user.isEnabled());
        userRepository.save(convertedUser);
    }

    public UserDto findById(Long id) {
        return mapperUtil.convert(userRepository.findById(id).
                orElseThrow(() -> new NoSuchElementException("User was not found")), new UserDto());
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id).get();

        user.setIsDeleted(true);
        user.setUsername(user.getUsername() + "-" + user.getId()
                + user.getCompany().getId()
                + user.getRole().getId());
        userRepository.save(user);

    }

    @Override
    public boolean emailExists(String username) {
        boolean match = userRepository.findAll().stream()
                .anyMatch(user -> user.getUsername().equals(username));
        return match;
    }




}











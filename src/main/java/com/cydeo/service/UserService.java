package com.cydeo.service;
import com.cydeo.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto findByUserName(String username);
    List<UserDto> listAllUsers();
    void save(UserDto userDto);
    void update(UserDto userDto);
    UserDto findById(Long id);
    void delete(Long id);

    boolean emailExists(String username);




}
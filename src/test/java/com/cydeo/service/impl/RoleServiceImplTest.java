package com.cydeo.service.impl;

import com.cydeo.dto.RoleDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Role;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.RoleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @InjectMocks
    RoleServiceImpl roleService;

    @Mock
    RoleRepository roleRepository;

    @Mock
    UserServiceImpl userService;

    @Mock
    SecurityServiceImpl securityService;

//    @Mock
//    MapperUtil mapperUtil;

    @Spy
    MapperUtil mapperUtil = new MapperUtil(new ModelMapper());


    @Test
    @DisplayName("When_find_by_id_then_success")
    void GIVEN_ID_WHEN_FIND_BY_ID_THEN_SUCCESS() {

        Role role=new Role("Root User");
        role.setId(1L);
       // String logedin=securityService.getLoggedInUser().getRole().getDescription();
        RoleDto roleDto=mapperUtil.convert(role,new RoleDto());

        when(roleRepository.findById(1L)).thenReturn(Optional.ofNullable(role));

        var returnedRole = roleService.findById(1L);

        Assertions.assertEquals("Root User", returnedRole.getDescription());
    }

    @Test
    @DisplayName("When_find_all_then_success")
    void GIVEN_ROOT_WHEN_FIND_ALL_THEN_SUCCESS() {

        Role role=new Role("Root User");
        role.setId(1L);
        Role role1=new Role("Admin");
        role1.setId(2L);
        Role role2=new Role("Manager");
        role2.setId(3L);
        List<Role> Rolelist=new ArrayList<>();
        Rolelist.add(role);
        Rolelist.add(role1);
        Rolelist.add(role2);
        List<RoleDto> roleDtoList=new ArrayList<>();
        Rolelist.stream().forEach(R-> {
            roleDtoList.add(mapperUtil.convert(R, new RoleDto()));
        });
        UserDto userDto=new UserDto();
        userDto.setRole(roleDtoList.get(0));


        doReturn(userDto).when(securityService).getLoggedInUser();
        //when(securityService.getLoggedInUser().getRole()).thenReturn(userDto.getRole());
        when(roleRepository.findAll()).thenReturn(Rolelist);

        var returnedRole = roleService.listAllRoles();

        assertThat(returnedRole.size() > 0);
        Assertions.assertEquals("Admin", returnedRole.get(0).getDescription());

    }

    @Test
    @DisplayName("When_find_all_then_success")
    void GIVEN_ADMIN_WHEN_FIND_ALL_THEN_SUCCESS() {

        Role role=new Role("Root User");
        role.setId(1L);
        Role role1=new Role("Admin");
        role1.setId(2L);
        Role role2=new Role("Manager");
        role2.setId(3L);
        List<Role> Rolelist=new ArrayList<>();
        Rolelist.add(role);
        Rolelist.add(role1);
        Rolelist.add(role2);
        List<RoleDto> roleDtoList=new ArrayList<>();
            Rolelist.stream().forEach(R-> {
                roleDtoList.add(mapperUtil.convert(R, new RoleDto()));
            });
        UserDto userDto=new UserDto();
        userDto.setRole(roleDtoList.get(1));

        doReturn(userDto).when(securityService).getLoggedInUser();
        //when(securityService.getLoggedInUser().getRole()).thenReturn(userDto.getRole());
        when(roleRepository.findAll()).thenReturn(Rolelist);

        var returnedRole = roleService.listAllRoles();

        assertThat(returnedRole.size() > 0);
        Assertions.assertEquals("Manager", returnedRole.get(1).getDescription());


    }
}